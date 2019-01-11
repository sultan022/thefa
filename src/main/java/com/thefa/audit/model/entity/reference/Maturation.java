package com.thefa.audit.model.entity.reference;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "fa_maturation")
public class Maturation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "maturation_id")
  private long maturationId;
  private long fanId;
  private String sts;
  private java.sql.Timestamp maturationDate;


  public long getMaturationId() {
    return maturationId;
  }

  public void setMaturationId(long maturationId) {
    this.maturationId = maturationId;
  }


  public long getFanId() {
    return fanId;
  }

  public void setFanId(long fanId) {
    this.fanId = fanId;
  }


  public String getSts() {
    return sts;
  }

  public void setSts(String sts) {
    this.sts = sts;
  }


  public java.sql.Timestamp getMaturationDate() {
    return maturationDate;
  }

  public void setMaturationDate(java.sql.Timestamp maturationDate) {
    this.maturationDate = maturationDate;
  }

}
