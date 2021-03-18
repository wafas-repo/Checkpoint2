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
  static public void main(String argv[]) {    
    /* Start the parser */
    int n = 0;
    String st = "-a";

    for (int i = 0; i < argv.length; i++) {
        if(argv[i].equals("-a")){
          SHOW_TREE = true;
        } else if (argv[i].equals("-s")){
          SHOW_TABLE = true;
        }
    }

    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Absyn result = (Absyn)(p.parse().value);      
      if (SHOW_TREE && result != null) {
         System.out.println("The abstract syntax tree is:");
         ShowTreeVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0); 
      }
      if (SHOW_TABLE) {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        result.accept(analyzer, 0);
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}


