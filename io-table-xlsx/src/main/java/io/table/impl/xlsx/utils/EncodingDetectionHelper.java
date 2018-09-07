/**
 *
 */
package io.table.impl.xlsx.utils;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Helper class for encoding detection.
 *
 * @author charles
 */
public final class EncodingDetectionHelper {

    /** The default encoding charset. */
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * Hidden constructor.
     */
    private EncodingDetectionHelper() {
        super();
    }

    public static String doRawStream(final byte[] data) throws IOException {
        final Charset bomEnc = EncodingDetectionHelper.getBOMEncoding(data);
        final Charset xmlGuessEnc = EncodingDetectionHelper.getXMLGuessEncoding(data);
        return EncodingDetectionHelper.calculateString(bomEnc, xmlGuessEnc, data);
    }

    // InputStream is passed for XmlReaderException creation only
    private static String calculateString(final Charset bomEnc, final Charset xmlGuessEnc, final byte[] data)
            throws IOException {
        final Charset encoding;
        final int offset;
        if (bomEnc == null) {
            offset = 0;
            if (xmlGuessEnc == null) {
                if (EncodingDetectionHelper.DEFAULT_CHARSET == null) {
                    encoding = StandardCharsets.UTF_8;
                } else {
                    encoding = EncodingDetectionHelper.DEFAULT_CHARSET;
                }
            } else if (xmlGuessEnc == StandardCharsets.UTF_8 || xmlGuessEnc == StandardCharsets.UTF_16BE
                    || xmlGuessEnc == StandardCharsets.UTF_16LE) {
                encoding = xmlGuessEnc;
            } else {
                throw new StreamCorruptedException();
            }
        } else if (bomEnc == StandardCharsets.UTF_8) {
            if (xmlGuessEnc != null && xmlGuessEnc != StandardCharsets.UTF_8) {
                throw new StreamCorruptedException();
            }
            encoding = StandardCharsets.UTF_8;
            offset = 3;
        } else if (bomEnc == StandardCharsets.UTF_16BE || bomEnc == StandardCharsets.UTF_16LE) {
            if (xmlGuessEnc != null && !xmlGuessEnc.equals(bomEnc)) {
                throw new StreamCorruptedException();
            }
            encoding = bomEnc;
            offset = 2;
        } else {
            throw new StreamCorruptedException();
        }
        return new String(data, offset, data.length - offset, encoding);
    }

    // returns the BOM in the stream, NULL if not present,
    // if there was BOM the in the stream it is consumed
    private static Charset getBOMEncoding(final byte[] data) throws IOException {
        Charset encoding = null;

        if (data[0] == 0xFE && data[1] == 0xFF) {
            encoding = StandardCharsets.UTF_16BE;
        } else if (data[0] == 0xFF && data[1] == 0xFE) {
            encoding = StandardCharsets.UTF_16LE;
        } else if (data[0] == 0xEF && data[1] == 0xBB && data[2] == 0xBF) {
            encoding = StandardCharsets.UTF_8;
        }
        return encoding;
    }

    // returns the best guess for the encoding by looking the first bytes of the
    // stream, '<?xm'
    private static Charset getXMLGuessEncoding(final byte[] data) throws IOException {
        Charset encoding = null;

        final int data0 = data[0] & 0xFF;
        final int data1 = data[1] & 0xFF;
        final int data2 = data[2] & 0xFF;
        final int data3 = data[3] & 0xFF;
        if (data0 == 0x00 && data1 == 0x3C && data2 == 0x00 && data3 == 0x3F) {
            encoding = StandardCharsets.UTF_16BE;
        } else if (data0 == 0x3C && data1 == 0x00 && data2 == 0x3F && data3 == 0x00) {
            encoding = StandardCharsets.UTF_16LE;
        } else if (data0 == 0x3C && data1 == 0x3F && data2 == 0x78 && data3 == 0x6D) {
            encoding = StandardCharsets.UTF_8;
        } else if (data0 == 0x4C && data1 == 0x6F && data2 == 0xA7 && data3 == 0x94) {
            encoding = StandardCharsets.ISO_8859_1;
        }
        return encoding;
    }
}
