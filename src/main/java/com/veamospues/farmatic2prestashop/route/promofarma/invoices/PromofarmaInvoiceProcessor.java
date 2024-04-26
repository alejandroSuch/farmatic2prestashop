package com.veamospues.farmatic2prestashop.route.promofarma.invoices;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.veamospues.farmatic2prestashop.config.PromofarmaConfiguration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(
  value = "route.promofarma-invoices.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class PromofarmaInvoiceProcessor extends RouteBuilder {

  static final String ROUTE_ID = "PromofarmaInvoiceProcessor";
  public static final String WITH_BLANK_SPACE = " ";
  public static final String LINE_BREAK = "\\r\\n";

  private final PromofarmaConfiguration promofarmaConfiguration;

  @Override
  public void configure() throws Exception {
    DataFormatDefinition csv = new CsvDataFormat(";");

    from(uri())
      .id(ROUTE_ID)
      .unmarshal(csv)
      .process(this::process)
      .split(body())
      .streaming()
      .to("seda:EnrichPromofarmaLineWithPucAndName?blockWhenFull=true");
  }

  private String uri() {
    return "file://" +
      promofarmaConfiguration.getInvoicesLocation() + "?" +
      "flatten=true&delay=5000";
  }

  private void process(Exchange exchange) {
    final List<List<String>> data = (List<List<String>>) exchange.getIn().getBody();

    List<Line> lines = data
      .stream()
      .map((List<String>line) -> {
        if (!"SALE".equals(line.get(0))) {
          return null;
        }

        return new Line(
          line.get(2),
          "", // NOMBRE DE PRODUCTO NO PROPORCIONADO 
          Integer.parseInt(line.get(3)), 
          Integer.parseInt(line.get(5)), 
          new BigDecimal(line.get(6).replaceAll(",", "."))
        );
      })
      .filter(it -> Objects.nonNull(it))
      .collect(Collectors.toList())
    ;

    exchange.getOut()
      .setHeader(
        "file",
        exchange.getIn()
          .getHeader("CamelFileNameOnly", String.class)
          .split("\\.")[0]
      );
    exchange.getOut().setBody(lines);
  }
}
