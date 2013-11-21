package net.codjo.maven.common.report;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.reporting.MavenReportException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 *
 */
public class XslGenerator {
    private Transformer transformer;
    private EntityResolver entityResolver;
    private TransformerFactory transformerFactory;


    public void setXslResourceName(String xslResourceName, String resourcesDirectory)
          throws MavenReportException {
        try {
            entityResolver = new DefaultResolver(new SystemStreamLog(), resourcesDirectory);

            InputStream resourceAsStream = getClass().getResourceAsStream(xslResourceName);
            if (transformerFactory == null) {
                TransformerFactory factory = TransformerFactory.newInstance();
                transformer = factory.newTransformer(new StreamSource(resourceAsStream));
            }
            else {
                transformer = transformerFactory.newTransformer(new StreamSource(resourceAsStream));
            }
        }
        catch (TransformerConfigurationException e) {
            throw new MavenReportException("Impossible de trouver la feuille de transformation XSL '"
                                           + xslResourceName + "' dans le classpath.");
        }
    }


    public TransformerFactory getTransformerFactory() {
        return transformerFactory;
    }


    public void setTransformerFactory(TransformerFactory transformerFactory) {
        this.transformerFactory = transformerFactory;
    }


    public EntityResolver getEntityResolver() {
        return entityResolver;
    }


    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }


    public void generate(File input, File output) throws MavenReportException {
        try {
            generate(input, new FileWriter(output));
        }
        catch (MavenReportException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MavenReportException("Probleme lors du traitement de '" + input
                                           + "' vers '" + output + "'", e);
        }
    }


    public void generate(File input, Writer output) throws MavenReportException {
        try {
            StreamResult result = new StreamResult(output);
            Document integrationPlanDom = toDocument(input, entityResolver);
            transformer.transform(new DOMSource(integrationPlanDom), result);
        }
        catch (Exception e) {
            throw new MavenReportException("Probleme lors du traitement de '" + input + "'", e);
        }
    }


    public static Document toDocument(final File dataFile, EntityResolver entityResolver)
          throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (entityResolver != null) {
            builder.setEntityResolver(entityResolver);
        }
        return builder.parse(dataFile);
    }


    public static class DefaultResolver implements EntityResolver {
        private final File root;
        private final Log log;


        public DefaultResolver(Log log, String resourcesDirectory) {
            this.log = log;
            root = new File(resourcesDirectory);
        }


        public InputSource resolveEntity(String publicId, String systemId) throws IOException {
            File entityFile = new File(root, systemId.substring("file:".length(), systemId.length()));

            log.info("Chargement de l'entite de systemeId='" + systemId
                     + "' a partir du fichier " + entityFile);

            if (!entityFile.exists()) {
                throw new IllegalArgumentException("Fichier introuvable : " + entityFile);
            }

            return new InputSource(new FileInputStream(entityFile));
        }
    }
}
