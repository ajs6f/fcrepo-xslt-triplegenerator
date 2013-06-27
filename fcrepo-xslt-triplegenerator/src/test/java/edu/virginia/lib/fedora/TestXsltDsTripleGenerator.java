package edu.virginia.lib.fedora;

import static java.net.URI.create;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.io.IOUtils;
import org.fcrepo.common.rdf.SimpleTriple;
import org.fcrepo.common.rdf.SimpleURIReference;
import org.fcrepo.server.errors.ResourceIndexException;
import org.fcrepo.server.errors.ServerException;
import org.fcrepo.server.errors.StreamIOException;
import org.fcrepo.server.storage.DOReader;
import org.fcrepo.server.storage.types.Datastream;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.core.io.FileSystemResource;

/**
 * Tests for {@link XsltDsTripleGenerator}
 * 
 * @author ajs6f
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class TestXsltDsTripleGenerator extends XsltDsTripleGenerator {

	private static final Logger logger = getLogger(TestXsltDsTripleGenerator.class);

	@Mock
	private Datastream mockDatastream;

	@Mock
	private DOReader mockReader;

	/**
	 * Checks for the presence of one RDF triple from the transformed datastream
	 * after extraction.
	 * 
	 * @throws ServerException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 */
	@Test
	public void testCatchOneTriple() throws ServerException, IOException,
			TransformerConfigurationException {
		logger.info("Running testCatchOneTriple()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		final String dsFile = "target/test-classes/mods.xml";

		when(mockReader.GetDatastream("MODS", null)).thenReturn(mockDatastream);
		when(mockDatastream.getContentStream()).thenReturn(
				new FileInputStream(dsFile));

		final InputStream dsLogStream = new FileInputStream(dsFile);
		logger.debug("Using mock datastream: {}", IOUtils.toString(dsLogStream));
		dsLogStream.close();

		setDatastreamId("MODS");

		setXsltInputStreamSource(new FileSystemResource(xsltFile));

		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		logger.debug("Using XSLT transform: {}",
				IOUtils.toString(xsltLogStream));
		xsltLogStream.close();
		compileXSLT();
		final Set<Triple> triples = getTriplesForObject(mockReader);
		assertTrue(
				"Didn't find appropriate triple!",
				triples.contains(new SimpleTriple(
						uri("info:fedora/uva-lib:1038847"),
						uri("http://fedora.lib.virginia.edu/relationships#isFollowingPageOf"),
						uri("info:fedora/uva-lib:1038846"))));
		logger.info("Found appropriate triple.");
	}

	/**
	 * Checks for appropriate failure in the presence of a non-XML datastream.
	 * 
	 * @throws ServerException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 */
	@Test
	public void testNonXMLDatastream() throws ServerException, IOException,
			TransformerConfigurationException {
		logger.info("Running testNonXMLDatastream()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		final String dsFile = "target/test-classes/non-XML-datastream.txt";

		when(mockReader.GetDatastream("MODS", null)).thenReturn(mockDatastream);
		when(mockDatastream.getContentStream()).thenReturn(
				new FileInputStream(dsFile));

		final InputStream dsLogStream = new FileInputStream(dsFile);
		logger.debug("Using mock datastream: {}", IOUtils.toString(dsLogStream));
		dsLogStream.close();

		setDatastreamId("MODS");
		setXsltInputStreamSource(new FileSystemResource(xsltFile));
		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		xsltLogStream.close();
		compileXSLT();

		try {
			getTriplesForObject(mockReader);
			fail("Shouldn't have been able to extract from that content!");
		} catch (final ResourceIndexException e) {
			logger.info("Behavior with non-XML datastream was as expected.");
		}

	}

	/**
	 * Checks for the production of triples in the absence of the selected
	 * datastream. This is correct behavior because an XSLT transform may
	 * produce triples with no other input than the object PID and datastream
	 * ID.
	 * 
	 * @throws ServerException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 */
	@Test
	public void testCatchOneTripleWithoutDatastream() throws ServerException,
			IOException, TransformerConfigurationException {
		logger.info("Running testCatchOneTripleWithoutDatastream()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		when(mockReader.GetDatastream("MODS", null)).thenReturn(null);
		when(mockReader.GetObjectPID()).thenReturn("test");

		setDatastreamId("MODS");
		setXsltInputStreamSource(new FileSystemResource(xsltFile));
		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		logger.debug("Using XSLT transform: {}",
				IOUtils.toString(xsltLogStream));
		xsltLogStream.close();

		compileXSLT();

		final Set<Triple> triples = getTriplesForObject(mockReader);
		assertTrue(
				"Didn't find our triple!",
				triples.contains(new SimpleTriple(
						uri("info:fedora/uva-lib:1038847"),
						uri("http://fedora.lib.virginia.edu/relationships#isFollowingPageOf"),
						uri("info:fedora/uva-lib:1038846"))));
		logger.info("Found appropriate triple.");
	}

	/**
	 * Checks for correct behavior in the presence of an unretrievable
	 * datastream.
	 * 
	 * @throws ServerException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 */
	@Test
	public void testCatchOneTripleWithUnretrievableDatastream()
			throws ServerException, IOException,
			TransformerConfigurationException {
		logger.info("Running testCatchOneTripleWithUnretrievableDatastream()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		when(mockReader.GetDatastream("MODS", null))
				.thenThrow(
						new StreamIOException(
								"Dummy exception representing a failure to retrieve datastream content."));
		when(mockReader.GetObjectPID()).thenReturn("test");

		setDatastreamId("MODS");
		setXsltInputStreamSource(new FileSystemResource(xsltFile));

		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		logger.debug("Using XSLT transform: {}",
				IOUtils.toString(xsltLogStream));
		xsltLogStream.close();
		compileXSLT();

		try {
			getTriplesForObject(mockReader);
			fail("Shouldn't have been able to extract from that unretrievable content!");
		} catch (final ServerException e) {
			logger.info("Behavior with unretrievable datastream was as expected.");
		}
	}

	/**
	 * Checks for correct behavior in the presence of ill-formed RDF/XML.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@Test
	public void testBadRdfStringForExtraction() throws SecurityException,
			NoSuchMethodException {
		try {
			extractTriples(getInputStream("Junk rdf"));
			fail("Shouldn't have been able to extract from that bad RDF String!");
		} catch (final ResourceIndexException e) {
			logger.info("Behavior with bad RDF String was as expected.");
		}
	}

	/**
	 * Checks for correct behavior in the presence of a malfunctioning RDF
	 * InputStream.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	@Test
	public void testBadRdfInputStreamForExtraction() throws SecurityException,
			NoSuchMethodException, IOException {
		final InputStream mockRdfXml = mock(InputStream.class);
		when(mockRdfXml.read(any(byte[].class))).thenThrow(new IOException());
		try {
			extractTriples(mockRdfXml);
			fail("Shouldn't have been able to extract from that bad RDF InputStream!");
		} catch (final ResourceIndexException e) {
			logger.info("Behavior with bad RDF InputStream was as expected.");
		}
	}

	/**
	 * Convenience method for creating RDF URIs from Strings
	 * 
	 * @param v
	 *            A {@link String} with the form of an {@link java.net.URI}
	 * @return {@link SimpleURIReference}
	 */
	private static URIReference uri(final String v) {
		return new SimpleURIReference(create(v));
	}

}
