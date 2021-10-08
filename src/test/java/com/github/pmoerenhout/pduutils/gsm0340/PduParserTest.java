package com.github.pmoerenhout.pduutils.gsm0340;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.freeutils.charset.gsm.CCPackedGSMCharset;
import net.freeutils.charset.gsm.SCPackedGSMCharset;

public class PduParserTest {

  private PduParser pduParser;

  @BeforeEach
  public void setUp() throws Exception {
    pduParser = new PduParser();
  }

  @Test
  void test_submit_sms_pdu() throws Exception {

    Pdu pdu = pduParser.parsePdu("0051000B911316240486F97F168F1702700000120D002101015241760000000000006D656173");

    System.out.print(pdu.getClass().getName());
    assertArrayEquals(new byte[]{ 0x00, 0x12, 0x0D, 0x00, 0x21, 0x01, 0x01, 0x52, 0x41, 0x76, 0, 0, 0, 0, 0, 0, 0x6d, 0x65, 0x61, 0x73 },
        pdu.getUserDataAsBytes());
    assertEquals("31614240689", pdu.getAddress());
    assertEquals(1, pdu.getTpMti());
    assertEquals(0x7f, pdu.getProtocolIdentifier());
    assertEquals(0x16, pdu.getDataCodingScheme());
    assertEquals(2, pdu.getUDHLength());
    assertArrayEquals(new byte[]{ 0x02, 0x70, 0x00 }, pdu.getUDHData());
  }

  @Test
  void test_deliver_sms_pdu() throws Exception {
    final SmsDeliveryPdu pdu = (SmsDeliveryPdu) pduParser.parsePdu("0791448720003023240DD0E474D81C0EBB010000111011315214000BE474D81C0EBB5DE3771B");
    assertEquals("447802000332", pdu.getSmscAddress());
    assertEquals(0x91, pdu.getSmscAddressType());
    assertTrue(pdu.hasTpMms());
    assertFalse(pdu.hasTpRp());
    assertEquals("diafaan", pdu.getAddress());
    assertEquals(0x81, pdu.getAddressType());
    //assertEquals(new Date(111,0,11,14,25,41), pdu.getTimestamp());
    // assertEquals(new Date(111,0,11,14,25,41).toInstant(), pdu.getTimestamp());
    assertEquals(ZonedDateTime.of(2011, 1, 11, 13, 25, 41, 0, ZoneOffset.ofTotalSeconds(0)), pdu.getServiceCentreTimestamp());
    assertEquals("diafaan.com", new String(pdu.getUserDataAsBytes(), new CCPackedGSMCharset()));
  }

  @Test
  void test_deliver_sms_pdu_with_rp_and_timezone_cet() throws Exception {
    final SmsDeliveryPdu pdu = (SmsDeliveryPdu) pduParser.parsePdu("07914487200030232C0DD0E474D81C0EBB010000111011315234400BE474D81C0EBB5DE3771B");
    assertEquals("447802000332", pdu.getSmscAddress());
    assertEquals(0x91, pdu.getSmscAddressType());
    assertTrue(pdu.hasTpMms());
    assertTrue(pdu.hasTpRp());
    assertEquals("diafaan", pdu.getAddress());
    assertEquals(0x81, pdu.getAddressType());
    //assertEquals(new Date(111,0,11,14,25,41), pdu.getTimestamp());
    // assertEquals(new Date(111,0,11,14,25,41).toInstant(), pdu.getTimestamp());
    assertEquals(ZonedDateTime.of(2011, 1, 11, 13, 25, 43, 0, ZoneOffset.ofHours(1)), pdu.getServiceCentreTimestamp());
    assertEquals("diafaan.com", new String(pdu.getUserDataAsBytes(), new CCPackedGSMCharset()));
  }

  @Test
  void test_deliver_sms_pdu_with_pst() throws Exception {
    final SmsDeliveryPdu pdu = (SmsDeliveryPdu) pduParser.parsePdu("0791448720003023240DD0E474D81C0EBB0100001110113152548A0BE474D81C0EBB5DE3771B");
    assertEquals("diafaan", pdu.getAddress());
    assertEquals(0x81, pdu.getAddressType());
    //assertEquals(new Date(111,0,11,14,25,41), pdu.getTimestamp());
    // assertEquals(new Date(111,0,11,14,25,41).toInstant(), pdu.getTimestamp());
    assertEquals(ZonedDateTime.of(2011, 1, 11, 13, 25, 45, 0, ZoneOffset.ofHours(-7)), pdu.getServiceCentreTimestamp());
    assertEquals("diafaan.com", new String(pdu.getUserDataAsBytes(), new CCPackedGSMCharset()));
  }

