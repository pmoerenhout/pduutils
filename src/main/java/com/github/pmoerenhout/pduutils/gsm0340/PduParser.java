package com.github.pmoerenhout.pduutils.gsm0340;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.github.pmoerenhout.pduutils.MsIsdn;
import com.github.pmoerenhout.pduutils.ie.InformationElement;
import com.github.pmoerenhout.pduutils.ie.InformationElementFactory;

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
//
public class PduParser {
  // ==================================================
  // RAW PDU PARSER
  // ==================================================
  // increments as methods are called
  private int position;

  private byte[] pduByteArray;

  // possible types of data
  // BCD digits
  // byte
  // gsm-septets
  // timestamp info
  private int readByte() {
    // read 8-bits forward
    int retVal = this.pduByteArray[this.position] & 0xff;
    this.position++;
    return retVal;
  }

  private int readSwappedNibbleBCDByte() {
    // read 8-bits forward, swap the nibbles
    int data = PduUtils.swapNibbles(readByte());
    return (((data >>> 4) & 0x0f) * 10) + ((data & 0x0f));
  }

  private ZonedDateTime readTimeStamp() {
    // reads timestamp info
    // 7 bytes in semi-octet(BCD) style
    int year = readSwappedNibbleBCDByte();
    int month = readSwappedNibbleBCDByte();
    int day = readSwappedNibbleBCDByte();
    int hour = readSwappedNibbleBCDByte();
    int minute = readSwappedNibbleBCDByte();
    int second = readSwappedNibbleBCDByte();
    // special treatment for timezone due to sign bit
    // swap nibbles, clear the sign bit, convert remaining bits to BCD
    int timezoneBcd = readByte();
    // System.out.println("Timezone BCD " + PduUtils.byteToPdu(timezoneBcd));
    final boolean negative = (timezoneBcd & 0x08) == 0x08; // check bit 3
    final int timezoneQuarters = (((timezoneBcd) & 0x07) * 10) + ((timezoneBcd >>> 4 & 0x0f));
    final ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(timezoneQuarters * (negative ? -900 : 900));
    if (year == 0 && month == 0 && day == 0 && hour == 0 && minute == 0 && second == 0 && timezoneBcd == 0) {
      return null;
    }
    return ZonedDateTime.of(year + 2000, month, day, hour, minute, second, 0, zoneOffset);
  }

  private String readAddress(int addressLength, int addressType) {
    // NOTE: the max number of octets on an address is 12 octets
    //       this means that an address field need only be 12 octets long
    //       what about for 7-bit?  This would be 13 chars at 12 octets?
    if (addressLength > 0) {
      // length is a semi-octet count
      int addressDataOctetLength = addressLength / 2 + ((addressLength % 2 == 1) ? 1 : 0);
      // extract data and increment position
      byte[] addressData = new byte[addressDataOctetLength];
      System.arraycopy(this.pduByteArray, this.position, addressData, 0, addressDataOctetLength);
      this.position = this.position + addressDataOctetLength;
      switch (PduUtils.extractAddressType(addressType)) {
        case PduUtils.ADDRESS_TYPE_ALPHANUMERIC:
          // extract and process encoded bytes
          byte[] uncompressed = PduUtils.encodedSeptetsToUnencodedSeptets(addressData);
          int septets = addressLength * 4 / 7;
          byte[] choppedAddressData = new byte[septets];
          System.arraycopy(uncompressed, 0, choppedAddressData, 0, septets);
          return PduUtils.unencodedSeptetsToString(choppedAddressData);
        default:
          // process BCD style data any other
          return PduUtils.readBCDNumbers(addressLength, addressData);
      }
    }
    return null;
  }

//  private byte[] readUserDataHeader() {
//  }

  private int readValidityPeriodInt() {
    // this will convert the VP to #MINUTES
    int validity = readByte();
    int minutes = 0;
    if ((validity > 0) && (validity <= 143)) {
      // groups of 5 min
      minutes = (validity + 1) * 5;
    } else if ((validity > 143) && (validity <= 167)) {
      // groups of 30 min + 12 hrs
      minutes = (12 * 60) + (validity - 143) * 30;
    } else if ((validity > 167) && (validity <= 196)) {
      // days
      minutes = (validity - 166) * 24 * 60;
    } else if ((validity > 197) && (validity <= 255)) {
      // weeks
      minutes = (validity - 192) * 7 * 24 * 60;
    }
    return minutes;
  }

  public Pdu parsePdu(final String rawPdu) {
    // encode pdu to byte[] for easier processing
    this.pduByteArray = PduUtils.pduToBytes(rawPdu);
    this.position = 0;
    // parse start and determine what type of pdu it is
    Pdu pdu = parseStart();
    pdu.setRawPdu(rawPdu);
    // parse depending on the pdu type
    switch (pdu.getTpMti()) {
      case PduUtils.TP_MTI_SMS_DELIVER:
        parseSmsDeliverMessage((SmsDeliveryPdu) pdu);
        break;
      case PduUtils.TP_MTI_SMS_SUBMIT:
        parseSmsSubmitMessage((SmsSubmitPdu) pdu);
        break;
      case PduUtils.TP_MTI_SMS_STATUS_REPORT:
        parseSmsStatusReportMessage((SmsStatusReportPdu) pdu);
        break;
    }
    return pdu;
  }

