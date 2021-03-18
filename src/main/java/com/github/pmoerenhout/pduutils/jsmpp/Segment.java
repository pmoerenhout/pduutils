package com.github.pmoerenhout.pduutils.jsmpp;

public class Segment {

  private int reference;
  private int part;
  private int total;
  private byte[] data;

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

  public byte[] getData() {
    return data;
  }

  public void setData(final byte[] data) {
    this.data = data;
  }
}
