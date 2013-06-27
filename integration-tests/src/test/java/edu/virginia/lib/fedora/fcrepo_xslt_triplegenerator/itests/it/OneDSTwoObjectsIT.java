package edu.virginia.lib.fedora.fcrepo_xslt_triplegenerator.itests.it;

import static com.yourmediashelf.fedora.client.FedoraClient.riSearch;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.yourmediashelf.fedora.client.FedoraClientException;

public class OneDSTwoObjectsIT extends XSLTTripleGeneratorTest {

	private static final Logger logger = getLogger(OneDSTwoObjectsIT.class);

	@Before
	public void ingestTwo() throws FedoraClientException, IOException {
		pids = Arrays.asList("test:1", "test:2");
		ingest(new File(foxmldir + "/test_1.xml"));
		ingest(new File(foxmldir + "/test_2.xml"));
	}

	@Test
	public void testFirstObject() throws FedoraClientException {
		logger.info("Running OneDSTwoObjectsIT.testFirstObject()...");
		final String rdf = riSearch(
				"DESCRIBE <info:fedora/" + pids.get(0) + ">").type("triples")
				.format("rdf/xml").flush(true).execute()
				.getEntity(String.class);
		logger.debug("Discovered RDF: \n {}", rdf);
		assertTrue("Didn't find success marker in: \n" + rdf,
				rdf.contains("Mr. Ward"));
		logger.info("Success!");
	}

	@Test
	public void testSecondObject() throws FedoraClientException {
		logger.info("Running OneDSTwoObjectsIT.testSecondObject()...");
		final String rdf = riSearch(
				"DESCRIBE <info:fedora/" + pids.get(1) + ">").type("triples")
				.format("rdf/xml").flush(true).execute()
				.getEntity(String.class);
		logger.debug("Discovered RDF: \n {}", rdf);
		assertTrue("Didn't find success marker in: \n" + rdf,
				rdf.contains("Funky image"));
		logger.info("Success!");
	}

}
