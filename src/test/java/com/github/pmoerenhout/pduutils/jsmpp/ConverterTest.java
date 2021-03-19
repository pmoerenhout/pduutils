package com.github.pmoerenhout.pduutils.jsmpp;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.OptionalParameter;
import org.junit.Assert;
import org.junit.Test;

import com.github.pmoerenhout.pduutils.Util;
import com.github.pmoerenhout.pduutils.ie.Concatenation8InformationElement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConverterTest {

  @Test
  public void test_deliver_sm_with_sar_in_smpp_optional() throws Exception {

    DeliverSm deliverSm = new DeliverSm();
    byte[] shortMessage = "Hello World".getBytes(StandardCharsets.ISO_8859_1);

    deliverSm.setShortMessage(new byte[0]);
    final List<OptionalParameter> optionalParameters = new ArrayList<>();
    optionalParameters.add(new OptionalParameter.Message_payload(shortMessage));
    optionalParameters.add(new OptionalParameter.Sar_msg_ref_num((short) 12345));
    optionalParameters.add(new OptionalParameter.Sar_total_segments((byte) 200));
    optionalParameters.add(new OptionalParameter.Sar_segment_seqnum((byte) 2));
    deliverSm.setOptionalParameters(optionalParameters.toArray(new OptionalParameter[0]));

    Segment segment = Converter.getSegment(deliverSm);

    Assert.assertEquals(12345, segment.getReference());
    Assert.assertEquals(200, segment.getTotal());
    Assert.assertEquals(2, segment.getPart());
    Assert.assertEquals("Hello World", new String(segment.getData(), StandardCharsets.ISO_8859_1));
  }

  @Test
  public void test_deliver_sm_with_sar_in_user_data_header() throws Exception {

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

    log.info("SM: {}", Util.bytesToHexString(shortMessage));

    Segment segment = Converter.getSegment(deliverSm);

    Assert.assertEquals(222, segment.getReference());
    Assert.assertEquals(2, segment.getTotal());
    Assert.assertEquals(1, segment.getPart());
    Assert.assertEquals("Hello World", new String(segment.getData(), StandardCharsets.ISO_8859_1));
  }
}