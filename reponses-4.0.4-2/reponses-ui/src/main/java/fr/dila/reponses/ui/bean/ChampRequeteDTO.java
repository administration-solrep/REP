package fr.dila.reponses.ui.bean;

import fr.dila.ss.core.enumeration.TypeChampEnum;
import java.util.HashMap;
import java.util.Map;

public class ChampRequeteDTO {

    public ChampRequeteDTO() {}

    private String label;

    private String field;

    private TypeChampEnum typeChamp;

    private Map<String, String> parameters = new HashMap<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public TypeChampEnum getTypeChamp() {
        return typeChamp;
    }

    public void setTypeChamp(TypeChampEnum typeChamp) {
        this.typeChamp = typeChamp;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
