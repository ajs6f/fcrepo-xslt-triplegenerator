package edu.virginia.lib.fedora;

import static com.google.common.collect.ImmutableSet.builder;
import static java.net.URI.create;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.writer.TripleHandlerException;
import org.fcrepo.common.rdf.SimpleLiteral;
import org.fcrepo.common.rdf.SimpleTriple;
import org.fcrepo.common.rdf.SimpleURIReference;
import org.jrdf.graph.URIReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * Tests for {@link SetTripleHandler}
 * 
 * @author ajs6f
 * 
 */
public class TestSetTripleHandler extends SetTripleHandler {

	private static final Logger logger = getLogger(TestSetTripleHandler.class);

	private final Any23 any23 = new Any23();

	private DocumentSource rdfXmlSource;

	/**
	 * Loads a sample RDF/XML document.
	 */
	@Before
	public void setUp() {
		rdfXmlSource = new FileDocumentSource(new File(
				"target/test-classes/rdf.xml"));
	}

	/**
	 * "Zeros-out" the {@link Set} of {@link Triple}s being accumulated.
	 */
	@After
	public void resetBuilder() {
		this.builder = builder();
	}

	/**
	 * Checks that a triple with all URI nodes from the sample RDF appears in
	 * the accumulated triples after extraction.
	 * 
	 * @throws IOException
	 * @throws ExtractionException
	 * @throws TripleHandlerException
	 */
	@Test
	public void testOneTriple() throws IOException, ExtractionException,
			TripleHandlerException {
		logger.info("Running testOneTriple()...");
		any23.extract(rdfXmlSource, this);
		assertTrue(
				"Didn't find appropriate triple!",
				getTriples()
						.contains(
								new SimpleTriple(
										uri("info:fedora/uva-lib:1038847"),
										uri("http://fedora.lib.virginia.edu/relationships#testPredicate"),
										uri("info:test/resource"))));
		logger.info("Found appropriate triple.");
		close();
	}

	/**
	 * Checks that a triple with URI nodes for subject and predicate and literal
	 * node for object from the sample RDF appears in the accumulated triples
	 * after extraction.
	 * 
	 * @throws IOException
	 * @throws ExtractionException
	 * @throws TripleHandlerException
	 */
	@Test
	public void testOneTripleWithLiteral() throws IOException,
			ExtractionException, TripleHandlerException {
		logger.info("Running testOneTripleWithLiteral()...");
		any23.extract(rdfXmlSource, this);
		assertTrue(
				"Didn't find appropriate triple!",
				getTriples()
						.contains(
								new SimpleTriple(
										uri("info:fedora/uva-lib:1038847"),
										uri("http://fedora.lib.virginia.edu/relationships#testPredicateWithLiteral"),
										new SimpleLiteral("literal value"))));
		logger.info("Found appropriate triple.");
		close();
	}

	/**
	 * Checks that a triple with absolute URI nodes for subject and predicate
	 * and relative URI node for object from the sample RDF appears in the
	 * accumulated triples after extraction. The literal node has the form of a
	 * relative URI, and Fedora's Resource Index does not contemplate such URIs,
	 * so we treat it as a literal.
	 * 
	 * @throws IOException
	 * @throws ExtractionException
	 * @throws TripleHandlerException
	 */
	@Test
	public void testOneTripleWithRelativeUri() throws IOException,
			ExtractionException, TripleHandlerException {
		logger.info("Running testOneTripleWithRelativeUri()...");
		any23.extract(rdfXmlSource, this);
		assertTrue(
				"Didn't find appropriate triple!",
				getTriples()
						.contains(
								new SimpleTriple(
										uri("info:fedora/uva-lib:1038847"),
										uri("http://fedora.lib.virginia.edu/relationships#testPredicateWithLiteral"),
										new SimpleLiteral("/relative/uri/"))));
		logger.info("Found appropriate triple.");
		close();
	}

	/**
	 * We do not implement setContentLength() because there is no need for it in
	 * the Fedora context.
	 */
	@Test
	public void testSetContentLength() {
		logger.info("Running testSetContentLength()...");
		try {
			setContentLength(0);
			fail("setContentLength() didn't throw an UnsupportedOperationException!");
		} catch (final UnsupportedOperationException e) {
			logger.info("Found correct behavior for setContentLength().");
		}
	}

	/**
	 * Convenience method.
	 * 
	 * @param v
	 *            A {@link String} with the form of an {@link java.net.URI}
	 * @return A {@link SimpleURIReference}
	 */
	private static URIReference uri(final String v) {
		return new SimpleURIReference(create(v));
	}

}
