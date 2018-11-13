package LexicalAnalysis;

// This class is used for double numbers.
public class Real extends Token {
    public final String value;

    public Real (double d) {
        super(Tag.REAL);
        value = String.valueOf(d);
    }
    public Real (String d) {
        super(Tag.REAL);
        value = d;
    }
    public String getValue() {
        return ""+value;
    }
    public String toString() {
        return "Real: " + value;
    }

}
