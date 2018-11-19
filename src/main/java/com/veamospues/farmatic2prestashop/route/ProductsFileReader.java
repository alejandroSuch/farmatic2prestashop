package com.veamospues.farmatic2prestashop.route;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.domain.StockAvailableRepository;
import lombok.AllArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@AllArgsConstructor
public class ProductsFileReader extends RouteBuilder {
    private static final String ROUTE_ID = "ProductsFileReader";
    private static final String PRODUCTS_CSV = "products.csv";
    private static final String TEN_SECONDS = "10000";

    private StockAvailableRepository stockAvailableRepository;
    private PrestashopConfiguration prestashopConfiguration;
    private CamelContext camelContext;
    private CsvDataFormat csv;

    @Override
    public void configure() throws Exception {
        from(productsFile())
                .routeId(ROUTE_ID)
                .unmarshal(csv)
                .process(stockAvailableRepository)
                .to(format("controlbus:route?routeId=%s&action=start", StockVariationsReader.ROUTE_ID))
                .to(format("controlbus:route?routeId=%s&action=start", FullStockProcessor.ROUTE_ID))
                .end()
        ;
    }

    private String productsFile() {
        return "file:" + prestashopConfiguration.getCsvLocation() + "?fileName=" + PRODUCTS_CSV + "&noop=true&idempotentKey=${file:name}-${file:modified}&delay=" + TEN_SECONDS;
    }
}
