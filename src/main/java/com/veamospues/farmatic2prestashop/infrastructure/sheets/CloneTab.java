package com.veamospues.farmatic2prestashop.infrastructure.sheets;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CopySheetToAnotherSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import java.io.IOException;
import java.util.Collections;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CloneTab {

  private GetTabs getTabs;
  private String templateName;

  public Integer fromTemplate(String withTitle)  {
    try {
      final Integer templateId = getTabs.execute().idOf(templateName);

      final Integer clonedSheetId = clone(templateId);
      rename(clonedSheetId, withTitle);

      return clonedSheetId;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private void rename(Integer sheetId, String withTitle) throws IOException {
    getTabs.sheets().spreadsheets().batchUpdate(
      getTabs.spreadsheetId(),
      new BatchUpdateSpreadsheetRequest()
        .setRequests(Collections.singletonList(
          new Request()
            .setUpdateSheetProperties(
              updateRequest(sheetId, withTitle)
            )
        ))
    )
    .execute();
  }

  private UpdateSheetPropertiesRequest updateRequest(Integer sheedId, String withTitle) {
    final UpdateSheetPropertiesRequest update = new UpdateSheetPropertiesRequest();
    update.setProperties(
      new SheetProperties()
        .set("sheetId", sheedId)
        .set("title", withTitle)
    );
    update.set("fields", "title");
    return update;
  }

  private Integer clone(Integer templateId) throws IOException {
    return getTabs.sheets()
      .spreadsheets()
      .sheets()
      .copyTo(
        getTabs.spreadsheetId(),
        templateId,
        new CopySheetToAnotherSpreadsheetRequest()
          .setDestinationSpreadsheetId(getTabs.spreadsheetId())
      )
      .execute()
      .getSheetId();
  }
}
