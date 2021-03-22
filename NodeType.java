import absyn.*;

public class NodeType {
    public String name;
    NameTy type;
    public int level;


    public NodeType(String name, NameTy type, int level) {
        this.name = name;
        this.type = type;
        this.level = level;
    }

    @Override
    public boolean equals(Object obj) {

        NodeType node = (NodeType)obj;

        if (!node.name.equals(this.name)){
            return false;
        }

        if (node.level != this.level) {
            return false;
        }

        if (node.type.typ != this.type.typ) {
            return false;
        }
             
        return true;
    }
    @Override
    public int hashCode() {
        return 31*name.hashCode()+type.hashCode()+level;
    }
    
}
