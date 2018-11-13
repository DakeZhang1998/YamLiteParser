import java.io.*;
import LexicalAnalysis.*;
import SyntaxAnalysis.ASTNode;
import SyntaxAnalysis.YAMLitePARSER;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class YAMLite {
    public static void main(String[] args) throws IOException {
        // Read the YAMLite file into a String.
        String str="";
        File file=new File(args[args.length-1]);
        try {
            FileInputStream in=new FileInputStream(file);
            int size=in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            str=new String(buffer,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error occurs in File reader.");
        }
        YAMLiteLEXER lexer = new YAMLiteLEXER(str);
        YAMLitePARSER parser = new YAMLitePARSER();
        Token tempToken = null;
        do {
            try {
                tempToken = lexer.scan();
            }
            catch (TokenException e) {
                System.out.println(e);
                return;
            }
            if (tempToken != null)
                parser.addInput(tempToken);
        } while (tempToken != null);
        ASTNode root = parser.startParse();
        if (root == null) {
            System.out.println("syntax error");
            return;
        }
        else
            System.out.println("valid");

        if (args[0].equals("-json")) {
            String jsonStr = parser.parseToJson();
            FileWriter writer;
            File resultJson = new File(args[1].substring(0, args[1].indexOf('.'))+".json");
            try {
                resultJson.createNewFile();
                writer = new FileWriter(resultJson);
                writer.write(jsonStr);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Parse successes.");
        }
        else if (args[1].equals("-find")) {
            String s = args[2];
            Find findTool = new Find();
            String result = findTool.findIt(root, s);
            System.out.println(result);
        }
    }
}
