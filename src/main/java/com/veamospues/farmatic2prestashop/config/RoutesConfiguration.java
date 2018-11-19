package com.veamospues.farmatic2prestashop.config;

import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.Boolean.TRUE;

@Configuration
public class RoutesConfiguration {
    @Bean
    public CsvDataFormat csv() {
        CsvDataFormat csv = new CsvDataFormat();
        csv.setDelimiter(";");
        csv.setIgnoreEmptyLines(true);
        csv.setSkipHeaderRecord(TRUE);
        return csv;
    }
}
