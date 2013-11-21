package net.codjo.maven.common.mock;
import net.codjo.maven.common.test.LogString;
import java.io.File;
import org.apache.maven.doxia.siterenderer.DefaultSiteRenderer;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
/**
 *
 */
public class RendererMock extends DefaultSiteRenderer {
    private LogString log = new LogString();


    public LogString getLog() {
        return log;
    }


    public void setLog(LogString log) {
        this.log = log;
    }


    public SiteRendererSink createSink(File moduleBaseDir, String document) {
        return new SiteRendererSink(null) {
            public void text(String text) {
//                log.call("text", text);
                log.info(text);
            }


            public void rawText(String text) {
                log.info(text);
            }


            protected void write(String text) {
//                log.info(text);
            }
        };
    }
}