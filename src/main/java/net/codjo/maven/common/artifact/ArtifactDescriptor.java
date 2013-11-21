/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.common.artifact;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
/**
 *
 */
public class ArtifactDescriptor {
    protected String groupId;
    protected String artifactId;
    protected String version;
    protected String classifier;
    protected String type;


    public String getClassifier() {
        return classifier;
    }


    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getGroupId() {
        return groupId;
    }


    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public String getArtifactId() {
        return artifactId;
    }


    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }


    public String getVersion() {
        return version;
    }


    public void setVersion(String version) {
        this.version = version;
    }


    public void resolveIncludeVersion(DependencyManagement dependencyManagement) {
        if (getVersion() != null) {
            return;
        }

        List dependencies = dependencyManagement.getDependencies();
        for (Iterator dependenciesIt = dependencies.iterator(); dependenciesIt.hasNext();) {
            Dependency dependency = (Dependency)dependenciesIt.next();
            if (getGroupId().equals(dependency.getGroupId())
                && getArtifactId().equals(dependency.getArtifactId())
                && isSameClassifier(dependency)) {
                setVersion(dependency.getVersion());
                return;
            }
        }

        throw new RuntimeException("Unable to find version for artifact " + artifactToString());
    }


    public void resolveType(String defaultType) {
        if (getType() != null) {
            return;
        }

        setType(defaultType);
    }


    private boolean isSameClassifier(Dependency dependency) {
        return getClassifier() == null && dependency.getClassifier() == null
               || getClassifier() != null && getClassifier().equals(dependency.getClassifier());
    }


    private String artifactToString() {
        return new StringBuilder()
              .append(emptyIfNull(groupId)).append(':')
              .append(emptyIfNull(artifactId)).append(':')
              .append(emptyIfNull(version)).append(':')
              .append(emptyIfNull(classifier)).append(':')
              .append(emptyIfNull(type)).toString();
    }


    private String emptyIfNull(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }
}
