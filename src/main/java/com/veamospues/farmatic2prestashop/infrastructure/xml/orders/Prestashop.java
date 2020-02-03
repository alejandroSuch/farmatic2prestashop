package com.veamospues.farmatic2prestashop.infrastructure.xml.orders;

import com.veamospues.farmatic2prestashop.dto.OrderId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Prestashop {

  private Orders orders;

  @XmlElement
  public Orders getOrders() {
    return orders;
  }

  public void setOrders(Orders orders) {
    this.orders = orders;
  }

  public List<OrderId> getOrderIds() {
    return Optional
      .ofNullable(getOrders().getOrder())
      .orElse(new ArrayList<>())
      .stream()
      .map(Order::getId)
      .map(OrderId::new)
      .collect(Collectors.toList());
  }
}
