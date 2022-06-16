package fr.dila.reponses.ui.bean;

import fr.dila.reponses.ui.th.bean.DelegationForm;
import fr.dila.reponses.ui.th.bean.DelegationsDonneesListForm;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class DelegationsDonneesList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<DelegationForm> liste = new ArrayList<>();

    private static final String COLUMN_A = "delegationDonneeA";
    private static final String COLUMN_DE = "delegationDonneeDe";
    private static final String COLUMN_DATE_DEBUT = "delegationDonneeDateDebut";
    private static final String COLUMN_DATE_FIN = "delegationDonneeDateFin";

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    public DelegationsDonneesList() {
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

    public void buildColonnes(DelegationsDonneesListForm form) {
        listeColonnes.clear();

        if (form != null) {
            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.utilisateurSource",
                    true,
                    COLUMN_DE,
                    form.getDelegationDonneeDe(),
                    false,
                    true
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.utilisateurDestinataire",
                    true,
                    COLUMN_A,
                    form.getDelegationDonneeA(),
                    false,
                    true
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.dateDebut",
                    true,
                    COLUMN_DATE_DEBUT,
                    form.getDelegationDonneeDateDebut(),
                    false,
                    true
                )
            );

            listeColonnes.add(
                new ColonneInfo(
                    "reponses.delegation.list.dateFin",
                    true,
                    COLUMN_DATE_FIN,
                    form.getDelegationDonneeDateFin(),
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
