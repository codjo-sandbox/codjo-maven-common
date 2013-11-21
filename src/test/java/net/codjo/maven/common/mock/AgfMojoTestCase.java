package net.codjo.maven.common.mock;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import net.codjo.maven.common.test.PathUtil;
import net.codjo.util.file.FileUtil;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
/**
 */
public abstract class AgfMojoTestCase extends AbstractMojoTestCase {
    /**
     * @noinspection StaticNonFinalField
     */
    static AgfMojoTestCase singleton = null;
    private File pomFile;
    private File targetDir;
    private MavenProjectMock project;
    private ReactorProjectsListMock reactorProjectsListMock;


    protected void setUp() throws Exception {
        super.setUp();
        AgfMojoComponent.declareComponent(this);
    }


    protected void tearDown() throws Exception {
        super.tearDown();
        AgfMojoComponent.getComponents().clear();
    }


    public void setupEnvironment(String pomFilePath) throws IOException {
        setupEnvironment(pomFilePath, USE_POM_DIRECTLY);
    }


    public void setupEnvironment(String pomFilePath, PomUseStrategy strategy) throws IOException {
        targetDir = createTargetDirectory();

        if (DUPLICATE_POM == strategy) {
            File realPomFile = getInputFile(pomFilePath);
            String content = FileUtil.loadContent(realPomFile);

            pomFile = new File(targetDir, realPomFile.getName());
            FileUtil.saveContent(pomFile, content);
        }
        else {
            this.pomFile = getInputFile(pomFilePath);
        }
    }


    private File createTargetDirectory() throws IOException {
        File dir = new File(PathUtil.findTargetDirectory(getClass()),
                            "test-harness/" + getClass().getName());

        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdirs();
        return dir;
    }


    public File getInputFile(String file) {
        return PathUtil.find(getClass(), file);
    }


    public File getOutputFile(String file) {
        return PathUtil.find(getClass(), file);
    }


    protected void deleteOutputFile(String fileName) {
        new File(getPomFile().getParent(), fileName).delete();
    }


    public File getPomFile() {
        return pomFile;
    }


    public File getTargetDir() {
        return targetDir;
    }


    public File getTargetFile(String fileName) {
        return new File(getTargetDir(), fileName);
    }


    public MavenProjectMock getProject() {
        return project;
    }


    static void setProject(MavenProjectMock project) {
        singleton.project = project;
        project.setFile(singleton.getPomFile());
    }


    public ReactorProjectsListMock getReactorProjectsList() {
        return reactorProjectsListMock;
    }


    public static void setReactorProjects(ReactorProjectsListMock reactorProjectsListMock) {
        singleton.reactorProjectsListMock = reactorProjectsListMock;
        singleton.reactorProjectsListMock.add(singleton.getProject());
    }


    public Artifact createArtifact(String groupId, String artifactId, String scope,
                                   String type) {
        VersionRange version = VersionRange.createFromVersion("1.0");

        DefaultArtifact defaultArtifact =
              new DefaultArtifact(groupId, artifactId, version, scope, type, "main", null);

        defaultArtifact.setFile(getInputFile("repository/" + groupId + "/" + artifactId
                                             + "/1.0/" + artifactId + "-1.0." + type));
        return defaultArtifact;
    }


    protected Mojo lookupMojo(String goal) throws Exception {
        singleton = this;
        return lookupMojo(goal, getPomFile());
    }


    public static String toUrl(String pathname) {
        try {
            File remoteDir = new File(pathname);
            return remoteDir.toURL().toExternalForm();
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("BadUrl");
        }
    }


    public void mockUserAnswer(String response) throws ComponentLookupException {
        InputHandlerMock inputHandlerMock = (InputHandlerMock)
              AgfMojoComponent.getComponent(InputHandlerMock.class);
        inputHandlerMock.mockReadLine(response);
    }


    /**
     * @noinspection ClassMayBeInterface
     */
    public static class PomUseStrategy {

        private PomUseStrategy() {
        }
    }
    public static final PomUseStrategy DUPLICATE_POM = new PomUseStrategy();
    public static final PomUseStrategy USE_POM_DIRECTLY = new PomUseStrategy();
}
