package it.bz.beacon.api.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EddystoneUrl {

    private EddystoneUrl() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<Byte, String> URI_SCHEMES = new HashMap<Byte, String>() {
        private static final long serialVersionUID = 1L;
        {
            put((byte) 0, "http://www.");
            put((byte) 1, "https://www.");
            put((byte) 2, "http://");
            put((byte) 3, "https://");
            put((byte) 4, "urn:uuid:");    // RFC 2141 and RFC 4122};
        }
    };

    private static final Map<Byte, String> URL_CODES = new HashMap<Byte, String>() {
        private static final long serialVersionUID = 1L;
            {
            put((byte) 0, ".com/");
            put((byte) 1, ".org/");
            put((byte) 2, ".edu/");
            put((byte) 3, ".net/");
            put((byte) 4, ".info/");
            put((byte) 5, ".biz/");
            put((byte) 6, ".gov/");
            put((byte) 7, ".com");
            put((byte) 8, ".org");
            put((byte) 9, ".edu");
            put((byte) 10, ".net");
            put((byte) 11, ".info");
            put((byte) 12, ".biz");
            put((byte) 13, ".gov");
        }
    };

    public static final String NO_URI = "";

    public static String encodeUri(String uri) {
        if (uri == null || uri.length() == 0) {
            return "";
        }
        ByteBuffer bb = ByteBuffer.allocate(uri.length());
        bb.order(ByteOrder.BIG_ENDIAN);
        int position = 0;

        Byte schemeCode = encodeUriScheme(uri);
        if (schemeCode == null) {
            return null;
        }
        String scheme = URI_SCHEMES.get(schemeCode);
        bb.put(schemeCode);
        position += scheme.length();

        return encodeUrl(uri, position, bb);
    }

    private static String encodeUrl(String url, int position, ByteBuffer bb) {
        while (position < url.length()) {
            byte expansion = findLongestExpansion(url, position);
            if (expansion >= 0) {
                bb.put(expansion);
                position += URL_CODES.get(expansion).length();
            } else {
                bb.put((byte) url.charAt(position++));
            }
        }
        return bytesToHex(byteBufferToArray(bb));
    }

    private static Byte encodeUriScheme(String uri) {
        String lowerCaseUri = uri.toLowerCase(Locale.ENGLISH);
        for (int i = 0; i < URI_SCHEMES.size(); i++) {
            // get the key and value.
            String value = URI_SCHEMES.get((byte)i);
            int key = URI_SCHEMES.entrySet().stream().filter(byteStringEntry -> value.equals(byteStringEntry.getValue())).map(Map.Entry::getKey).findFirst().get();
            if (lowerCaseUri.startsWith(value)) {
                return (byte) key;
            }
        }
        return null;
    }

    private static byte[] byteBufferToArray(ByteBuffer bb) {
        byte[] bytes = new byte[bb.position()];
        bb.rewind();
        bb.get(bytes, 0, bytes.length);
        return bytes;
    }

    private static byte findLongestExpansion(String uriString, int pos) {
        byte expansion = -1;
        int expansionLength = 0;
        for (int i = 0; i < URL_CODES.size(); i++) {
            // get the key and value.
            String value = URL_CODES.get((byte)i);
            int key = URL_CODES.entrySet().stream().filter(byteStringEntry -> value.equals(byteStringEntry.getValue())).map(Map.Entry::getKey).findFirst().get();
            if (value.length() > expansionLength && uriString.startsWith(value, pos)) {
                expansion = (byte) key;
                expansionLength = value.length();
            }
        }
        return expansion;
    }

    public static String decodeUri(String hexString) {
        if (hexString == null) {
            return null;
        }
        return decodeUri(hexStringToByteArray(hexString), 0);
    }

    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String decodeUri(byte[] serviceData, int offset) {
        if (serviceData.length == offset) {
            return NO_URI;
        }
        StringBuilder uriBuilder = new StringBuilder();
        if (offset < serviceData.length) {
            byte b = serviceData[offset++];
            String scheme = URI_SCHEMES.get(b);
            if (scheme != null) {
                uriBuilder.append(scheme);
                return decodeUrl(serviceData, offset, uriBuilder);
            }
        }
        return null;
    }

    private static String decodeUrl(byte[] serviceData, int offset, StringBuilder urlBuilder) {
        while (offset < serviceData.length) {
            byte b = serviceData[offset++];
            String code = URL_CODES.get(b);
            if (code != null) {
                urlBuilder.append(code);
            } else {
                urlBuilder.append((char) b);
            }
        }
        return urlBuilder.toString();
    }
}
