package com.github.pmoerenhout.pduutils.gsm0340.ie;

import com.github.pmoerenhout.pduutils.gsm0340.PduUtils;

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
public class InformationElement {

  // https://en.wikipedia.org/wiki/User_Data_Header

  protected byte identifier;
  protected byte[] data;

  InformationElement() {
  }

  protected InformationElement(byte identifier) {
    this.identifier = identifier;
  }

  // iei
  // iel (implicit length of data)
  // ied (raw ie data)
  protected InformationElement(byte identifier, byte[] data) {
    initialize(identifier, data);
  }

  // for outgoing messages
  void initialize(byte identifier, byte[] data) {
    this.identifier = identifier;
    this.data = data;
  }

  // IEI
  public int getIdentifier() {
    return (this.identifier & 0xff);
  }

  // IEL
  public int getLength() {
    return this.data.length;
  }

  public byte[] getData() {
    return this.data;
  }

  public void setData(final byte[] data) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName() + "[");
    sb.append(PduUtils.byteToPdu(this.identifier));
    sb.append(", ");
    sb.append(PduUtils.byteToPdu(this.data.length));
    sb.append(", ");
    sb.append(PduUtils.bytesToPdu(this.data));
    sb.append("]");
    return sb.toString();
  }
}
