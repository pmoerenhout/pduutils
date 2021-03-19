package com.github.pmoerenhout.pduutils.jsmpp;

import java.nio.ByteBuffer;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.OptionalParameter;

import com.github.pmoerenhout.pduutils.gsm0340.PduUtils;
import com.github.pmoerenhout.pduutils.ie.ConcatenationInformationElement;
import com.github.pmoerenhout.pduutils.ie.InformationElement;
import com.github.pmoerenhout.pduutils.ie.InformationElementFactory;
import com.github.pmoerenhout.pduutils.ie.InvalidUserDataHeaderException;

public class Converter {

  public static Segment getSegment(final DeliverSm deliverSm)
      throws InvalidUserDataHeaderException {

    final byte[] shortMessage = SmppUtil.getShortMessageOrPayload(deliverSm);

    /*
     * First check is the SAR information is in the SMPP headers
     */
    final OptionalParameter sarReference = deliverSm.getOptionalParameter(OptionalParameter.Tag.SAR_MSG_REF_NUM);
    if (sarReference != null) {
      final OptionalParameter sarTotal = deliverSm.getOptionalParameter(OptionalParameter.Tag.SAR_TOTAL_SEGMENTS);
      final OptionalParameter sarSequenceNumber = deliverSm.getOptionalParameter(OptionalParameter.Tag.SAR_SEGMENT_SEQNUM);
      return new Segment(
          ((OptionalParameter.Sar_msg_ref_num) sarReference).getValue() & 0xffff,
          ((OptionalParameter.Sar_total_segments) sarTotal).getValue() & 0xff,
          ((OptionalParameter.Sar_segment_seqnum) sarSequenceNumber).getValue() & 0xff,
          deliverSm.getDataCoding(),
          shortMessage);
    }

    /*
     * Second, check is the SAR information is in the user data headers
     */

    boolean udhi = deliverSm.isUdhi();
    if (!udhi) {
      // The message consists of 1 segment
      return new Segment(-1, 1, 1, deliverSm.getDataCoding(), shortMessage);
    }

    final Segment segment = new Segment();

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
        throw new InvalidUserDataHeaderException(
            "Information element 0x" + PduUtils.byteToPdu(iei) + " invalid, need " + iedl + " bytes, but " + (endUdh - bb.position() + " available"));
      }
      byte[] ieData = new byte[iedl];
      bb.get(ieData);
      InformationElement ie = InformationElementFactory.createInformationElement(iei, ieData);
      if (ie instanceof ConcatenationInformationElement) {
        final ConcatenationInformationElement cie = (ConcatenationInformationElement) ie;
        segment.setPart(cie.getMpSeqNo());
        segment.setTotal(cie.getMpMaxNo());
        segment.setReference(cie.getMpRefNo());
      }
    }
    if (bb.position() > endUdh) {
      // at the end, position after adding should be exactly at endUdh
      throw new InvalidUserDataHeaderException("UDH is shorter than expected endUdh=" + endUdh + ", position=" + bb.position());
    }
    // Read remaining bytes as user data
    byte[] ud = new byte[bb.remaining()];
    bb.get(ud);
    segment.setData(ud);
    return segment;
  }
}
