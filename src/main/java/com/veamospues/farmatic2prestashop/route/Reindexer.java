package com.veamospues.farmatic2prestashop.route;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Reindexer extends RouteBuilder {

  private static final int ZERO = 0;
  private static final String ROUTE_IR = "Reindexer";
  private static final String URI = "timer://ReindexerTimer?fixedRate=true&period=4h";

  private PrestashopConfiguration prestashopConfiguration;

  @Override
  public void configure() {
    from(URI)
      .routeId(ROUTE_IR)
      .log("Reindexing...")
      .process(exchange -> {
        Unirest.setTimeouts(ZERO, ZERO);

        HttpResponse<String> response = Unirest
          .get(prestashopConfiguration.getReindexUrl())
          .asString();

        exchange.getIn().setHeader("result", response.getStatus());
      })
      .log("Reindexing finished with status ${in.header.result}")
    ;
  }

}
