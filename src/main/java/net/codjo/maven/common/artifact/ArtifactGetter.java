/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.common.artifact;
import java.io.File;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.transform.SnapshotTransformation;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
/**
 *
 */
public class ArtifactGetter {
    private final ArtifactFactory artifactFactory;
    private final ArtifactRepository localRepository;
    private final WagonManager wagonManager;
    private final List remoteArtifactRepositories;
    private final RepositoryMetadataManager metadataManager;


    public ArtifactGetter(ArtifactFactory artifactFactory,
                          ArtifactRepository localRepository, List remoteArtifactRepositories,
                          WagonManager wagonManager, RepositoryMetadataManager repositoryMetadataManager) {
        this.artifactFactory = artifactFactory;
        this.localRepository = localRepository;
        this.wagonManager = wagonManager;
        this.remoteArtifactRepositories = remoteArtifactRepositories;
        this.metadataManager = repositoryMetadataManager;
    }


    public Artifact getArtifact(ArtifactDescriptor artifactDescriptor)
          throws TransferFailedException, ResourceDoesNotExistException,
                 ArtifactNotFoundException, ArtifactResolutionException {
        Artifact artifact = createArtifact(artifactDescriptor);
        SnapshotTransformation snapshotTransformation = new SnapshotTransformation() {
            public void transformForResolve(Artifact artifact,
                                            List remoteRepositories,
                                            ArtifactRepository localRepository)
                  throws ArtifactResolutionException {
                repositoryMetadataManager = metadataManager;
                super.transformForResolve(artifact, remoteRepositories, localRepository);
            }
        };
        snapshotTransformation.enableLogging(new ConsoleLogger(Logger.LEVEL_INFO, "SnapshotTransformation"));
        snapshotTransformation.transformForResolve(artifact, remoteArtifactRepositories,
                                                   localRepository);

        String artifactLocalPath =
              localRepository.getBasedir() + File.separator
              + localRepository.pathOf(artifact);
        artifact.setFile(new File(artifactLocalPath));

        if (!artifact.getFile().exists()) {
            wagonManager.getArtifact(artifact, remoteArtifactRepositories);
        }
        return artifact;
    }


    protected ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }


    protected Artifact createArtifact(ArtifactDescriptor artifactDescriptor) {
        return getArtifactFactory().createArtifactWithClassifier(artifactDescriptor.getGroupId(),
                                                                 artifactDescriptor.getArtifactId(),
                                                                 artifactDescriptor.getVersion(),
                                                                 artifactDescriptor.getType(),
                                                                 artifactDescriptor.getClassifier());
    }
}
