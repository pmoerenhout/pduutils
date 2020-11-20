package com.github.pmoerenhout.pduutils.gsm0340.ie;

// PduUtils Library - A Java library for generating GSM 03.40 Protocol Data Units (PDUs)
//
//Copyright (C) 2008, Ateneo Java Wireless Competency Center/Blueblade Technologies, Philippines.
//PduUtils is distributed under the terms of the Apache License version 2.0
//
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

import static com.github.pmoerenhout.pduutils.gsm0340.ie.Concatenation16InformationElement.IEI_CONCATENATION_16BIT;
import static com.github.pmoerenhout.pduutils.gsm0340.ie.Concatenation8InformationElement.IEI_CONCATENATION_8BIT;
import static com.github.pmoerenhout.pduutils.gsm0340.ie.PortInformationElement.IEI_PORT_16BIT;

public class InformationElementFactory {

  private static byte defaultConcatenationIdentifier = IEI_CONCATENATION_8BIT;
  private static byte defaultConcatenationLength = Concatenation8InformationElement.IEL_CONCATENATION_8BIT;

  // used  to determine what InformationElement to use based on bytes from a UDH
  public static InformationElement createInformationElement(final int id, final byte[] data) {
    byte iei = (byte) (id & 0xff);
    switch (iei) {
      case IEI_CONCATENATION_8BIT:
        return new Concatenation8InformationElement(iei, data);
      case IEI_CONCATENATION_16BIT:
        return new Concatenation16InformationElement(iei, data);
      case IEI_PORT_16BIT:
        return new PortInformationElement(iei, data);
      default:
        return new InformationElement(iei, data);
    }
  }

  public static Concatenation8InformationElement generateConcatenation8InformationElement(int mpRefNo, int mpMaxNo, int partNo) {
        return new Concatenation8InformationElement(mpRefNo, mpMaxNo, partNo);
  }

  public static Concatenation16InformationElement generateConcatenation16InformationElement(int mpRefNo, int mpMaxNo, int partNo) {
    return new Concatenation16InformationElement(mpRefNo, mpMaxNo, partNo);
  }

  public static ConcatenationInformationElement generateConcatenationInformationElement(int mpRefNo, int mpMaxNo, int partNo) {
    switch (getDefaultConcatenationIdentifier()){
      case IEI_CONCATENATION_8BIT:
        return new Concatenation8InformationElement(mpRefNo, mpMaxNo, partNo);
      case IEI_CONCATENATION_16BIT:
        return new Concatenation16InformationElement(mpRefNo, mpMaxNo, partNo);
      default:
        throw new IllegalStateException("Default concatenation identifier is invalid");
    }
  }

  public static PortInformationElement generatePortInformationElement(final int destPort, final int srcPort) {
    final PortInformationElement portInfo = new PortInformationElement(IEI_PORT_16BIT, destPort, srcPort);
    return portInfo;
  }

  public static int getDefaultConcatenationLength() {
    return defaultConcatenationLength;
  }

  public static int getDefaultConcatenationIdentifier() {
    return defaultConcatenationIdentifier;
  }

  public static void setDefaultConcatenationType(final byte identifier) {
    switch (identifier) {
      case IEI_CONCATENATION_8BIT:
        defaultConcatenationIdentifier = IEI_CONCATENATION_8BIT;
        defaultConcatenationLength = Concatenation8InformationElement.IEL_CONCATENATION_8BIT;
        break;
      case IEI_CONCATENATION_16BIT:
        defaultConcatenationIdentifier = IEI_CONCATENATION_16BIT;
        defaultConcatenationLength = Concatenation16InformationElement.IEL_CONCATENATION_16BIT;
        break;
      default:
        throw new RuntimeException("Invalid concatenation ie identifier");
    }
  }
}
