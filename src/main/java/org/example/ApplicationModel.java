package org.example;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

@ApplicationScoped
public class ApplicationModel implements Serializable {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private ArrayList<ResultData> resultData;
    private int resultIndex;

    @Inject
    public ApplicationModel(){ reset(); }

    public void setResultData(ArrayList<ResultData> resultData){
        ArrayList<ResultData> oldResultData = this.resultData;
        this.resultData = resultData;
        this.pcs.firePropertyChange("resultData", oldResultData, resultData);
    }

    public ArrayList<ResultData> getResultData(){ return resultData; }

    public void setResultIndex(int resultIndex){
        int oldResultIndex = this.resultIndex;
        this.resultIndex = resultIndex;
        this.pcs.firePropertyChange("resultData", oldResultIndex, resultIndex);
    }

    public int getResultIndex(){ return resultIndex; }

    public void reset(){
        resultData = new ArrayList<>();
        resultIndex = 0;
    }
}
