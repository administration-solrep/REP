package fr.dila.reponses.ui.bean;

import fr.dila.reponses.ui.th.bean.DelegationForm;
import fr.dila.reponses.ui.th.bean.DelegationsRecuesListForm;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class DelegationsRecuesList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<DelegationForm> liste = new ArrayList<>();

    private static final String COLUMN_A = "delegationRecueA";
    private static final String COLUMN_DE = "delegationRecueDe";
    private static final String COLUMN_DATE_DEBUT = "delegationRecueDateDebut";
    private static final String COLUMN_DATE_FIN = "delegationRecueDateFin";

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    public DelegationsRecuesList() {
        this.nbTotal = 0;
    }

    public List<DelegationForm> getListe() {
        return liste;
    }

    public void setListe(List<DelegationForm> liste) {
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

    public void buildColonnes(DelegationsRecuesListForm form) {
        listeColonnes.clear();

        if (form != null) {
            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.utilisateurSource",
                    true,
                    COLUMN_DE,
                    form.getDelegationRecueDe(),
                    false,
                    true
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.utilisateurDestinataire",
                    true,
                    COLUMN_A,
                    form.getDelegationRecueA(),
                    false,
                    true
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.dateDebut",
                    true,
                    COLUMN_DATE_DEBUT,
                    form.getDelegationRecueDateDebut(),
                    false,
                    true
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.dateFin",
                    true,
                    COLUMN_DATE_FIN,
                    form.getDelegationRecueDateFin(),
                    true,
                    true
                )
            );
        }
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
