package com.veamospues.farmatic2prestashop.route.orderstats;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.dto.Order;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty(
  value="route.orderstats.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class SaveLastDate extends RouteBuilder {

  private static final String ROUTE_ID = "Order stats - Save last date";
  private static final String FILE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final String LAST_DATE_FILE = "lastDate.txt";

  private PrestashopConfiguration prestashopConfiguration;

  @Override
  public void configure() {
    from("seda:writeOrderDateToFile?concurrentConsumers=1&multipleConsumers=false")
      .id(ROUTE_ID)
      .process(fromInputStream())
      .process(putDateToSaveIntoBody())
      .setHeader(Exchange.FILE_NAME, constant(LAST_DATE_FILE))
      .to("file://" + prestashopConfiguration.getCsvLocation());
  }

  private Processor fromInputStream() {
    return exchange -> {
      final ByteArrayInputStream body = (ByteArrayInputStream) exchange.getIn().getBody();
      final ObjectInputStream objectInputStream = new ObjectInputStream(body);
      final Object object = objectInputStream.readObject();

      exchange.getOut().setBody(object);
    };
  }

  private Processor putDateToSaveIntoBody() {
    return exchange -> {
      final Order order = exchange.getIn().getBody(Order.class);
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FILE_DATE_PATTERN);
      final String dateToSave = order.deliveryDate().plusSeconds(1).format(formatter);

      exchange.getOut().setBody(dateToSave);
    };
  }
}
