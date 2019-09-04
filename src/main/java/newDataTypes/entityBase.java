package newDataTypes;

public class entityBase {


    protected String name;
    protected int hashCode;

    public entityBase(String name)
    {
        this.name=name.toLowerCase();
        this.hashCode=this.name.hashCode();
    }


    public int getHashCode()
    {
        return hashCode;
    }

    public String getName()
    {
        return name;
    }

}
