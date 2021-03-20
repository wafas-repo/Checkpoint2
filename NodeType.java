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
        if (!(obj instanceof NodeType))  {
            return false;
        }
        NodeType other = (NodeType)obj;
        return name.equals(other.name)
            && type.equals(other.type) && level == other.level;
    }
    @Override
    public int hashCode() {
        return 31*name.hashCode()+type.hashCode()+level;
    }
    
}
