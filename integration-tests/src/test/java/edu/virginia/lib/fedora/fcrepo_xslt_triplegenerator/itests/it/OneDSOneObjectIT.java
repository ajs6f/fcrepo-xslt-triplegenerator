package edu.virginia.lib.fedora.fcrepo_xslt_triplegenerator.itests.it;

import static com.yourmediashelf.fedora.client.FedoraClient.riSearch;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.yourmediashelf.fedora.client.FedoraClientException;

public class OneDSOneObjectIT extends XSLTTripleGeneratorTest {

	private static final Logger logger = getLogger(OneDSOneObjectIT.class);

	@Before
	public void ingestOne() throws FedoraClientException, IOException {
		pids = asList("test:1");
		ingest(new File(foxmldir + "/test_1.xml"));
	}

	@Test
	public void testOneObject() throws FedoraClientException {
		logger.info("Running OneDSOneObjectIT.testOneObject()...");
		final String rdf = riSearch(
				"DESCRIBE <info:fedora/" + pids.get(0) + ">").type("triples")
				.format("rdf/xml").flush(true).execute()
				.getEntity(String.class);
		logger.debug("Discovered RDF: \n {}", rdf);
		assertTrue("Didn't find success marker in: \n" + rdf,
				rdf.contains("Mr. Ward"));
		logger.info("Success!");
	}

}
