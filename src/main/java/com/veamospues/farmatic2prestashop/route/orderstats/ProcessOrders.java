package com.veamospues.farmatic2prestashop.route.orderstats;

import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.infrastructure.xml.orders.Prestashop;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
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
  private static final String HTTP_METHOD_GET = "GET";

  private PrestashopConfiguration prestashopConfiguration;

  @Override
  public void configure() throws JAXBException {
    from("seda:processOrders?concurrentConsumers=1&multipleConsumers=false")
      .id(ROUTE_ID)
      .setHeader(
        Exchange.HTTP_METHOD,
        simple(HTTP_METHOD_GET)
      )
      .setHeader(
        Exchange.HTTP_QUERY,
        simple(
          "filter[delivery_date]=[${in.header.from},${in.header.to}]&filter[current_state]=4&sort=[delivery_date_ASC]"
        )
      )
      .to(orders())
      .unmarshal(new JaxbDataFormat(JAXBContext.newInstance(Prestashop.class)))
      .setBody(simple("body.orderIds"))
      .split(body()).streaming()
      .setBody(simple("${body.id}"))
      .to("seda:processOrder?blockWhenFull=true")
    ;
  }

  private String orders() {
    return prestashopConfiguration.getOrdersUrl() +
      "?authMethod=Basic&authUsername=" +
      prestashopConfiguration.getApiToken() +
      "&authPassword=";
  }
}
