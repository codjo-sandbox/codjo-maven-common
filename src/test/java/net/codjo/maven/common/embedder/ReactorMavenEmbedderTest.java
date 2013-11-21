package net.codjo.maven.common.embedder;
import java.io.File;
import java.util.Arrays;
import junit.framework.TestCase;
import org.apache.maven.monitor.logging.DefaultLog;
/**
 *
 */
public class ReactorMavenEmbedderTest extends TestCase {
    private static final String BUILD_SUCCESSFUL = "BUILD SUCCESSFUL";
    private static final String BUILD_ERROR = "BUILD ERROR";
    private ReactorMavenEmbedder reactorMavenEmbedder = new ReactorMavenEmbedder();
    private StringBuilder log = new StringBuilder();


    public void test_execute() throws Exception {
        reactorMavenEmbedder.execute(Arrays.asList(new String[]{"clean", "install"}),
                                     new File(getClass().getResource("reactor").toURI()),
                                     new DefaultLog(new TestLogger(log)));

        assertTrue(log.toString().contains(BUILD_SUCCESSFUL));
        assertFalse(log.toString().contains(BUILD_ERROR));
    }


    public void test_execute_withDatabaseIntegration() throws Exception {
        System.setProperty("database", "integration");

        try {
            reactorMavenEmbedder.execute(Arrays.asList(new String[]{"install"}),
                                         new File(getClass().getResource("reactor").toURI()),
                                         new DefaultLog(new TestLogger(log)));
            fail();
        }
        catch (Exception e) {
            ;
        }

        assertFalse(log.toString().contains(BUILD_SUCCESSFUL));
        assertTrue(log.toString().contains(BUILD_ERROR));
        assertTrue(log.toString().contains("Failed to resolve artifact."));
    }
}
