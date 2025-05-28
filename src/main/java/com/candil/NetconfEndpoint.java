package com.candil;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

/**
 * camel-netconf component which does bla bla.
 *
 * TODO: Update one line description above what the component does, and update
 * Category.
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = "netconf", title = "NETCONF", syntax = "netconf:username@host[:port]", category = {
        Category.NETWORKING })
public class NetconfEndpoint extends DefaultEndpoint {
    private static final List<String> ALLOWED_DATASTORES = Arrays.asList(
            "running", "candidate", "startup", "intended", "operational");

    @UriPath
    @Metadata(required = true)
    private String host;

    @UriPath(defaultValue = "830")
    @Metadata(required = false)
    private Integer port;

    @UriParam
    @Metadata(required = true)
    private String xmlFilterFile;

    @UriParam(defaultValue = "true")
    private boolean insecure;

    @UriParam
    @Metadata(required = true)
    private String username;

    @UriParam
    @Metadata(required = true)
    private String password;

    @UriParam
    @Metadata(required = true)
    private String datastore;

    public NetconfEndpoint() {
    }

    public NetconfEndpoint(String endpointUri, NetconfComponent component) {
        super(endpointUri, component);
    }

    /**
     * This parameter contains the host of the NETCONF server
     */
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * This parameter contains the port of the NETCONF server
     */
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Parameter used to provide the XML subtree filter
     */
    public String getXmlFilterFile() {
        return xmlFilterFile;
    }

    public void setXmlFilterFile(String xmlFilterFile) {
        this.xmlFilterFile = xmlFilterFile;
    }

    /**
     * Parameter used to indicate insecure connection with the NETCONF server
     */
    public boolean isInsecure() {
        return insecure;
    }

    public void setInsecure(boolean insecure) {
        this.insecure = insecure;
    }

    /**
     * This parameter contains the username to authenticate against the NETCONF
     * server
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This parameter contains the password to authenticate against the NETCONF
     * server
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This parameter indicates the YANG datastores to access in the NETCONF server
     */
    public String getDatastore() {
        return datastore;
    }

    public void setDatastore(String datastore) {
        if (!ALLOWED_DATASTORES.contains(datastore)) {
            throw new IllegalArgumentException("datastore must be one of: " + ALLOWED_DATASTORES);
        }
        this.datastore = datastore;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new NetconfConsumer(this, processor);
    }

    @Override
    public org.apache.camel.Producer createProducer() {
        throw new UnsupportedOperationException("Producing is not supported by the netconf endpoint");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
