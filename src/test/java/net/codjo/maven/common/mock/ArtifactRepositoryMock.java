package net.codjo.maven.common.mock;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
/**
 *
 */
public class ArtifactRepositoryMock extends DefaultArtifactRepository {
    public ArtifactRepositoryMock() {
        super("mock", AgfMojoTestCase.toUrl("./target/localRepository"), new DefaultRepositoryLayout());
    }
}
