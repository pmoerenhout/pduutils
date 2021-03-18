package com.github.pmoerenhout.pduutils.ie;

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
public abstract class ConcatenationInformationElement extends InformationElement {

  protected ConcatenationInformationElement() {
    super();
  }

  protected ConcatenationInformationElement(byte identifier) {
    super(identifier);
  }

  protected ConcatenationInformationElement(byte identifier, byte[] data) {
    super(identifier, data);
    validate(getMpMaxNo(), getMpSeqNo());
//    if (getIdentifier() == Concatenation8InformationElement.IEI_CONCATENATION_8BIT) {
//      // iei + iel + ref + max + seq
//      if (data.length != Concatenation8InformationElement.IEL_CONCATENATION_8BIT) {
//        throw new RuntimeException("Invalid data length in: " + getClass().getSimpleName());
//      }
//    } else if (getIdentifier() == Concatenation16InformationElement.IEI_CONCATENATION_16BIT) {
//      // iei + iel + ref(2 bytes) + max + seq
//      if (data.length != Concatenation16InformationElement.IEL_CONCATENATION_16BIT) {
//        throw new RuntimeException("Invalid data length in: " + getClass().getSimpleName());
//      }
//    } else {
//      throw new RuntimeException("Invalid identifier in data in: " + getClass().getSimpleName());
//    }
  }

//  ConcatenationInformationElement(int identifier, int mpRefNo, int mpMaxNo, int mpSeqNo) {
//    super();
//    byte[] data;
//    switch (identifier) {
//      case Concatenation8InformationElement.IEI_CONCATENATION_8BIT:
//        data = new byte[Concatenation8InformationElement.IEL_CONCATENATION_8BIT];
//        data[0] = (byte) (mpRefNo & 0xff);
//        data[1] = (byte) (mpMaxNo & 0xff);
//        data[2] = (byte) (mpSeqNo & 0xff);
//        break;
//      case Concatenation16InformationElement.IEI_CONCATENATION_16BIT:
//        data = new byte[Concatenation16InformationElement.IEL_CONCATENATION_16BIT];
//        data[0] = (byte) ((mpRefNo & 0xff00) >>> 8);
//        data[1] = (byte) (mpRefNo & 0xff);
//        data[2] = (byte) (mpMaxNo & 0xff);
//        data[3] = (byte) (mpSeqNo & 0xff);
//        break;
//      default:
//        throw new RuntimeException("Invalid identifier for " + getClass().getSimpleName());
//    }
//    initialize((byte) (identifier & 0xff), data);
//    validate();
//  }

  public int getMpRefNo() {
    throw new RuntimeException("Not implemented");
  }

  public void setMpRefNo(int mpRefNo) {
    throw new RuntimeException("Not implemented");
  }

  public int getMpMaxNo() {
    throw new RuntimeException("Not implemented");
  }

  public void setMpMaxNo(int mpMaxNo) {
    throw new RuntimeException("Not implemented");
  }

  public int getMpSeqNo() {
    throw new RuntimeException("Not implemented");
  }

  public void setMpSeqNo(int mpSeqNo) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString());
    sb.append("[mpRefNo: ");
    sb.append(getMpRefNo());
    sb.append(", mpMaxNo: ");
    sb.append(getMpMaxNo());
    sb.append(", mpSeqNo: ");
    sb.append(getMpSeqNo());
    sb.append("]");
    return sb.toString();
  }

  protected void validate(final int mpMaxNo, final int mpSeqNo) {
    if (mpMaxNo == 0) {
      throw new IllegalArgumentException("mpMaxNo must be > 0");
    }
    if (mpSeqNo == 0) {
      throw new IllegalArgumentException("mpSeqNo must be > 0");
    }
    if (mpSeqNo > mpMaxNo) {
      throw new IllegalArgumentException("mpSeqNo must be <= mpMaxNo");
    }
  }
}
