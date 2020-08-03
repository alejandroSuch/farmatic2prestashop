package com.veamospues.farmatic2prestashop.route.promofarma.invoices;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(
  value = "route.promofarma-invoices.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class InvoiceLineProcessor extends RouteBuilder {

  public final static String ROUTE_ID = "InvoiceLineProcessor";
  private static final Pattern PATTERN = Pattern.compile(
    "^(\\d{6,13})\\s(.*)\\s(\\d+)\\s(\\d+(,?\\d+)?\\.\\d+)\\s€\\s(\\d+)%\\s?(\\d+(,?\\d+)?\\.\\d+)\\s€\\s(\\d+(,?\\d+)?\\.\\d+)\\s€$"
  );
  private static final String URI = "seda:processPromofarmaLine?concurrentConsumers=1&multipleConsumers=false";
  private static final String COMMA = ",";
  private static final String WITH_EMPTY = "";

  @Override
  public void configure() throws Exception {
    from(URI)
      .id(ROUTE_ID)
      .process(this::toLine)
      .choice()
      .when().simple("${body.hasCode()} == true")
      .to("seda:enrichPromofarmaLineWithPuc?blockWhenFull=true")
      .otherwise().to("seda:enrichPromofarmaLineWithCode?blockWhenFull=true");
  }

  private void toLine(Exchange exchange) {
    final String body = exchange.getIn().getBody(String.class);
    final Matcher matcher = PATTERN.matcher(body);

    if (matcher.find()) {
      addLineToBody(exchange, matcher);
    }
  }

  private void addLineToBody(Exchange exchange, Matcher matcher) {
    final Line line = new Line(
      matcher.group(1),
      matcher.group(2),
      Integer.parseInt(matcher.group(3)),
      Integer.parseInt(matcher.group(6)),
      new BigDecimal(matcher.group(9).replaceAll(COMMA, WITH_EMPTY))
    );

    exchange.getIn().setBody(line);
  }
}
