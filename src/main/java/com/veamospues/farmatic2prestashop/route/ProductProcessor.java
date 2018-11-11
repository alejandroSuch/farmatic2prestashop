package com.veamospues.farmatic2prestashop.route;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.domain.Product;
import com.veamospues.farmatic2prestashop.domain.StockAvailable;
import com.veamospues.farmatic2prestashop.domain.StockAvailableRepository;
import lombok.AllArgsConstructor;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ProductProcessor extends RouteBuilder {
    private static final String ROUTE_ID = "ProductProcessor";
    private static final String URI = "seda:productProcessor?multipleConsumers=true&concurrentConsumers=10";
    private static final String PRODUCT_HEADER = "product";
    private static final String STOCK_HEADER = "stock";

    StockAvailableRepository repository;
    PrestashopConfiguration prestashopConfiguration;

    @Override
    public void configure() {
        from(URI).routeId(ROUTE_ID)
                .process(productToStockTemplate())
                .choice()
                .when(body().isNull())
                .log("${in.header.product} won't be processed because it's not in prestashop")
                .otherwise()
                .to("seda:stockUpdater")
                .end()
        ;
    }

    private Processor productToStockTemplate() {
        return exchange -> {
            final Product product = exchange.getIn().getBody(Product.class);
            final Optional<StockAvailable> stockAvailable = repository.findByProductReference(product.getId());
            final String stockTemplate = stockAvailable.isPresent() ? createStockTemplate(product, stockAvailable.get()) : null;

            exchange.getIn().setHeader(PRODUCT_HEADER, product.getName());
            exchange.getIn().setHeader(STOCK_HEADER, product.getStock());
            exchange.getIn().setBody(stockTemplate);
        };
    }

    private String createStockTemplate(Product product, StockAvailable stockAvailable) {
        return prestashopConfiguration.getUpdateStockTemplate(
                stockAvailable.getIdStockAvailable(),
                stockAvailable.getIdProduct(),
                stockAvailable.getIdProductAttribute(),
                stockAvailable.getIdShop(),
                stockAvailable.getIdShopGroup(),
                product.getStock(),
                stockAvailable.getDependsOnStock(),
                stockAvailable.getOutOfStock()
        );
    }
}
