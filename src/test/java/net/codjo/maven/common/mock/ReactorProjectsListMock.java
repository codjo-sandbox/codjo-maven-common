package net.codjo.maven.common.mock;
import java.util.ArrayList;
/**
 *
 */
public class ReactorProjectsListMock extends ArrayList {
    public ReactorProjectsListMock() {
        AgfMojoTestCase.setReactorProjects(this);
    }
}
