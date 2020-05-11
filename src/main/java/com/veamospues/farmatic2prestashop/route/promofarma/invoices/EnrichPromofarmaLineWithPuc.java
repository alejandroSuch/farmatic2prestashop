package com.veamospues.farmatic2prestashop.route.promofarma.invoices;

import java.math.BigDecimal;
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
public class EnrichPromofarmaLineWithPuc extends RouteBuilder {

  private final static String ROUTE_ID = "EnrichPromofarmaLineWithPuc";
  private static final String URI = "seda:enrichPromofarmaLineWithPuc?concurrentConsumers=1&multipleConsumers=false";
  private final static String QUERY = "SELECT a.Puc as puc "
    + "FROM Articu a "
    + "WHERE a.IdArticu = :#code";

  @Override
  public void configure() throws Exception {
    from(URI)
      .id(ROUTE_ID)
      .setHeader("code", simple("${body.code()}"))
      .enrich()
      .simple("sql:" + QUERY)
      .aggregationStrategy(changeLinePuc())
      .to("seda:sendPromofarmaLineToSpreadsheet?blockWhenFull=true");
  }

  private AggregationStrategy changeLinePuc() {
    return (oldExchange, newExchange) -> {
      final Line line = oldExchange.getIn().getBody(Line.class);
      final ArrayList<LinkedCaseInsensitiveMap> rows = newExchange.getIn().getBody(ArrayList.class);

      if (!rows.isEmpty()) {
        line.changePuc(BigDecimal.valueOf((Double) rows.get(0).get("puc")));
      }

      return oldExchange;
    };
  }
}
