package net.codjo.maven.common.artifact;
import junit.framework.TestCase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
/**
 *
 */
public class ArtifactDescriptorTest extends TestCase {
    private DependencyManagement dependencyManagement = new DependencyManagement();
    private ArtifactDescriptor artifactDescriptor = new ArtifactDescriptor();


    protected void setUp() throws Exception {
        addClassifierDependency("sql", "sql-version");
        addClassifierDependency("client", "client-version");
        addClassifierDependency("server", "server-version");

        artifactDescriptor.setGroupId("net.codjo.datagen");
        artifactDescriptor.setArtifactId("agf-datagen");
    }


    public void test_resolveIncludeVersion() throws Exception {
        artifactDescriptor.setClassifier("client");

        artifactDescriptor.resolveIncludeVersion(dependencyManagement);

        assertEquals("client-version", artifactDescriptor.getVersion());
    }


    public void test_resolveIncludeVersion_noVersionFound() throws Exception {
        artifactDescriptor.setClassifier("unknown");

        try {
            artifactDescriptor.resolveIncludeVersion(dependencyManagement);
            fail();
        }
        catch (Exception e) {
            assertEquals("Unable to find version for artifact net.codjo.datagen:agf-datagen::unknown:",
                         e.getMessage());
        }
    }


    public void test_resolveIncludeVersion_doNothing() throws Exception {
        artifactDescriptor.setClassifier("client");
        artifactDescriptor.setVersion("SNAPSHOT");

        artifactDescriptor.resolveIncludeVersion(dependencyManagement);

        assertEquals("SNAPSHOT", artifactDescriptor.getVersion());
    }


    public void test_resolveType() throws Exception {
        artifactDescriptor.resolveType("zip");

        assertEquals("zip", artifactDescriptor.getType());
    }


    public void test_resolveType_doNothing() throws Exception {
        artifactDescriptor.setType("xml");

        artifactDescriptor.resolveType("zip");

        assertEquals("xml", artifactDescriptor.getType());
    }


    private void addClassifierDependency(String classifier, String version) {
        addDependency("net.codjo.datagen", "agf-datagen", classifier, version);
    }


    private void addDependency(String groupId, String artifactId, String classifier, String version) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setClassifier(classifier);
        dependency.setVersion(version);
        dependencyManagement.addDependency(dependency);
    }
}
