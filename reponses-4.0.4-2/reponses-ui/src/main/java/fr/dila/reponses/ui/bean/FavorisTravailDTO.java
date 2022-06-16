package fr.dila.reponses.ui.bean;

public class FavorisTravailDTO {
    private String id;
    private String nom;
    private String link;

    public FavorisTravailDTO() {
        super();
    }

    public FavorisTravailDTO(String id, String nom, String link) {
        super();
        this.id = id;
        this.nom = nom;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
