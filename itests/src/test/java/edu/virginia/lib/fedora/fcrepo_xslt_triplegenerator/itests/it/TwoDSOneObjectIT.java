package edu.virginia.lib.fedora.fcrepo_xslt_triplegenerator.itests.it;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;


public class TwoDSOneObjectIT extends XSLTTripleGeneratorTest {

    private static final Logger logger = LoggerFactory
            .getLogger(TwoDSOneObjectIT.class);
    
    @Before
    public void ingestOne() throws FedoraClientException, IOException {
        pids = Arrays.asList("test:3");
        ingest(new File(foxmldir + "/test_3.xml"));
    }
    
    @Test
    public void testOneObject() throws FedoraClientException {
        logger.info("Running TwoDSOneObjectIT.testOneObject()...");
        String rdf =
                FedoraClient.riSearch("DESCRIBE <info:fedora/" + pids.get(0) + ">")
                        .type("triples").format("rdf/xml").flush(true)
                        .execute().getEntity(String.class);
        assertTrue("Didn't find success marker in: \n" + rdf, rdf
                .contains("VRA-SUCCESS"));
        assertTrue("Didn't find success marker in: \n" + rdf, rdf
                .contains("MIX-SUCCESS"));
        logger.info("Success!");
    }
    
}
