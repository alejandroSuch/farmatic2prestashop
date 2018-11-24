package com.veamospues.farmatic2prestashop.route;

import com.veamospues.farmatic2prestashop.config.SqlQueriesConfiguration;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StockVariationsReader extends RouteBuilder {
    static final String ROUTE_ID = "StockVariationsReader";
    private static final String FIVE_MINUTES = "300000";
    private static final String PRODUCT_PROCESSOR = "seda:productProcessor";

    private SqlQueriesConfiguration queries;

    @Override
    public void configure() throws Exception {
        from(uri()).routeId(ROUTE_ID)
                .noAutoStartup()
                .split(body()).streaming()
                .to(PRODUCT_PROCESSOR)
                .end()
        ;
    }

    private String uri() {
        return "sql://" + queries.getStockVariations()+"?outputType=StreamList&outputClass=com.veamospues.farmatic2prestashop.domain.Product&consumer.delay=" + FIVE_MINUTES;
    }
}