package com.veamospues.farmatic2prestashop.route.orderstats;

import com.mashape.unirest.http.Unirest;
import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.infrastructure.xml.orders.Prestashop;
import java.io.ByteArrayInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty(
  value="route.orderstats.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class ProcessOrders extends RouteBuilder {

  private static final String ROUTE_ID = "Order stats - Process orders";
  private static final String EMPTY_STRING = "";

  private PrestashopConfiguration prestashopConfiguration;

  @Override
  public void configure() throws JAXBException {
    from("seda:processOrders?concurrentConsumers=1&multipleConsumers=false")
      .id(ROUTE_ID)
      .process(fetchOrders())
      .unmarshal(new JaxbDataFormat(JAXBContext.newInstance(Prestashop.class)))
      .setBody(simple("body.orderIds"))
      .split(body()).streaming()
      .setBody(simple("${body.id}"))
      .to("seda:processOrder?blockWhenFull=true")
    ;
  }

  private Processor fetchOrders() {
    return exchange -> {
      String query = "filter[delivery_date]=[" + exchange.getIn().getHeader("from") + "," + exchange.getIn().getHeader("to") + "]&filter[current_state]=4&sort=[delivery_date_ASC]";
      String response = Unirest
        .get(prestashopConfiguration.getOrdersUrl())
        .basicAuth(prestashopConfiguration.getApiToken(), EMPTY_STRING)
        .queryString("filter[delivery_date]", "[" + exchange.getIn().getHeader("from") + "," + exchange.getIn().getHeader("to") + "]")
        .queryString("filter[current_state]", "4")
        .queryString("sort", "[delivery_date_ASC]")
        .asString()
        .getBody();

      exchange.getIn().setBody(new ByteArrayInputStream(response.getBytes()));
    };
  }
}
