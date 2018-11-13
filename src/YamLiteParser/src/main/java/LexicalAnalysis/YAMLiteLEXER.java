package LexicalAnalysis;

import java.util.*;

public class YAMLiteLEXER {
    // Current line number
    public int lineNum = 0;
    // Current column number (initialized to be 1 in a new line)
    public int errorCol = 0;
    // Points to the current character
    public int cur = -1;
    // The string to be parsed
    public String str = null;
    // The length of the string
    public int len = 0;
    // Flag: whether this is the first part(INDENT) of the current line.
    public int hasContent = 0;
    // Contains the length of each line for rolling back
    public Stack<Integer> lineLength = new Stack<Integer>();
    Hashtable words = new Hashtable();
    void reserve(Word w) { words.put(w.lexeme, w); }

    // "str" is the String of the file to be analyzed.
    public YAMLiteLEXER(String str) {
        this.str = str;
        this.len = str.length();
        this.errorCol = 0;
        this.cur = -1;
        this.lineNum = 1;
        hasContent = 0;
        reserve(new Word("false", Tag.FALSE));
        reserve(new Word("true",  Tag.TRUE));
    }

    // Main function of Class VAMLiteLEXER
    public Token scan() throws TokenException {
        char curChar = nextChar();
        if (curChar == '$') {
            return null;
        }
        for (;;curChar = nextChar()) {
            if (curChar == '$')
                return null;
            else if (curChar == '#') {
                int tempLineNum = lineNum;
                while (tempLineNum == lineNum) {
                    curChar = nextChar();
                    if (curChar == '$')
                        return null;
                }
                revertChar();
            }
            else if (curChar == '\n' || curChar == '\r') {
                continue;
            }
            else if (curChar == ' ' && nextChar(' ') && hasContent == 0 && !isBlankLine(cur) && !isAnnotationLine(cur)) {
                break;
            }
            else if (curChar == ' ') {
                continue;
            }
            else
                break;
        }
        if (curChar == ' ' && !nextChar(' ') && hasContent == 0 && !isBlankLine(cur)) {
            throw generateTokenException("wrong indent", errorCol+1);
        }
        if (curChar == ' ' && nextChar(' ') && hasContent == 0 && !isBlankLine(cur)) {
            nextChar();
            return new Token(Tag.INDENT);
        }
        switch (curChar) {
            case ':':
                hasContent = 1;
                if (nextChar(' ') || nextChar('\r')) {
                    nextChar();
                    return new Token(Tag.COLON);
                }
                else throw generateTokenException("expected< >(blank)", errorCol+1);
            case '-':
                hasContent = 1;
                if (nextChar(' ') || nextChar('\r')) {
                    nextChar();
                    return new Token(Tag.HYPEN);
                }
                else throw generateTokenException("expected< >(blank)", errorCol+1);
        }
        // String Token.
        if (curChar == '\"') {
            curChar = nextChar();
            StringBuffer b = new StringBuffer();
            do {
                b.append(curChar);
                curChar = nextChar();
            } while (curChar != '\"' && curChar != '\r' &&  curChar != '#' && curChar != '$');
            if (curChar != '\"') {
                if (curChar == '$')
                    throw generateTokenException("expected <\">", errorCol, lineNum-1);
                throw generateTokenException("expected <\">", errorCol);
            }
            String result = b.toString();
            return new Str(result);
        }
        // Number Token.
        if (Character.isDigit(curChar)) {
            hasContent = 1;
            int v = 0;
            do {
                v = 10 * v + Character.digit(curChar, 10);
                curChar = nextChar();
            } while (Character.isDigit(curChar));
            if (curChar != '.') {
                if (curChar == '$')
                    return new Num(v);
                revertChar();
                return new Num(v);
            }
            double x = v;
            double d = 10;
            StringBuffer doubleVal = new StringBuffer(String.valueOf(v));
            doubleVal.append('.');
            for (;;) {
                curChar = nextChar();
                if (!Character.isDigit(curChar))
                    break;
                x = x + Character.digit(curChar, 10) / d;
                d = d * 10;
                doubleVal.append(curChar);
            }
            if (curChar != 'e') {
                revertChar();
                return new Real(doubleVal.toString());
            }
            v = 0;
            curChar = nextChar();
            if (curChar == '-') {
                curChar = nextChar();
                do {
                    v = 10 * v + Character.digit(curChar, 10);
                    curChar = nextChar();
                } while (Character.isDigit(curChar));
                v = v * -1;
            }
            else if (Character.isDigit(curChar)) {
                do {
                    v = 10 * v + Character.digit(curChar, 10);
                    curChar = nextChar();
                } while (Character.isDigit(curChar));
            }
            x = x * Math.pow(10, v);
            return new Real(x);
        }
        // Word Token.
        if (Character.isLetter(curChar)) {
            StringBuffer c = new StringBuffer();
            do {
                c.append(curChar);
                curChar = nextChar();
            } while (Character.isLetterOrDigit(curChar) || curChar == '_');
            char judge = curChar;
            revertChar();
            if (getCurChar() == '_') {
                throw generateTokenException("Invalid identifier(cannot ended with \"_\").", errorCol);
            }
            if (judge != ':' && !c.toString().equals("true") && !c.toString().equals("false"))
                throw generateTokenException("Invalid identifier(only digit/letter/_ are allowed).", errorCol+1);
            if (judge == '$')
                nextChar();
            String s = c.toString();
            Word w = (Word)words.get(s);
            if (w != null) return w;
            w = new Word(s, Tag.ID);
            words.put(s,w);
            return w;
        }
        return null;
    }

