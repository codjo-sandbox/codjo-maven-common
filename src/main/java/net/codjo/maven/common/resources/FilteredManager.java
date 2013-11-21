/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.common.resources;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.resources.PropertyUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

public class FilteredManager {
    private static final String[] EMPTY_STRING_ARRAY = {};
    private static final String[] DEFAULT_INCLUDES = {"**/**"};
    private MavenProject project;
    private final Properties filterProperties;
    private final List filterTokens = new ArrayList();


    public FilteredManager(MavenProject project) throws IOException {
        this(project, new Properties());
    }


    public FilteredManager(MavenProject project, Properties filterProperties) throws IOException {
        this(project, (Map)filterProperties);
    }


    public FilteredManager(MavenProject project, Map filterProperties) throws IOException {
        this.project = project;
        this.filterProperties = new Properties();
        this.filterProperties.putAll(filterProperties);
        addFilterToken("${", "}");
        addFilterToken("@", "@");
        initializeFiltering();
    }


    public void setFilterToken(String prefix, String suffix) {
        filterTokens.clear();
        addFilterToken(prefix, suffix);
    }


    public void addFilterToken(String prefix, String suffix) {
        filterTokens.add(new FilterToken(prefix, suffix));
    }


    private void initializeFiltering() throws IOException {

        // System properties
        filterProperties.putAll(System.getProperties());

        // Project properties
        filterProperties.putAll(project.getProperties());

        for (Iterator i = project.getBuild().getFilters().iterator(); i.hasNext();) {
            String filtersfile = (String)i.next();

            try {
                Properties properties = PropertyUtils.loadPropertyFile(new File(filtersfile), true, true);

                filterProperties.putAll(properties);
            }
            catch (IOException e) {
                throw new IOException(
                      "Error loading property file '" + filtersfile + "' " + e.getLocalizedMessage());
            }
        }
    }


    public void copyFile(File sourceFile, File targetFile, boolean filtering) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        try {
            executeCopyFile(sourceFile, targetFile, filtering);
        }
        catch (Exception exception) {
            throw new IOException("Error copying resources : " + exception.getLocalizedMessage());
        }
    }


    public void copyResources(List resources, String outputDirectory) throws IOException {
        for (Iterator i = resources.iterator(); i.hasNext();) {
            Resource resource = (Resource)i.next();

            String targetPath = resource.getTargetPath();

            File resourceDirectory = new File(resource.getDirectory());

            if (!resourceDirectory.exists()) {
                continue;
            }

            // this part is required in case the user specified "../something" as destination
            // see MNG-1345
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    throw new IOException("Cannot create resource output directory: "
                                          + outputDir);
                }
            }

            DirectoryScanner scanner = new DirectoryScanner();

            scanner.setBasedir(resource.getDirectory());
            if (resource.getIncludes() != null && !resource.getIncludes().isEmpty()) {
                scanner.setIncludes((String[])resource.getIncludes().toArray(EMPTY_STRING_ARRAY));
            }
            else {
                scanner.setIncludes(DEFAULT_INCLUDES);
            }
            if (resource.getExcludes() != null && !resource.getExcludes().isEmpty()) {
                scanner.setExcludes((String[])resource.getExcludes().toArray(EMPTY_STRING_ARRAY));
            }

            scanner.addDefaultExcludes();
            scanner.scan();

            List includedFiles = Arrays.asList(scanner.getIncludedFiles());
            for (Iterator j = includedFiles.iterator(); j.hasNext();) {
                String name = (String)j.next();

                File sourceFile = new File(resource.getDirectory(), name);
                File targetFile = toDestinationFile(name, targetPath, outputDirectory);
                copyFile(sourceFile, targetFile, resource.isFiltering());
            }
        }
    }


    private File toDestinationFile(String name, String targetPath, String outputDirectory) {
        String destination = name;

        if (targetPath != null) {
            destination = targetPath + "/" + name;
        }
        return new File(outputDirectory, destination);
    }


    private void executeCopyFile(File from, File to, boolean filtering) throws Exception {
        if (!filtering) {
            if (to.lastModified() < from.lastModified()) {
                FileUtils.copyFile(from, to);
            }
        }
        else {
            String fileContents = FileUtils.fileRead(from);
            FileUtils.fileWrite(to.getPath(), filteredString(fileContents));
        }
    }


    private String filteredString(String fileContents) throws Exception {
        for (int i = 0; i < filterTokens.size(); i++) {
            FilterToken filterToken = (FilterToken)filterTokens.get(i);

            String prefix = filterToken.getPrefix();
            String suffix = filterToken.getSuffix();
            int prefixIndex;
            int suffixIndex = -1;

            int offset = 0;
            while (offset != -1) {
                prefixIndex = fileContents.indexOf(prefix, offset);

                if (prefixIndex > -1) {
                    suffixIndex = fileContents.indexOf(suffix, prefixIndex + prefix.length());

                    if (suffixIndex > -1) {
                        String key = fileContents.substring(prefixIndex + prefix.length(), suffixIndex);
                        String completeKey = prefix + key + suffix;

                        String value = getValue(key);
                        if (value == null) {
                            value = completeKey;
                        }
                        else {
                            value = filteredString(value);
                        }
                        fileContents = fileContents.replace(completeKey, value);

                        offset = prefixIndex + value.length();
                    }
                }
                if (prefixIndex < 0 || suffixIndex < 0) {
                    offset = -1;
                }
            }
        }
        return fileContents;
    }


    private String getValue(String key) throws Exception {
        Object mavenProperty = ReflectionValueExtractor.evaluate(key, project);
        String value = null;
        if (mavenProperty == null) {
            String property = filterProperties.getProperty(key);
            if (property != null) {
                value = property.replace("\\", "/");
            }
        }
        else if (File.class.isInstance(mavenProperty)) {
            value = ((File)mavenProperty).getAbsolutePath().replace("\\", "/");
        }
        else if (String.class.isInstance(mavenProperty)) {
            value = mavenProperty.toString().replace("\\", "/");
        }
        return value;
    }


    private static class FilterToken {
        private final String prefix;
        private final String suffix;


        private FilterToken(final String prefix, final String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }


        public String getPrefix() {
            return prefix;
        }


        public String getSuffix() {
            return suffix;
        }
    }
}
