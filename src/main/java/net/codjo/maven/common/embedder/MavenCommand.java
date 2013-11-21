package net.codjo.maven.common.embedder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.maven.BuildFailureException;
import org.apache.maven.cli.ConsoleDownloadMonitor;
import org.apache.maven.embedder.AbstractMavenEmbedderLogger;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.monitor.event.DefaultEventMonitor;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.dag.CycleDetectedException;

public class MavenCommand {
    private Properties properties;


    public Properties getProperties() {
        return properties;
    }


    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public void execute(String[] goals, MavenProject[] mavenProjects, Log log) throws Exception {
        execute(Arrays.asList(goals), Arrays.asList(mavenProjects), log);
    }


    public void execute(List goals, List mavenProjects, Log log) throws Exception {
        CustomizedMavenEmbedder embedder = createDefaultEmbedder(log);
        doExecute(embedder, goals, mavenProjects);
    }


    public void execute(String[] goals, File targetDirectory, Log log, boolean reactorMode) throws Exception {
        execute(Arrays.asList(goals), targetDirectory, log, reactorMode);
    }


    public void execute(List goals, File targetDirectory, Log log, boolean reactorMode) throws Exception {
        if (reactorMode) {
            doExecuteReactorMode(goals, targetDirectory, log);
        }
        else {
            doExecuteDefaultMode(goals, targetDirectory, log);
        }
    }


    private void doExecuteReactorMode(List goals, File targetDirectory, Log log)
          throws IOException, InterruptedException {
        ReactorMavenEmbedder reactorMavenEmbedder = new ReactorMavenEmbedder();
        reactorMavenEmbedder.setProperties(properties);
        reactorMavenEmbedder.execute(goals, targetDirectory, log);
    }


    private void doExecuteDefaultMode(List goals, File targetDirectory, Log log)
          throws DuplicateProjectException, LifecycleExecutionException, BuildFailureException,
                 CycleDetectedException, ProjectBuildingException, MavenEmbedderException {
        CustomizedMavenEmbedder embedder = createDefaultEmbedder(log);
        MavenProject mavenProject = embedder.readProject(new File(targetDirectory, "pom.xml"));
        List projects = new ArrayList();
        projects.add(mavenProject);
        doExecute(embedder, goals, projects);
    }


    private void doExecute(CustomizedMavenEmbedder embedder, List goals, List mavenProjects)
          throws DuplicateProjectException, BuildFailureException, LifecycleExecutionException,
                 CycleDetectedException {
        PlexusLoggerAdapter logger = new PlexusLoggerAdapter(new MavenEmbedderConsoleLogger());
        EventMonitor eventMonitor = new DefaultEventMonitor(logger);
        ConsoleDownloadMonitor monitor = new ConsoleDownloadMonitor();
        embedder.execute(mavenProjects, goals, eventMonitor, monitor, getProperties(),
                         ((MavenProject)mavenProjects.get(0)).getBasedir());
    }


    private CustomizedMavenEmbedder createDefaultEmbedder(Log log) throws MavenEmbedderException {
        CustomizedMavenEmbedder embedder = new CustomizedMavenEmbedder();
        embedder.setClassLoader(Thread.currentThread().getContextClassLoader());
        embedder.setLogger(new EmbedderToMavenLogger(log));
        embedder.start();
        return embedder;
    }


    private static class EmbedderToMavenLogger extends AbstractMavenEmbedderLogger {
        private Log log;


        EmbedderToMavenLogger(Log log) {
            this.log = log;
            if (getLog().isDebugEnabled()) {
                setThreshold(LEVEL_DEBUG);
            }
            else if (getLog().isInfoEnabled()) {
                setThreshold(LEVEL_INFO);
            }
            else if (getLog().isWarnEnabled()) {
                setThreshold(LEVEL_WARN);
            }
            else {
                setThreshold(LEVEL_ERROR);
            }
            setThreshold(LEVEL_DEBUG);
        }


        public Log getLog() {
            return log;
        }


        public void fatalError(String message, Throwable throwable) {
            getLog().error(message, throwable);
        }


        public void error(String message, Throwable throwable) {
            getLog().error(message, throwable);
        }


        public void warn(String message, Throwable throwable) {
            getLog().warn(message, throwable);
        }


        public void info(String message, Throwable throwable) {
            getLog().info(message, throwable);
        }


        public void debug(String message, Throwable throwable) {
            getLog().debug(message, throwable);
        }
    }
}
