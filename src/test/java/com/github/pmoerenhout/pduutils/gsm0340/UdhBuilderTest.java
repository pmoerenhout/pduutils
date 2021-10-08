package com.github.pmoerenhout.pduutils.gsm0340;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.pmoerenhout.pduutils.ie.InformationElement;
import com.github.pmoerenhout.pduutils.ie.InformationElementFactory;
import net.freeutils.charset.gsm.CCPackedGSMCharset;

public class UdhBuilderTest {

  private UdhBuilder udhBuilder;

  @BeforeAll
  public void setUp() throws Exception {
    udhBuilder = new UdhBuilder();
  }

  @Test
  void test_udh_with_single_ie() {

    InformationElement ie = InformationElementFactory.generateConcatenation8InformationElement(0xab, 2, 1);
    udhBuilder.addInformationElement(ie);

    byte[] udh = udhBuilder.getUserDataHeader();

    assertArrayEquals(new byte[]{ (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0xab, (byte) 0x02, (byte) 0x01 }, udh);
  }

  @Test
  void test_udh_with_multiple_ie() {
    udhBuilder.addInformationElement(InformationElementFactory.generateConcatenation8InformationElement(0xab, 2, 1));
    udhBuilder.addInformationElement(InformationElementFactory.generatePortInformationElement(0x1234, 0x5678));

    byte[] udh = udhBuilder.getUserDataHeader();

    assertArrayEquals(
        new byte[]{ (byte) 0x0b,
            (byte) 0x00, (byte) 0x03, (byte) 0xab, (byte) 0x02, (byte) 0x01,
            (byte) 0x05, (byte) 0x04, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78 },
        udh);
  }

  @Test
  void test_udh_with_multiple_ie_static() {

    final byte[] udh = udhBuilder.getUserDataHeader(
        InformationElementFactory.generateConcatenation8InformationElement(0xab, 2, 1),
        InformationElementFactory.generatePortInformationElement(0x1234, 0x5678)
    );

    assertArrayEquals(
        new byte[]{ (byte) 0x0b,
            (byte) 0x00, (byte) 0x03, (byte) 0xab, (byte) 0x02, (byte) 0x01,
            (byte) 0x05, (byte) 0x04, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78 },
        udh);
  }

  @Test
  void test_gsm_packed_length() {
    String s = StringUtils.repeat('a', 154);
    byte[] b = s.getBytes(new CCPackedGSMCharset());
    System.out.println(s);
    System.out.println(b.length);
  }


}