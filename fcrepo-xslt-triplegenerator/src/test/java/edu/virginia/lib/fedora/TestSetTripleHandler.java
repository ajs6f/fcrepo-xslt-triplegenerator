package edu.virginia.lib.fedora;

import static java.net.URI.create;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.writer.TripleHandlerException;
import org.fcrepo.common.rdf.SimpleLiteral;
import org.fcrepo.common.rdf.SimpleTriple;
import org.fcrepo.common.rdf.SimpleURIReference;
import org.jrdf.graph.URIReference;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class TestSetTripleHandler {

	private static final Logger logger = getLogger(TestSetTripleHandler.class);

	private final Any23 any23 = new Any23();

	DocumentSource rdfXmlSource;

	@Before
	public void setUp() {
		rdfXmlSource = new FileDocumentSource(new File(
				"target/test-classes/rdf.xml"));
	}

	@Test
	public void testOneTriple() throws IOException, ExtractionException,
			TripleHandlerException {
		logger.info("Running testOneTriple()...");
		final SetTripleHandler handler = new SetTripleHandler();
		any23.extract(rdfXmlSource, handler);
		assertTrue(
				"Didn't find appropriate triple!",
				handler.getTriples()
						.contains(
								new SimpleTriple(
										uri("info:fedora/uva-lib:1038847"),
										uri("http://fedora.lib.virginia.edu/relationships#testPredicate"),
										uri("info:test/resource"))));
		logger.info("Found appropriate triple.");
		handler.close();
	}

	@Test
	public void testOneTripleWithLiteral() throws IOException,
			ExtractionException, TripleHandlerException {
		logger.info("Running testOneTripleWithLiteral()...");
		final SetTripleHandler handler = new SetTripleHandler();
		any23.extract(rdfXmlSource, handler);
		assertTrue(
				"Didn't find appropriate triple!",
				handler.getTriples()
						.contains(
								new SimpleTriple(
										uri("info:fedora/uva-lib:1038847"),
										uri("http://fedora.lib.virginia.edu/relationships#testPredicateWithLiteral"),
										new SimpleLiteral("literal value"))));
		logger.info("Found appropriate triple.");
		handler.close();
	}

	@Test
	public void testOneTripleWithRelativeUri() throws IOException,
			ExtractionException, TripleHandlerException {
		logger.info("Running testOneTripleWithRelativeUri()...");
		final SetTripleHandler handler = new SetTripleHandler();
		any23.extract(rdfXmlSource, handler);
		assertTrue(
				"Didn't find appropriate triple!",
				handler.getTriples()
						.contains(
								new SimpleTriple(
										uri("info:fedora/uva-lib:1038847"),
										uri("http://fedora.lib.virginia.edu/relationships#testPredicateWithLiteral"),
										new SimpleLiteral("/relative/uri/"))));
		logger.info("Found appropriate triple.");
		handler.close();
	}

	@Test
	public void testSetContentLength() {
		logger.info("Running testSetContentLength()...");
		final SetTripleHandler handler = new SetTripleHandler();
		try {
			handler.setContentLength(0);
			fail("setContentLength() didn't throw an UnsupportedOperationException!");
		} catch (final UnsupportedOperationException e) {
			logger.info("Found correct behavior for setContentLength().");
		}
	}

	private static URIReference uri(final String v) {
		return new SimpleURIReference(create(v));
	}

}
