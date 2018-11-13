import java.io.*;
import java.util.ArrayList;

import LexicalAnalysis.*;
import SyntaxAnalysis.*;
import LexicalAnalysis.*;

public class Find {
    public String argument;
    public ASTNode root;
    public ASTNode curNode;
    public String[] ids;
    public char curChar;
    public int cur;
    public int index;
    public String id;

    public Find() {
        argument = "";
        root = null;
        curNode = null;
        ids = null;
        curChar = '0';
        index = -1;
        id = "";
        cur = 0;
    }
    // This operation firstly divide the parameter accordingly,
    // and then call the recursive function.
    public String findIt(ASTNode node, String s) {
        String result = "null";
        root = node;
        curNode = node;
        argument = s + "$";
        StringBuffer idBuffer = new StringBuffer();
        curChar = argument.charAt(cur);
        do {
            idBuffer.append(curChar);
            cur++;
            curChar = argument.charAt(cur);
        } while (curChar != '.' && curChar != '[' && curChar != '$');
        if (curChar == '[') {
            StringBuffer tempBuffer = new StringBuffer();
            curChar = argument.charAt(++cur);
            do {
                tempBuffer.append(curChar);
                cur++;
                curChar = argument.charAt(cur);
            } while (curChar != ']');
            cur++;
            index = Integer.valueOf(tempBuffer.toString());
            curNode = searchArray(curNode, idBuffer.toString());
            if (curNode == null)
                return result;
            if (index+2 < curNode.children.get(2).children.size()) {
                curNode = curNode.children.get(2).children.get(index);
            }
            cur--;
        }
        if (curChar == '$') {
            curNode = searchKey(curNode, idBuffer.toString());
            if (curNode == null)
                return result;
        }
        while (cur < argument.length()-1) {
            if (cur == '$')
                break;
            idBuffer.setLength(0);
            do {
                idBuffer.append(curChar);
                cur++;
                curChar = argument.charAt(cur);
            } while (curChar != '.' && curChar != '[' && curChar != '$');
            if (curChar == '[') {
                StringBuffer tempBuffer = new StringBuffer();
                curChar = argument.charAt(++cur);
                do {
                    tempBuffer.append(curChar);
                    cur++;
                    curChar = argument.charAt(cur);
                } while (curChar != ']');
                cur++;
                index = Integer.valueOf(tempBuffer.toString());
                curNode = searchArray(curNode, idBuffer.toString());
                if (curNode == null)
                    return result;
                if (index+2 < curNode.children.get(2).children.size()) {
                    curNode = curNode.children.get(2).children.get(index);
                }
                cur--;
            }
            if (curChar == '$') {
                if (idBuffer.toString().equals(curNode.children.get(0).value))
                    result = curNode.children.get(3).toString();
                return result;
            }
        }

        result = curNode.toString();
        return result;



//        int curStr = 0;
//        if (argument.contains("[") && argument.contains("]")) {
//            if (argument.contains(".")) {
//                int curStr1 = 1;
//                String keyName = argument.substring(argument.indexOf('.')+1, argument.length());
//                argument = argument.substring(0, argument.indexOf('.'));
//                ids = argument.split("\\[");
//                for (int i = 0; i < ids.length; i++) {
//                    if (ids[i].contains("]"))
//                        ids[i] = ids[i].substring(0, ids[i].indexOf(']'));
//                }
//
//            }
//            else {
//                int curStr2 = 1;
//                ids = argument.split("\\[");
//                for (int i = 0; i < ids.length; i++) {
//                    if (ids[i].contains("]"))
//                        ids[i] = ids[i].substring(0, ids[i].indexOf(']'));
//                }
//                for (String st: ids) {
//                    System.out.println(st);
//                }
//            }
//        }
//        else {
//            for (ASTNode tempNode: root.children)
//                if (tempNode.value == "Key" && tempNode.tag == ASTNodeTag.NONTERMINITOR)
//                    if (argument.equals(tempNode.children.get(0).value))
//                        result = tempNode.children.get(2).value;
//        }
//        return result;



//        if (argument.contains("."))
//            ids = s.split("\\.");
//        else {
//            ids = new String[1];
//            ids[0] = argument;
//        }
//        ASTNode curNode = root;
//        if (ids[0].contains("[") && ids[0].contains("]")) {
//            String temp = ids[curStr];
//            String name = temp.substring(0, temp.indexOf('['));
//            int index = Integer.valueOf(temp.substring(temp.indexOf('[')+1, temp.indexOf(']')));
//            for (ASTNode tempNode: root.children)
//                if (tempNode.value == "Array" && tempNode.tag == ASTNodeTag.NONTERMINITOR)
//                    if (name.equals(tempNode.children.get(0).value))
//                        curNode = tempNode;
//            if (curNode == null)
//                return result;
//            curStr++;
//            if (curStr < ids.length) {
//                String temp2 = ids[curStr];
//                if (ids[0].contains("[") && ids[0].contains("]"))
//                    result = findInSubArray(curNode, temp2, curStr, ids.length);
//                else
//                    result = findKeyInSubArray(curNode, temp2, curStr, ids.length);
//            }
//        }
//        else {
//            for (ASTNode tempNode: root.children)
//                if (tempNode.value == "Key" && tempNode.tag == ASTNodeTag.NONTERMINITOR)
//                    if (ids[0].equals(tempNode.children.get(0).value))
//                        result = tempNode.children.get(2).value;
//        }
//        return result;
    }
    public ASTNode searchKey(ASTNode node, String keyName) {
        for (ASTNode tempNode: node.children)
            if (tempNode.value == "Key" && tempNode.tag == ASTNodeTag.NONTERMINITOR)
                if (keyName.equals(tempNode.children.get(0).value))
                    return tempNode;
        return null;
    }

    public ASTNode searchArray(ASTNode node, String arrayName) {
        for (ASTNode tempNode: node.children)
            if (tempNode.value.equals("Key") && tempNode.tag == ASTNodeTag.NONTERMINITOR)
                if (arrayName.equals(tempNode.children.get(0).value))
                    return tempNode;
        return null;
    }


    public String findInSubArray(ASTNode node, String argument, int curStr, int length) {
        if (curStr == length) {
            return null;
        }
        return null;
    }

    public String findKeyInSubArray(ASTNode node, String argument, int curStr, int length) {
        return null;
    }
}