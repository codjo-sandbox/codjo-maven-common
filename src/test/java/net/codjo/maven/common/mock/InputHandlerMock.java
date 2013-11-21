package net.codjo.maven.common.mock;
import java.io.IOException;
import java.util.List;
import org.codehaus.plexus.components.interactivity.InputHandler;
/**
 *
 */
public class InputHandlerMock implements InputHandler {
    private String readLine;


    public InputHandlerMock() {
        AgfMojoComponent.declareComponent(this);
    }


    public String readLine() throws IOException {
        return readLine;
    }


    public String readPassword() throws IOException {
        return null;
    }


    public List readMultipleLines() throws IOException {
        return null;
    }


    public void mockReadLine(String line) {
        this.readLine = line;
    }
}
