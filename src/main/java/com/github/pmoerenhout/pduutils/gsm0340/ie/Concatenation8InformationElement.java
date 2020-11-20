package com.github.pmoerenhout.pduutils.gsm0340.ie;

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

public class Concatenation8InformationElement extends ConcatenationInformationElement {

  // IEI
  public static final byte IEI_CONCATENATION_8BIT = (byte) 0x00;
  public static final int IEL_CONCATENATION_8BIT = 3;

  public Concatenation8InformationElement(final byte identifier, final byte[] data) {
    super(identifier, data);
    if (identifier != IEI_CONCATENATION_8BIT) {
      throw new IllegalArgumentException("Invalid ie identifier: " + getClass().getSimpleName());
    }
    if (data.length != IEL_CONCATENATION_8BIT) {
      throw new IllegalArgumentException("Invalid ie data length in: " + getClass().getSimpleName());
    }
  }

  public Concatenation8InformationElement(int mpRefNo, int mpMaxNo, int mpSeqNo) {
    super(IEI_CONCATENATION_8BIT);
    if (mpRefNo < 0 || mpRefNo > 255) {
      throw new IllegalArgumentException("Invalid ie concatenation reference");
    }
    validate(mpMaxNo, mpSeqNo);
    data = new byte[IEL_CONCATENATION_8BIT];
    data[0] = (byte) (mpRefNo & 0xff);
    data[1] = (byte) (mpMaxNo & 0xff);
    data[2] = (byte) (mpSeqNo & 0xff);
  }

  public int getMpRefNo() {
    // this is 8-bit in 0x00 and 16-bit in 0x08
    return (data[0] & 0xff);
  }

  public void setMpRefNo(int mpRefNo) {
    data[0] = (byte) (mpRefNo & 0xff);
  }

  public int getMpMaxNo() {
    return (data[1] & 0xff);
  }

  public void setMpMaxNo(int mpMaxNo) {
    data[1] = (byte) (mpMaxNo & 0xff);
  }

  public int getMpSeqNo() {
    return (data[2] & 0xff);
  }

  public void setMpSeqNo(int mpSeqNo) {
    data[2] = (byte) (mpSeqNo & 0xff);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString());
    sb.append("[MpRefNo: ");
    sb.append(getMpRefNo());
    sb.append(", MpMaxNo: ");
    sb.append(getMpMaxNo());
    sb.append(", MpSeqNo: ");
    sb.append(getMpSeqNo());
    sb.append("]");
    return sb.toString();
  }
}
