package com.veamospues.farmatic2prestashop.route.orderstats;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.veamospues.farmatic2prestashop.config.PrestashopConfiguration;
import com.veamospues.farmatic2prestashop.config.SqlQueriesConfiguration;
import com.veamospues.farmatic2prestashop.dto.Order;
import com.veamospues.farmatic2prestashop.dto.Product;
import com.veamospues.farmatic2prestashop.infrastructure.sheets.CloneTab;
import com.veamospues.farmatic2prestashop.infrastructure.sheets.GetTabs;
import com.veamospues.farmatic2prestashop.infrastructure.xml.order.Prestashop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

@Slf4j
@Component
@AllArgsConstructor
public class ProcessOrder extends RouteBuilder {

  private static final String ROUTE_ID = "Order stats - Process order";
  private static final int FIVE_SECONDS = 5000;
  private static final String TAB_NAME_PATTERN = "yyyy/MM";
  private static final String HTTP_METHOD_GET = "GET";

  private PrestashopConfiguration prestashopConfiguration;
  private GetTabs getTabs;
  private CloneTab cloneTab;
  private SqlQueriesConfiguration queries;

  @Override
  public void configure() throws JAXBException {

    from("seda:processOrder?concurrentConsumers=1&multipleConsumers=false")
      .id(ROUTE_ID)
      .setHeader(Exchange.HTTP_METHOD, simple(HTTP_METHOD_GET))
      .setHeader(Exchange.HTTP_URI, simple(prestashopConfiguration.getOrdersUrl() + "/${body}"))
      .to(
        "http://orderDetail?authMethod=Basic&authUsername=" +
          prestashopConfiguration.getApiToken() +
          "&authPassword="
      )
      .unmarshal(new JaxbDataFormat(JAXBContext.newInstance(Prestashop.class)))
      .setBody(simple("${body.toOrder()}"))
      .process(productIdsToHeaders())
      .process(invoiceDateToHeaders())
      .process(appendSheetIdAndCodesToHeaders())
      .enrich()
      .simple("sql://" + queries.getPucsInDate())
      .aggregationStrategy(addPucToProducts())
      .process(addRowToSpreadsheet())
      .delay(FIVE_SECONDS)
      .process(toInputStream())
      .to("seda:writeOrderDateToFile?blockWhenFull=true");
  }

  private Processor productIdsToHeaders() {
    return exchange -> {
      final Order order = exchange.getIn().getBody(Order.class);

      exchange.getIn()
        .setHeader(
          "productIds", order.products()
            .stream()
            .map(Product::code)
            .collect(Collectors.joining(", "))
        );
    };
  }

  private Processor invoiceDateToHeaders() {
    return exchange -> {
      final Order order = exchange.getIn().getBody(Order.class);
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

      exchange.getIn()
        .setHeader(
          "invoiceDate",
          order.invoiceDate().format(formatter)
        );
    };
  }

  private Processor appendSheetIdAndCodesToHeaders() {
    return exchange -> {
      final Order order = exchange.getIn().getBody(Order.class);
      final String sheetName = order.invoiceDate()
        .format(DateTimeFormatter.ofPattern(TAB_NAME_PATTERN));

      final Integer sheetId = Optional
        .ofNullable(this.getTabs.idOf(sheetName))
        .orElseGet(() -> cloneTab.fromTemplate(sheetName));

      final String codes = order.products()
        .stream()
        .map(product -> "'" + product.code() + "'")
        .collect(Collectors.joining(","));

      exchange.getIn().setHeader("sheetName", sheetName);
      exchange.getIn().setHeader("sheetId", sheetId);
      exchange.getIn().setHeader("codes", codes);
      exchange.getIn().setBody(exchange.getIn().getBody());
    };
  }

  private AggregationStrategy addPucToProducts() {
    return (oldExchange, newExchange) -> {
      final Order order = oldExchange.getIn().getBody(Order.class);
      final ArrayList<LinkedCaseInsensitiveMap> pucs = newExchange.getIn().getBody(ArrayList.class);

      pucs.forEach(it -> {
        final String idArticu = (String) it.get("idArticu");
        final Double puc = (Double) it.get("Puc");

        try {
          Optional
            .ofNullable(order.product(idArticu))
            .orElse(order.product(Integer.valueOf(idArticu).toString()))
            .puc(BigDecimal.valueOf(puc));
        } catch (Exception exception) {
          log.error(
            "Error al añadir el articulo " + idArticu + " al pedido " + order.id() + "(" + order.invoiceDate() + ")",
            exception
          );
        }
      });

      return oldExchange;
    };
  }

  private Processor addRowToSpreadsheet() {
    return exchange -> {
      final String sheetName = exchange.getIn().getHeader("sheetName", String.class);
      final Order order = exchange.getIn().getBody(Order.class);

      getTabs.sheets()
        .spreadsheets()
        .values()
        .append(
          getTabs.spreadsheetId(),
          sheetName + "!A:N",
          new ValueRange().setValues(valuesFrom(order))
        )
        .setValueInputOption("USER_ENTERED")
        .execute();

      exchange.getOut().setBody(order);
    };
  }

  private ArrayList<List<Object>> valuesFrom(Order order) {
    final ArrayList<List<Object>> data = new ArrayList<>();
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    for (int i = 0; i < order.products().size(); i++) {
      final Product product = order.products().get(i);
      final boolean isFirstRow = i == 0;

      data.add(Arrays.asList(
        order.invoiceDate().format(formatter),
        order.code(),
        product.code(),
        product.description(),
        product.quantity(),
        product.taxes(),
        product.unitPrice(),
        product.puc(),
        "=INDIRECT(\"R[0]C[-2]\"; FALSE) * INDIRECT(\"R[0]C[-4]\"; FALSE)",
        "=INDIRECT(\"R[0]C[-2]\"; FALSE) * INDIRECT(\"R[0]C[-5]\"; FALSE)",
        "=INDIRECT(\"R[0]C[-2]\"; FALSE) - INDIRECT(\"R[0]C[-1]\"; FALSE)",
        isFirstRow ? order.ownDeliveryCost() : "",
        isFirstRow ? order.customerDeliveryCost() : "",
        isFirstRow ? order.reimbursement() : "",
        isFirstRow ? order.boxCost() : "",
        isFirstRow ? order.discount() : ""
        )
      );
    }

    return data;
  }

  private Processor toInputStream() {
    return exchange -> {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);

      oos.writeObject(exchange.getIn().getBody());
      oos.flush();
      oos.close();

      exchange.getOut().setBody(new ByteArrayInputStream(baos.toByteArray()));
    };
  }
}
