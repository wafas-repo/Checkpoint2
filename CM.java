/*
  Created by: Fei Song
  File Name: Main.java
  To Build: 
  After the scanner, tiny.flex, and the parser, tiny.cup, have been created.
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny

  where gcd.tiny is an test input file for the tiny language.
*/
   
import java.io.*;
import absyn.*;
   
class CM {
  public static boolean SHOW_TREE = false;
  public static boolean SHOW_TABLE = false;
  public static final String EXT_ABS_STRING = ".abs";
  public static final String EXT_SYM_STRING = ".sym";

  static public void main(String argv[]) {    
    /* Start the parser */
    int n = 0;
    String st = "-a";
    String file = "";

    for (int i = 0; i < argv.length; i++) {
        if(argv[i].equals("-a")){
          SHOW_TREE = true;
        } else if (argv[i].equals("-s")){
          SHOW_TABLE = true;
        }
        else if (argv[i].endsWith(".cm")) {
          file = argv[i];
        }
    }

    try {
      parser p = new parser(new Lexer(new FileReader(file)));
      Absyn result = (Absyn)(p.parse().value);
      if(file != null && file.contains(".")) 
        file = file.substring(0, file.lastIndexOf('.'));
      if (SHOW_TREE && result != null) {
        String output_file = file + EXT_ABS_STRING; 
        PrintStream o = new PrintStream(new File(output_file));
        PrintStream console = System.out;  
        System.setOut(o);  
        System.out.println("The abstract syntax tree is:");
        ShowTreeVisitor visitor = new ShowTreeVisitor();
        result.accept(visitor, 0);
      }
      if (SHOW_TABLE) {
        String output_file = file + EXT_SYM_STRING; 
        PrintStream o = new PrintStream(new File(output_file));
        PrintStream console = System.out;  
        System.setOut(o);  
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        result.accept(analyzer, 0);
      }


    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}


