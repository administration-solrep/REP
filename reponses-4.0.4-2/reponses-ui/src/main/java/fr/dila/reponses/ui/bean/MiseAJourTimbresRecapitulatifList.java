package fr.dila.reponses.ui.bean;

import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.CURRENT_GOUVERNEMENT;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.NEXT_GOUVERNEMENT;

import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;

public class MiseAJourTimbresRecapitulatifList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<MiseAJourTimbresRecapitulatifDTO> liste = new ArrayList<>();

    public MiseAJourTimbresRecapitulatifList(SpecificContext context) {
        STGouvernementService gouvernementService = STServiceLocator.getSTGouvernementService();
        this.listeColonnes.add(
                new ColonneInfo(
                    gouvernementService.getGouvernement(context.getFromContextData(CURRENT_GOUVERNEMENT)).getLabel(),
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    gouvernementService.getGouvernement(context.getFromContextData(NEXT_GOUVERNEMENT)).getLabel(),
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "majtimbres.recapitulatif.liste.header.label.migrerDossierClos",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo("majtimbres.recapitulatif.liste.header.label.briserSignature", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "majtimbres.recapitulatif.liste.header.label.nbQuestionsCloses",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "majtimbres.recapitulatif.liste.header.label.nbQuestionOuvertes",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "majtimbres.recapitulatif.liste.header.label.nbQuestionsEnAttente",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo("majtimbres.recapitulatif.liste.header.label.nbModeleFDR", false, true, false, true)
            );
    }

    public List<MiseAJourTimbresRecapitulatifDTO> getListe() {
        return liste;
    }

    public void setListe(List<MiseAJourTimbresRecapitulatifDTO> liste) {
        this.liste = liste;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }
}
