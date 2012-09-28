
package edu.virginia.lib.fedora;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.source.ByteArrayDocumentSource;
import org.apache.any23.source.DocumentSource;
import org.fcrepo.server.errors.ResourceIndexException;
import org.fcrepo.server.errors.ServerException;
import org.fcrepo.server.resourceIndex.TripleGenerator;
import org.fcrepo.server.storage.DOReader;
import org.fcrepo.server.storage.types.Datastream;
import org.jrdf.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.InputStreamSource;
import org.xml.sax.InputSource;

public class XsltDsTripleGenerator implements TripleGenerator, InitializingBean {

    private InputStreamSource xsltInputStreamSource;

    private String datastreamId;

    private Transformer transformer;

    private Any23 any23 = new Any23();

    private static final Logger logger = LoggerFactory
            .getLogger(XsltDsTripleGenerator.class);

    @Override
    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {

        Source datastreamSource = null;
        StreamResult rdfXmlResult = new StreamResult(new StringWriter());
        try {
            Datastream datastream = reader.GetDatastream(datastreamId, null);
            datastreamSource =
                    new SAXSource(
                            new InputSource(datastream.getContentStream()));

            transformer.transform(datastreamSource, rdfXmlResult);
        } catch (ServerException e) {
            throw new ResourceIndexException(e.getLocalizedMessage(), e);
        } catch (TransformerException e) {
            throw new ResourceIndexException(e.getLocalizedMessage(), e);
        }

        // TODO this should be improved to use better streaming;
        String rdfXmlString = rdfXmlResult.getWriter().toString();
        logger.debug("Extracting triples from: {}", rdfXmlString);
        InputStream rdfXmlInputStream =
                new ByteArrayInputStream(rdfXmlString.getBytes());
        SetTripleHandler handler = new SetTripleHandler();

        try {
            DocumentSource source =
                    new ByteArrayDocumentSource(rdfXmlInputStream,
                            "http://dummy.absolute.url", "application/rdf+xml");
            any23.extract(source, handler);
            return handler.getTriples();
        } catch (IOException e) {
            throw new ResourceIndexException(e.getLocalizedMessage(), e);
        } catch (ExtractionException e) {
            throw new ResourceIndexException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws TransformerConfigurationException,
            IOException {
        transformer =
                TransformerFactory.newInstance().newTransformer(
                        new SAXSource(new InputSource(xsltInputStreamSource
                                .getInputStream())));
    }

    public void setXsltInputStreamSource(InputStreamSource source) {
        this.xsltInputStreamSource = source;
    }

    public void setDatastreamId(String dsID) {
        this.datastreamId = dsID;
    }

}
