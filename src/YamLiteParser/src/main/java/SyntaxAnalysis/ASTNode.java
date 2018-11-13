package SyntaxAnalysis;
import LexicalAnalysis.*;
import java.util.*;

public class ASTNode {
    public ArrayList<ASTNode> children;
    public int tag;
    public String value;
    public int level;

    public ASTNode(int tag, String value,int level) {
        this.tag = tag;
        this.value = value;
        this.level = level;
        children = new ArrayList<ASTNode>();
    }

    public void addChild(ASTNode ndoe) {
        children.add(ndoe);
    }
    public void printChildren() {
        for (ASTNode node: children) {
            System.out.println(node);
        }
    }

    @Override
    public String toString() {
        String result = "";
        if (value.equals("Program") && tag == ASTNodeTag.NONTERMINITOR) {
            result += "{\n";
            for (ASTNode node : children) {
                for (int i = 0; i < level+1; i++) {
                    result += "  ";
                }
                result += node.toString();
            }
            result = result.substring(0, result.length()-2);
            result += "\n}\n";
        }
        else if (value.equals("Key") && tag == ASTNodeTag.NONTERMINITOR && level == 0) {
            for (ASTNode node : children) {
                result += node.toString();
            }
            result += ",\n";
        }
        else if (value.equals("Key") && tag == ASTNodeTag.NONTERMINITOR) {
            result += "{\n";
            for (int j = 0; j < level+1; j++) {
                result += "  ";
            }
            for (ASTNode node : children) {
                result += node.toString();
            }
            result += "\n";
            for (int j = 0; j < level; j++) {
                result += "  ";
            }
            result += "}";
        }
        else if (value.equals("Array") && tag == ASTNodeTag.NONTERMINITOR) {
            result += children.get(0);
            result += children.get(1);
            result += "[\n";
            for (int i = 2; i < children.size(); i++) {
                ASTNode node = children.get(i);
                for (int j = 0; j < level+1; j++) {
                    result += "  ";
                }
                result += node.toString();
                result = result + ",\n";
            }
            result = result.substring(0, result.length()-2);
            result += "\n";
            for (int j = 0; j < level; j++) {
                result += "  ";
            }
            result += "],\n";
        }
        else if (value.equals("subArray") && tag == ASTNodeTag.NONTERMINITOR) {
            result += "[\n";
            for (ASTNode node: children) {
                for (int j = 0; j < level+1; j++) {
                    result += "  ";
                }
                result += node.toString();
                result = result + ",\n";
            }
            result = result.substring(0, result.length()-2);
            result += "\n";
            for (int i = 0; i < level; i++) {
                result += "  ";
            }
            result += "]";
        }
        else if (tag == ASTNodeTag.COLON)
            result += ": ";
        else if (tag == ASTNodeTag.NUM || tag == ASTNodeTag.REAL || tag == ASTNodeTag.FALSE
                || tag == ASTNodeTag.TRUE || tag == ASTNodeTag.FALSE)
            result += value;
        else if (tag == ASTNodeTag.STRING || tag == ASTNodeTag.ID)
            result = result + "\"" + value + "\"";

        return result;
    }
}
