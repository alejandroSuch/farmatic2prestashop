package com.veamospues.farmatic2prestashop.domain;

import lombok.Data;
import lombok.ToString;

import static java.lang.String.format;

@Data
@ToString
public class Product {
    Integer id;
    String name;
    Integer stock;

    public String getReference() {
        return format("%06d", id);
    }
}