    // Get the next character and update the lineNum and colMarks.
    public char nextChar() {
        if (cur >= len-1) {
            lineNum++;
            return '$';
        }
        cur++;
        errorCol++;
        char c = str.charAt(cur);
        if (c == '\n') {
            lineNum++;
            hasContent = 0;
            lineLength.push(errorCol);
            errorCol = 0;
        }
        return c;
    }
    public boolean nextChar(char c) {
        if (cur >= len - 1) {
            return false;
        }
        else {
            char temp = str.charAt(cur+1);
            if (temp == c)
                return true;
            else
                return false;
        }
    }
    // Get the current character.
    public char getCurChar(){
        if (cur >= len) {
            return '$';
        }
        else{
            return str.charAt(cur);
        }
    }
    // Get the last character and update the lineNum and colMarks.
    public int revertChar() {
        if (cur <= 0) {
            return 0;
        }
        int rcur = cur--;
        errorCol--;
        char c = str.charAt(rcur);
        if (c == '\n') {
            lineNum--;
            hasContent = 1;
            errorCol = lineLength.pop();
        }
        return rcur;
    }

    public boolean isAnnotationLine(int current) {
        int index = str.indexOf('#', current);
        if (index == -1) {
            return false;
        }
        String judge = str.substring(current, index);
        boolean isAnnotationLine = true;
        for (int i = 0; i < judge.length(); i++) {
            char tempChar = judge.charAt(i);
            if (tempChar == '\r' || tempChar == ' ')
                continue;
            isAnnotationLine = false;
        }
        return isAnnotationLine;
    }

    public boolean isBlankLine(int current) {
        int index = str.indexOf('\n', current);
        if (index == -1) {
            return false;
        }
        String judge = str.substring(current, index);
        boolean isAnnotationLine = true;
        for (int i = 0; i < judge.length(); i++) {
            char tempChar = judge.charAt(i);
            if (tempChar == '\r' || tempChar == ' ')
                continue;
            isAnnotationLine = false;
        }
        return isAnnotationLine;
    }

    // Generate TokenException.
    public TokenException generateTokenException(String str) {
        return new TokenException(lineNum, errorCol, str);
    }
    public TokenException generateTokenException(String str, int errorColumn) {
        return new TokenException(lineNum, errorColumn, str);
    }
    public TokenException generateTokenException(String str, int errorColumn, int line) {
        return new TokenException(line, errorColumn, str);
    }

}
