package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class EliminationDonneesConsultationList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<EliminationDonneesDossierDTO> liste = new ArrayList<>();

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    public EliminationDonneesConsultationList() {
        this.nbTotal = 0;
        this.listeColonnes.add(new ColonneInfo(null, false, true, false, true));
        this.listeColonnes.add(
                new ColonneInfo("eliminationDonneesConsultation.liste.header.label.origine", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("eliminationDonneesConsultation.liste.header.label.question", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("eliminationDonneesConsultation.liste.header.label.nature", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "eliminationDonneesConsultation.liste.header.label.datePublication",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo("eliminationDonneesConsultation.liste.header.label.auteur", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("eliminationDonneesConsultation.liste.header.label.motsCles", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "eliminationDonneesConsultation.liste.header.label.ministereAttributaire",
                    false,
                    true,
                    false,
                    true
                )
            );
    }

    public List<EliminationDonneesDossierDTO> getListe() {
        return liste;
    }

    public void setListe(List<EliminationDonneesDossierDTO> liste) {
        this.liste = liste;
    }

    public Integer getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }
}
