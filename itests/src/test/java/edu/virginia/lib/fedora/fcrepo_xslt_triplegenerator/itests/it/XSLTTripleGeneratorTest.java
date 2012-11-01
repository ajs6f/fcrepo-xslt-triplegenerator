
package edu.virginia.lib.fedora.fcrepo_xslt_triplegenerator.itests.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.FedoraRequest;
import com.yourmediashelf.fedora.client.response.IngestResponse;

public abstract class XSLTTripleGeneratorTest {

    // must be set by extending classes
    List<String> pids = null;

    static String foxmldir;

    private static final Logger logger = LoggerFactory
            .getLogger(XSLTTripleGeneratorTest.class);

    @BeforeClass
    public static void setUp() throws MalformedURLException {
        String port = System.getProperty("servlet.port");
        String baseUrl = "http://localhost:" + port + "/fedora";
        FedoraRequest.setDefaultClient(new FedoraClient(new FedoraCredentials(
                baseUrl, "fedoraAdmin", "fc")));
        foxmldir = System.getProperty("foxml.dir");

    }

    public void ingest(File foxml) throws FedoraClientException, IOException {
        logger.debug("Using foxml: \n {}",
                inputStreamToString(new FileInputStream(foxml)));

        IngestResponse res =
                FedoraClient.ingest().content(new FileInputStream(foxml))
                        .format("info:fedora/fedora-system:FOXML-1.1")
                        .logMessage(
                                "Ingesting fcrepo-xslt-triplegenerator test:1")
                        .execute();
        logger.info("Ingested object with result: {}", res
                .getEntity(String.class));
    }

    @After
    public void purge() throws FedoraClientException {
        for (String pid : pids)
            FedoraClient.purgeObject(pid).execute();
    }

    static String inputStreamToString(InputStream is) throws IOException {
        return CharStreams.toString(new InputStreamReader(is));
    }

}
