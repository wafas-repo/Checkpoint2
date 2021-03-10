import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

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

  public void visit( AssignExp exp, int level ) {
    indent( level );
    System.out.println( "AssignExp:" );
    level++;
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );
  }

  public void visit( IfExp exp, int level ) {
    indent( level );
    System.out.println( "IfExp:" );
    level++;
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
       exp.elsepart.accept( this, level );
  }

  public void visit( IntExp exp, int level ) {
    indent( level );
    System.out.println( "IntExp: " + exp.value ); 
  }

  public void visit( OpExp exp, int level ) {
    indent( level );
    System.out.print( "OpExp:" ); 
    switch( exp.op ) {
      case OpExp.PLUS:
        System.out.println( " + " );
        break;
      case OpExp.MINUS:
        System.out.println( " - " );
        break;
      case OpExp.TIMES:
        System.out.println( " * " );
        break;
      case OpExp.OVER:
        System.out.println( " / " );
        break;
      case OpExp.EQ:
        System.out.println( " = " );
        break;
      case OpExp.NEQ:
        System.out.println( " != " );
        break;
      case OpExp.LT:
        System.out.println( " < " );
        break;
      case OpExp.LTE:
        System.out.println( " <= " );
        break;
      case OpExp.GT:
        System.out.println( " > " );
        break;
      case OpExp.GTE:
        System.out.println( " >= " );
        break;
      case OpExp.COMPARE:
        System.out.println( " == " );
        break;
      default:
        System.out.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
    }
    level++;
    exp.left.accept( this, level );
    exp.right.accept( this, level );
  }

  public void visit( VarExp exp, int level ) {
    indent( level );
    System.out.println( "VarExp: ");
    level++;
    exp.variable.accept(this, level);

  }

  @Override
  public void visit(SimpleVar exp, int level) {
    indent( level );
    System.out.println( "SimpleVar: " + exp.name);
    
  }

  @Override
  public void visit(NilExp exp, int level) {
    indent(level);
    System.out.println( "NilExp: " );
    
  }

  @Override
  public void visit(ReturnExp exp, int level) {
    indent(level);
    System.out.println( "ReturnExp: ");
    level++;
    exp.exp.accept(this, level);
  }

  @Override
  public void visit(IndexVar exp, int level) {
    indent(level);
    level++;
    System.out.println( "IndexVar: ");
    exp.index.accept(this, level);
  }

  @Override
  public void visit(CallExp exp, int level) {
    indent(level);
    System.out.println( "CallExp: " + exp.func);
    level++;
    ExpList ex = exp.args;
    while( ex != null ) {
      ex.head.accept( this, level );
      ex = ex.tail;
    } 
  }

  @Override
  public void visit(WhileExp exp, int level) {
    indent(level);
    System.out.println( "WhileExp:" );
    level++;
    exp.test.accept( this, level );
    exp.body.accept( this, level ); 
  }

  @Override
  public void visit(CompoundExp exp, int level) {
     indent(level);
     System.out.println("CompoundExp: " );
     level++;
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
  public void visit(NameTy exp, int level) {
    indent(level);
    if (exp.typ == 0) { 
      System.out.println("NameTy: INT");
    } else if (exp.typ == 1) {
      System.out.println("NameTy: VOID");
    }
  }

  @Override
  public void visit(FunctionDec exp, int level) {
    indent(level);
    System.out.println( "FunctionDec:" );
    level++;
    exp.result.accept(this, level);
    indent(level);
    System.out.println("ID: " + exp.func);
    VarDecList ex = exp.params;
    while( ex != null ) {
      ex.head.accept( this, level );
      ex = ex.tail;
    } 
    exp.body.accept(this, level);
  }

  @Override
  public void visit(SimpleDec exp, int level) {
     indent( level );
    System.out.println( "SimpleDec:" );
    level++;
    exp.typ.accept( this, level );
    indent(level);
    System.out.println("ID: " + exp.name);
  }

  @Override
  public void visit(ArrayDec exp, int level) {
    indent( level );
    System.out.println( "ArrayDec:" );
    level++;
    exp.typ.accept( this, level );
    indent(level);
    System.out.println("ID: " + exp.name);  
    if (exp.size != null) {
      exp.size.accept( this, level );
    } 
    }

  @Override
  public void visit(DecList exp, int level) {
     while( exp != null ) {
      exp.head.accept( this, level );
      exp = exp.tail;
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
}

