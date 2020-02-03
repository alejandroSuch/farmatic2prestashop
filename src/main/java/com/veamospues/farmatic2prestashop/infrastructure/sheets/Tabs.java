package com.veamospues.farmatic2prestashop.infrastructure.sheets;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;
import lombok.Data;

@Data
public class Tabs extends GenericJson {

  @Key
  private List<Sheet> sheets;

  public Integer idOf(String tabName) {
    return sheets.stream()
      .filter(sheet -> sheet.hasName(tabName))
      .map(Sheet::id)
      .findFirst()
      .orElse(null);
  }

}
