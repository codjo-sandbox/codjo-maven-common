package net.codjo.maven.common.resources;
import net.codjo.maven.common.test.DirectoryFixture;
import net.codjo.maven.common.test.FileUtil;
import java.io.File;
import junit.framework.TestCase;
import org.apache.maven.project.MavenProject;

public class FilteredManagerTest extends TestCase {
    private DirectoryFixture fixture = DirectoryFixture.newTemporaryDirectoryFixture();


    public void testFilteredCopy() throws Exception {
        File source = new File(fixture, "source.properties");
        FileUtil.saveContent(source, "directory=${project.basedir}\ndirectory2=@project.basedir@");

        File target = new File(fixture, "target.properties");

        MavenProject project = new MavenProject();
        project.setFile(new File("c:\\project\\tiger\\pom.xml"));

        FilteredManager filteredManager = new FilteredManager(project);
        filteredManager.copyFile(source, target, true);

        assertEquals("directory=c:/project/tiger\ndirectory2=c:/project/tiger", FileUtil.loadContent(target));
    }


    public void testFilteredCopyWithUserProperty() throws Exception {
        File source = new File(fixture, "source.properties");
        FileUtil.saveContent(source, "directory=${eye}\ndirectory2=@eye@");

        File target = new File(fixture, "target.properties");

        MavenProject project = new MavenProject();
        project.setFile(new File("c:\\project\\tiger\\pom.xml"));
        project.getProperties().put("eye", "${project.basedir}/toto.txt");

        FilteredManager filteredManager = new FilteredManager(project);
        filteredManager.copyFile(source, target, true);

        assertEquals("directory=c:/project/tiger/toto.txt\n"
                     + "directory2=c:/project/tiger/toto.txt",
                     FileUtil.loadContent(target));
    }


    public void testBackslash() throws Exception {
        File source = new File(fixture, "source.properties");
        FileUtil.saveContent(source, "directory=@eye@");

        File target = new File(fixture, "target.properties");

        MavenProject project = new MavenProject();
        project.setFile(new File("c:\\project\\tiger\\pom.xml"));
        project.getProperties().put("eye", "c:\\temp\\toto");

        FilteredManager filteredManager = new FilteredManager(project);
        filteredManager.copyFile(source, target, true);

        assertEquals("directory=c:/temp/toto", FileUtil.loadContent(target));
    }


    public void testFilteredCopyWithFilterToken() throws Exception {
        File source = new File(fixture, "source.properties");
        FileUtil.saveContent(source, "directory=${eye}\ndirectory2=@eye@\ndirectory3=%eye%");

        File target = new File(fixture, "target.properties");

        MavenProject project = new MavenProject();
        project.setFile(new File("c:\\project\\tiger\\pom.xml"));
        project.getProperties().put("eye", "${project.basedir}/toto.txt");

        FilteredManager filteredManager = new FilteredManager(project);
        filteredManager.addFilterToken("%", "%");
        filteredManager.copyFile(source, target, true);

        assertEquals("directory=c:/project/tiger/toto.txt\n"
                     + "directory2=c:/project/tiger/toto.txt\n"
                     + "directory3=c:/project/tiger/toto.txt",
                     FileUtil.loadContent(target));

        filteredManager.setFilterToken("${", "}");
        filteredManager.copyFile(source, target, true);

        assertEquals("directory=c:/project/tiger/toto.txt\ndirectory2=@eye@\ndirectory3=%eye%",
                     FileUtil.loadContent(target));

        filteredManager.setFilterToken("${", "}");
        filteredManager.addFilterToken("%", "%");
        filteredManager.copyFile(source, target, true);

        assertEquals(
              "directory=c:/project/tiger/toto.txt\ndirectory2=@eye@\ndirectory3=c:/project/tiger/toto.txt",
              FileUtil.loadContent(target));
    }


    protected void setUp() throws Exception {
        fixture.doSetUp();
    }


    protected void tearDown() throws Exception {
        fixture.doTearDown();
    }
}
