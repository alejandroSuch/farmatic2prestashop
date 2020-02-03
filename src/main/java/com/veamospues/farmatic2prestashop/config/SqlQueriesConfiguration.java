package com.veamospues.farmatic2prestashop.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("sql")
public class SqlQueriesConfiguration {
    private static final String LINE_BREAK = "\\n";
    private static final String MULTIPLE_SPACES = "\\s+";
    private static final String WITH_BLANK_SPACE = " ";

    private String stockVariations;
    private String allArticles;
    private String pucsInDate;

    public void setStockVariations(String stockVariations) {
        this.stockVariations = stockVariations.replaceAll(LINE_BREAK, WITH_BLANK_SPACE).replaceAll(MULTIPLE_SPACES, WITH_BLANK_SPACE);
    }

    public void setAllArticles(String allArticles) {
        this.allArticles = allArticles.replaceAll(LINE_BREAK, WITH_BLANK_SPACE).replaceAll(MULTIPLE_SPACES, WITH_BLANK_SPACE);
    }

    public void setPucsInDate(String pucsInDate) {
        this.pucsInDate = pucsInDate.replaceAll(LINE_BREAK, WITH_BLANK_SPACE).replaceAll(MULTIPLE_SPACES, WITH_BLANK_SPACE);
    }
}
