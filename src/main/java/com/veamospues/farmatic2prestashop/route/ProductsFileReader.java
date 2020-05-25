package com.veamospues.farmatic2prestashop.route;

import static java.lang.String.format;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.domain.StockAvailableRepository;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
  value="route.products.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class ProductsFileReader extends RouteBuilder {

  private static final String ROUTE_ID = "ProductsFileReader";
  private static final String PRODUCTS_CSV = "products.csv";
  private static final String TEN_SECONDS = "10000";

  private StockAvailableRepository stockAvailableRepository;
  private PrestashopConfiguration prestashopConfiguration;
  private CsvDataFormat csv;

  public ProductsFileReader(
    CamelContext context,
    StockAvailableRepository stockAvailableRepository,
    PrestashopConfiguration prestashopConfiguration,
    CsvDataFormat csv
  ) {
    super(context);
    this.stockAvailableRepository = stockAvailableRepository;
    this.prestashopConfiguration = prestashopConfiguration;
    this.csv = csv;
  }

  @Override
  public void configure() {
    from(productsFile())
      .routeId(ROUTE_ID)
      .unmarshal(csv)
      .process(stockAvailableRepository)
      .to(format("controlbus:route?routeId=%s&action=stop", StockVariationsReader.ROUTE_ID))
      .to(format("controlbus:route?routeId=%s&action=start", StockVariationsReader.ROUTE_ID))
      .to(format("controlbus:route?routeId=%s&action=stop", FullStockProcessor.ROUTE_ID))
      .to(format("controlbus:route?routeId=%s&action=start", FullStockProcessor.ROUTE_ID))
      .end()
    ;
  }

  private String productsFile() {
    return "file:" +
      prestashopConfiguration.getCsvLocation() +
      "?fileName=" +
      PRODUCTS_CSV
      + "&noop=true&idempotentKey=${file:name}-${file:modified}&delay=" +
      TEN_SECONDS
    ;
  }
}
