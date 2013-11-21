package net.codjo.maven.common.test;
/**
 * Interface d�crivant les fixtures de test.
 */

public interface Fixture {

    public void doSetUp() throws Exception;


    public void doTearDown() throws Exception;
}
