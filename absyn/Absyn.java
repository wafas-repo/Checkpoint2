package absyn;

abstract public class Absyn {
  public int pos, row, col;

  abstract public void accept( AbsynVisitor visitor, int level );
}
