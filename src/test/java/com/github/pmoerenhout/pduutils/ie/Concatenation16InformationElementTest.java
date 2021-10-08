package com.github.pmoerenhout.pduutils.ie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Concatenation16InformationElementTest {

  @Test
  public void test_construct_concatenation_16bit_reference() {

    final Concatenation16InformationElement ie = new Concatenation16InformationElement(64002, 251, 3);

    assertEquals(8, ie.getIdentifier());
    assertEquals(4, ie.getLength());
    assertArrayEquals(new byte[]{ (byte) 0xfa, (byte) 0x02, (byte) 0xfb, (byte) 0x03 }, ie.getData());
    assertEquals(64002, ie.getMpRefNo());
    assertEquals(251, ie.getMpMaxNo());
    assertEquals(3, ie.getMpSeqNo());
  }
}