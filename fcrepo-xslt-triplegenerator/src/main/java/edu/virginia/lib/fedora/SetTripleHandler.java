package edu.virginia.lib.fedora;

import static java.net.URI.create;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URISyntaxException;
import java.util.Set;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.fcrepo.common.rdf.SimpleLiteral;
import org.fcrepo.common.rdf.SimpleTriple;
import org.fcrepo.common.rdf.SimpleURIReference;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet.Builder;

/**
 * Utility class to produce a set of triples from a stream of triples produced
 * by Any23 parsing.
 * 
 * @author ajs6f
 */

public class SetTripleHandler implements TripleHandler {

	private final Builder<Triple> builder = new Builder<Triple>();

	private static final Logger logger = getLogger(SetTripleHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.any23.writer.TripleHandler#receiveTriple(org.openrdf.model
	 * .Resource, org.openrdf.model.URI, org.openrdf.model.Value,
	 * org.openrdf.model.URI, org.apache.any23.extractor.ExtractionContext)
	 */
	@Override
	public void receiveTriple(final Resource arg0, final URI arg1,
			final Value arg2, final URI arg3, final ExtractionContext arg4)
			throws TripleHandlerException {
		final SimpleTriple triple = new SimpleTriple(uri(arg0), uri(arg1),
				objectValue(arg2));
		builder.add(triple);
		logger.debug("Added triple: {}", triple.toString());
	}

	public static URIReference uri(final Value v) {
		logger.debug("Creating URI from Value: {}", v.stringValue());
		return new SimpleURIReference(create(v.stringValue()));
	}

	private static ObjectNode objectValue(final Value v) {
		try {
			return new SimpleURIReference(new java.net.URI(v.stringValue()));
		} catch (final URISyntaxException e) {
			// the RDF object is a literal, not an URI
			return new SimpleLiteral(v.stringValue());
		}
	}

	public Set<Triple> getTriples() {
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.any23.writer.TripleHandler#close()
	 */
	@Override
	public void close() throws TripleHandlerException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.any23.writer.TripleHandler#startDocument(org.openrdf.model
	 * .URI)
	 */
	@Override
	public void startDocument(final URI arg0) throws TripleHandlerException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.any23.writer.TripleHandler#endDocument(org.openrdf.model.URI)
	 */
	@Override
	public void endDocument(final URI arg0) throws TripleHandlerException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.any23.writer.TripleHandler#setContentLength(long)
	 */
	@Override
	public void setContentLength(final long arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.any23.writer.TripleHandler#openContext(org.apache.any23.extractor
	 * .ExtractionContext)
	 */
	@Override
	public void openContext(final ExtractionContext arg0)
			throws TripleHandlerException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.any23.writer.TripleHandler#receiveNamespace(java.lang.String,
	 * java.lang.String, org.apache.any23.extractor.ExtractionContext)
	 */
	@Override
	public void receiveNamespace(final String arg0, final String arg1,
			final ExtractionContext arg2) throws TripleHandlerException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.any23.writer.TripleHandler#closeContext(org.apache.any23.extractor
	 * .ExtractionContext)
	 */
	@Override
	public void closeContext(final ExtractionContext arg0)
			throws TripleHandlerException {
	}
}
