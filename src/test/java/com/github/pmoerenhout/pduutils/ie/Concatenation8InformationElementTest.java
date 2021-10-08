package com.github.pmoerenhout.pduutils.ie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Concatenation8InformationElementTest {

  @Test
  void test_construct_concatenation_8bit_reference() {

    final Concatenation8InformationElement ie = new Concatenation8InformationElement(56, 5, 3);

    assertEquals(0, ie.getIdentifier());
    assertEquals(3, ie.getLength());
    assertArrayEquals(new byte[]{ (byte) 0x38, (byte) 0x05, (byte) 0x03 }, ie.getData());
    assertEquals(56, ie.getMpRefNo());
    assertEquals(5, ie.getMpMaxNo());
    assertEquals(3, ie.getMpSeqNo());
  }

}