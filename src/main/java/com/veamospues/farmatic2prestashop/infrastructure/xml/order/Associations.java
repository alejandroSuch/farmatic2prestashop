package com.veamospues.farmatic2prestashop.infrastructure.xml.order;

import com.veamospues.farmatic2prestashop.dto.Product;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Associations {

  OrderRows order_rows;

  @XmlElement
  public OrderRows getOrder_rows() {
    return order_rows;
  }

  public void setOrder_rows(OrderRows order_rows) {
    this.order_rows = order_rows;
  }

  public List<Product> toProductList() {
    return this.order_rows.toProductList();
  }

}
