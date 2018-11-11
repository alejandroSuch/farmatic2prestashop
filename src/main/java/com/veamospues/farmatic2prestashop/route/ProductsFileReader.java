package com.veamospues.farmatic2prestashop.route;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.domain.StockAvailableRepository;
import lombok.AllArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.stereotype.Component;

import static java.lang.Boolean.TRUE;

@Component
@AllArgsConstructor
public class ProductsFileReader extends RouteBuilder {
    public static final String ROUTE_ID = "ProductsFileReader";

    private StockAvailableRepository stockAvailableRepository;
    private PrestashopConfiguration prestashopConfiguration;
    private CamelContext camelContext;

    @Override
    public void configure() throws Exception {
        from("file:"+prestashopConfiguration.getCsvLocation()+"?fileName=products.csv&noop=true&idempotentKey=${file:name}-${file:modified}").routeId(ROUTE_ID)
                .unmarshal(csv())
                .process(stockAvailableRepository)
                .process(initializeDatabaseReaderIfNeeded())
                .end()
        ;
    }

    private CsvDataFormat csv() {
        CsvDataFormat csv = new CsvDataFormat();
        csv.setDelimiter(";");
        csv.setIgnoreEmptyLines(true);
        csv.setSkipHeaderRecord(TRUE);
        return csv;
    }

    private Processor initializeDatabaseReaderIfNeeded() {
        return exchange -> {
            Route route = camelContext.getRoute(DatabaseReader.ROUTE_ID);

            if (route.getUptimeMillis() == 0) {
                camelContext.startRoute(DatabaseReader.ROUTE_ID);
            }
        };
    }
}
