package com.veamospues.farmatic2prestashop.infrastructure.xml.orders;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Order {

  private String id;

  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
