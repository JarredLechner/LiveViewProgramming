import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Literate {
    static boolean isText = false;
    static String tangle(String path) {
        return tangle(path, "//<<program>>");
    }
    static String tangle(String path, String label){
        StringBuilder finalCode = new StringBuilder();
        String content = Text.cutOut(path, label);
        String[] lines = content.split("\\n");
        for( String line : lines) {
            boolean wasText = isText;
            isText(line);
            if(isText || wasText) continue;
            if(isLabelDeclaration(line)) {
                line = line.replace("<<<", "<<").replace(">>>", ">>");
                finalCode.append(tangle(path,line));
            }
            else if(!isLabel(line)&& !isLabelDeclaration(line)) {
                finalCode.append(line).append("\n");
            }
        }
        return finalCode.toString();
    }
        
        
    
    static boolean isLabel(String line) {
        return line.startsWith("//<<") && line.endsWith(">>");
    }
    static boolean isLabelDeclaration(String line) {
        return line.startsWith("//<<<") && line.endsWith(">>>");
    }
    static void isText(String line){
        if(line.startsWith("//<text>")){
            isText = !isText;
        }
    }

    
}
