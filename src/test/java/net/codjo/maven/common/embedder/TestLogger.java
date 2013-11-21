package net.codjo.maven.common.embedder;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
/**
 *
 */
class TestLogger extends AbstractLogger {
    private final StringBuilder loggerBuilder;


    TestLogger(StringBuilder loggerBuilder) {
        super(AbstractLogger.LEVEL_DEBUG, "logger");
        this.loggerBuilder = loggerBuilder;
    }


    public void debug(String message, Throwable throwable) {
        loggerBuilder.append("DEBUG : ").append(message).append("\n");
    }


    public void info(String message, Throwable throwable) {
        loggerBuilder.append("INFO : ").append(message).append("\n");
    }


    public void warn(String message, Throwable throwable) {
        loggerBuilder.append("WARN : ").append(message).append("\n");
    }


    public void error(String message, Throwable throwable) {
        loggerBuilder.append("ERROR : ").append(message).append("\n");
    }


    public void fatalError(String message, Throwable throwable) {
        loggerBuilder.append("FATAL ERROR : ").append(message).append("\n");
    }


    public Logger getChildLogger(String name) {
        return null;
    }
}
