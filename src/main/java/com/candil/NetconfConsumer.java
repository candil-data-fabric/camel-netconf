package com.candil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;

import com.candil.jnc.Element;
import com.candil.jnc.NetconfSession;
import com.candil.jnc.SSHConnection;
import com.candil.jnc.SSHSession;
import com.candil.jnc.XMLParser;

public class NetconfConsumer extends DefaultConsumer {
    private final NetconfEndpoint endpoint;

    private Map<String, Integer> datastoreMap = Map.ofEntries(
	  	Map.entry("candidate", NetconfSession.CANDIDATE),
	  	Map.entry("startup", NetconfSession.STARTUP),
		Map.entry("running", NetconfSession.RUNNING)
  	);

    public NetconfConsumer(NetconfEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // Validate required parameters
        if (endpoint.getHost() == null || endpoint.getHost().isEmpty()) {
            throw new IllegalArgumentException("Host must be specified for the netconf endpoint");
        }
        if (endpoint.getUsername() == null || endpoint.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username must be specified for the netconf endpoint");
        }
        if (endpoint.getPassword() == null || endpoint.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password must be specified for the netconf endpoint");
        }
        if (endpoint.getDatastore() == null || endpoint.getDatastore().isEmpty()) {
            throw new IllegalArgumentException("Datastore must be specified for the netconf endpoint");
        }
        if (endpoint.getXmlFilterFile() == null || endpoint.getXmlFilterFile().isEmpty()) {
            throw new IllegalArgumentException("xmlFilterFile must be specified for the netconf endpoint");
        }

        // Load XML filter from file
        String xmlFilter = new String(
                Files.readAllBytes(Paths.get(endpoint.getXmlFilterFile())),
                StandardCharsets.UTF_8);

        String host = endpoint.getHost();
        Integer port = endpoint.getPort() != null ? endpoint.getPort() : 830;
        boolean insecure = endpoint.isInsecure();
        String username = endpoint.getUsername();
        String password = endpoint.getPassword();
        String datastore = endpoint.getDatastore();

        SSHConnection c = new SSHConnection();
        if (insecure == false) {
            throw new UnsupportedOperationException("Verified secure connection not implemented");
        } else {
            c.setHostVerification(null).connect(host, port);
        }
        c.authenticateWithPassword(username, password);
        SSHSession ssh = new SSHSession(c);
        NetconfSession nc = new NetconfSession(ssh);

        String netconfResponse;
        // Select NETCONF operation based on type of datastore
        //
        // NETCONF Operational datastore
        if (datastore.equals("operational")) {
            Element subtree = new XMLParser().parse(xmlFilter);
            netconfResponse = nc.get(subtree).toXMLString();

        } else { // Conventional (configuration) datastores
            Element subtree = new XMLParser().parse(xmlFilter);
            netconfResponse = nc.getConfig(datastoreMap.get(datastore), subtree).toXMLString();
        }

        // Send response to Camel route
        Exchange exchange = getEndpoint().createExchange();
        exchange.getIn().setBody(netconfResponse);
        getProcessor().process(exchange);
    }

    @Override
    protected void doStop() throws Exception {
        // Clean up NETCONF connection if needed
        super.doStop();
    }
}
