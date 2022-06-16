package fr.dila.reponses.ui.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexationItemDTO {
    private List<String> allValues = new ArrayList<>();

    private Map<String, String> labels = new HashMap<>();

    public List<String> getAllValues() {
        return allValues;
    }

    public void setAllValues(List<String> allValues) {
        this.allValues = allValues;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
}
