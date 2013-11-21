/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.common.test;
import java.io.File;
import java.net.URL;
import junit.framework.AssertionFailedError;
/**
 * Classe de gestion des chemins d'acces aux répertoires target, src et test.
 */
public final class PathUtil {
    public static final String TARGET_DIRECTORY_NAME = "target";
    public static final String TEST_DIRECTORY_NAME = "test";
    public static final String SRC_DIRECTORY_NAME = "src";
    private static final String TEST_DIRECTORY_MAVEN_2 = "src/test/java";
    private static final String SRC_DIRECTORY_MAVEN_2 = "src/main/java";
    private final Class root;


    public PathUtil(Class baseClass) {
        this.root = baseClass;
    }


    public Directory findTargetDirectory() {
        return findTargetDirectory(root);
    }


    public static Directory findTargetDirectory(Class baseClass) {
        return findDirectory(baseClass, TARGET_DIRECTORY_NAME, TARGET_DIRECTORY_NAME);
    }


    public Directory findTestDirectory() {
        return findTestDirectory(root);
    }


    public static Directory findTestDirectory(Class baseClass) {
        return findDirectory(baseClass, TEST_DIRECTORY_NAME, TEST_DIRECTORY_MAVEN_2);
    }


    public Directory findSrcDirectory() {
        return findSrcDirectory(root);
    }


    public static Directory findSrcDirectory(Class baseClass) {
        return findDirectory(baseClass, SRC_DIRECTORY_NAME, SRC_DIRECTORY_MAVEN_2);
    }


    public Directory findBaseDirectory() {
        return findBaseDirectory(root);
    }


    public static Directory findBaseDirectory(Class baseClass) {
        return findDirectory(baseClass, "", "");
    }


    public Directory findJavaFileDirectory() {
        return findJavaFileDirectory(root);
    }


    public static Directory findJavaFileDirectory(Class baseClass) {
        String name = "/" + baseClass.getName().replace('.', '/');

        return new Directory(new File(findTestDirectory(baseClass), name).getParentFile().getAbsolutePath());
    }


    public File find(String resourceName) {
        return find(root, resourceName);
    }


    public static File find(Class baseClass, String resourceName) {
        URL resource = baseClass.getResource(resourceName);

        if (resource == null) {
            throw new AssertionFailedError("Resource '" + resourceName + "' est introuvable.");
        }

        return new File(resource.getFile());
    }


    private static Directory findDirectory(Class baseClass, String directoryForMavenOne,
                                           String directoryForMavenTwo) {
        String name = "/" + baseClass.getName().replace('.', '/') + ".class";

        String absolutePath = baseClass.getResource(name).getFile();

        Directory inMavenTwo = toDirectory(absolutePath, directoryForMavenTwo);

        if (inMavenTwo.exists()) {
            return inMavenTwo;
        }

        return toDirectory(absolutePath, directoryForMavenOne);
    }


    private static Directory toDirectory(String absolutePath, String directoryForMavenTwo) {
        return new Directory(absolutePath.substring(1,
                                                    absolutePath.lastIndexOf(PathUtil.TARGET_DIRECTORY_NAME))
                             + "/" + directoryForMavenTwo);
    }
}
