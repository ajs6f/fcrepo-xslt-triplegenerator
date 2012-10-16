
package edu.virginia.lib.fedora;

import java.util.Set;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.fcrepo.common.rdf.SimpleTriple;
import org.fcrepo.common.rdf.SimpleURIReference;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author ajs6f
 * Utility class to produce a set of triples from a stream of triples
 * produced by Any23 parsing.
 */
public class SetTripleHandler implements TripleHandler {

    private Builder<Triple> builder = new Builder<Triple>();
    
    private static final Logger logger = LoggerFactory
            .getLogger(SetTripleHandler.class);

    @Override
    public void receiveTriple(Resource arg0, URI arg1, Value arg2, URI arg3,
            ExtractionContext arg4) throws TripleHandlerException {
        SimpleTriple triple = new SimpleTriple(uri(arg0), uri(arg1), uri(arg2));
        builder.add(triple);
        logger.debug("Added triple: {}",triple.toString());
    }

    public Set<Triple> getTriples() {
        return builder.build();
    }

    @Override
    public void close() throws TripleHandlerException {
        builder = null;
    }
    
    public static URIReference uri(Value v) {
        return new SimpleURIReference(java.net.URI.create(v.stringValue()));
    }

    @Override
    public void startDocument(URI arg0) throws TripleHandlerException {
    }
    
    @Override
    public void endDocument(URI arg0) throws TripleHandlerException {
    }

    @Override
    public void setContentLength(long arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void openContext(ExtractionContext arg0)
            throws TripleHandlerException {
    }

    @Override
    public void receiveNamespace(String arg0, String arg1,
            ExtractionContext arg2) throws TripleHandlerException {
    }

    @Override
    public void closeContext(ExtractionContext arg0)
            throws TripleHandlerException {
    }
}
