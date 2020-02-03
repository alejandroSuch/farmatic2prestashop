package com.veamospues.farmatic2prestashop.infrastructure.xml.order;

import static java.util.stream.Collectors.toList;

import com.veamospues.farmatic2prestashop.dto.Product;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrderRows {

  List<OrderRow> order_row;

  @XmlElement
  public List<OrderRow> getOrder_row() {
    return order_row;
  }

  public void setOrder_row(List<OrderRow> order_row) {
    this.order_row = order_row;
  }

  List<Product> toProductList() {
    return order_row.stream().map(OrderRow::toProduct).collect(toList());
  }
}
