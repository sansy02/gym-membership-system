package gym.server;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {

    public static Map<String, Object> parseObject(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) {
            return map;
        }

        Pattern p = Pattern.compile(
                "\"([^\"]+)\"\\s*:\\s*" +
                "(\"((?:\\\\.|[^\"\\\\])*)\"|(-?\\d+(?:\\.\\d+)?)|(true|false)|(null))");
        Matcher m = p.matcher(json);
        while (m.find()) {
            String key = m.group(1);
            String strVal = m.group(3);
            String numVal = m.group(4);
            String boolVal = m.group(5);
            String nullVal = m.group(6);

            if (strVal != null) {
                map.put(key, unescape(strVal));
            } else if (numVal != null) {
                map.put(key, Double.parseDouble(numVal));
            } else if (boolVal != null) {
                map.put(key, Boolean.parseBoolean(boolVal));
            } else if (nullVal != null) {
                map.put(key, null);
            }
        }
        return map;
    }

    public static int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object v = map.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        if (v instanceof String) {
            try {
                return Integer.parseInt(((String) v).trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public static String getString(Map<String, Object> map, String key, String defaultValue) {
        Object v = map.get(key);
        if (v == null) {
            return defaultValue;
        }
        return v.toString().trim();
    }

    public static double getDouble(Map<String, Object> map, String key, double defaultValue) {
        Object v = map.get(key);
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        if (v instanceof String) {
            try {
                return Double.parseDouble(((String) v).trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public static String resultSetToJson(ResultSet rs) throws Exception {
        StringBuilder sb = new StringBuilder("[");
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        boolean firstRow = true;

        while (rs.next()) {
            if (!firstRow) {
                sb.append(",");
            }
            firstRow = false;
            sb.append("{");
            for (int i = 1; i <= cols; i++) {
                if (i > 1) {
                    sb.append(",");
                }
                String colName = meta.getColumnLabel(i);
                Object value = rs.getObject(i);
                sb.append("\"").append(colName).append("\":");
                appendJsonValue(sb, value);
            }
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void appendJsonValue(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof Number) {
            sb.append(value.toString());
        } else if (value instanceof Boolean) {
            sb.append(value.toString());
        } else {
            sb.append("\"").append(escape(value.toString())).append("\"");
        }
    }

    public static String buildObject(String... keyValuePairs) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(keyValuePairs[i]).append("\":");
            sb.append(keyValuePairs[i + 1]);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static String unescape(String s) {
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}
