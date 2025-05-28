package com.candil;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfProducer.class);
    private NetconfEndpoint endpoint;

    public NetconfProducer(NetconfEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());
    }

}
