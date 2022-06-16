package fr.dila.reponses.ui.bean;

import java.util.List;
import java.util.Map;

public class IndexationDTO {
    private Map<String, IndexationItemDTO> indexationAN;

    private Map<String, IndexationItemDTO> indexationSENAT;

    private Map<String, IndexationItemDTO> indexationMinistere;

    private List<String> directoriesAN;

    private List<String> directoriesSENAT;

    private List<String> directoriesMinistere;

    public IndexationDTO() {}

    public Map<String, IndexationItemDTO> getIndexationAN() {
        return indexationAN;
    }

    public void setIndexationAN(Map<String, IndexationItemDTO> indexationAN) {
        this.indexationAN = indexationAN;
    }

    public Map<String, IndexationItemDTO> getIndexationSENAT() {
        return indexationSENAT;
    }

    public Map<String, IndexationItemDTO> getIndexationMinistere() {
        return indexationMinistere;
    }

    public void setIndexationMinistere(Map<String, IndexationItemDTO> indexationMinistere) {
        this.indexationMinistere = indexationMinistere;
    }

    public void setIndexationSENAT(Map<String, IndexationItemDTO> indexationSENAT) {
        this.indexationSENAT = indexationSENAT;
    }

    public List<String> getDirectoriesAN() {
        return directoriesAN;
    }

    public void setDirectoriesAN(List<String> directoriesAN) {
        this.directoriesAN = directoriesAN;
    }

    public List<String> getDirectoriesSENAT() {
        return directoriesSENAT;
    }

    public void setDirectoriesSENAT(List<String> directoriesSENAT) {
        this.directoriesSENAT = directoriesSENAT;
    }

    public List<String> getDirectoriesMinistere() {
        return directoriesMinistere;
    }

    public void setDirectoriesMinistere(List<String> directoriesMinistere) {
        this.directoriesMinistere = directoriesMinistere;
    }
}
