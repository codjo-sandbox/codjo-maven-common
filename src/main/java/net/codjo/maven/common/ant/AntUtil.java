package net.codjo.maven.common.ant;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.util.FileUtils;
/**
 *
 */
public class AntUtil {
    private static final String UNZIP_TARGET = "unzip_target";


    private AntUtil() {}


    public static String getContentOfFile(File file)
          throws BuildException {
        String contentOfFile;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            contentOfFile = FileUtils.readFully(fileReader);
        }
        catch (IOException e) {
            throw new BuildException("Impossible de lire le fichier "
                                     + file.getAbsolutePath(), e);
        }
        finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return contentOfFile;
    }


    public static Project createAntProject(Target defaultTarget, String defaultTargetName) {
        Project project = new Project();
        defaultTarget.setName(defaultTargetName);
        defaultTarget.setProject(project);
        project.addTarget(defaultTarget);
        return project;
    }


    public static void copyFile(File srcFile, File destDir) {
        Copy copyTask = new Copy();
        copyTask.setTodir(destDir);
        copyTask.setFile(srcFile);
        copyTask.setOverwrite(true);
        Project project = new Project();
        copyTask.setProject(project);
        project.init();
        copyTask.execute();
    }


    public static void zip(File directoryToZip, File zipFile) {
        Zip zipTask = new Zip();
        zipTask.setBasedir(directoryToZip);
        zipTask.setDestFile(zipFile);

        Project antProject = new Project();
        zipTask.setProject(antProject);
        antProject.init();
        zipTask.execute();
    }


    public static void unzipFiles(File[] files, File destination) {
        if ((files == null) || (files.length == 0)) {
            return;
        }

        Target antTarget = new Target();
        Project antProject = createAntProject(antTarget, UNZIP_TARGET);

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            antTarget.addTask(createUnzipTask(file, destination, antProject));
        }

        antProject.init();
        antProject.executeTarget(UNZIP_TARGET);
    }


    public static Expand createUnzipTask(File fileToUnzip, File destination, Project antProject) {
        Expand unzipTask = new Expand();
        unzipTask.setSrc(fileToUnzip);
        unzipTask.setDest(destination);
        unzipTask.setProject(antProject);
        return unzipTask;
    }
}
