package com.veamospues.farmatic2prestashop.infrastructure.xml.orders;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Orders {

  private List<Order> order;

  @XmlElement
  public List<Order> getOrder() {
    return order;
  }

  public void setOrder(List<Order> orders) {
    this.order = orders;
  }
}
