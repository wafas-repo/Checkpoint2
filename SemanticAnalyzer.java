import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

  HashMap<String, ArrayList<NodeType>> table;
  private static String tempParams = "";
  private static String scopeParams = "";
  public SemanticAnalyzer() {

    table = new HashMap<String, ArrayList<NodeType>>();

  } 

  final static int SPACES = 4;

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
  }
  
  ////////////////////////////////// Dont touch
  public void visit( ExpList expList, int level ) {
    while( expList != null ) {
      if (expList.head != null){
        expList.head.accept( this, level );
        expList = expList.tail;
      }  
    } 
  }

  //////////////////////////// Dont touch
  public void visit( IntExp exp, int level ) {

  }

  /////////////////////// Dont touch
  @Override
  public void visit(NilExp exp, int level) {

    
  }

  /////////////////////////// Dont touch
  @Override
  public void visit(NameTy exp, int level) {
  
  }

  // Dont touch
  @Override
  public void visit(CompoundExp exp, int level) {
     VarDecList dec = exp.decs;
     while( dec != null ) {
      if(dec.head != null) {
        dec.head.accept( this, level );
      }
        dec = dec.tail;
     } 
     ExpList ex = exp.exps;
     while( ex != null ) {
        if(ex.head != null) {
          ex.head.accept( this, level );
        }
        ex = ex.tail;
     } 
  }

  ////////////////////// Dont touch
@Override
public void visit(VarDecList exp, int level) {
    while( exp != null ) {

      if (exp.head != null) {
        exp.head.accept( this, level );
        exp = exp.tail;
      }
    } 
}


  public void visit( AssignExp exp, int level ) {
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );
  }

  public void visit( OpExp exp, int level ) {
    exp.left.accept( this, level );
    exp.right.accept( this, level );
  }

  public void visit( VarExp exp, int level ) {
    exp.variable.accept(this, level);

  }

  
  @Override
  public void visit(SimpleVar exp, int level) {

    
  }

  
  @Override
  public void visit(ReturnExp exp, int level) {
    exp.exp.accept(this, level);
  }

  @Override
  public void visit(IndexVar exp, int level) {
    exp.index.accept(this, level);
  }

  @Override
  public void visit(CallExp exp, int level) {
    ExpList ex = exp.args;
    while( ex != null ) {
      ex.head.accept( this, level );
      ex = ex.tail;
    } 
  }

  @Override
  public void visit(WhileExp exp, int level) {
    level++;
    indent(level);
    System.out.println("Entering a new while block: " + level);
    exp.test.accept( this, level );
    exp.body.accept( this, level ); 

    printMap(level, "");
    delete(table.entrySet().iterator(), level);
    indent(level);
    System.out.println("leaving while block: " + level);
  }
  
  public void visit( IfExp exp, int level ) {
    level++;
    indent( level );
    System.out.println("Entering a new if block: " + level);
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    
    printMap(level, "");
    delete(table.entrySet().iterator(), level);
    indent(level);
    System.out.println("Leaving the if block:  " + level);
    
    if (exp.elsepart != null ) {
      indent(level);
      System.out.println("Entering a new else block: " + level);
      exp.elsepart.accept( this, level );
      printMap(level, "");
      delete(table.entrySet().iterator(), level);
      indent(level);
      System.out.println("Leaving the else block: " + level);
    }
       
  }
  
  @Override
  public void visit(FunctionDec exp, int level) {
    NodeType entry = new NodeType(exp.func, exp.result, level);
    level++;
    indent(level);
    System.out.println("Entering the scope for function: " + level);
    //exp.result.accept(this, level);
    VarDecList ex = exp.params;
    //String scopeParams = "";
    tempParams = "";
    while( ex != null ) {
      ex.head.accept( this, level );
      ex = ex.tail;
    }
    scopeParams = tempParams;
    exp.body.accept(this, level);
    if (table.get(exp.func) == null) {
      table.put(exp.func, new ArrayList<NodeType>());
    }
    table.get(exp.func).add(0, entry);
    printMap(level, "");
    delete(table.entrySet().iterator(), level);
    indent(level);
    System.out.println("Leaving the scope for function: " + level);
  
  }

  @Override
  public void visit(SimpleDec exp, int level) {

    insert(exp.name, level, exp.typ);
    tempParams += exp.typ.typ + " ";
    //exp.typ.accept( this, level );
  }

  @Override
  public void visit(ArrayDec exp, int level) {
    String name = "";
    name = exp.name + "[";
    if (exp.size != null)
        name = name + exp.size.value + "";
    name = name + "]";

    insert(name, level, exp.typ);
    tempParams += exp.typ.typ + " ";

  }

  @Override
  public void visit(DecList exp, int level) {
    System.out.println("Entering global scope: " + level);
     while( exp != null ) {
      exp.head.accept( this, level );
      exp = exp.tail;
    }
    printMap(level, scopeParams);
    delete(table.entrySet().iterator(), level);
    System.out.println("Leaving global scope: "  + level);
  }

public void printMap(int level, String params) {
  for (Entry<String, ArrayList<NodeType>> ee : table.entrySet()) {
    for (NodeType node : ee.getValue()) {
      if (node.level == level) {
        level++;
        indent(level);
        level--;
        System.out.print(ee.getKey() + ": ");
        if (!params.isEmpty()) {
          String[] tokens = params.split(" ");
          System.out.print("( ");
          for (String s : tokens) {
            if (s.equals("0"))
                System.out.print("int ");
            else if (s.equals("1"))
                System.out.print("void ");
          }
          System.out.print(") -> ");
        }
        for (NodeType nt : ee.getValue()) {
          if(nt.type.typ == 1) {
            System.out.println("void");
          } else if (nt.type.typ == 0) {
            System.out.println("int");
          }
        }
      }
    } 
  }
}

// Need to handle cases for double declarations within scope 
public void insert(String name, int level, NameTy type){

  NodeType entry = new NodeType(name, type, level);
    if (table.get(name) == null) {
      table.put(name, new ArrayList<NodeType>());
    }
    table.get(name).add(0, entry);
}

// use for searching defined/ undefined vars ?
public void lookup() {

}

public void delete(Iterator i, int level) {
    while (i.hasNext()) {
      Entry<String, ArrayList<NodeType>> e = (Entry<String, ArrayList<NodeType>>) i.next();
      for (NodeType node : e.getValue()) {
        if(node.level == level) {
          i.remove();
        }
      }     
    }
}
}

