package net.codjo.maven.common.report;
import net.codjo.maven.common.test.LogString;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.apache.maven.reporting.MavenReportException;
import org.xml.sax.EntityResolver;
/**
 *
 */
public class XslGeneratorMock extends XslGenerator {
    private LogString log = new LogString();
    private String content;


    public XslGeneratorMock() {
    }


    public XslGeneratorMock(LogString log) {
        this.log = log;
    }


    public XslGeneratorMock(LogString log, String mockContent) {
        this.log = log;
        this.content = mockContent;
    }


    public void setXslResourceName(String xslResourceName, String resourcesDirectory)
          throws MavenReportException {
        log.call("setXslResourceName", xslResourceName, resourcesDirectory);
    }


    public void setEntityResolver(EntityResolver entityResolver) {
        log.call("setEntityResolver", entityResolver);
    }


    public void generate(File input, File output) throws MavenReportException {
        log.call("generate", input, output);
    }


    public void generate(File input, Writer output) throws MavenReportException {
        log.call("generate", input, toSimpleClassName(output.getClass()));
        if (content != null) {
            try {
                output.write(content);
            }
            catch (IOException e) {
                throw new MavenReportException(e.getLocalizedMessage(), e);
            }
        }
    }


    public void mockGeneratedContent(String value) {
        this.content = value;
    }


    private String toSimpleClassName(Class clazz) {
        String name = clazz.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
