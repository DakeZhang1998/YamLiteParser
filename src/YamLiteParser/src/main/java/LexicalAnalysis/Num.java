package LexicalAnalysis;

// This class is used for integer tokens.
public class Num extends Token {
    public final int value;
    public Num (int v) {
        super(Tag.NUM);
        value = v;
    }
    public String getValue() {
        return ""+value;
    }
    public String toString() {
        return "Num: " + value;
    }
}
