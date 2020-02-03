package com.veamospues.farmatic2prestashop.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class OrderId implements Serializable {

  private final String id;
}
