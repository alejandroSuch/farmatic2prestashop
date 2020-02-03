package com.veamospues.farmatic2prestashop.infrastructure.xml;

import com.veamospues.farmatic2prestashop.infrastructure.xml.orders.Order;
import com.veamospues.farmatic2prestashop.infrastructure.xml.orders.Prestashop;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class OrdersXMLToOrdersDto {

  public List<Order> parse(InputStream inputStream) {
    try {
      final JAXBContext jaxbContext = JAXBContext.newInstance(Prestashop.class);
      final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

      final Prestashop prestashop = (Prestashop) unmarshaller.unmarshal(inputStream);

      return Collections.unmodifiableList(prestashop.getOrders().getOrder());
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
