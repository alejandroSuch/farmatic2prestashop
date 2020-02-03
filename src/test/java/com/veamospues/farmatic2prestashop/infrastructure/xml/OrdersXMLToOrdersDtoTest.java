package com.veamospues.farmatic2prestashop.infrastructure.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.veamospues.farmatic2prestashop.infrastructure.xml.orders.Order;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class OrdersXMLToOrdersDtoTest {

  @Test
  public void testOk() throws IOException {
    final InputStream inputStream = new ClassPathResource("order-list.xml")
      .getInputStream();

    final List<Order> orders = new OrdersXMLToOrdersDto().parse(inputStream);

    assertEquals(11, orders.size());
    assertTrue(
      Stream
        .of(995, 996, 997, 998, 999, 1000, 1001, 1002, 1003, 1004, 1005)
        .allMatch(
          id -> orders.stream()
            .map(Order::getId)
            .anyMatch(
              orderId -> orderId.equals(id)
            )
        )
    );
  }

  @Test
  public void testDt() {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    final TemporalAccessor parse = formatter.parse("2016-05-20 09:51:26");
    final LocalDateTime from = LocalDateTime.from(parse);

    System.out.println("");
  }
}
