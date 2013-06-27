package edu.virginia.lib.fedora;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
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
import org.fcrepo.server.errors.DatastreamNotFoundException;
import org.fcrepo.server.errors.ResourceIndexException;
import org.fcrepo.server.errors.ServerException;
import org.fcrepo.server.resourceIndex.TripleGenerator;
import org.fcrepo.server.storage.DOReader;
import org.fcrepo.server.storage.types.Datastream;
import org.jrdf.graph.Triple;
import org.slf4j.Logger;
import org.springframework.core.io.InputStreamSource;
import org.xml.sax.InputSource;

/**
 * Component for Fedora Commons repositories that uses XSLT to extract RDF from
 * XML metadata for Fedora's Resource Index.
 * 
 * @author ajs6f
 */

public class XsltDsTripleGenerator implements TripleGenerator {

	/**
	 * A Spring {@link InputStreamSource} from which to get the XSLT.
	 */
	private InputStreamSource xsltInputStreamSource;

	/**
	 * Selects the datastream against which to transform.
	 */
	private String datastreamId;

	/**
	 * Thread-safe compiled version of the XSLT.
	 */
	private Templates template;

	private final Any23 any23 = new Any23();

	private static final Logger logger = getLogger(XsltDsTripleGenerator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fcrepo.server.resourceIndex.TripleGenerator#getTriplesForObject(org
	 * .fcrepo.server.storage.DOReader)
	 */
	@Override
	public Set<Triple> getTriplesForObject(final DOReader reader)
			throws ResourceIndexException {

		Source datastreamSource = null;
		InputStream dsContentStream = null;
		final StreamResult rdfXmlResult = new StreamResult(new StringWriter());

		// retrieve the datastream
		try {
			final String pid = reader.GetObjectPID();
			logger.debug("Retrieving datastream: {} from object: {}",
					datastreamId, pid);
			final Datastream datastream = reader.GetDatastream(datastreamId,
					null);
			if (datastream == null) {
				logger.info(
						"Missing datastream for triple extraction:",
						new DatastreamNotFoundException(format(
								"Failed to find datastream: %s in object: %s",
								datastreamId, pid)));
				/**
				 * We do not return an empty Set<Triple> at this point to allow
				 * for the possibility that the XSLT may generate triples based
				 * only on the pid and datastream ID. Instead, we generate dummy
				 * datastream content.
				 */

				dsContentStream = new ByteArrayInputStream(
						"<dummy/>".getBytes());
			} else {
				dsContentStream = datastream.getContentStream();
			}

			// transform the datastream content with the configured XSLT
			datastreamSource = new SAXSource(new InputSource(dsContentStream));
			// make the datastream ID and pid available as XSLT parameters
			final Transformer transformer = template.newTransformer();
			logger.debug(
					"Constructed XSLT transformer, now setting datastreamId parameter to: {} and pid parameter to: {}.",
					datastreamId, pid);
			transformer.setParameter("datastreamId", datastreamId);
			transformer.setParameter("pid", pid);
			transformer.transform(datastreamSource, rdfXmlResult);
		} catch (final TransformerException e) {
			throw new ResourceIndexException(e.getLocalizedMessage(), e);
		} catch (final ServerException e) {
			throw new ResourceIndexException(e.getLocalizedMessage(), e);
		}

		// TODO use better streaming when Any23 provides for it
		final String rdfXmlString = rdfXmlResult.getWriter().toString();
		logger.debug("Extracting triples from: {}", rdfXmlString);
		return extractTriples(getInputStream(rdfXmlString));
	}

	/**
	 * @param rdfXmlString
	 *            {@link String} containing RDF/XML
	 * @return A {@link Set<Triple>} of triples extracted
	 * @throws ResourceIndexException
	 */
	protected Set<Triple> extractTriples(final InputStream rdfXml)
			throws ResourceIndexException {
		// handler will collect emitted triples into a set
		final SetTripleHandler handler = new SetTripleHandler();

		/*
		 * We use a dummy absolute document because Any23 requires it, expecting
		 * as it does to absolutize any relative URLs. In fact we know that no
		 * URI offered for extraction will properly be relative-- Fedora's
		 * Resource Index doesn't contemplate such mechanics.
		 */
		try {
			final DocumentSource source = new ByteArrayDocumentSource(rdfXml,
					"http://dummy.absolute.url", "application/rdf+xml");
			any23.extract(source, handler);
			rdfXml.close();
			return handler.getTriples();
		} catch (final IOException e) {
			throw new ResourceIndexException(e.getLocalizedMessage(), e);
		} catch (final ExtractionException e) {
			throw new ResourceIndexException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * @param rdfXmlString
	 * @return An {@link InputStream} backed by rdfXmlString
	 */
	protected static InputStream getInputStream(final String rdfXmlString) {
		return new ByteArrayInputStream(rdfXmlString.getBytes());
	}

	/**
	 * Pre-compiles the XSLT transform for efficient execution.
	 * 
	 * @throws TransformerConfigurationException
	 * @throws IOException
	 */
	@PostConstruct
	public void compileXSLT() throws TransformerConfigurationException,
			IOException {
		final InputStream stream = xsltInputStreamSource.getInputStream();
		template = TransformerFactory.newInstance().newTemplates(
				new SAXSource(new InputSource(stream)));
		stream.close();
		logger.debug("Finished constructing XSLT tranformer template.");
	}

	/**
	 * @param source
	 *            A Spring {@link InputStreamSource} from which to get the XSLT.
	 */
	public void setXsltInputStreamSource(final InputStreamSource source) {
		this.xsltInputStreamSource = source;
		logger.debug("Set XSLT source to: {}", source.toString());
	}

	/**
	 * @param dsID
	 *            The datastream ID to be retrieved for transformation.
	 */
	public void setDatastreamId(final String dsID) {
		this.datastreamId = dsID;
		logger.debug("Set datastream ID to: {}", dsID);
	}

}
