package org.codehaus.mojo.servicedocgen;

/**
 * Class with static methods for escaping strings in velocity templates.
 *
 * @author jguenther
 */
public class EscapeHelper {

    /**
     * @param jsonString the json string to escape, may be {@code null}
     * @return the string escaped for use between the quotes of a JSON string value, empty for {@code null}
     */
    public static String escapeJson(String jsonString) {
        if (jsonString == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(jsonString.length() + 16);
        for (int i = 0; i < jsonString.length(); i++) {
            char c = jsonString.charAt(i);
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
}
