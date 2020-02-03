package com.veamospues.farmatic2prestashop.infrastructure.sheets;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.Objects;
import lombok.Data;

@Data
public class Sheet extends GenericJson {

  @Key
  private Properties properties;

  boolean hasName(String tabName) {
    return Objects.equals(properties.getTitle(), tabName);
  }

  public Integer id() {
    return properties.getSheetId();
  }
}
