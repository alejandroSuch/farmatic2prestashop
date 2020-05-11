package com.veamospues.farmatic2prestashop.route.promofarma.invoices;

import com.veamospues.farmatic2prestashop.config.PromofarmaConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
public class PromofarmaInvoiceProcessor extends RouteBuilder {

  static final String ROUTE_ID = "PromofarmaInvoiceProcessor";
  public static final String WITH_BLANK_SPACE = " ";
  public static final String LINE_BREAK = "\\r\\n";

  private final PromofarmaConfiguration promofarmaConfiguration;

  @Override
  public void configure() throws Exception {
    from(uri())
      .id(ROUTE_ID)
      .convertBodyTo(String.class)
      .process(this::process)
      .split(body())
      .streaming()
      .to("seda:processPromofarmaLine?blockWhenFull=true");
  }

  private String uri() {
    return "file://" +
      promofarmaConfiguration.getInvoicesLocation() + "?" +
      "flatten=true&delay=60000";
  }

  private void process(Exchange exchange) {
    System.out.println("");

    final Pattern pattern = Pattern.compile("(\\d{6,13}\\s+)", Pattern.MULTILINE);
    final String body = exchange.getIn()
      .getBody(String.class)
      .replaceAll(LINE_BREAK, WITH_BLANK_SPACE)
      .replaceAll("CN / EAN13 Producto Unidades Total Bruto sin IVA IVA Importe IVA Total IVA incl ", "")
      .replaceAll("TOTAL FACTURA (.*)", "")
      .trim();
    final Matcher matcher = pattern.matcher(body);

    List<String> lines = new ArrayList<>();

    StringBuilder sb = new StringBuilder();
    int position = 0;
    while(matcher.find()) {
      sb.append(body.substring(position, matcher.start()));

      if(position != 0) {
        lines.add(sb.toString().trim());
        sb.setLength(0);
      }

      sb.append(matcher.group());
      position = matcher.end();
    }
    sb.append(body.substring(position));
    lines.add(sb.toString());

    final String collect = lines.stream()
      .collect(Collectors.joining("\n"));

    exchange.getOut().setHeader("file", exchange.getIn().getHeader("CamelFileNameOnly", String.class).split("\\.")[0]);
    exchange.getOut().setBody(lines);
  }
}
