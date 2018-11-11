package com.veamospues.farmatic2prestashop.route;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StockUpdater extends RouteBuilder {
    private static final String AUTH_HEADER = "authorization";
    private static final String BASIC = "Basic ";

    private static final String TEXT_XML = "text/xml";
    private static final String PUT = "PUT";

    private static final String ROUTE_ID = "StockUpdater";
    private static final String URI = "seda:stockUpdater?multipleConsumers=true&concurrentConsumers=2";

    private PrestashopConfiguration prestashopConfiguration;

    @Override
    public void configure() {
        from(URI).routeId(ROUTE_ID)
                .log("Updating ${in.header.product} with stock ${in.header.stock}")
                .log("${in.body}")
                .setHeader(AUTH_HEADER, token())
                .setHeader(Exchange.HTTP_METHOD, constant(PUT))
                .setHeader(Exchange.CONTENT_TYPE, constant(TEXT_XML))
                .to(prestashopConfiguration.getStockAvailablesUrl())
                .end();
    }

    private ValueBuilder token() {
        return constant(BASIC + prestashopConfiguration.getApiToken());
    }
}
