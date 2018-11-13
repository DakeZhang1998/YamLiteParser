package SyntaxAnalysis;
import LexicalAnalysis.*;
import java.util.*;

public class YAMLitePARSER {
    // Contains the input token string
    public ArrayList<Token> input = new ArrayList<Token>();
    // Points to the current token
    public int inputCur = -1;
    // The root of the syntax tree
    public ASTNode root;
    // To get the tokens from LexicalAnalysis
    public void addInput(Token t) {
        input.add(t);
    }

    // Entrance of this class
    public ASTNode startParse() {
        Token lastToken = new Token(Tag.EOF);
        input.add(lastToken);
        root = parseProgram(0);
        if (root == null) {
            return root;
        }
        return root;
    }

    public String parseToJson() {
        if (root == null)
            return null;
        return root.toString();

    }    // Program -> Key Program | Array Program
    public ASTNode parseProgram(int curLevel) {
        ASTNode root = new ASTNode(ASTNodeTag.NONTERMINITOR, "Program", 0);
        Token token;
        do {
            if (inputCur == input.size()-1)
                break;
            token = input.get(++inputCur);
            if (token.tag == Tag.EOF)
                break;
            inputCur--;
            ASTNode node = parseKey(curLevel);
            if (node == null) {
                node = parseArray(curLevel+1);
                if (node == null) {
                    return null;
                }
                root.addChild(node);
            }
            else {
                root.addChild(node);
            }
        } while (true);
        return root;
    }

    // Array -> word : ArrayItem
    public ASTNode parseArray(int curLevel) {
        int move = 0;
        inputCur++;
        move++;
        Token curToken = input.get(inputCur);
        ASTNode array = new ASTNode(ASTNodeTag.NONTERMINITOR, "Array", curLevel);
        if (curToken.tag == Tag.ID) {
            inputCur++;
            move++;
            array.addChild(new ASTNode(ASTNodeTag.ID, curToken.getValue(), curLevel+1));
            curToken = input.get(inputCur);
            if (curToken.tag == Tag.COLON) {
                array.addChild(new ASTNode(ASTNodeTag.COLON, ":", curLevel+1));
                array = parseArrayItem(array, curLevel);
                if (array == null) {
                    rollback(move);
                    return null;
                }
                else if (array.children.size() == 2) {
                    rollback(move);
                    return null;
                }
                return array;
            }
            else {
                rollback(move);
                return null;
            }
        }
        else {
            rollback(move);
            return null;
        }
    }

    // ArrayItem -> (IND) - rightValue ArrayItem
    //           -> (IND) - subKey ArrayItem
    //           -> (IND) - subArray ArrayItem
    // (IND) number depends on the variable curLevel.
    public ASTNode parseArrayItem(ASTNode node, int curLevel) {
        int tempCurLevel = curLevel;
        int move = 0;
        Token curToken;
        if (curLevel == 1) {
            inputCur++;
            move++;
            curToken = input.get(inputCur);
            if (curToken.tag == Tag.ID || curToken.tag == Tag.EOF) {
                return node;
            }
            inputCur--;
            move--;
            for (int i = 0; i < tempCurLevel; i++) {
                inputCur++;
                move++;
                curToken = input.get(inputCur);
                if (curToken.tag != Tag.INDENT) {
                    rollback(move);
                    return null;
                }
            }
        }
        else {
            for (int i = 0; i < tempCurLevel; i++) {
                inputCur++;
                move++;
                curToken = input.get(inputCur);
                if (curToken.tag != Tag.INDENT) {
                    rollback(move);
                    return node;
                }
            }
        }
        inputCur++;
        move++;
        curToken = input.get(inputCur);
        if (curToken.tag == Tag.HYPEN) {
            inputCur++;
            move++;
            ASTNode rightValue = parseRightValue(curLevel+1);
            if (rightValue != null) {
                node.addChild(rightValue);
                node = parseArrayItem(node, curLevel);
                return node;
            }
            inputCur--;
            move--;
            ASTNode subKey = parseKey(curLevel+1);
            if (subKey != null && subKey.children.size() > 2) {
                node.addChild(subKey);
                node = parseArrayItem(node, curLevel);
                return node;
            }
            ASTNode subArray = parseSubArray(curLevel+1);
            if (subArray != null && subArray.children.size() > 0) {
                node.addChild(subArray);
                node = parseArrayItem(node, curLevel);
                return node;
            }
            rollback(move);
            return null;
        }
        rollback(move);
        return null;
    }

