package com.github.pmoerenhout.pduutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MsIsdnTest {

  @Test
  void test_parse_international() {
    MsIsdn msisdn = new MsIsdn("+31612345678");

    Assertions.assertEquals("31612345678", msisdn.getAddress());
    Assertions.assertEquals(MsIsdn.Type.INTERNATIONAL, msisdn.getType());
  }

  @Test
  void test_parse_international_trunk_prefix() {
    MsIsdn msisdn = new MsIsdn("0031612345678");

    Assertions.assertEquals("31612345678", msisdn.getAddress());
    Assertions.assertEquals(MsIsdn.Type.INTERNATIONAL, msisdn.getType());
  }

  @Test
  void test_parse_national() {
    MsIsdn msisdn = new MsIsdn("0612345678");

    Assertions.assertEquals("612345678", msisdn.getAddress());
    Assertions.assertEquals(MsIsdn.Type.NATIONAL, msisdn.getType());
  }

  @Test
  void test_parse_text() {
    MsIsdn msisdn = new MsIsdn("ABCDEF");

    Assertions.assertEquals("ABCDEF", msisdn.getAddress());
    Assertions.assertEquals(MsIsdn.Type.TEXT, msisdn.getType());
  }

  @Test
  void test_void() {
    MsIsdn msisdn = new MsIsdn("");

    Assertions.assertEquals("", msisdn.getAddress());
    Assertions.assertEquals(MsIsdn.Type.VOID, msisdn.getType());
  }
}