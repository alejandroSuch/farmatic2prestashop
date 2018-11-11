package com.veamospues.farmatic2prestashop.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockAvailable {
    Integer idStockAvailable;
    Integer idProduct;
    Integer idProductAttribute;
    Integer idShop;
    Integer idShopGroup;
    Integer dependsOnStock;
    Integer outOfStock;
}
