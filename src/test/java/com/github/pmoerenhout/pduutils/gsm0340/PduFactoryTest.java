package com.github.pmoerenhout.pduutils.gsm0340;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.pmoerenhout.pduutils.MsIsdn;

public class PduFactoryTest {

  @Test
  void test_submit_sms_pdu() throws Exception {

    PduParser pduParser = new PduParser();

    Pdu pdu = pduParser.parsePdu("0051000B911316240486F97F168F1702700000120D002101015241760000000000006D656173");

    assertEquals(SmsSubmitPdu.class, pdu.getClass());
    assertArrayEquals(new byte[]{ 0x00, 0x12, 0x0D, 0x00, 0x21, 0x01, 0x01, 0x52, 0x41, 0x76, 0, 0, 0, 0, 0, 0, 0x6d, 0x65, 0x61, 0x73 },
        pdu.getUserDataAsBytes());
    assertEquals("31614240689", pdu.getAddress());
    assertEquals(0x7f, pdu.getProtocolIdentifier());
    assertEquals(0x16, pdu.getDataCodingScheme());
    assertEquals(2, pdu.getUDHLength());
    assertArrayEquals(new byte[]{ 0x02, 0x70, 0x00 }, pdu.getUDHData());
  }

  @Test
  void test_deliver_sms_pdu() throws Exception {

    PduFactory pduFactory = new PduFactory();
    PduGenerator pduGenerator = new PduGenerator();

    Pdu pdu = PduFactory.newSmsDeliveryPdu();
    pdu.setAddress(new MsIsdn("31614240689"));

  }

  @Test
  void test_sms_deliver_pdu() throws Exception {

    PduParser pduParser = new PduParser();

    String smsc = "07 91 13 26 04 00 00 F0";

    //String s = "D1 4F 02 02  82 81 06 02  80 01 8B 45  40 05 81 12";
    String s = "40 05 81 12";
    //String s = "8B 45 40 05 81 12";
    s += "50 F3 7F F6  21 20 30 22  22 22 00 35  02 70 00 00";
    s += "30 15 16 01  15 15 00 00  00 05 C6 5F  33 10 20 35";
    s += "74 CE BD C9  AE 9D A9 58  09 5A A1 8B  0F 24 9B 0A ";
    s += "49 15 7A B9  B1 EE 84 14  75 E3 64 27  76 EC 77 1D";
    s += "75";
    s = smsc + s;
    s = s.replace(" ", "");
    System.out.println(s);
    System.out.println(s);
    System.out.println(s);

    Pdu pdu = pduParser.parsePdu(s.replace(" ", ""));

    System.out.println(pdu.getClass().getName());
    System.out.println(pdu.getClass().getName());
    System.out.println(pdu);
    System.out.println(pdu.getAddress());
    System.out.println(pdu.getSmscAddress());
  }

}