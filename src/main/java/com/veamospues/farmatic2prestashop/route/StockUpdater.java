package com.veamospues.farmatic2prestashop.route;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import lombok.AllArgsConstructor;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty(
  value="route.products.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class StockUpdater extends RouteBuilder {

  private static final String TEXT_XML = "text/xml";

  private static final String ROUTE_ID = "StockUpdater";
  private static final String URI = "seda:stockUpdater?multipleConsumers=true&concurrentConsumers=2";
  private static final String EMPTY_STRING = "";
  private static final String CONTENT_TYPE = "Content-Type";

  private PrestashopConfiguration prestashopConfiguration;

  @Override
  public void configure() {
    from(URI).routeId(ROUTE_ID)
      .log("Updating ${in.header.product} with stock ${in.header.stock}")
      .process(stockUpdate())
      .choice()
      .when(header("responseStatus").isEqualTo(200))
      .log("${in.header.product} updated successfully!")
      .otherwise()
      .log("There was a problem updating ${in.header.product}. Response status: ${in.header.responseStatus} ${in.header.responseStatusText}\n${in.header.responseBody}")
      .end()
    ;
  }

  private Processor stockUpdate() {
    return exchange -> {
      HttpResponse<String> response = doRequest(exchange.getIn().getBody(String.class));

      exchange.getIn().setHeader("responseStatus", response.getStatus());
      exchange.getIn().setHeader("responseStatusText", response.getStatusText());
      exchange.getIn().setHeader("responseBody", response.getBody());
    };
  }

  private HttpResponse<String> doRequest(String body) throws UnirestException {
    return Unirest
      .put(prestashopConfiguration.getStockAvailablesUrl())
      .header(CONTENT_TYPE, TEXT_XML)
      .basicAuth(prestashopConfiguration.getApiToken(), EMPTY_STRING)
      .body(body)
      .asString();
  }
}
