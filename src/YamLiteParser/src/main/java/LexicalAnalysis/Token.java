package LexicalAnalysis;

public class Token {
    public final int tag;

    public Token(int t) {
        tag = t;
    }
    public String getValue() {return null;}
    public String toString() {
        return "" + (char)tag;
    }

}
