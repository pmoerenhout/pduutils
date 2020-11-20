package com.github.pmoerenhout.pduutils.gsm0340;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

//PduUtils Library - A Java library for generating GSM 03.40 Protocol Data Units (PDUs)
//Copyright (C) 2008, Ateneo Java Wireless Competency Center/Blueblade Technologies, Philippines.
//PduUtils is distributed under the terms of the Apache License version 2.0


//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
public class SmsDeliveryPdu extends Pdu {
  // ==================================================
  // SERVICE CENTRE TIMESTAMP
  // ==================================================
  private ZonedDateTime serviceCentreTimestamp;

  // can only create via the factory
  protected SmsDeliveryPdu() {
  }

  public ZonedDateTime getServiceCentreTimestamp() {
    return this.serviceCentreTimestamp;
  }

  public void setServiceCentreTimestamp(ZonedDateTime timestamp) {
    this.serviceCentreTimestamp = timestamp;
  }

  public Calendar getServiceCentreTimestampAsCalendar() {
    return GregorianCalendar.from(this.serviceCentreTimestamp);
  }

  // ==================================================
  // FIRST OCTET UTILITIES
  // ==================================================
  public void setTpMms(int value) {
    checkTpMti(new int[]{ PduUtils.TP_MTI_SMS_DELIVER, PduUtils.TP_MTI_SMS_STATUS_REPORT });
    // for SMS-DELIVER and SMS-STATUS-REPORT only
    setFirstOctetField(PduUtils.TP_MMS_MASK, value, new int[]{ PduUtils.TP_MMS_MORE_MESSAGES, PduUtils.TP_MMS_NO_MESSAGES });
  }

  public boolean hasTpMms() {
    checkTpMti(new int[]{ PduUtils.TP_MTI_SMS_DELIVER, PduUtils.TP_MTI_SMS_STATUS_REPORT });
    // for SMS-DELIVER and SMS-STATUS-REPORT only
    return getFirstOctetField(PduUtils.TP_MMS_MASK) == PduUtils.TP_MMS_MORE_MESSAGES;
  }

  public void setTpRp(int value) {
    setFirstOctetField(PduUtils.TP_RP_MASK, value, new int[]{ PduUtils.TP_RP_REPLY_PATH, PduUtils.TP_RP_NO_REPLY_PATH });
  }

  public boolean hasTpRp() {
    return getFirstOctetField(PduUtils.TP_RP_MASK) == PduUtils.TP_RP_REPLY_PATH;
  }

  public void setTpSri(int value) {
    setFirstOctetField(PduUtils.TP_SRI_MASK, value, new int[]{ PduUtils.TP_SRI_NO_REPORT, PduUtils.TP_SRI_REPORT });
  }

  public boolean hasTpSri() {
    return getFirstOctetField(PduUtils.TP_SRI_MASK) == PduUtils.TP_SRI_REPORT;
  }

  @Override
  protected String pduSubclassInfo() {
    final StringBuilder sb = new StringBuilder();
    // originator address
    if (getAddress() != null) {
      sb.append("Originator Address: [Length: " + getAddress().length() + " (" + PduUtils.byteToPdu((byte) getAddress().length()) + ")");
      sb.append(", Type: " + PduUtils.byteToPdu(getAddressType()) + " (" + PduUtils.byteToBits((byte) getAddressType()) + ")");
      sb.append(", Address: " + getAddress());
      sb.append("]");
    } else {
      sb.append("Originator Address: [Length: 0");
      sb.append(", Type: " + PduUtils.byteToPdu(getAddressType()) + " (" + PduUtils.byteToBits((byte) getAddressType()) + ")");
      sb.append("]");
    }
    sb.append("\n");
    // protocol id
    sb.append("TP-PID: " + PduUtils.byteToPdu(getProtocolIdentifier()) + " (" + PduUtils.byteToBits((byte) getProtocolIdentifier()) + ")");
    sb.append("\n");
    // dcs
    sb.append("TP-DCS: " + PduUtils.byteToPdu(getDataCodingScheme()) + " (" + PduUtils.decodeDataCodingScheme(this) + ") (" + PduUtils
        .byteToBits((byte) getDataCodingScheme()) + ")");
    sb.append("\n");
    // timestamp
    sb.append("TP-SCTS: ");
    sb.append(formatTimestamp(getServiceCentreTimestamp()));
    sb.append("\n");
    return sb.toString();
  }
}
