package com.veamospues.farmatic2prestashop.route;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.config.SqlQueriesConfiguration;
import com.veamospues.farmatic2prestashop.domain.Product;
import com.veamospues.farmatic2prestashop.domain.StockAvailable;
import com.veamospues.farmatic2prestashop.domain.StockAvailableRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.ResultSetIterator;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@Slf4j
@Component
@AllArgsConstructor
public class FullStockProcessor extends RouteBuilder {
    static final String ROUTE_ID = "FullStockProcessor";
    private static final String ONE_HOUR = "10000";
    private static final String PROCESSED_HEADER = "Processed";
    private static final String UNPROCESSED_HEADER = "Unprocessed";
    private static final String DATE_HEADER = "Date";
    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private SqlQueriesConfiguration queries;
    private StockAvailableRepository repository;
    private PrestashopConfiguration prestashopConfiguration;
    private CsvDataFormat csv;


    @Override
    public void configure() throws Exception {
        from(uri()).routeId(ROUTE_ID)
                .noAutoStartup()
                .process(queryToResult())
                .marshal(csv)
                .to(outputFile())
                .process(toStats())
                .to(outputStats())
                .end()
        ;
    }

    private Processor toStats() {
        return exchange -> {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            Date date = exchange.getIn().getHeader(DATE_HEADER, Date.class);
            Integer processed = exchange.getIn().getHeader(PROCESSED_HEADER, Integer.class);
            Integer unprocessed = exchange.getIn().getHeader(UNPROCESSED_HEADER, Integer.class);

            exchange.getIn().setBody(format("%s:\nProcesados:%d\nNo procesados:%d\n\n", sdf.format(date), processed, unprocessed));
        };
    }

    private String outputFile() {
        return "file:" + prestashopConfiguration.getCsvLocation() + "?fileName=full-stock.csv&fileExist=Override";
    }

    private String outputStats() {
        return "file:" + prestashopConfiguration.getCsvLocation() + "?fileName=full-stock-stats.txt&fileExist=Move&moveExisting=${file:name.noext}";
    }

    private Processor queryToResult() {
        return exchange -> {
            ResultSetIterator body = exchange.getIn().getBody(ResultSetIterator.class);
            ArrayList<List<Object>> result = new ArrayList<>();
            result.add(asList("ID", "REFERENCE", "STOCK"));

            Integer processed = 0;
            Integer unprocessed = 0;

            while (body.hasNext()) {
                Product product = (Product) body.next();
                Optional<StockAvailable> byProductReference = repository.findByProductReference(product.getId());

                if (byProductReference.isPresent()) {
                    processed++;
                    StockAvailable stockAvailable = byProductReference.get();
                    result.add(csvRowFrom(product, stockAvailable));
                } else {
                    unprocessed++;
                }
            }

            exchange.getIn().setHeader(DATE_HEADER, new Date());
            exchange.getIn().setHeader(PROCESSED_HEADER, processed);
            exchange.getIn().setHeader(UNPROCESSED_HEADER, unprocessed);

            exchange.getIn().setBody(result);
        };
    }

    private List<Object> csvRowFrom(Product product, StockAvailable stockAvailable) {
        final String idProduct = stockAvailable.getIdProduct().toString();
        final String reference = product.getReference();
        final int stock = product.getStock() == 0 ? -1 : product.getStock();

        return asList(idProduct, reference, stock);
    }

    private String uri() {
        return "sql://" + queries.getAllArticles() + "?outputType=StreamList&outputClass=com.veamospues.farmatic2prestashop.domain.Product&consumer.delay=" + ONE_HOUR;
    }
}
