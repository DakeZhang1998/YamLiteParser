package LexicalAnalysis;

public class TokenException extends Exception {
    private int colNum = 0;
    private int lineNum = 0;
    private String errorMessage = "";

    public TokenException() {
        super();
    }

    public TokenException(int lineNum, int colNum, String message) {
        this.colNum = colNum;
        this.lineNum = lineNum;
        this.errorMessage = message;
    }

    public String toString() {
        return "line " + lineNum + ", position " + colNum + ": " + errorMessage;
    }
}