    // subArray -> ArrayItem
    public ASTNode parseSubArray(int curLevel) {
        ASTNode subArray = new ASTNode(ASTNodeTag.NONTERMINITOR, "subArray", curLevel);
        int tempCurLevel = curLevel;
        int move = 0;
        Token curToken;
        for (int i = 0; i < tempCurLevel; i++) {
            inputCur++;
            move++;
            curToken = input.get(inputCur);
            if (curToken.tag != Tag.INDENT) {
                rollback(move);
                return null;
            }
        }
        inputCur++;
        move++;
        curToken = input.get(inputCur);
        if (curToken.tag == Tag.HYPEN) {
            inputCur++;
            move++;
            ASTNode rightValue = parseRightValue(curLevel+1);
            if (rightValue != null) {
                subArray.addChild(rightValue);
                subArray = parseArrayItem(subArray, curLevel);
                return subArray;
            }
            inputCur--;
            move--;
            ASTNode subKey = parseKey(curLevel+1);
            if (subKey != null && subKey.children.size() > 2) {
                subArray.addChild(subKey);
                subArray = parseArrayItem(subArray, curLevel);
                return subArray;
            }
            ASTNode subSubArray = parseSubArray(curLevel+1);
            if (subSubArray != null) {
                subArray.addChild(subSubArray);
                subArray = parseArrayItem(subArray, curLevel);
                return  subArray;
            }
            rollback(move);
            return null;
        }
        else {
            rollback(move);
            return null;
        }
    }

//    // subKey -> (IND) - Key
//    // (IND) number depends on the variable curLevel.
//    public ASTNode parseSubKey(int curLevel) {
//        int tempCurLevel = curLevel;
//        int move = 0;
//        Token curToken;
//        for (int i = 0; i < tempCurLevel; i++) {
//            inputCur++;
//            move++;
//            curToken = input.get(inputCur);
//            if (curToken.tag != Tag.INDENT) {
//                rollback(move)
//                return null;
//            }
//        }
//        inputCur++;
//        move++;
//        curToken = input.get(inputCur);
//        ASTNode subKey = new ASTNode(ASTNodeTag.NONTERMINITOR, "subKey", curLevel);
//        if (curToken.tag == Tag.ID) {
//            inputCur++;
//            move++;
//            subKey.addChild(new ASTNode(ASTNodeTag.ID, curToken.getValue(), curLevel));
//            curToken = input.get(inputCur);
//            if (curToken.tag == Tag.COLON) {
//                inputCur++;
//                move++;
//                subKey.addChild(new ASTNode(ASTNodeTag.COLON, ":", curLevel));
//                ASTNode node = parseRightValue(curLevel);
//                if (node == null) {
//                    rollback(move);
//                    return null;
//                }
//                subKey.addChild(node);
//                return subKey;
//            }
//            else {
//                rollback(move);
//                return null;
//            }
//        }
//        else {
//            rollback(move);
//            return null;
//        }
//    }

    // Key -> word : rightValue
    public ASTNode parseKey(int curLevel) {
        int tempCurLevel = curLevel;
        int move = 0;
        Token curToken;
        for (int i = 0; i < tempCurLevel; i++) {
            inputCur++;
            move++;
            curToken = input.get(inputCur);
            if (curToken.tag != Tag.INDENT) {
                rollback(move);
                return null;
            }
        }
        inputCur++;
        move++;
        curToken = input.get(inputCur);
        ASTNode key = new ASTNode(ASTNodeTag.NONTERMINITOR, "Key", curLevel);
        if (curToken.tag == Tag.ID) {
            inputCur++;
            move++;
            key.addChild(new ASTNode(ASTNodeTag.ID, curToken.getValue(), curLevel));
            curToken = input.get(inputCur);
            if (curToken.tag == Tag.COLON) {
                inputCur++;
                move++;
                key.addChild(new ASTNode(ASTNodeTag.COLON, ":", curLevel));
                ASTNode node = parseRightValue(curLevel);
                if (node == null) {
                    inputCur--;
                    move--;
                    node = parseKey(curLevel+1);
                    if (node == null) {
                        node = parseSubArray(curLevel+1);
                        if (node == null) {
                            rollback(move);
                            return null;
                        }
                        key.addChild(node);
                        return key;
                    }
                    key.addChild(node);
                    return key;
                }
                key.addChild(node);
                return key;
            }
            else {
                rollback(move);
                return null;
            }
        }
        else {
            rollback(move);
            return null;
        }
    }

    // rightValue -> string | true | false | num | real
    public ASTNode parseRightValue(int curLevel) {
        Token curToken = input.get(inputCur);
        if (curToken.tag == Tag.STRING)
            return new ASTNode(ASTNodeTag.STRING, curToken.getValue(), curLevel);
        else if (curToken.tag == Tag.TRUE)
            return new ASTNode(ASTNodeTag.TRUE, curToken.getValue(), curLevel);
        else if (curToken.tag == Tag.FALSE)
            return new ASTNode(ASTNodeTag.FALSE, curToken.getValue(), curLevel);
        else if (curToken.tag == Tag.NUM)
            return new ASTNode(ASTNodeTag.NUM, curToken.getValue(), curLevel);
        else if (curToken.tag == Tag.REAL)
            return new ASTNode(ASTNodeTag.REAL, curToken.getValue(), curLevel);
        else
            return null;
    }

    // When fails, the recursive sub-function will roll back the inputCur.
    public void rollback(int i) {
        inputCur = inputCur - i;
    }
}
