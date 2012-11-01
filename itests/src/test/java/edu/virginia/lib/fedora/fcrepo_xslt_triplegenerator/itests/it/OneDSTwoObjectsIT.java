
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

public class OneDSTwoObjectsIT extends XSLTTripleGeneratorTest {

    private static final Logger logger = LoggerFactory
            .getLogger(OneDSTwoObjectsIT.class);
    
    @Before
    public void ingestTwo() throws FedoraClientException, IOException {
        pids = Arrays.asList("test:1", "test:2");
        ingest(new File(foxmldir + "/test_1.xml"));
        ingest(new File(foxmldir + "/test_2.xml"));
    }
    
    @Test
    public void testFirstObject() throws FedoraClientException {
        logger.info("Running OneDSTwoObjectsIT.testFirstObject()...");
        String rdf =
                FedoraClient.riSearch("DESCRIBE <info:fedora/" + pids.get(0) + ">")
                        .type("triples").format("rdf/xml").flush(true)
                        .execute().getEntity(String.class);
        assertTrue("Didn't find success marker in: \n" + rdf, rdf
                .contains("MODS-SUCCESS"));
        logger.info("Success!");
    }
    
    @Test
    public void testSecondObject() throws FedoraClientException {
        logger.info("Running OneDSTwoObjectsIT.testSecondObject()...");
        String rdf =
                FedoraClient.riSearch("DESCRIBE <info:fedora/" + pids.get(1) + ">")
                        .type("triples").format("rdf/xml").flush(true)
                        .execute().getEntity(String.class);
        assertTrue("Didn't find success marker in: \n" + rdf, rdf
                .contains("VRA-SUCCESS"));
        logger.info("Success!");
    }

}
