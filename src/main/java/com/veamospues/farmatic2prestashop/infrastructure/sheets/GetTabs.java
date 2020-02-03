package com.veamospues.farmatic2prestashop.infrastructure.sheets;

import com.google.api.client.util.Key;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsRequest;
import java.io.IOException;

public class GetTabs extends SheetsRequest<Tabs> {

  private static final String REST_PATH = "v4/spreadsheets/{spreadsheetId}?&fields=sheets.properties";

  // https://developers.google.com/sheets/api/samples/sheet#determine_sheet_id_and_other_properties
  @Key
  private java.lang.String spreadsheetId;

  private Sheets sheets;

  public GetTabs(Sheets client, String spreadsheetId) {
    super(client, "GET", REST_PATH, null, Tabs.class);
    this.spreadsheetId = spreadsheetId;
    this.sheets = client;
  }

  public Sheets sheets() {
    return this.sheets;
  }

  public String spreadsheetId() {
    return this.spreadsheetId;
  }

  public Integer idOf(String tabName) throws IOException {
    return this.execute().idOf(tabName);
  }
}
