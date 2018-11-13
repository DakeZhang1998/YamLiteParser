package LexicalAnalysis;

// This class is used for identifiers, true and false.
public class Word extends Token {
    public String lexeme = "";

    public Word (String s, int tag) {
        super(tag);
        lexeme = s;
    }
    public String getValue() {
        return lexeme;
    }
    public String toString() {
        return "Word: "+lexeme;
    }
    public static final Word
        True = new Word("true", Tag.TRUE),
        False = new Word("false", Tag.FALSE);
}
