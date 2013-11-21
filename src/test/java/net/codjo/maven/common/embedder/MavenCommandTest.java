package net.codjo.maven.common.embedder;
import java.io.File;
import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.model.Dependency;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class MavenCommandTest extends TestCase {

    public void test_version() throws Exception {
        MavenEmbedder embedder = new MavenEmbedder();
        embedder.setClassLoader(Thread.currentThread().getContextClassLoader());
        embedder.start();

        File pomFile = new File(getClass().getResource("version/pom.xml").getPath());
        MavenProject project = embedder.readProject(pomFile);

        List dependencies = project.getDependencies();
        Dependency mavenEmbedderDependency = null;
        for (int i = 0; i < dependencies.size(); i++) {
            if ("maven-embedder".equals(((Dependency)dependencies.get(i)).getArtifactId())) {
                mavenEmbedderDependency = (Dependency)dependencies.get(i);
            }
        }

        if (mavenEmbedderDependency == null) {
            fail();
        }
        else {
            assertEquals("Il existe une nouvelle version de maven-embedder !!!", "2.0.3",
                         mavenEmbedderDependency.getVersion());
        }
    }


    public void test_execute() throws Exception {
        File targetDirectory = new File(getClass().getResource("execute").getPath());
        StringBuilder loggerBuilder = new StringBuilder();
        Log log = new DefaultLog(new TestLogger(loggerBuilder));
        new MavenCommand().execute(new String[]{"clean"}, targetDirectory, log, false);

        String expected = "DEBUG : Found 0 components to load on start";
        assertEquals(expected, loggerBuilder.toString().substring(0, expected.length()));
    }


    public void test_execute_reactor() throws Exception {
        StringBuilder loggerBuilder = new StringBuilder();
        Log log = new DefaultLog(new TestLogger(loggerBuilder));
        new MavenCommand().execute(new String[]{"-v"}, new File("."), log, true);

        assertTrue(loggerBuilder.toString().contains("INFO : Maven version:"));
    }
}
