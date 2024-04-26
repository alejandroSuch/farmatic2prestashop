package com.veamospues.farmatic2prestashop.route.promofarma.invoices;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(
  value = "route.promofarma-invoices.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class EnrichPromofarmaLineWithCode extends RouteBuilder {

  private final static String ROUTE_ID = "EnrichPromofarmaLineWithCode";
  private static final String URI = "seda:enrichPromofarmaLineWithCode?concurrentConsumers=1&multipleConsumers=false";
  private static final String QUERY =
    "SELECT DISTINCT a.IdArticu as id, a.Descripcion as name, a.StockActual as stock, s.Sinonimo as ean "
      + "FROM Articu a "
      + "LEFT JOIN Sinonimo s ON s.IdArticu = a.IdArticu "
      + "WHERE s.Sinonimo = :#ean";

  @Override
  public void configure() throws Exception {
    from(URI)
      .id(ROUTE_ID)
      .setHeader("ean", simple("${body.ean()}"))
      .enrich()
      .simple("sql:" + QUERY)
      .aggregationStrategy(changeLineCode())
      .choice()
        .when().simple("${body.hasCode()} == true").to("seda:EnrichPromofarmaLineWithPucAndName?blockWhenFull=true")
        .otherwise().to("seda:sendPromofarmaLineToSpreadsheet?blockWhenFull=true");
  }

  private AggregationStrategy changeLineCode() {
    return (oldExchange, newExchange) -> {
      final Line line = oldExchange.getIn().getBody(Line.class);
      final ArrayList<LinkedCaseInsensitiveMap> rows = newExchange.getIn().getBody(ArrayList.class);

      if (!rows.isEmpty()) {
        line.changeCode((String) rows.get(0).get("id"));
      }

      return oldExchange;
    };
  }
}
