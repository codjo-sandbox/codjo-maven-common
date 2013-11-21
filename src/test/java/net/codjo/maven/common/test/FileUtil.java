/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.common.test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
/**
 * Classe utilitaire sur les fichiers
 */
public final class FileUtil {
    private FileUtil() {
    }


    public static String loadContent(Reader reader)
          throws IOException {
        StringBuffer fileContent = new StringBuffer();

        char[] buffer = new char[10000];

        int charRead = reader.read(buffer);

        while (charRead != -1) {
            fileContent.append(buffer, 0, charRead);

            charRead = reader.read(buffer);
        }

        return fileContent.toString();
    }


    public static String loadContent(File file) throws IOException {
        Reader reader = new BufferedReader(new FileReader(file));

        try {
            return loadContent(reader);
        }

        finally {
            reader.close();
        }
    }


    public static void saveContent(File file, String fileContent)
          throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(file));

        try {
            writer.write(fileContent);
        }

        finally {
            writer.close();
        }
    }
}
