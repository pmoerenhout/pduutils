package com.github.pmoerenhout.pduutils;

import java.util.Objects;

import lombok.NonNull;

public class MsIsdn {

  private final String address;
  private final Type type;

  public MsIsdn() {
    this("", Type.VOID);
  }

  public MsIsdn(@NonNull String number) {
    if (number.length() > 0) {
      if (number.charAt(0) == '+') {
        this.address = number.substring(1);
        this.type = Type.INTERNATIONAL;
        return;
      } else if (number.charAt(0) == '0') {
        if (number.length() > 1 && number.charAt(1) == '0') {
          this.address = number.substring(2);
          this.type = Type.INTERNATIONAL;
        } else {
          this.address = number.substring(1);
          this.type = Type.NATIONAL;
        }
        return;
      }
      for (int i = 0; i < number.length(); i++) {
        if (!Character.isDigit(number.charAt(i))) {
          this.address = number;
          this.type = Type.TEXT;
          return;
        }
      }
    }
    this.address = number;
    this.type = Type.VOID;
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
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final MsIsdn msIsdn = (MsIsdn) o;
    return Objects.equals(address, msIsdn.address) && type == msIsdn.type;
  }

  @Override
  public String toString() {
    return String.format("[%s / %s]", getType(), getAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, type);
  }

  public enum Type {
    VOID,
    NATIONAL,
    INTERNATIONAL,
    TEXT
  }
}
