package com.github.pmoerenhout.pduutils.gsm0340;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import com.github.pmoerenhout.pduutils.ie.InformationElement;

public class UdhBuilder {

  private HashMap<Integer, InformationElement> ieMap = new HashMap<>();

  public void addInformationElement(final InformationElement ie) {
    if (ieMap.containsKey(ie.getIdentifier())) {
      throw new IllegalArgumentException("IEI already exist");
    }
    if (ie.getLength() < 0 || ie.getLength() > 255 || ie.getLength() != ie.getData().length) {
      throw new IllegalArgumentException("IE length is not consistent");
    }
    this.ieMap.put(ie.getIdentifier(), ie);
  }

  public byte[] getUserDataHeader() {
    // Including the UDHL
    final int length = ieMap.values().stream().mapToInt(ie -> 2 + ie.getLength()).reduce(0, Integer::sum);
    final ByteBuffer bb = ByteBuffer.allocate(1 + length);
    bb.put((byte) (length & 0xff));
    for (final InformationElement ie : ieMap.values()) {
      bb.put((byte) (ie.getIdentifier() & 0xff));
      bb.put((byte) (ie.getLength() & 0xff));
      bb.put(ie.getData());
    }
    return bb.array();
  }

  public static byte[] getUserDataHeader(final InformationElement... informationElements) {
    // Including the UDHL
    final int length = Arrays.stream(informationElements).mapToInt(ie -> 2 + ie.getLength()).reduce(0, Integer::sum);
    if (length > 255) {
      throw new IllegalArgumentException("UDH is too large");
    }
    final ByteBuffer bb = ByteBuffer.allocate(1 + length);
    bb.put((byte) (length & 0xff));
    for (final InformationElement ie : informationElements) {
      if (ie.getLength() < 0 || ie.getLength() > 255 || ie.getLength() != ie.getData().length) {
        throw new IllegalArgumentException("IE length is not consistent");
      }
      bb.put((byte) (ie.getIdentifier() & 0xff));
      bb.put((byte) (ie.getLength() & 0xff));
      bb.put(ie.getData());
    }
    return bb.array();
  }
}
