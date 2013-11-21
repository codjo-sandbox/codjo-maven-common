package net.codjo.maven.common.embedder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.maven.plugin.logging.Log;
/**
 *
 */
class ReactorMavenEmbedder {
    private Properties properties;


    ReactorMavenEmbedder() {
    }


    public Properties getProperties() {
        return properties;
    }


    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public void execute(List goals, File workingDirectory, Log log) throws IOException, InterruptedException {
        String commandLine = buildCommandLine(goals);

        log.info("");
        log.info("Executing : " + commandLine);
        log.info("");

        Process process = new ProcessBuilder(convertToArray(commandLine))
              .directory(workingDirectory)
              .redirectErrorStream(true)
              .start();

        int loggerResult = new StreamLogger(process.getInputStream(), log).waitFor();
        int processResult = process.waitFor();
        if (processResult != 0 || loggerResult != 0) {
            throw new RuntimeException("Error executing : " + commandLine);
        }
    }


    private String buildCommandLine(List goals) {
        StringBuilder commandLine = new StringBuilder("cmd /C mvn -r");
        if (properties != null) {
            appendProperties(commandLine, properties);
        }
        appendProperties(commandLine, filterSystemProperties());
        appendGoals(goals, commandLine);
        return commandLine.toString();
    }


    private void appendGoals(List goals, StringBuilder commandLine) {
        for (Iterator it = goals.iterator(); it.hasNext();) {
            commandLine.append(" ").append((String)it.next());
        }
    }


    private void appendProperties(StringBuilder commandLine, Properties notNullProperties) {
        for (Iterator it = notNullProperties.entrySet().iterator(); it.hasNext();) {
            Entry entry = (Entry)it.next();
            commandLine.append(" -D").append(entry.getKey()).append("=").append(entry.getValue());
        }
    }


    private Properties filterSystemProperties() {
        Properties filteredProperties = new Properties();
        for (Iterator it = System.getProperties().entrySet().iterator(); it.hasNext();) {
            Entry property = (Entry)it.next();
            String key = (String)property.getKey();
            if (!key.equals(key.toUpperCase())
                && !key.startsWith("java.")
                && !key.startsWith("sun.")
                && !key.startsWith("awt.")
                && !key.startsWith("os.")
                && !key.startsWith("user.")
                && !key.startsWith("file.")
                && !key.endsWith(".separator")) {
                String value = (String)property.getValue();
                if (value.contains(" ")) {
                    value = new StringBuffer().append("\"").append(value).append("\"").toString();
                }
                filteredProperties.setProperty(key, value);
            }
        }
        return filteredProperties;
    }


    private String[] convertToArray(String commandLine) {
        StringTokenizer stringTokenizer = new StringTokenizer(commandLine);
        String[] commandArray = new String[stringTokenizer.countTokens()];
        for (int i = 0; stringTokenizer.hasMoreTokens(); i++) {
            commandArray[i] = stringTokenizer.nextToken();
        }
        return commandArray;
    }


    private static class StreamLogger implements Runnable {
        private final InputStream inputStream;
        private final Log log;
        private final Thread thread;
        private boolean errorDetected = false;


        private StreamLogger(InputStream inputStream, Log log) {
            this.inputStream = inputStream;
            this.log = log;
            thread = new Thread(this);
            thread.start();
        }


        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line = reader.readLine();
                while (line != null) {
                    if (!errorDetected && line.startsWith("[ERROR]")) {
                        errorDetected = true;
                    }
                    log.info(line);
                    line = reader.readLine();
                }
            }
            catch (IOException e) {
                errorDetected = true;
                log.error(e);
            }
            finally {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    ;
                }
                synchronized (thread) {
                    thread.notifyAll();
                }
            }
        }


        public int waitFor() throws InterruptedException {
            synchronized (thread) {
                while (thread.isAlive()) {
                    thread.wait();
                }
            }
            return errorDetected ? 1 : 0;
        }
    }
}
