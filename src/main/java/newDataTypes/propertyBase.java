package newDataTypes;

public class propertyBase {

    protected String predicate, value;

    public propertyBase(String predicate, String value)
    {
        this.predicate=predicate.toLowerCase();
        this.value=value.toLowerCase();
    }

    public String getPredicate()
    {
        return predicate;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value) {
        this.value= value;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }


}
