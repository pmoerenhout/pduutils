package com.github.pmoerenhout.pduutils.jsmpp;

import java.nio.ByteBuffer;
import java.util.Optional;

import org.jsmpp.bean.DeliverSm;

import com.github.pmoerenhout.pduutils.gsm0340.PduUtils;
import com.github.pmoerenhout.pduutils.ie.ConcatenationInformationElement;
import com.github.pmoerenhout.pduutils.ie.InformationElement;
import com.github.pmoerenhout.pduutils.ie.InformationElementFactory;

public class Converter {

  public static Optional<Segment> getSegment(final DeliverSm deliverSm)
      throws InvalidMessagePayloadException {

    boolean udhi = deliverSm.isUdhi();
    if (!udhi) {
      return Optional.empty();
    }
    byte[] shortMessage = SmppUtil.getShortMessageOrPayload(deliverSm);
//    log.info("shortMessage: {}", Util.bytesToHexString(shortMessage));
//    byte udhl = shortMessage[0];
//    log.info("UDHL: {}", Util.bytesToHexString(udhl));
//    byte[] udh = new byte[udhl];
//    System.arraycopy(shortMessage, 1, udh, 0, udhl);
//    log.info("UDH: {}", Util.bytesToHexString(udh));
//    int length = shortMessage.length - udhl - 1;
//    byte[] ud = new byte[length];
//    System.arraycopy(shortMessage, 1 + udhl, ud, 0, length);
//    log.info("UD: {}", Util.bytesToHexString(ud));

    Segment segment = new Segment();

    // Parse
    ByteBuffer bb = ByteBuffer.wrap(shortMessage);
    int udhLength = bb.get();
    // udh data (iterate till udh is consumed)
    // iei id
    // iei data length
    // iei data
    int endUdh = bb.position() + udhLength;
    while (bb.position() < endUdh) {
      int iei = bb.get();
      int iedl = bb.get();
      if (iedl > endUdh - bb.position()) {
        throw new InvalidMessagePayloadException(
            "Information element 0x" + PduUtils.byteToPdu(iei) + " invalid, need " + iedl + " bytes, but " + (endUdh - bb.position() + " available"));
      }
      byte[] ieData = new byte[iedl];
      bb.get(ieData);
      InformationElement ie = InformationElementFactory.createInformationElement(iei, ieData);
      if (ie instanceof ConcatenationInformationElement) {
        ConcatenationInformationElement cie = (ConcatenationInformationElement) ie;
        segment.setPart(cie.getMpSeqNo());
        segment.setTotal(cie.getMpMaxNo());
        segment.setReference(cie.getMpRefNo());
      }
    }
    if (bb.position() > endUdh) {
      // at the end, position after adding should be exactly at endUdh
      throw new InvalidMessagePayloadException("UDH is shorter than expected endUdh=" + endUdh + ", position=" + bb.position());
    }
    // Read remaining bytes as user data
    byte[] ud = new byte[bb.remaining()];
    bb.get(ud);
    segment.setData(ud);
    return Optional.of(segment);
  }
}
