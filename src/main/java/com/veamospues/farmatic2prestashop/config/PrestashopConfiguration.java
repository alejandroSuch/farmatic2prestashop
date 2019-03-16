package com.veamospues.farmatic2prestashop.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("prestashop")
public class PrestashopConfiguration {
    private static final String LINE_BREAK = "\\n";
    private static final String MULTIPLE_SPACES = "\\s+";
    private static final String WITH_BLANK_SPACE = " ";

    private String apiToken;
    private String stockAvailablesUrl;
    private String productsUrl;
    private String csvLocation;
    private String reindexUrl;


    private String updateStockTemplate;

    public String getUpdateStockTemplate(
            Integer id,
            Integer productId,
            Integer productAttribute,
            Integer idShop,
            Integer idShopGroup,
            Integer stock,
            Integer dependsOnStock,
            Integer outOfStock
    ) {
        return updateStockTemplate
                .replace("{ID}", id.toString())
                .replace("{PRODUCT_ID}", productId.toString())
                .replace("{PRODUCT_ATTRIBUTE}", productAttribute.toString())
                .replace("{ID_SHOP}", idShop.toString())
                .replace("{ID_SHOP_GROUP}", idShopGroup.toString())
                .replace("{STOCK}", stock.toString())
                .replace("{DEPENDS_ON_STOCK}", dependsOnStock.toString())
                .replace("{OUT_OF_STOCK}", outOfStock.toString())
                ;
    }

    public void setUpdateStockTemplate(String updateStockTemplate) {
        this.updateStockTemplate = updateStockTemplate
                .replaceAll(LINE_BREAK, WITH_BLANK_SPACE)
                .replaceAll(MULTIPLE_SPACES, WITH_BLANK_SPACE);
    }
}
