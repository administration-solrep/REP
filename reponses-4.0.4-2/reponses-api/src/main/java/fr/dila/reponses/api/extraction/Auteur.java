package fr.dila.reponses.api.extraction;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "id_auteur", "civilite", "nom", "prenom" })
public class Auteur {
    private String id_auteur;
    private String civilite;
    private String nom;
    private String prenom;

    public Auteur() {
        super();
    }

    public Auteur(String id_auteur, String civilite, String nom, String prenom) {
        super();
        this.id_auteur = id_auteur;
        this.civilite = civilite;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getId_auteur() {
        return id_auteur;
    }

    public void setId_auteur(String id_auteur) {
        this.id_auteur = id_auteur;
    }

    public String getCivilite() {
        return civilite;
    }

    public void setCivilite(String civilite) {
        this.civilite = civilite;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
