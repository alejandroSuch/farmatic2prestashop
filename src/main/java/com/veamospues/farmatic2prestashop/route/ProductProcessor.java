package com.veamospues.farmatic2prestashop.route;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.domain.Product;
import com.veamospues.farmatic2prestashop.domain.StockAvailable;
import com.veamospues.farmatic2prestashop.domain.StockAvailableRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty(
  value="route.products.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class ProductProcessor extends RouteBuilder {

  private static final String ROUTE_ID = "ProductProcessor";
  private static final String URI = "seda:productProcessor?multipleConsumers=true&concurrentConsumers=10";
  private static final String PRODUCT_HEADER = "product";
  private static final String STOCK_HEADER = "stock";
  private static final String LOG_MESSAGE = "${in.header.product} won't be processed because it's not in prestashop";
  private static final String SEDA_STOCK_UPDATER = "seda:stockUpdater";

  StockAvailableRepository repository;
  PrestashopConfiguration prestashopConfiguration;

  @Override
  public void configure() {
    from(URI).routeId(ROUTE_ID)
      .process(productToStockTemplate())
      .choice()
      .when(body().isNull())
      .log(LOG_MESSAGE)
      .otherwise()
      .to(SEDA_STOCK_UPDATER)
      .end()
    ;
  }

  private Processor productToStockTemplate() {
    return exchange -> {
      final Product product = exchange.getIn().getBody(Product.class);
      final Optional<StockAvailable> stockAvailable = repository.findByProductReference(product.getId());
      final String stockTemplate = stockAvailable
        .map(available -> createStockTemplate(product, available))
        .orElse(null);

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
