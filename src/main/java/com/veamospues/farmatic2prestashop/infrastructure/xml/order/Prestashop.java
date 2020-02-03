package com.veamospues.farmatic2prestashop.infrastructure.xml.order;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Prestashop {

  private Order order;

  @XmlElement
  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public com.veamospues.farmatic2prestashop.dto.Order toOrder() {
    return order.toOrder();
  }
}
