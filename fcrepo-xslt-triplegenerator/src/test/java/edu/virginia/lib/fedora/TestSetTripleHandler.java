package edu.virginia.lib.fedora;

import static java.net.URI.create;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
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
	public void testOneTriple() throws IOException, ExtractionException {
		final SetTripleHandler handler = new SetTripleHandler();
		any23.extract(rdfXmlSource, handler);
		final SimpleTriple triple = new SimpleTriple(
				uri("info:fedora/uva-lib:1038847"),
				uri("http://fedora.lib.virginia.edu/relationships#isFollowingPageOf"),
				uri("info:fedora/uva-lib:1038846"));
		logger.info("Looking for triple: {}", triple.toString());
		assertTrue("Oh, no! Didn't find the triple", handler.getTriples()
				.contains(triple));
		logger.info("Found it!");
	}

	public static URIReference uri(final String v) {
		return new SimpleURIReference(create(v));
	}

}
