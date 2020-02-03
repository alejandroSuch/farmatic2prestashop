package com.veamospues.farmatic2prestashop.infrastructure.xml;

import com.veamospues.farmatic2prestashop.infrastructure.xml.order.Prestashop;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class OrderToOrderDtoTest {
  @Test
  public void testOk() {
    try {
      final InputStream inputStream = new ClassPathResource("order.xml").getInputStream();

      final JAXBContext jaxbContext = JAXBContext.newInstance(Prestashop.class);
      final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

      final Prestashop prestashop = (Prestashop) unmarshaller.unmarshal(inputStream);

      System.out.println();
    } catch (JAXBException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
