package com.github.pmoerenhout.pduutils.jsmpp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.OptionalParameter;
import org.junit.jupiter.api.Test;

import com.github.pmoerenhout.pduutils.Util;
import com.github.pmoerenhout.pduutils.ie.Concatenation8InformationElement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConverterTest {

  @Test
  void test_deliver_sm_default_with_sar_in_smpp_optional() throws Exception {

    DeliverSm deliverSm = new DeliverSm();
    byte[] shortMessage = "Hello World".getBytes(StandardCharsets.ISO_8859_1);

    deliverSm.setShortMessage(new byte[0]);
    final List<OptionalParameter> optionalParameters = new ArrayList<>();
    optionalParameters.add(new OptionalParameter.Message_payload(shortMessage));
    optionalParameters.add(new OptionalParameter.Sar_msg_ref_num((short) 12345));
    optionalParameters.add(new OptionalParameter.Sar_total_segments((byte) 200));
    optionalParameters.add(new OptionalParameter.Sar_segment_seqnum((byte) 2));
    deliverSm.setOptionalParameters(optionalParameters.toArray(new OptionalParameter[0]));
    deliverSm.setDataCoding(DataCodings.ZERO.toByte());

    Segment segment = Converter.getSegment(deliverSm);

    assertEquals(12345, segment.getReference());
    assertEquals(200, segment.getTotal());
    assertEquals(2, segment.getPart());
    assertEquals("Hello World", new String(segment.getData(), StandardCharsets.ISO_8859_1));
    assertEquals((byte) 0x00, segment.getDcs());
  }

  @Test
  void test_deliver_sm_latin1_with_sar_in_user_data_header() throws Exception {

    DeliverSm deliverSm = new DeliverSm();
    Concatenation8InformationElement ie = new Concatenation8InformationElement(222, 2, 1);
    byte[] udh = ie.toBytes();
    byte[] ud = "Hello World".getBytes(StandardCharsets.ISO_8859_1);

    log.info("UDHL: {}", Util.bytesToHexString((byte) udh.length));
    log.info("UDH: {}", Util.bytesToHexString(udh));
    log.info("UD: {}", Util.bytesToHexString(ud));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(udh.length);
    baos.write(udh);
    baos.write(ud);
    byte[] shortMessage = baos.toByteArray();

    deliverSm.setUdhi();
    deliverSm.setShortMessage(shortMessage);
    deliverSm.setDataCoding(new GeneralDataCoding(Alphabet.ALPHA_LATIN1).toByte());

    log.info("SM: {}", Util.bytesToHexString(shortMessage));

    Segment segment = Converter.getSegment(deliverSm);

    assertEquals(222, segment.getReference());
    assertEquals(2, segment.getTotal());
    assertEquals(1, segment.getPart());
    assertEquals("Hello World", new String(segment.getData(), StandardCharsets.ISO_8859_1));
    assertEquals((byte) 0x03, segment.getDcs());
  }

  @Test
  void test_deliver_sm_ucs2_with_sar_in_user_data_header() throws Exception {

    DeliverSm deliverSm = new DeliverSm();
    Concatenation8InformationElement ie = new Concatenation8InformationElement(223, 2, 1);
    byte[] udh = ie.toBytes();
    byte[] ud = "Hello World".getBytes(StandardCharsets.ISO_8859_1);

    log.info("UDHL: {}", Util.bytesToHexString((byte) udh.length));
    log.info("UDH: {}", Util.bytesToHexString(udh));
    log.info("UD: {}", Util.bytesToHexString(ud));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(udh.length);
    baos.write(udh);
    baos.write(ud);
    byte[] shortMessage = baos.toByteArray();

    deliverSm.setUdhi();
    deliverSm.setShortMessage(shortMessage);
    deliverSm.setDataCoding(new GeneralDataCoding(Alphabet.ALPHA_UCS2).toByte());

    log.info("SM: {}", Util.bytesToHexString(shortMessage));

    Segment segment = Converter.getSegment(deliverSm);

    assertEquals(223, segment.getReference());
    assertEquals(2, segment.getTotal());
    assertEquals(1, segment.getPart());
    assertEquals("Hello World", new String(segment.getData(), StandardCharsets.ISO_8859_1));
    assertEquals((byte) 0x08, segment.getDcs());
  }
}
