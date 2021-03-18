package com.github.pmoerenhout.pduutils.jsmpp;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.SubmitSm;

import com.github.pmoerenhout.pduutils.Util;
import lombok.extern.slf4j.Slf4j;
import net.freeutils.charset.gsm.CCGSMCharset;

@Slf4j
public class SmppUtil {

  private static final CCGSMCharset GSM_CHARSET = new CCGSMCharset();
  private static final Charset ASCII_CHARSET = StandardCharsets.US_ASCII;
  private static final Charset LATIN_CHARSET = StandardCharsets.ISO_8859_1;
  private static final Charset LATIN_CYRILLIC_CHARSET = Charset.forName("ISO-8859-5");
  private static final Charset LATIN_HEBREW_CHARSET = Charset.forName("ISO-8859-8");
  private static final Charset JIS_X0208_1990_CHARSET = Charset.forName("ISO-2022-JP");
  private static final Charset JIS_X0212_1990_CHARSET = Charset.forName("JIS_X0212-1990");
  private static final Charset UCS2_CHARSET = StandardCharsets.UTF_16BE;

  public static String decode(final byte dcs, final byte[] ud, final Charset defaultCharset) {
    final Alphabet alphabet = Alphabet.parseDataCoding(dcs);
    // log.trace("Alphabet for DCS {} is {}", Util.bytesToHexString(dcs), alphabet);
    switch (alphabet) {
      case ALPHA_DEFAULT:
        return new String(ud, defaultCharset);
      case ALPHA_IA5:
        return new String(ud, ASCII_CHARSET);
      case ALPHA_LATIN1:
        return new String(ud, LATIN_CHARSET);
      case ALPHA_JIS:
        return new String(ud, JIS_X0208_1990_CHARSET);
      case ALPHA_CYRILLIC:
        return new String(ud, LATIN_CYRILLIC_CHARSET);
      case ALPHA_LATIN_HEBREW:
        return new String(ud, LATIN_HEBREW_CHARSET);
      case ALPHA_UCS2:
        return new String(ud, UCS2_CHARSET);
      case ALPHA_JIS_X_0212_1990:
        return new String(ud, JIS_X0212_1990_CHARSET);
    }
    throw new IllegalArgumentException("Unknown alphabet for DCS " + Util.bytesToHexString(dcs) + ": " + alphabet);
  }

  /*
   ** Decode with SMSC default alphabet GSM charset
   */
  public static String decode(final byte dcs, final byte esmClass, final byte[] data) {
    return decode(dcs, esmClass, data, GSM_CHARSET);
  }

  public static String decode(final byte dcs, final byte esmClass, final byte[] data, final Charset defaultCharset) {
    final boolean udhi = GSMSpecificFeature.UDHI.containedIn(esmClass);
    final byte[] ud = (udhi ? ArrayUtils.subarray(data, data[0] + 1, data.length) : data);
    return decode(dcs, ud, defaultCharset);
  }

  public static boolean isBinary(final byte dcs) {
    final Alphabet alphabet = Alphabet.parseDataCoding(dcs);
    return alphabet == Alphabet.ALPHA_8_BIT;
  }

  public static boolean isBinaryMessageClass2(final byte dcs) {
    final MessageClass messageClass = MessageClass.parseDataCoding(dcs);
    return isBinary(dcs) && MessageClass.CLASS2 == messageClass;
  }

  public static byte[] getMessageWithoutUdh(final boolean udhi, final byte[] message) {
    if (udhi) {
      final int udhl = message[0];
      final byte[] ud = new byte[message.length - 1 - udhl];
      System.arraycopy(message, 1 + udhl, ud, 0, ud.length);
      return ud;
    }
    return message;
  }

  public static boolean isBinaryMessageClass2CommandPacket(final byte dcs, final boolean udhi, final byte[] message) {
    final MessageClass messageClass = MessageClass.parseDataCoding(dcs);
    // However, in the case of a Response Packet originating from the UICC, due to the inability of the UICC to indicate to a
    // ME that the UDHI bit should be set, the Response Packet SMS will not have the UDHI bit set, and the Sending Entity
    // shall treat the Response Packet as if the UDHI bit was set.
    if (isBinary(dcs) && MessageClass.CLASS2 == messageClass && message.length >= 3) {
      // TODO: Parse the user data header including concatenation etc
      final int iel = message[0];
      final byte iei = message[1];
      final byte ied = message[2];
      if (iei == (byte) 0x70) {
        log.debug("Message is a command packet (UDHI:{} DCS:{} MESSAGE:{})", udhi, Util.bytesToHexString(dcs), Util.bytesToHexString(message));
        return true;
      }
    }
    log.debug("Message is NOT a command packet (UDHI:{} DCS:{} MESSAGE:{})", udhi, Util.bytesToHexString(dcs), Util.bytesToHexString(message));
    return false;
  }

  /*
   ** Encode with default GSM charset
   */
  public static byte[] encode(final byte dcs, final String message) {
    return encode(dcs, message, GSM_CHARSET);
  }

