package com.veamospues.farmatic2prestashop.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Builder;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.veamospues.farmatic2prestashop.infrastructure.sheets.CloneTab;
import com.veamospues.farmatic2prestashop.infrastructure.sheets.GetTabs;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleSheetsConfiguration {

  @Bean
  public GoogleCredentials googleCredentials(
    @Value("${google.sheets.config.folder}") String sheetsConfigFolder,
    @Value("${google.sheets.credentials.file}") String sheetsCredentialsFile
  ) throws IOException {
    return GoogleCredentials
      .fromStream(
        new FileInputStream(
          sheetsConfigFolder +
            File.separator +
            sheetsCredentialsFile
        )
      )
      .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
  }

  @Bean
  public Sheets sheets(GoogleCredentials googleCredentials)
    throws GeneralSecurityException, IOException {
    return new Builder(
      GoogleNetHttpTransport.newTrustedTransport(),
      JacksonFactory.getDefaultInstance(),
      new HttpCredentialsAdapter(googleCredentials)
    ).build();
  }

  @Bean
  public GetTabs getTabs(
    Sheets sheets,
    @Value("${google.spreadsheet.id}") String spreadsheetId
  ) {

    return new GetTabs(sheets, spreadsheetId);
  }

  @Bean
  public CloneTab cloneTabFromTemplate(
    GetTabs getTabs,
    @Value("${google.spreadsheet.template.name}") String templateName
  ) {
    return new CloneTab(getTabs, templateName);
  }

  @Bean
  public GetTabs getPromofarmaSpreadsheetTabs(
    Sheets sheets,
    @Value("${google.spreadsheet.promofarma.id}") String spreadsheetId
  ) {
    return new GetTabs(sheets, spreadsheetId);
  }

  @Bean
  public CloneTab clonePromofarmaTabFromTemplate(
    GetTabs getPromofarmaSpreadsheetTabs,
    @Value("${google.spreadsheet.template.name}") String templateName
  ) {
    return new CloneTab(getPromofarmaSpreadsheetTabs, templateName);
  }
}
