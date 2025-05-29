package com.github.candildatafabric;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

@Component("netconf")
public class NetconfComponent extends DefaultComponent {
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        NetconfEndpoint endpoint = new NetconfEndpoint(uri, this);

        // Parse username@host[:port]
        // Examples: "admin@localhost:1830", "user@10.0.0.1"
        if (remaining != null && !remaining.isEmpty()) {
            String[] userHost = remaining.split("@");
            if (userHost.length == 2) {
                endpoint.setUsername(userHost[0]);
                String hostPort = userHost[1];
                String[] hostParts = hostPort.split(":");
                endpoint.setHost(hostParts[0]);
                if (hostParts.length > 1) {
                    endpoint.setPort(Integer.parseInt(hostParts[1]));
                }
            } else {
                throw new IllegalArgumentException("Endpoint URI must be in the format username@host[:port]");
            }
        }

        setProperties(endpoint, parameters);
        return endpoint;
    }
}
