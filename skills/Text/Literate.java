import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Literate {
    static boolean isText = false;
    static String tangle(String path) {
        return tangle(path, "//<<program>>");
    }
    static String tangle(String path, String label){
        StringBuilder finalCode = new StringBuilder();
        String content = Text.cutOut(path, label);
        String[] lines = content.split("\\n");
        boolean isText = false;
        boolean wasText = false;
        for( String line : lines) {
             wasText = isText;
            isText = isText(line);
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

    static String weave(String path) {
        List<String> text = new ArrayList<>();
        try{ text = Files.readAllLines(Path.of(path));
        }catch(Exception e) {
            System.err.println("Error reading file");
            System.exit(1);
        }
       
        boolean skip = true;
        StringBuilder finalText = new StringBuilder();
        
        for(String line : text) {
           
            if(!skip &&isLabelDeclaration(line)) {
               finalText.append(tangle(path, line)).append("\\n");
                continue;
            }
            if(isText(line)) {
               skip = false;
                continue;
            }
            if(isTextEnd(line)){
                skip = true;
                continue;
            }
            
            if(isExample(line)) {
                finalText.append("```java").append("\\n");
                skip = !skip;
                continue;
            }
            if(isExampleEnd(line)) {
                finalText.append("```").append("\\n");
                skip = !skip;
                continue;
            }
            if(!skip && !isLabel(line)) {
                finalText.append(line).append("\\n");
            }
            
        }
        return finalText.toString();
    }
        
        
    
    static boolean isLabel(String line) {
        return line.startsWith("//<<") && line.endsWith(">>");
    }
    static boolean isLabelDeclaration(String line) {
        return line.startsWith("//<<<") && line.endsWith(">>>");
    }
    static boolean isText(String line){
      return line.startsWith("//<text>");
    }

    static boolean isTextEnd(String line){
      return line.startsWith("//</text>");
    }
    static boolean isExample(String line){
      return line.startsWith("//<example>");
    }
    static boolean isExampleEnd(String line){
      return line.startsWith("//</example>");
    }

    
}
