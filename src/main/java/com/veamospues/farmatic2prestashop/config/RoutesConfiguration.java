package com.veamospues.farmatic2prestashop.config;


import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.csv.QuoteMode.NON_NUMERIC;

@Configuration
public class RoutesConfiguration {
    @Bean
    public CsvDataFormat csv() {
        CsvDataFormat csv = new CsvDataFormat();
        csv.setDelimiter(';');
        csv.setIgnoreEmptyLines(true);
        csv.setQuoteDisabled(FALSE);
        csv.setQuoteMode(NON_NUMERIC);
        csv.setSkipHeaderRecord(TRUE);
        return csv;
    }
}
