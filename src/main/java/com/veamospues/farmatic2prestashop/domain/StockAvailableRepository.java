package com.veamospues.farmatic2prestashop.domain;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Component
public class StockAvailableRepository implements Processor {
    private static final String PAD_LEFT_WITH_SIX_ZEROES = "%06d";

    private Map<String, StockAvailable> database = new HashMap<>();

    @Override
    public void process(Exchange exchange) {
        ArrayList<ArrayList<String>> stockAvailables = exchange.getIn().getBody(ArrayList.class);
        database.clear();
        stockAvailables.forEach(this::insert);
    }

    public Optional<StockAvailable> findByProductReference(Integer productReference) {
        return findByProductReference(format(PAD_LEFT_WITH_SIX_ZEROES, productReference));
    }

    private Optional<StockAvailable> findByProductReference(String productReference) {
        return ofNullable(database.get(productReference));
    }

    private void insert(ArrayList<String> item) {
        final String reference;

        try {
            reference = format(PAD_LEFT_WITH_SIX_ZEROES, parseInt(item.get(2)));
        } catch (NumberFormatException nfe) {
            log.error("Could not parse reference " + item.get(2));
            return;
        }

        StockAvailable stockAvailable = StockAvailable
                .builder()
                .idStockAvailable(parseInt(item.get(0)))
                .idProduct(parseInt(item.get(1)))
                .idProductAttribute(parseInt(item.get(3)))
                .idShop(parseInt(item.get(4)))
                .idShopGroup(parseInt(item.get(5)))
                .dependsOnStock(parseInt(item.get(7)))
                .outOfStock(parseInt(item.get(8)))
                .build();

        database.put(reference, stockAvailable);
    }
}
