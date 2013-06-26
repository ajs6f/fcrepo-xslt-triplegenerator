
package edu.virginia.lib.fedora;

import static java.net.URI.create;
import static org.junit.Assert.assertTrue;
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
import org.fcrepo.server.errors.ServerException;
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
        logger.debug("Using XSLT transform: {}", IOUtils
                .toString(xsltLogStream));
        xsltLogStream.close();

        triplegen.compileXSLT();

        final Set<Triple> triples = triplegen.getTriplesForObject(reader);
        final SimpleTriple triple =
                new SimpleTriple(
                        uri("info:fedora/uva-lib:1038847"),
                        uri("http://fedora.lib.virginia.edu/relationships#isFollowingPageOf"),
                        uri("info:fedora/uva-lib:1038846"));
        logger.info("Looking for triple: {}", triple.toString());
        assertTrue("Uh oh! Didn't find it", triples.contains(triple));
        logger.info("Found it!");
    }

    public static URIReference uri(final String v) {
        return new SimpleURIReference(create(v));
    }

}
