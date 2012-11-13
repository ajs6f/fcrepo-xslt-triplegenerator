
package edu.virginia.lib.fedora;

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.any23.Any23;
import org.apache.any23.source.ByteArrayDocumentSource;
import org.apache.any23.source.DocumentSource;
import org.fcrepo.server.errors.DatastreamNotFoundException;
import org.fcrepo.server.errors.ResourceIndexException;
import org.fcrepo.server.resourceIndex.TripleGenerator;
import org.fcrepo.server.storage.DOReader;
import org.fcrepo.server.storage.types.Datastream;
import org.jrdf.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.InputStreamSource;
import org.xml.sax.InputSource;

/**
 * Component for Fedora Commons repositories that uses XSLT
 * to extract RDF from XML metadata and particularly to supply it to Fedora's
 * Resource Index.
 * 
 * @author ajs6f
 */

public class XsltDsTripleGenerator implements TripleGenerator, InitializingBean {

    private InputStreamSource xsltInputStreamSource;

    private String datastreamId;

    // thread-safe compiled version of the XSLT
    private Templates templates;

    private Any23 any23 = new Any23();

    private static final Logger logger = LoggerFactory
            .getLogger(XsltDsTripleGenerator.class);

    /*
     * (non-Javadoc)
     * @see
     * org.fcrepo.server.resourceIndex.TripleGenerator#getTriplesForObject(org
     * .fcrepo.server.storage.DOReader)
     */
    @Override
    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {

        Source datastreamSource = null;
        StreamResult rdfXmlResult = new StreamResult(new StringWriter());
        // first we retrieve the datastream
        try {
            String pid = reader.GetObjectPID();
            logger.debug("Retrieving datastream: {} from object: {}",
                    datastreamId, pid);
            Datastream datastream = reader.GetDatastream(datastreamId, null);
            if (datastream == null) {
                throw new DatastreamNotFoundException(format(
                        "Failed to find datastream: %1$ in object: %2$",
                        datastreamId, pid));
            }
            // now we transform it with the configured XSLT
            datastreamSource =
                    new SAXSource(
                            new InputSource(datastream.getContentStream()));
            // note that we make the datastream ID and pid available as XSLT parameters
            Transformer transformer = templates.newTransformer();
            logger.debug(
                    "Constructed XSLT transformer, now setting datastreamId parameter to: {} and pid parameter to: {}.",
                    datastreamId, pid);
            transformer.setParameter("datastreamId", datastreamId);
            transformer.setParameter("pid", pid);
            transformer.transform(datastreamSource, rdfXmlResult);
        } catch (Exception e) {
            throw new ResourceIndexException(e.getLocalizedMessage(), e);
        }

        // TODO this should be improved to use better streaming
        String rdfXmlString = rdfXmlResult.getWriter().toString();
        InputStream rdfXmlInputStream =
                new ByteArrayInputStream(rdfXmlString.getBytes());
        // handler will collect emitted triples into a set
        SetTripleHandler handler = new SetTripleHandler();

        // the actual extraction of triples
        logger.debug("Extracting triples from: {}", rdfXmlString);
        try {
            DocumentSource source =
                    new ByteArrayDocumentSource(rdfXmlInputStream,
                            "http://dummy.absolute.url", "application/rdf+xml");
            any23.extract(source, handler);
            return handler.getTriples();
        } catch (Exception e) {
            throw new ResourceIndexException(e.getLocalizedMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws TransformerConfigurationException,
            IOException {
        templates =
                TransformerFactory.newInstance().newTemplates(
                        new SAXSource(new InputSource(xsltInputStreamSource
                                .getInputStream())));
        logger.debug("Finished constructing XSLT tranformer template.");
    }

    public void setXsltInputStreamSource(InputStreamSource source) {
        this.xsltInputStreamSource = source;
        logger.debug("Set XSLT source to: {}", source.toString());
    }

    public void setDatastreamId(String dsID) {
        this.datastreamId = dsID;
        logger.debug("Set datastream ID to: {}", dsID);
    }

}