  public static byte[] encode(final byte dcs, final String message, final Charset charset) {
    final Alphabet alphabet = Alphabet.parseDataCoding(dcs);
    switch (alphabet) {
      case ALPHA_DEFAULT:
        return message.getBytes(charset);
      case ALPHA_IA5:
        return message.getBytes(ASCII_CHARSET);
      case ALPHA_LATIN1:
        return message.getBytes(LATIN_CHARSET);
      case ALPHA_JIS:
        return message.getBytes(JIS_X0208_1990_CHARSET);
      case ALPHA_CYRILLIC:
        return message.getBytes(LATIN_CYRILLIC_CHARSET);
      case ALPHA_LATIN_HEBREW:
        return message.getBytes(LATIN_HEBREW_CHARSET);
      case ALPHA_UCS2:
        return message.getBytes(UCS2_CHARSET);
      case ALPHA_JIS_X_0212_1990:
        return message.getBytes(JIS_X0212_1990_CHARSET);
    }
    throw new IllegalArgumentException("Unknown alphabet for DCS " + Util.bytesToHexString(dcs) + ": " + alphabet);
  }

  public static byte[] encodeGsm(final String message) {
    return message.getBytes(GSM_CHARSET);
  }

  public static byte[] encodeUnicode(final String message) {
    return message.getBytes(UCS2_CHARSET);
  }

  public static boolean isEncodable(final String text, final Charset charset) {
    return charset.newEncoder().canEncode(text);
  }

  public static DataCoding getDataCodingScheme(final String text, final Charset defaultCharset) {
    if (isEncodable(text, defaultCharset)) {
      return GeneralDataCoding.DEFAULT;
    }
    if (isEncodable(text, LATIN_CHARSET)) {
      return new GeneralDataCoding(Alphabet.ALPHA_LATIN1);
    }
    if (isEncodable(text, LATIN_CYRILLIC_CHARSET)) {
      return new GeneralDataCoding(Alphabet.ALPHA_CYRILLIC);
    }
    if (isEncodable(text, LATIN_HEBREW_CHARSET)) {
      return new GeneralDataCoding(Alphabet.ALPHA_LATIN_HEBREW);
    }
    if (isEncodable(text, JIS_X0208_1990_CHARSET)) {
      return new GeneralDataCoding(Alphabet.ALPHA_JIS);
    }
    if (isEncodable(text, JIS_X0212_1990_CHARSET)) {
      return new GeneralDataCoding(Alphabet.ALPHA_JIS_X_0212_1990);
    }
    if (isEncodable(text, UCS2_CHARSET)) {
      return new GeneralDataCoding(Alphabet.ALPHA_UCS2);
    }
    throw new IllegalArgumentException("An DCS cannot be found for text");
  }

  public static void logOptionalParameters(final OptionalParameter[] optionalParameters, final String prefix) {
    Arrays.stream(optionalParameters).forEach(o -> {
          log.debug("{} optional {} {}: {}", prefix, o.getClass().getName(), o.tag, Util.bytesToHexString(o.serialize()));
          if (o instanceof OptionalParameter.Null) {
            log.info("{} optional {}: null", prefix, o.tag);
          } else if (o instanceof OptionalParameter.Byte) {
            log.info("{} optional {}: {}", prefix, o.tag, Util.bytesToHexString(((OptionalParameter.Byte) o).getValue()));
          } else if (o instanceof OptionalParameter.Short) {
            log.info("{} optional {}: {}", prefix, o.tag, ((OptionalParameter.Short) o).getValue());
          } else if (o instanceof OptionalParameter.Int) {
            log.info("{} optional {}: {}", prefix, o.tag, ((OptionalParameter.Int) o).getValue());
          } else if (o instanceof OptionalParameter.COctetString) {
            log.info("{} optional {}: {}", prefix, o.tag, ((OptionalParameter.COctetString) o).getValueAsString());
          } else if (o instanceof OptionalParameter.OctetString) {
            log.info("{} optional {}: {}", prefix, o.tag, Util.bytesToHexString(((OptionalParameter.OctetString) o).getValue()));
          }
        }
    );
  }

  public static byte[] getShortMessageOrPayload(final SubmitSm submitSm) throws InvalidMessagePayloadException {
    final OptionalParameter.OctetString messagePayload = submitSm.getOptionalParameter(OptionalParameter.Message_payload.class);
    return getShortMessageOrPayload(submitSm.getShortMessage(), messagePayload);
  }

  public static byte[] getShortMessageOrPayload(final DeliverSm deliverSm) throws InvalidMessagePayloadException {
    final OptionalParameter.OctetString messagePayload = deliverSm.getOptionalParameter(OptionalParameter.Message_payload.class);
    return getShortMessageOrPayload(deliverSm.getShortMessage(), messagePayload);
  }

  public static byte[] getShortMessageOrPayload(final byte[] shortMessage, final OptionalParameter.OctetString messagePayload)
      throws InvalidMessagePayloadException {
    if (shortMessage.length != 0 && messagePayload != null) {
      log.warn("The message contains shortMessage and also messagePayload, which is not allowed!");
      throw new InvalidMessagePayloadException("The message contains shortMessage and also messagePayload");
    }
    return shortMessage.length != 0 ? shortMessage : (messagePayload != null ? messagePayload.getValue() : new byte[]{});
  }
}
