package com.veamospues.farmatic2prestashop.route.promofarma.invoices;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.veamospues.farmatic2prestashop.infrastructure.sheets.CloneTab;
import com.veamospues.farmatic2prestashop.infrastructure.sheets.GetTabs;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Processor;
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
public class SendPromofarmaLineToSpreadsheet extends RouteBuilder {

  private final static String ROUTE_ID = "SendPromofarmaLineToSpreadsheet";
  private static final String URI = "seda:sendPromofarmaLineToSpreadsheet?concurrentConsumers=1&multipleConsumers=false";
  private static final int FIVE_SECONDS = 5000;

  private GetTabs getPromofarmaSpreadsheetTabs;
  private CloneTab clonePromofarmaTabFromTemplate;

  @Override
  public void configure() throws Exception {
    from(URI)
      .id(ROUTE_ID)
      .delay(FIVE_SECONDS)
      .process(getSheetId())
      .process(exchange -> {
        final String sheetName = exchange.getIn().getHeader("sheetName", String.class);
        final Line line = exchange.getIn().getBody(Line.class);

        int retries = 0;
        boolean done = false;

        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList(
          Objects.isNull(line.code()) ? line.ean() : line.code(),
          line.name(),
          line.units(),
          BigDecimal.valueOf(line.vat()).divide(BigDecimal.valueOf(100)),
          line.totalGrossWithVat(),
          "=INDIRECT(\"R[0]C[-1]\"; FALSE) / INDIRECT(\"R[0]C[-3]\"; FALSE)",
          Objects.isNull(line.puc()) ? "" : line.puc(),
          "=INDIRECT(\"R[0]C[-2]\"; FALSE) - INDIRECT(\"R[0]C[-1]\"; FALSE)",
          "=INDIRECT(\"R[0]C[-1]\"; FALSE) * INDIRECT(\"R[0]C[-6]\"; FALSE)",
          "=(INDIRECT(\"R[0]C[-4]\"; FALSE) - INDIRECT(\"R[0]C[-3]\"; FALSE)) / INDIRECT(\"R[0]C[-3]\"; FALSE)"
        ));

        do {
          try {
            getPromofarmaSpreadsheetTabs
              .sheets()
              .spreadsheets()
              .values()
              .append(
                getPromofarmaSpreadsheetTabs.spreadsheetId(),
                sheetName + "!A:L",
                new ValueRange().setValues(data)
              )
              .setValueInputOption("USER_ENTERED")
              .execute();
            done = true;
          } catch (Exception e) {
            retries++;

            if (5 == retries) {
              log.error("Error inserting in " + sheetName + ". Data is: " + data.toString(), e);
            }
          }
        } while (!done && retries < 5);
      })
      .end();
  }

  private Processor getSheetId() {
    return exchange -> {
      final String sheetName = exchange.getIn().getHeader("file", String.class);

      final Integer sheetId = Optional
        .ofNullable(this.getPromofarmaSpreadsheetTabs.idOf(sheetName))
        .orElseGet(() -> clonePromofarmaTabFromTemplate.fromTemplate(sheetName));

      exchange.getIn().setHeader("sheetId", sheetId);
      exchange.getIn().setHeader("sheetName", sheetName);
    };
  }
}
