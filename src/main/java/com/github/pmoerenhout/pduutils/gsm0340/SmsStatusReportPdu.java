package com.github.pmoerenhout.pduutils.gsm0340;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

// PduUtils Library - A Java library for generating GSM 03.40 Protocol Data Units (PDUs)
//
// Copyright (C) 2008, Ateneo Java Wireless Competency Center/Blueblade Technologies, Philippines.
// PduUtils is distributed under the terms of the Apache License version 2.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

public class SmsStatusReportPdu extends Pdu {

  private int messageReference = 0x00;
  private int status = 0x00;
  private ZonedDateTime timestamp;
  private ZonedDateTime dischargeTime;

  // can only create via the factory
  protected SmsStatusReportPdu() {
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

  public void setTpSri(int value) {
    setFirstOctetField(PduUtils.TP_SRI_MASK, value, new int[]{ PduUtils.TP_SRI_NO_REPORT, PduUtils.TP_SRI_REPORT });
  }

  public boolean hasTpSri() {
    return getFirstOctetField(PduUtils.TP_SRI_MASK) == PduUtils.TP_SRI_REPORT;
  }

  public int getMessageReference() {
    return this.messageReference;
  }

  public void setMessageReference(int reference) {
    this.messageReference = reference;
  }

  public int getStatus() {
    return this.status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public ZonedDateTime getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(ZonedDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Calendar getTimestampAsCalendar() {
    return GregorianCalendar.from(this.timestamp);
  }

  public ZonedDateTime getDischargeTime() {
    return this.dischargeTime;
  }

  public void setDischargeTime(ZonedDateTime myDischargeTime) {
    this.dischargeTime = myDischargeTime;
  }

  public Calendar getDischargeTimeAsCalendar() {
    return GregorianCalendar.from(this.dischargeTime);
  }

  @Override
  protected String pduSubclassInfo() {
    final StringBuilder sb = new StringBuilder();
    // message reference
    sb.append("Message Reference: " + PduUtils.byteToPdu(getMessageReference()));
    sb.append("\n");
    // destination address
    if (getAddress() != null) {
      sb.append("Destination Address: [Length: " + getAddress().length() + " (" + PduUtils.byteToPdu((byte) getAddress().length()) + ")");
      sb.append(", Type: " + PduUtils.byteToPdu(getAddressType()) + " (" + PduUtils.byteToBits((byte) getAddressType()) + ")");
      sb.append(", Address: " + getAddress());
      sb.append("]");
    } else {
      sb.append("Destination Address: [Length: 0");
      sb.append(", Type: " + PduUtils.byteToPdu(getAddressType()) + " (" + PduUtils.byteToBits((byte) getAddressType()) + ")");
      sb.append("]");
    }
    sb.append("\n");
    // timestamp
    sb.append("TP-SCTS: " + formatTimestamp(getTimestampAsCalendar()));
    sb.append("\n");
    // discharge time
    sb.append("Discharge Time: " + formatTimestamp(getDischargeTimeAsCalendar()));
    sb.append("\n");
    // status
    sb.append("Status: " + PduUtils.byteToPdu(getStatus()));
    sb.append("\n");
    return sb.toString();
  }
}
