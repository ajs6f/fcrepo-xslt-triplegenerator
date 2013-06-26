package edu.virginia.lib.fedora.fcrepo_xslt_triplegenerator.itests.it;

import static com.yourmediashelf.fedora.client.FedoraClient.purgeObject;
import static com.yourmediashelf.fedora.client.request.FedoraRequest.setDefaultClient;
import static java.lang.System.getProperty;
import static org.slf4j.LoggerFactory.getLogger;

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

import com.google.common.io.CharStreams;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.IngestResponse;

public abstract class XSLTTripleGeneratorTest {

	// must be set by extending classes
	List<String> pids = null;

	static String foxmldir;

	private static final Logger logger = getLogger(XSLTTripleGeneratorTest.class);

	@BeforeClass
	public static void setUp() throws MalformedURLException {
		final String port = getProperty("servlet.port");
		final String baseUrl = "http://localhost:" + port + "/fedora";
		setDefaultClient(new FedoraClient(new FedoraCredentials(baseUrl,
				"fedoraAdmin", "fc")));
		foxmldir = getProperty("foxml.dir");

	}

	public void ingest(final File foxml) throws FedoraClientException,
			IOException {
		logger.debug("Using foxml: \n {}",
				inputStreamToString(new FileInputStream(foxml)));

		final IngestResponse res = FedoraClient.ingest()
				.content(new FileInputStream(foxml))
				.format("info:fedora/fedora-system:FOXML-1.1")
				.logMessage("Ingesting fcrepo-xslt-triplegenerator test:1")
				.execute();
		logger.info("Ingested object with result: {}",
				res.getEntity(String.class));
	}

	@After
	public void purge() throws FedoraClientException {
		for (final String pid : pids)
			purgeObject(pid).execute();
	}

	static String inputStreamToString(final InputStream is) throws IOException {
		return CharStreams.toString(new InputStreamReader(is));
	}

}
