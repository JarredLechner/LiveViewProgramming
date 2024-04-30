import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Literate {
   
    static String tangle(String path) {
        StringBuilder finalCode = new StringBuilder();
        List<String> text = new ArrayList<>();
        try{ text = Files.readAllLines(Path.of(path));
        }catch(Exception e) {
            System.err.println("Error reading file");
            System.exit(1);
        }
        boolean skip = false;
        for(String line : text) {
            if (Syntax.isText(line)) {
                skip = true;
                
            }
            
            if (Syntax.isTextEnd(line)) {
                skip = false;
                continue;
            }
            if(Syntax.isLabel(line)){
                skip = true;
            }
            if(Syntax.isEndLabel(line)){
                skip = false;
                continue;
            }
            if (skip) {
                continue;
            }
            if(Syntax.isLabelDeclaration(line)){
                finalCode.append(replaceLabel(Syntax.useLabel(line),Path.of(path)));
                continue;
            }
            finalCode.append(line).append("  \n");
        }
        return finalCode.toString();

    }
    static String replaceLabel( String label, Path path){
        StringBuilder finalCode = new StringBuilder();
        String content = extractMacro(label, path);
        
        String[] lines = content.split("\\n");
        boolean isText = false;
        for (String line : lines) {
           
           
            
            if (Syntax.isLabelDeclaration(line)) {
                line = Syntax.useLabel(line);
                finalCode.append(replaceLabel( line,path));
            } else if (!Syntax.isLabel(line) && !Syntax.isLabelDeclaration(line)&& !Syntax.isEndLabel(line)) {
                finalCode.append(line).append("  \n");
            }
        }
        return finalCode.toString();
    }

    static String extractMacro(String label, Path path){
        StringBuilder macro = new StringBuilder();
        List<String> text = new ArrayList<>();
        try{ text = Files.readAllLines(path);
        }catch(Exception e) {
            System.err.println("Error reading file");
            System.exit(1);
        }
        boolean skip = true;
        for(String line :text){
            if(Syntax.isLabel(line) && line.contains(label)){
                skip = false;
            }
            if(Syntax.isEndLabel(line) && line.contains(label)){
                return macro.toString();
            }
            if(skip){
                continue;
            }
            macro.append(line+ "  \n");

        }
        return macro.toString();
    }
   

    static String weave(String path) {
        List<String> text = new ArrayList<>();
        try{ text = Files.readAllLines(Path.of(path));
        }catch(Exception e) {
            System.err.println("Error reading file");
            System.exit(1);
        }
       
        boolean skip = false;
        boolean inCode = false;
        StringBuilder finalText = new StringBuilder();
        
        for(String line : text) {
            System.out.println(line);
            if(skip){
                continue;
            }
            if(Syntax.isText(line)){
                continue;
            }
            if(Syntax.isTextEnd(line)){
                continue;
            }
            if(Syntax.isLabel(line) && !Syntax.isLabelDeclaration(line)){
                finalText.append("# ").append(line+ "  \n");
                
               finalText.append("```  \n ");
                continue;
            }
            finalText.append(line).append("   \n ");

        }
        return finalText.toString();
    }
        
        
    
    
   

    
}

class Syntax{
    static boolean isLabel(String line) {
        return line.startsWith("Label:");
    }
    static boolean isEndLabel(String line) {
        return line.startsWith("EndLabel:");
    }
    static boolean isLabelDeclaration(String line) {
        return line.startsWith("UseLabel:");
    }
    static boolean isText(String line){
      return line.contains("//<text>");
    }

    static boolean isTextEnd(String line){
      return line.contains("//</text>");
    }
    static boolean isExample(String line){
      return line.contains("//<example>");
    }
    static boolean isExampleEnd(String line){
      return line.contains("//</example>");
    }
    static String useLabel(String label){
        return label.replace("UseLabel:","Label:");
    }
    static String endLabel(String label){
        return label.replace("Label:","EndLabel:");
    }

}
