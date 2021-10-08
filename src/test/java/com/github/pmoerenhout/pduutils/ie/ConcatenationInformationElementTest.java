package com.github.pmoerenhout.pduutils.ie;

import static com.github.pmoerenhout.pduutils.ie.Concatenation16InformationElement.IEI_CONCATENATION_16BIT;
import static com.github.pmoerenhout.pduutils.ie.Concatenation8InformationElement.IEI_CONCATENATION_8BIT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConcatenationInformationElementTest {

  @Test
  void test_parse_concatenation_8bit() {

    final byte[] data = new byte[]{ (byte) 0xaa, (byte) 0xcc, (byte) 0xbb };

    ConcatenationInformationElement ie = (ConcatenationInformationElement) InformationElementFactory
        .createInformationElement(IEI_CONCATENATION_8BIT, data);

    assertEquals(0, ie.getIdentifier());
    assertEquals(data.length, ie.getLength());
    assertEquals(data, ie.getData());
    assertEquals(170, ie.getMpRefNo());
    assertEquals(204, ie.getMpMaxNo());
    assertEquals(187, ie.getMpSeqNo());
  }

  @Test
  void test_parse_concatenation_16bit() {

    final byte[] data = new byte[]{ (byte) 0xaa, (byte) 0xbb, (byte) 0xdd, (byte) 0xcc };

    ConcatenationInformationElement ie = (ConcatenationInformationElement) InformationElementFactory
        .createInformationElement(IEI_CONCATENATION_16BIT, data);

    assertEquals(8, ie.getIdentifier());
    assertEquals(data.length, ie.getLength());
    assertEquals(data, ie.getData());
    assertEquals(65467, ie.getMpRefNo());
    assertEquals(221, ie.getMpMaxNo());
    assertEquals(204, ie.getMpSeqNo());
  }

}