package com.candil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Paths;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class NetconfComponentTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        // Load filter.xml from resources
        URL resource = getClass().getClassLoader().getResource("filter.xml");
        if (resource == null) {
            throw new IllegalArgumentException("filter.xml not found in test resources");
        }
        String xmlFilterFilePath = Paths.get(resource.toURI()).toAbsolutePath().toString();

        // Adjust the URI for your NETCONF server
        String netconfUri = String.format(
                "netconf:admin@localhost:830"
                        + "?password=NokiaSrl1!"
                        + "&xmlFilterFile=%s"
                        + "&insecure=true"
                        + "&datastore=operational",
                xmlFilterFilePath
        );

        return new RouteBuilder() {
            @Override
            public void configure() {
                from(netconfUri)
                    .to("mock:result");
            }
        };
    }

    @Test
    public void testNetconfConnectionAndQuery() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        // Let Camel start the route and handle the context
        mock.assertIsSatisfied();

        String body = mock.getExchanges().get(0).getIn().getBody(String.class);

        assertTrue(body.contains("<yang-library"), "Response should contain <yang-library");
    }
}
