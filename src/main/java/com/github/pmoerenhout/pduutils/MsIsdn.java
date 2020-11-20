package com.github.pmoerenhout.pduutils;

import org.apache.commons.lang3.StringUtils;

public class MsIsdn {

  private String address = null;
  private Type type = Type.VOID;

  public MsIsdn() {
    this("", Type.VOID);
  }

  public MsIsdn(String number) {
    if (number.length() > 0 && number.charAt(0) == '+') {
      this.address = number.substring(1);
      this.type = Type.INTERNATIONAL;
    } else {
      this.address = number;
      this.type = typeOf(number);
    }
  }

  public MsIsdn(String address, Type type) {
    if (address == null) {
      throw new IllegalArgumentException("Address is null");
    }
    if (address.charAt(0) == '+') {
      throw new IllegalArgumentException("Address cannot contain the + sign");
    }
    this.address = address;
    this.type = type;
  }

  public MsIsdn(MsIsdn msisdn) {
    this.type = msisdn.getType();
    this.address = msisdn.getAddress();
  }

  private static Type typeOf(final String number) {
    if (StringUtils.isBlank(number)) {
      return Type.VOID;
    }
    for (int i = 0; i < number.length(); i++) {
      if (!Character.isDigit(number.charAt(i))) {
        return Type.TEXT;
      }
    }
    return Type.INTERNATIONAL;
  }

  public String getAddress() {
    return this.address;
  }

  public Type getType() {
    return this.type;
  }

  public boolean isVoid() {
    return (this.type == Type.VOID);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MsIsdn)) {
      return false;
    }
    return (this.address.equalsIgnoreCase(((MsIsdn) o).getAddress()));
  }

  @Override
  public String toString() {
    return String.format("[%s / %s]", getType(), getAddress());
  }

  @Override
  public int hashCode() {
    return this.address.hashCode() + (15 * this.type.hashCode());
  }

  public enum Type {
    VOID,
    NATIONAL,
    INTERNATIONAL,
    TEXT
  }
}
