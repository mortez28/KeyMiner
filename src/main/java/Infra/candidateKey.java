package Infra;

import newDataTypes.propertyBase;

public class candidateKey extends propertyBase {

    private int support, max;

    public candidateKey(String pred, String obj, int support, int max)
    {
        super(pred,obj);
        this.support=support;
        this.max=max;
    }

    public int getSupport()
    {
        return support;
    }

    public int getMax()
    {
        return max;
    }
}
