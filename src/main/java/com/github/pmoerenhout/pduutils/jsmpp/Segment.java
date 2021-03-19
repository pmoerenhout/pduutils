package com.github.pmoerenhout.pduutils.jsmpp;

public class Segment {

  private int reference;
  private int total;
  private int part;
  private byte dcs;
  private byte[] data;

  public Segment() {
  }

  public Segment(final int reference, final int total, final int part, final byte dcs, final byte[] data) {
    this.reference = reference;
    this.total = total;
    this.part = part;
    this.dcs = dcs;
    this.data = data;
  }

  public int getReference() {
    return reference;
  }

  public void setReference(final int reference) {
    this.reference = reference;
  }

  public int getPart() {
    return part;
  }

  public void setPart(final int part) {
    this.part = part;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(final int total) {
    this.total = total;
  }

  public byte getDcs() {
    return dcs;
  }

  public void setDcs(final byte dcs) {
    this.dcs = dcs;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(final byte[] data) {
    this.data = data;
  }
}
