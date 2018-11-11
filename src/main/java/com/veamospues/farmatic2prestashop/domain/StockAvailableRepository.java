package com.veamospues.farmatic2prestashop.domain;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;

@Component
public class StockAvailableRepository implements Processor {
    private static final String PAD_LEFT_WITH_SIX_ZEROES = "%06d";

    Map<String, StockAvailable> database = new HashMap<>();

    @Override
    public void process(Exchange exchange) throws Exception {
        ArrayList<ArrayList<String>> stockAvailables = exchange.getIn().getBody(ArrayList.class);
        database.clear();
        stockAvailables.forEach(this::insert);
    }

    public Optional<StockAvailable> findByProductReference(Integer productReference) {

        if(!productReference.equals(965012)) {
            return Optional.empty();
        }

        return findByProductReference(String.format(PAD_LEFT_WITH_SIX_ZEROES, productReference));
    }

    public Optional<StockAvailable> findByProductReference(String productReference) {
        return Optional.ofNullable(database.get(productReference));
    }

    private void insert(ArrayList<String> item) {
        String reference = String.format("%06d", parseInt(item.get(2)));

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
