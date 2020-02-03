package com.veamospues.farmatic2prestashop.infrastructure.sheets;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.Data;

@Data
public class Properties extends GenericJson {

  @Key
  private Integer sheetId;

  @Key
  private String title;

}
