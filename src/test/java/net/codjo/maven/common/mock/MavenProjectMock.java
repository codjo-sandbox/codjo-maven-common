package net.codjo.maven.common.mock;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public class MavenProjectMock extends MavenProject {
    private DependencyManagement dependencyManagement;


    public MavenProjectMock() {
        super(new Model());
        ArtifactRepository remote =
              new DefaultArtifactRepository("for-test",
                                            AgfMojoTestCase.toUrl("src/test/resources/remoteRepository"),
                                            new DefaultRepositoryLayout());
        setRemoteArtifactRepositories(Collections.singletonList(remote));
        setFile(new File("./pom.xml"));
        AgfMojoTestCase.setProject(this);
        setArtifact(new ArtifactMock());

        setPluginArtifacts(new HashSet());
        setReportArtifacts(new HashSet());
        setExtensionArtifacts(new HashSet());
        setPluginArtifactRepositories(new ArrayList());

        getBuild().setDirectory(AgfMojoTestCase.singleton.getTargetDir().getAbsolutePath());
        getBuild().setOutputDirectory("target/target-test");
    }


    public void addToDependencyManagement(Dependency dependency) {
        if (dependencyManagement == null) {
            dependencyManagement = new DependencyManagement();
            getModel().setDependencyManagement(dependencyManagement);
        }

        dependencyManagement.addDependency(dependency);
    }


    private static class ArtifactMock extends DefaultArtifact {
        ArtifactMock() {
            super("group", "art", VersionRange.createFromVersion("1.0"), "runtime", "jar", "main", null);
        }
    }
}
