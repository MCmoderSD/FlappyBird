package de.MCmoderSD.utilities.json;

@SuppressWarnings("unused")
public class JsonValue {

    // Attribute
    private final String value;

    // Constructor
    public JsonValue(String value) {
        this.value = value;
    }

    // Getters
    public String asText() {
        return value;
    }

    public byte asByte() {
        return Byte.parseByte(value);
    }

    public short asShort() {
        return Short.parseShort(value);
    }

    public int asInt() {
        return Integer.parseInt(value);
    }

    public long asLong() {
        return Long.parseLong(value);
    }

    public float asFloat() {
        return Float.parseFloat(value);
    }

    public double asDouble() {
        return Double.parseDouble(value);
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(value);
    }

    public char[] asChar() {
        return value.toCharArray();
    }

    public char asChar(int index) {
        return value.charAt(index);
    }

    public JsonValue asValue() {
        return this;
    }
}