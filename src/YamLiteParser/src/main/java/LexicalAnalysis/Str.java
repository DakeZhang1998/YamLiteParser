package LexicalAnalysis;

// This class is used for String tokens.
public class Str extends Token {
    public final String value;

    public Str (String s) {
        super(Tag.STRING);
        value = s;
    }
    public String getValue() {
        return ""+value;
    }
    public String toString() {
        return "String: " + value;
    }
}
