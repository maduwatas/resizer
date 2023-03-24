package es.xproject.resizer.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class StringHelper {

    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String numbers = "0123456789";
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static final DecimalFormat NODECIMAL = new DecimalFormat("#");
    public static final DecimalFormat ONEDECIMAL = new DecimalFormat("#.#");
    public static final DecimalFormat TWODECIMAL = new DecimalFormat("#.##");
    public static final DecimalFormat THREEDECIMAL = new DecimalFormat("#.###");
    public static final DecimalFormat SIXDECIMAL = new DecimalFormat("#.######");
    public static final DecimalFormat PERCENT = new DecimalFormat("#.#%");
    public static final DecimalFormat PERCENT_ND = new DecimalFormat("#%");

    static {
        DecimalFormatSymbols fs = new DecimalFormatSymbols(Locale.US);
        fs.setDecimalSeparator('.');
        ONEDECIMAL.setDecimalFormatSymbols(fs);

        TWODECIMAL.setMinimumFractionDigits(2);
        TWODECIMAL.setDecimalFormatSymbols(fs);

        SIXDECIMAL.setDecimalFormatSymbols(fs);
        SIXDECIMAL.setMinimumFractionDigits(6);

        THREEDECIMAL.setMinimumFractionDigits(3);
        THREEDECIMAL.setDecimalFormatSymbols(fs);
    }

    public static boolean isNumeric(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {

        }

        return false;
    }

    public static Integer getValueAsInt(String value, Integer defaultValue) {
        if (!isNumeric(value)) {
            return defaultValue;
        }
        return getValueAsInt(value);
    }

    public static Integer getValueAsInt(String value) {

        try {
            if (!isNumeric(value)) {
                return null;
            }
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            try {
                Double d = Double.valueOf(value);
                return Math.round(d.floatValue());
            } catch (Exception e1) {

            }

        }

        return 0;
    }

    public static Float getValueAsFloat(String value) {

        try {
            return Float.parseFloat(value.replace(",", "."));
        } catch (Exception e) {

        }
        return 0F;
    }

    public static Double randomNumber(int size) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < size; i++) {
            buf.append(numbers.charAt(Double.valueOf(Math.random() * (numbers.length() - 1)).intValue()));
        }

        return Double.valueOf(buf.toString());
    }

    public static Double randomNumber(int size, double maxValue) {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < size; i++) {
            buf.append(numbers.charAt(Double.valueOf(Math.random() * (numbers.length() - 1)).intValue()));
        }

        double d = Double.parseDouble(buf.toString());

        if (d > maxValue) {
            d -= maxValue;
        }

        return d;
    }

    public static String randomString(int size) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < size; i++) {
            buf.append(characters.charAt(Double.valueOf(Math.random() * (characters.length() - 1)).intValue()));
        }

        return buf.toString();
    }

    public static String parseTimeSecs(long time) {

        long h = time / 3600;
        long m = (time % 3600) / 60;
        long s = time % 60;

        if (h > 0) {
            return String.format("%d h %d m %d s", h, m, s);
        } else if (m > 0) {
            return String.format("%d m %d s", m, s);
        } else {
            return String.format("%d s", s);
        }
    }

    public static String parseTimeMin(long time) {

        long h = time / 3600;
        long m = (time % 3600) / 60;
        long s = time % 60;

        if (h > 0) {
            return String.format("%d h %d m", h, m);
        } else if (m > 0) {
            return String.format("%d m", m);
        } else {
            return String.format("%d s", s);
        }
    }

    public static byte encodeUInt16(int value) {
        String hex = Integer.toHexString(value);
        return Byte.parseByte(hex, 16);
    }

    public static int decodeUInt16(final byte[] data, int offset) {
        int l = 0;
        l |= (data[offset + 0] & 0xff) << 0;
        l |= (data[offset + 1] & 0xff) << 8;
        return l;
    }

    public static int decodeSInt16(final byte[] data, int offset) {
        short l = 0;
        l |= (data[offset + 0] & 0xff) << 0;
        l |= (data[offset + 1] & 0xff) << 8;
        return l;
    }

    public static long decodeUInt32(final byte[] data, int offset) {
        long l = 0;
        l |= (data[offset + 0] & 0xff) << 0;
        l |= (data[offset + 1] & 0xff) << 8;
        l |= (data[offset + 2] & 0xff) << 16;
        l |= (data[offset + 3] & 0xff) << 24;
        return l;
    }

    public static String toHex(final byte[] data) {
        if (data == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("%02X", data[i] & 0xff));
            if ((i + 1) < data.length) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String noDecimal(double value) {
        return NODECIMAL.format(Math.round(value));
    }

    public static String oneDecimal(double value) {
        return ONEDECIMAL.format(value);
    }

    public static String twoDecimal(double value) {

        return TWODECIMAL.format(value);
    }

    public static String sixDecimals(double value) {
        return SIXDECIMAL.format(value);
    }

    public static String threeDecimal(double value) {
        return THREEDECIMAL.format(value);
    }

    public static String percent(double value) {
        return PERCENT.format(value);
    }

    public static String percentNoDecimal(double value) {
        return PERCENT_ND.format(value);
    }

    public static String percentNoDecimalZero(double value) {
        if (value >= 0.001 && value < 0.01) {
            return PERCENT.format(value);
        } else {
            return PERCENT_ND.format(value);
        }

    }

    public static String removeAccents(String s) {
        try {

            s = Normalizer.normalize(s, Normalizer.Form.NFD);
            s = s.replaceAll("[^\\p{ASCII}]", "");
            s = s.replaceAll("[^a-zA-Z0-9.]", " ");

        } catch (Exception e) {

        }
        return s;
    }

    public static String substring(String s, int sizeMax) {
        if (s != null && s.length() > sizeMax) {
            return s.substring(0, sizeMax);
        }
        return s;
    }

    public static String twoDecimal(float value) {
        return TWODECIMAL.format(value);
    }

    public static String formatTimeFromSeconds(float timestamp) {

        if (timestamp == 0) {
            return "";
        }

        if (timestamp < 60) {
            return String.format("%.0f''", timestamp);
        } else if (timestamp > 3600) {

            int minutes = (int) (timestamp % 3600) / 60;
            int hours = (int) timestamp / 3600;

            if (minutes > 0) {

                int seconds = (int) timestamp % 60;
                if (seconds > 0) {
                    return String.format("%d h %d' %d''", hours, minutes, seconds);
                } else {
                    return String.format("%d h %d'", hours, minutes);
                }
            } else {
                return String.format("%d h", hours);
            }
        } else {
            int minutes = (int) (timestamp / 60);
            int seconds = (int) timestamp % 60;

            if (seconds > 0) {
                return String.format("%d' %d''", minutes, seconds);
            } else {
                return String.format("%d'", minutes);
            }

        }
    }

    public static String formatChronoFromSeconds(long timestamp) {
        if (timestamp < 60) {
            return String.format("00:00:%02d", timestamp);
        } else if (timestamp > 3600) {
            long minutes = (timestamp % 3600) / 60;
            long hours = timestamp / 3600;
            long seconds = timestamp % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);

        } else {
            long minutes = timestamp / 60;
            long seconds = timestamp % 60;

            return String.format("00:%02d:%02d", minutes, seconds);

        }
    }

    public static String formatRunningPace(Float avgSpeed) {
        float decimal = 60 / avgSpeed;
        int minutes = Float.valueOf(decimal).intValue();
        int seconds = Float.valueOf(60f * (decimal - minutes)).intValue();
        return String.format("%d:%d min/km", minutes, seconds);
    }

    public static String formatSwimmingPace(Float avgSpeed) {
        return String.format("%.0f m/min", (1000f * avgSpeed / 60f));
    }

    public static boolean startWith(String string, String value) {
        return string != null && string.startsWith(value);
    }

    public static Long toLongSafe(String string) {
        try {
            return Long.parseLong(string);
        } catch (Exception ignored) {

        }
        return 0L;
    }

    public static boolean containsSafe(String name, String filter) {

        if (isEmpty(name)) {
            return false;
        }

        if (isEmpty(filter)) {
            return true;
        }

        return name != null && name.toUpperCase().contains(filter.toUpperCase());
    }

    public static String formatFileErrors(ArrayList<String> fileNameError) {
        StringBuilder result = new StringBuilder("\n");
        for (String fileName : fileNameError) {
            result.append("\n");
            result.append(fileName);
        }
        return result.toString();
    }

    public static String toUpper(String units) {
        return units != null ? units.toUpperCase() : "";
    }

    public static boolean isEmpty(String target) {
        return target != null && !target.isEmpty();
    }
}