  @Test
  void test_decode_pdu() throws Exception {

    Pdu pdu = pduParser.parsePdu("0791447779078404040D91137910904560F500000220623134630014C432E82C7F97E96537C81E7683964F693104");

    if (pdu instanceof SmsDeliveryPdu) {
      SmsDeliveryPdu smsDeliveryPdu = (SmsDeliveryPdu) pdu;
      System.out.println("SMS-DELIVERY PDU: " + pdu.getRawPdu());
      System.out.println("Address: " + pdu.getAddress() + " " + pdu.getAddressType());
      System.out.println("SMSC Address: " + pdu.getSmscAddress() + " " + pdu.getSmscAddressType());
      byte[] udhdata = pdu.getUDHData();
      if (udhdata != null) {
        System.out.println("UserDataHeader: " + PduUtils.bytesToPdu(pdu.getUDHData()));
      }
      System.out.println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()));
      System.out
          .println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()) + " (" + new String(pdu.getUserDataAsBytes(), new SCPackedGSMCharset()) + ")");
      System.out.println("Decoded Text: " + pdu.getDecodedText());
      System.out.println("DCS: " + PduUtils.byteToPdu(pdu.getDataCodingScheme()));
      System.out.println("PID: " + PduUtils.byteToPdu(pdu.getProtocolIdentifier()));
      System.out.println("Timestamp: " + smsDeliveryPdu.getServiceCentreTimestamp());
      System.out.println("Timestamp: " + smsDeliveryPdu.getServiceCentreTimestampAsCalendar().getTime());
    } else if (pdu instanceof SmsSubmitPdu) {
      SmsSubmitPdu smsSubmitPdu = (SmsSubmitPdu) pdu;
      System.out.println("SMS-SUBMIT PDU: " + pdu.getRawPdu());
      System.out.println("Address: " + pdu.getAddress() + " " + pdu.getAddressType());
      System.out.println("SMSC Address: " + pdu.getSmscAddress() + " " + pdu.getSmscAddressType());
      System.out.println("UserDataHeader: " + PduUtils.bytesToPdu(pdu.getUDHData()));
      System.out.println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()));
      System.out
          .println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()) + " (" + new String(pdu.getUserDataAsBytes(), new SCPackedGSMCharset()) + ")");
      System.out.println("Decoded Text: " + pdu.getDecodedText());
      System.out.println("DCS: " + PduUtils.byteToPdu(pdu.getDataCodingScheme()));
      System.out.println("PID: " + PduUtils.byteToPdu(pdu.getProtocolIdentifier()));
      System.out.println("MessageReference: " + smsSubmitPdu.getMessageReference());
    }
  }

  @Test
  void test_decode_pdu_bug() throws Exception {

    System.out.println("07911356049938004407D0CBA7B4080004027061610144800B0299990102030405060708");
    System.out.println("4407D0CBA7B4080004027061610144800B0299990102030405060708");
    System.out.println("0B0299990102030405060708");
    Pdu pdu = pduParser.parsePdu("07911356049938004407D0CBA7B4080004027061610144800B0299990102030405060708");

    if (pdu instanceof SmsDeliveryPdu) {
      SmsDeliveryPdu smsDeliveryPdu = (SmsDeliveryPdu) pdu;
      System.out.println("SMS-DELIVERY PDU: " + pdu.getRawPdu());
      System.out.println("Address: " + pdu.getAddress() + " " + pdu.getAddressType());
      System.out.println("SMSC Address: " + pdu.getSmscAddress() + " " + pdu.getSmscAddressType());
      byte[] udhdata = pdu.getUDHData();
      if (udhdata != null) {
        System.out.println("UserDataHeader: " + PduUtils.bytesToPdu(pdu.getUDHData()));
      }
      System.out.println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()));
      System.out
          .println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()) + " (" + new String(pdu.getUserDataAsBytes(), new SCPackedGSMCharset()) + ")");
      System.out.println("Decoded Text: " + pdu.getDecodedText());
      System.out.println("DCS: " + PduUtils.byteToPdu(pdu.getDataCodingScheme()));
      System.out.println("PID: " + PduUtils.byteToPdu(pdu.getProtocolIdentifier()));
      System.out.println("Timestamp: " + smsDeliveryPdu.getServiceCentreTimestamp());
      System.out.println("Timestamp: " + smsDeliveryPdu.getServiceCentreTimestampAsCalendar().getTime());
    } else if (pdu instanceof SmsSubmitPdu) {
      SmsSubmitPdu smsSubmitPdu = (SmsSubmitPdu) pdu;
      System.out.println("SMS-SUBMIT PDU: " + pdu.getRawPdu());
      System.out.println("Address: " + pdu.getAddress() + " " + pdu.getAddressType());
      System.out.println("SMSC Address: " + pdu.getSmscAddress() + " " + pdu.getSmscAddressType());
      System.out.println("UserDataHeader: " + PduUtils.bytesToPdu(pdu.getUDHData()));
      System.out.println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()));
      System.out
          .println("UserData: " + PduUtils.bytesToPdu(pdu.getUserDataAsBytes()) + " (" + new String(pdu.getUserDataAsBytes(), new SCPackedGSMCharset()) + ")");
      System.out.println("Decoded Text: " + pdu.getDecodedText());
      System.out.println("DCS: " + PduUtils.byteToPdu(pdu.getDataCodingScheme()));
      System.out.println("PID: " + PduUtils.byteToPdu(pdu.getProtocolIdentifier()));
      System.out.println("MessageReference: " + smsSubmitPdu.getMessageReference());
    }
  }

}