  private Pdu parseStart() {
    // SMSC info
    // length
    // address type
    // smsc data
    final int addressLength = readByte();
    final Pdu pdu;
    if (addressLength > 0) {
      final int addressType = readByte();
      final String smscAddress = readAddress((addressLength - 1) * 2, addressType);
      // first octet - determine how to parse and how to store
      final int firstOctet = readByte();
      pdu = PduFactory.createPdu(firstOctet);
      // generic methods
      pdu.setSmscAddressType(addressType);
      pdu.setSmscAddress(smscAddress);
      pdu.setSmscInfoLength(addressLength);
    } else {
      // first octet - determine how to parse and how to store
      final int firstOctet = readByte();
      pdu = PduFactory.createPdu(firstOctet);
    }
    return pdu;
  }

  private void parseUserData(Pdu pdu) {
    // ud length
    // NOTE: - the udLength value is just stored, it is not used to determine the length
    //         of the remaining data (it may be a septet length not an octet length)
    //       - parser just assumes that the remaining PDU data is for the User-Data field
    int udLength = readByte();
    pdu.setUDLength(udLength);

    // user data
    // NOTE: UD Data does not contain the length octet
    int udOctetLength = this.pduByteArray.length - this.position;
    byte[] udData = new byte[udOctetLength];
    System.arraycopy(this.pduByteArray, this.position, udData, 0, udOctetLength);
    // save the UD data
    pdu.setUDData(udData);
    // user data header (if present)
    // position is still at the start of the UD
    if (pdu.hasTpUdhi()) {
      // udh length
      int udhLength = readByte();
      // udh data (iterate till udh is consumed)
      // iei id
      // iei data length
      // iei data
      int endUdh = this.position + udhLength;
      while (this.position < endUdh) {
        int iei = readByte();
        int iedl = readByte();
        if (iedl > endUdh - this.position) {
          System.out.println(
              "Information element 0x" + PduUtils.byteToPdu(iei) + " invalid, need " + iedl + " bytes, but " + (endUdh - this.position) + " available");
          break;
        }
        byte[] ieData = new byte[iedl];
        System.arraycopy(this.pduByteArray, this.position, ieData, 0, iedl);
        InformationElement ie = InformationElementFactory.createInformationElement(iei, ieData);
        pdu.addInformationElement(ie);
        this.position += iedl;
        if (this.position > endUdh) {
          // at the end, position after adding should be exactly at endUdh
          throw new RuntimeException("UDH is shorter than expected endUdh=" + endUdh + ", position=" + this.position);
        }
      }
    }
  }

  private void parseSmsDeliverMessage(SmsDeliveryPdu pdu) {
    // originator address info
    // address length
    // type of address
    // address data
    int addressLength = readByte();
    int addressType = readByte();
    String originatorAddress = readAddress(addressLength, addressType);
    pdu.setAddressType(addressType);
    if (originatorAddress != null) {
      pdu.setAddress(new MsIsdn(originatorAddress, addressType));
    }
    // protocol id
    int protocolId = readByte();
    pdu.setProtocolIdentifier(protocolId);
    // data coding scheme
    int dcs = readByte();
    pdu.setDataCodingScheme(dcs);
    // service centre timestamp
    ZonedDateTime timestamp = readTimeStamp();
    pdu.setServiceCentreTimestamp(timestamp);
    // user data
    parseUserData(pdu);
  }

  private void parseSmsStatusReportMessage(final SmsStatusReportPdu pdu) {
    // message reference
    int messageReference = readByte();
    pdu.setMessageReference(messageReference);
    // destination address info
    int addressLength = readByte();
    int addressType = readByte();
    String destinationAddress = readAddress(addressLength, addressType);
    pdu.setAddressType(addressType);
    pdu.setAddress(new MsIsdn(destinationAddress, addressType));
    // timestamp
    ZonedDateTime timestamp = readTimeStamp();
    pdu.setTimestamp(timestamp);
    // discharge time(timestamp)
    ZonedDateTime dischargeTime = readTimeStamp();
    pdu.setDischargeTime(dischargeTime);
    // status
    int status = readByte();
    pdu.setStatus(status);
  }

  // NOTE: the following is just for validation of the PduGenerator
  //       - there is no normal scenario where this is used
  private void parseSmsSubmitMessage(SmsSubmitPdu pdu) {
    // message reference
    int messageReference = readByte();
    pdu.setMessageReference(messageReference);
    // destination address info
    int addressLength = readByte();
    int addressType = readByte();
    String destinationAddress = readAddress(addressLength, addressType);
    pdu.setAddressType(addressType);
    pdu.setAddress(new MsIsdn(destinationAddress, addressType));
    // protocol id
    int protocolId = readByte();
    pdu.setProtocolIdentifier(protocolId);
    // data coding scheme
    int dcs = readByte();
    pdu.setDataCodingScheme(dcs);
    // validity period
    switch (pdu.getTpVpf()) {
      case PduUtils.TP_VPF_NONE:
        break;
      case PduUtils.TP_VPF_INTEGER:
        int validityInt = readValidityPeriodInt();
        pdu.setValidityPeriod(validityInt / 60); // pdu assumes hours
        break;
      case PduUtils.TP_VPF_TIMESTAMP:
        ZonedDateTime validityDate = readTimeStamp();
        pdu.setValidityTimestamp(validityDate);
        break;
    }
    parseUserData(pdu);
  }
}
