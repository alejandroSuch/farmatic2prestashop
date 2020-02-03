package com.veamospues.farmatic2prestashop.infrastructure.xml.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Order {

  int id;

  String reference;

  int id_customer;

  double total_paid_tax_incl;

  double total_paid_tax_excl;

  double total_shipping_tax_incl;

  double total_shipping_tax_excl;

  double total_products_wt;

  double total_discounts_tax_incl;

  String invoice_date;

  String delivery_date;

  String date_add;

  String date_upd;

  @XmlElement
  public Associations getAssociations() {
    return associations;
  }

  public void setAssociations(Associations associations) {
    this.associations = associations;
  }

  Associations associations;


  @XmlElement
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @XmlElement
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  @XmlElement
  public int getId_customer() {
    return id_customer;
  }

  public void setId_customer(int id_customer) {
    this.id_customer = id_customer;
  }

  @XmlElement
  public double getTotal_paid_tax_incl() {
    return total_paid_tax_incl;
  }

  public void setTotal_paid_tax_incl(double total_paid_tax_incl) {
    this.total_paid_tax_incl = total_paid_tax_incl;
  }

  @XmlElement
  public double getTotal_paid_tax_excl() {
    return total_paid_tax_excl;
  }

  public void setTotal_paid_tax_excl(double total_paid_tax_excl) {
    this.total_paid_tax_excl = total_paid_tax_excl;
  }

  @XmlElement
  public double getTotal_shipping_tax_incl() {
    return total_shipping_tax_incl;
  }

  public void setTotal_shipping_tax_incl(double total_shipping_tax_incl) {
    this.total_shipping_tax_incl = total_shipping_tax_incl;
  }

  @XmlElement
  public double getTotal_shipping_tax_excl() {
    return total_shipping_tax_excl;
  }

  public void setTotal_shipping_tax_excl(double total_shipping_tax_excl) {
    this.total_shipping_tax_excl = total_shipping_tax_excl;
  }

  @XmlElement
  public double getTotal_products_wt() {
    return total_products_wt;
  }

  public void setTotal_products_wt(double total_products_wt) {
    this.total_products_wt = total_products_wt;
  }

  @XmlElement
  public double getTotal_discounts_tax_incl() {
    return total_discounts_tax_incl;
  }

  public void setTotal_discounts_tax_incl(double total_discounts_tax_incl) {
    this.total_discounts_tax_incl = total_discounts_tax_incl;
  }

  @XmlElement
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getInvoice_date() {
    return invoice_date;
  }

  public void setInvoice_date(String invoice_date) {
    this.invoice_date = invoice_date;
  }

  @XmlElement
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getDelivery_date() {
    return delivery_date;
  }

  public void setDelivery_date(String delivery_date) {
    this.delivery_date = delivery_date;
  }

  @XmlElement
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getDate_add() {
    return date_add;
  }

  public void setDate_add(String date_add) {
    this.date_add = date_add;
  }

  @XmlElement
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public String getDate_upd() {
    return date_upd;
  }

  public void setDate_upd(String date_upd) {
    this.date_upd = date_upd;
  }

  public com.veamospues.farmatic2prestashop.dto.Order toOrder() {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    final TemporalAccessor parse = formatter.parse("2016-05-20 09:51:26");
    final LocalDateTime from = LocalDateTime.from(parse);

    return new com.veamospues.farmatic2prestashop.dto.Order(
      this.getId(),
      this.getReference(),
      BigDecimal.valueOf(this.getTotal_products_wt()),
      BigDecimal.valueOf(this.getTotal_shipping_tax_incl()),
      BigDecimal.valueOf(this.getTotal_discounts_tax_incl()),
      LocalDateTime.from(formatter.parse(getInvoice_date())),
      LocalDateTime.from(formatter.parse(getDelivery_date())),
      getAssociations().toProductList()
    );
  }
}
