package com.veamospues.farmatic2prestashop.route.orderstats;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetFirstAndLastDates extends RouteBuilder {

  private static final String ROUTE_ID = "Order stats - Get dates";
  private static final String FIVE_MINUTES = "300000";
  private static final String FILE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final String HEADER_FROM = "from";
  private static final String HEADER_TO = "to";
  private static final String NEW_LINE = "\\n";
  private static final String WITH_BLANK = "";
  private static final String LAST_DATE_FILE = "lastDate.txt";

  private PrestashopConfiguration prestashopConfiguration;

  @Override
  public void configure() {
    from(lastDateFile())
      .id(ROUTE_ID)
      .autoStartup(true)
      .process(getFirstAndLastDate())
      .to("seda:processOrders?blockWhenFull=true");
  }

  private String lastDateFile() {
    return "file://" +
      prestashopConfiguration.getCsvLocation() +
      "?fileName=" +
      LAST_DATE_FILE +
      "&noop=true&idempotent=false&delay=" +
      FIVE_MINUTES;
  }

  private Processor getFirstAndLastDate() {
    return exchange -> {
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILE_DATE_PATTERN);
      final LocalDateTime from = LocalDateTime.from(formatter.parse(fromDate(exchange)));
      final LocalDateTime to = LocalDateTime.now();

      exchange.getOut().setHeader(HEADER_FROM, formatter.format(from));
      exchange.getOut().setHeader(HEADER_TO, formatter.format(to));
    };
  }

  private String fromDate(Exchange exchange) throws IOException {
    final GenericFile body = (GenericFile) exchange.getIn().getBody();
    final Path path = Paths.get(body.getAbsoluteFilePath());
    return new String(Files.readAllBytes(path)).replaceAll(NEW_LINE, WITH_BLANK);
  }
}
