package edu.virginia.lib.fedora;

import static java.net.URI.create;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

@RunWith(MockitoJUnitRunner.class)
public class TestXsltDsTripleGenerator {

	private static final Logger logger = getLogger(TestXsltDsTripleGenerator.class);

	@Mock
	private Datastream datastream;

	@Mock
	private DOReader reader;

	@Test
	public void testCatchOneTriple() throws ServerException, IOException,
			TransformerConfigurationException {
		logger.info("Running testCatchOneTriple()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		final String dsFile = "target/test-classes/mods.xml";

		when(reader.GetDatastream("MODS", null)).thenReturn(datastream);
		when(datastream.getContentStream()).thenReturn(
				new FileInputStream(dsFile));

		final InputStream dsLogStream = new FileInputStream(dsFile);
		logger.debug("Using mock datastream: {}", IOUtils.toString(dsLogStream));
		dsLogStream.close();

		final XsltDsTripleGenerator triplegen = new XsltDsTripleGenerator();
		triplegen.setDatastreamId("MODS");

		triplegen.setXsltInputStreamSource(new FileSystemResource(xsltFile));

		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		logger.debug("Using XSLT transform: {}",
				IOUtils.toString(xsltLogStream));
		xsltLogStream.close();

		triplegen.compileXSLT();

		final Set<Triple> triples = triplegen.getTriplesForObject(reader);
		final SimpleTriple triple = new SimpleTriple(
				uri("info:fedora/uva-lib:1038847"),
				uri("http://fedora.lib.virginia.edu/relationships#isFollowingPageOf"),
				uri("info:fedora/uva-lib:1038846"));
		logger.info("Looking for triple: {}", triple.toString());
		assertTrue("Uh oh! Didn't find it", triples.contains(triple));
		logger.info("Found it!");
	}

	@Test
	public void testNonXMLDatastream() throws ServerException, IOException,
			TransformerConfigurationException {
		logger.info("Running testNonXMLDatastream()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		final String dsFile = "target/test-classes/bad-mods.txt";

		when(reader.GetDatastream("MODS", null)).thenReturn(datastream);
		when(datastream.getContentStream()).thenReturn(
				new FileInputStream(dsFile));

		final InputStream dsLogStream = new FileInputStream(dsFile);
		logger.debug("Using mock datastream: {}", IOUtils.toString(dsLogStream));
		dsLogStream.close();

		final XsltDsTripleGenerator triplegen = new XsltDsTripleGenerator();
		triplegen.setDatastreamId("MODS");

		triplegen.setXsltInputStreamSource(new FileSystemResource(xsltFile));

		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		logger.debug("Using XSLT transform: {}",
				IOUtils.toString(xsltLogStream));
		xsltLogStream.close();

		triplegen.compileXSLT();

		try {
			triplegen.getTriplesForObject(reader);
			fail("Shouldn't have been able to extract from that content!");
		} catch (final ResourceIndexException e) {
			logger.info("Behavior with non-XML datastream was as expected.");
		}

	}

	@Test
	public void testCatchOneTripleWithoutDatastream() throws ServerException,
			IOException, TransformerConfigurationException {
		logger.info("Running testCatchOneTripleWithoutDatastream()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		when(reader.GetDatastream("MODS", null)).thenReturn(null);
		when(reader.GetObjectPID()).thenReturn("test");

		final XsltDsTripleGenerator triplegen = new XsltDsTripleGenerator();
		triplegen.setDatastreamId("MODS");

		triplegen.setXsltInputStreamSource(new FileSystemResource(xsltFile));

		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		logger.debug("Using XSLT transform: {}",
				IOUtils.toString(xsltLogStream));
		xsltLogStream.close();

		triplegen.compileXSLT();

		final Set<Triple> triples = triplegen.getTriplesForObject(reader);
		final SimpleTriple triple = new SimpleTriple(
				uri("info:fedora/uva-lib:1038847"),
				uri("http://fedora.lib.virginia.edu/relationships#isFollowingPageOf"),
				uri("info:fedora/uva-lib:1038846"));
		logger.info("Looking for triple: {}", triple.toString());
		assertTrue("Uh oh! Didn't find it", triples.contains(triple));
		logger.info("Found it!");
	}

	@Test
	public void testCatchOneTripleWithUnretrievableDatastream()
			throws ServerException, IOException,
			TransformerConfigurationException {
		logger.info("Running testCatchOneTripleWithoutDatastream()...");
		final String xsltFile = "target/test-classes/mods2rdf.xsl";
		when(reader.GetDatastream("MODS", null))
				.thenThrow(
						new StreamIOException(
								"Dummy exception representing a failure to retrieve datastream content."));
		when(reader.GetObjectPID()).thenReturn("test");

		final XsltDsTripleGenerator triplegen = new XsltDsTripleGenerator();
		triplegen.setDatastreamId("MODS");

		triplegen.setXsltInputStreamSource(new FileSystemResource(xsltFile));

		final InputStream xsltLogStream = new FileInputStream(xsltFile);
		logger.debug("Using XSLT transform: {}",
				IOUtils.toString(xsltLogStream));
		xsltLogStream.close();

		triplegen.compileXSLT();

		try {
			triplegen.getTriplesForObject(reader);
			fail("Shouldn't have been able to extract from that unretrievable content!");
		} catch (final ServerException e) {
			logger.info("Behavior with unretrievable datastream was as expected.");
		}
	}

	/**
	 * Convenience method for creating RDF URIs from Strings
	 * 
	 * @param v
	 * @return {@link SimpleURIReference}
	 */
	private static URIReference uri(final String v) {
		return new SimpleURIReference(create(v));
	}

}
