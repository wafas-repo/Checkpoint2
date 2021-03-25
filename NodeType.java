import absyn.*;

public class NodeType {
    public String name;
    NameTy type;
    public int level;
    public String params;


    public NodeType(String name, NameTy type, int level, String params) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.params = params;
    }

    public NodeType(String name2, int level2) {
        this.name = name2;
        this.level = level2;
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
             
        return true;
    }
    @Override
    public int hashCode() {
        return 31*name.hashCode()+type.hashCode()+level;
    }
    
}
