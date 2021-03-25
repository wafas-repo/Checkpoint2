import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

  HashMap<String, ArrayList<NodeType>> table;
  private static String tempParams = "";
  private static String currFunc = "";
  public static int x;
  ArrayList<String> temp = new ArrayList<String>();
  public SemanticAnalyzer() {

    table = new HashMap<String, ArrayList<NodeType>>();

  } 

  final static int SPACES = 4;

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
  }
  
  public void visit( ExpList expList, int level ) {
    while( expList != null ) {
      if (expList.head != null){
        expList.head.accept( this, level );
        expList = expList.tail;
      }  
    } 
  }

  public void visit( IntExp exp, int level ) {

  }

  @Override
  public void visit(NilExp exp, int level) {

    
  }

  @Override
  public void visit(NameTy exp, int level) {
  
  }

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
    String var = "";
    if (exp.rhs instanceof CallExp) {
      var = ((CallExp) exp.rhs).func;
      if (!isInt(var)){
        System.err.printf("line %d: error: illegal use of function call must be int.\n", exp.rhs.pos+1);
      }

    }

  }

  public void visit( OpExp exp, int level ) {
    exp.left.accept( this, level );
    exp.right.accept( this, level );
    String var = "";
    if (exp.left instanceof CallExp) {
      var = ((CallExp) exp.left).func;
      if (!isInt(var)) 
        System.err.printf("line %d: error: illegal use of function call must be int.\n", exp.left.pos+1);
    } else if (exp.right instanceof CallExp) {
      var = ((CallExp) exp.right).func;
      if (!isInt(var))
        System.err.printf("line %d: error: illegal use of function call must be int.\n", exp.right.pos+1);
    }
  }

  public void visit( VarExp exp, int level ) {
    exp.variable.accept(this, level);

  }

  
  @Override
  public void visit(SimpleVar exp, int level) {
    
    if (findSymbol(exp.name)) {
      if (!isInt(exp.name))
        System.err.printf("line %d: error: invalid data type. Variable must be int -> %s\n", exp.pos+1, exp.name);
    } else {
      System.err.printf("line %d: error: cannot find symbol %s\n", exp.pos+1, exp.name);
    }

  }

  
  @Override
  public void visit(ReturnExp exp, int level) {
   
    if (exp.exp instanceof NilExp) {
      if (isInt(currFunc)) {
        System.err.printf("line %d: error: incompatible types: missing return value\n", exp.pos+1);
      }
    } else {
      exp.exp.accept(this, level);
      if (!isInt(currFunc)) {
        if (!(exp.exp instanceof NilExp)) {
          System.err.printf("line %d: error: incompatible types: unexpected return value\n", exp.pos+1);
        }
      }
    }
  }

  @Override
  public void visit(IndexVar exp, int level) {
    exp.index.accept(this, level);
    String var = "";
    if (exp.index instanceof CallExp) var = ((CallExp) exp.index).func;
    else if (((VarExp) exp.index).variable instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.index).variable).name;
    else if (((VarExp) exp.index).variable instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.index).variable).name;
    if (findSymbol(var)){
      if (!isInt(var)) {
        System.err.printf("line %d: error: invalid data type. index must be int\n", exp.pos+1);
      }  
    } else {
      System.err.printf("line %d: error: cannot find symbol %s\n", exp.pos+1, exp.name);
    }

  }

  @Override
  public void visit(CallExp exp, int level) {
    ExpList ex = exp.args;
    while( ex != null ) {
      ex.head.accept( this, level );
      ex = ex.tail;
    } 

    if (!findSymbol(exp.func)) {
      System.err.printf("line %d: error: function %s is undeclared\n", exp.pos+1, exp.func); 
    } 
    
  }

  @Override
  public void visit(WhileExp exp, int level) {
    level++;
    indent(level);
    System.out.println("Entering a new while block: ");
    exp.test.accept( this, level );
    exp.body.accept( this, level ); 

    printMap(level);
    delete(level);
    indent(level);
    System.out.println("leaving while block: ");
    String var = "";
    if (exp.test instanceof CallExp) {
      var = ((CallExp) exp.test).func;
      if (!isInt(var)) 
        System.err.printf("line %d: error: Test expression %s is not an integer.\n", exp.pos+1, var);
    }



  }
  
  public void visit( IfExp exp, int level ) {
    level++;
    indent( level );
    System.out.println("Entering a new if block: ");
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    String var = "";
    printMap(level);
    delete(level);
    indent(level);
    System.out.println("Leaving the if block:  ");
    
    if (exp.elsepart != null ) {
      indent(level);
      System.out.println("Entering a new else block: ");
      exp.elsepart.accept( this, level );
      printMap(level);
      delete(level);
      indent(level);
      System.out.println("Leaving the else block: ");
    }

    if ((exp.test instanceof VarExp) || (exp.test instanceof CallExp)) {
      if (exp.test instanceof CallExp) var = ((CallExp) exp.test).func;
      else if (((VarExp) exp.test).variable instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.test).variable).name;
      else if (((VarExp) exp.test).variable instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.test).variable).name;
          if (!isInt(var)){
            System.err.printf("line %d: error: Test variable must be int.\n", exp.pos+1);
          }
    }
       
  }
  
  @Override
  public void visit(FunctionDec exp, int level) {
    NodeType entry = new NodeType(exp.func, exp.result, level, "");
    if (!varExistsInCurrentScope(exp.func, level)){
      level++;
      indent(level);
      System.out.println("Entering the scope for function: "+ exp.func);
      currFunc = exp.func;
      VarDecList ex = exp.params;
      tempParams = "";
      while( ex != null ) {
        ex.head.accept( this, level );
        ex = ex.tail;
      }
      entry.params = tempParams;
     
      if (table.get(exp.func) == null) {
        table.put(exp.func, new ArrayList<NodeType>());
      }
      
      table.get(exp.func).add(0, entry);
      exp.body.accept(this, level);
      printMap(level);
      delete(level);
      indent(level);
      System.out.println("Leaving the scope for function: " + exp.func);
      removeFuncVars(table.entrySet().iterator());
      for (int i = 0; i < temp.size(); i++) 
        table.put(temp.get(i), new ArrayList<NodeType>());
        
      currFunc = "";
    } else {
      System.err.printf("line %d: error: function %s is already defined within scope.\n", exp.pos+1, exp.func);
    }
  }

  @Override
  public void visit(SimpleDec exp, int level) {

    boolean err;
    err = insert(exp.name, level, exp.typ);
    tempParams += exp.typ.typ + " ";
    if (err == false) {
      System.err.printf("line %d: error: variable %s is already defined within scope.\n", exp.pos+1, exp.name);
    }

  }

  @Override
  public void visit(ArrayDec exp, int level) {
    String name = "";
    name = exp.name + "[";
    if (exp.size != null)
        name = name + exp.size.value + "";
    name = name + "]";

    if (level == 0) {
      temp.add(exp.name);
    }

    NodeType entry = new NodeType(exp.name, exp.typ, level, "");
    if (table.get(exp.name) == null) {
      table.put(name, new ArrayList<NodeType>());
      table.put(exp.name, new ArrayList<NodeType>());
      
    } 
    if (!varExistsInCurrentScope(exp.name, level)){
      table.get(name).add(entry);
    } else {
      System.err.printf("line %d: error: variable %s is already defined within scope.\n", exp.pos+1, exp.name);
    }
   
    tempParams += exp.typ.typ + " ";

  }

  @Override
  public void visit(DecList exp, int level) {
    System.out.println("Entering global scope: ");
     while( exp != null ) {
      exp.head.accept( this, level );
      exp = exp.tail;
    }
    printMap(level);
    delete(level);
    System.out.println("Leaving global scope: ");
  }

  public void printMap(int level) {
   // System.out.println(table);
    for (Entry<String, ArrayList<NodeType>> ee : table.entrySet()) {
      for (NodeType node : ee.getValue()) {
        if (node.level == level) {
          level++;
          indent(level);
          level--;
          System.out.print(ee.getKey() + ": ");
          if (!node.params.isEmpty()) { 
            String[] tokens = node.params.split(" ");
            System.out.print("( ");
            for (String s : tokens) {
              if (s.equals("0"))
                  System.out.print("int ");
              else if (s.equals("1"))
                  System.out.print("void ");
            }
            System.out.print(") -> ");
          }
            if (node.level == level) {
              if(node.type.typ == 1) {
                System.out.println("void");
              } else if (node.type.typ == 0) {
                System.out.println("int");
              }
            }  
        }
      } 
    }
  }

  public boolean insert(String name, int level, NameTy type){  
    NodeType entry = new NodeType(name, type, level, ""); 
      if (table.get(name) == null) {
        table.put(name, new ArrayList<NodeType>());
      } 
      if (!varExistsInCurrentScope(name, level)){
        table.get(name).add(entry);
        return true;
      } 
      return false;  
  }

  public boolean varExistsInCurrentScope(String name, int level){
    NodeType node = new NodeType(name, level);
		for (Entry<String, ArrayList<NodeType>> ee : table.entrySet()) {
      ArrayList<NodeType> values = ee.getValue();
      for (NodeType obj : values) {
        if (obj.equals(node)) {
             return true;   
        }
      }
    }
    return false;
  }

  public boolean findSymbol(String name) { 
    if (table.containsKey(name)) {
      return true;
    }
    return false;
  }

  public void removeFuncVars(Iterator it) {
    while (it.hasNext()) {
      Entry<String, ArrayList<NodeType>> e = (Entry<String, ArrayList<NodeType>>) it.next();
      if(e.getValue().isEmpty()) {
        it.remove();
      } 
    } 
  }
  
  public void delete(int level) {
    for(ArrayList<NodeType> node : table.values()) {
      for(int j=node.size()-1;j>=0;j--) {
          if (node.get(j).level == level)
              node.remove(j);
      }
    }
  }

  public boolean isInt(String name){
    for (Entry<String, ArrayList<NodeType>> ee : table.entrySet()) {
      String key = ee.getKey();
      ArrayList<NodeType> values = ee.getValue();
      for (NodeType obj : values) {
        if (obj.name.equals(name)) {
          if(obj.type.typ == 0) {
            return true; 
          } 
        }
      }
    }
    return false;
  }
}



