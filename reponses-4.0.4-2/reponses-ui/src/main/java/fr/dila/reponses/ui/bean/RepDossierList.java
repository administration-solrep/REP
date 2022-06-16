package fr.dila.reponses.ui.bean;

import static fr.dila.reponses.ui.th.bean.DossierListForm.AUTEUR_SORT_NAME;
import static fr.dila.reponses.ui.th.bean.DossierListForm.DATE_SIGNALEMENT_SORT_NAME;
import static fr.dila.reponses.ui.th.bean.DossierListForm.DATE_SORT_NAME;
import static fr.dila.reponses.ui.th.bean.DossierListForm.INDEXATION_PRINCIPALE_SORT_NAME;
import static fr.dila.reponses.ui.th.bean.DossierListForm.MINISTERE_ATTRIBUTAIRE_SORT_NAME;
import static fr.dila.reponses.ui.th.bean.DossierListForm.MINISTERE_INTERROGE_SORT_NAME;
import static fr.dila.reponses.ui.th.bean.DossierListForm.NATURE_SORT_NAME;
import static fr.dila.reponses.ui.th.bean.DossierListForm.QUESTION_SORT_NAME;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.ss.ui.bean.SSDossierList;
import java.util.List;

public class RepDossierList extends SSDossierList<RepDossierListingDTO> {

    public RepDossierList() {
        super();
    }

    public void buildColonnes(DossierListForm form, List<String> lstUSerColonnes, boolean fromEspTravail) {
        getListeColonnes().clear();

        if (form != null) {
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.sourceNumeroQuestion",
                        null,
                        lstUSerColonnes,
                        true,
                        QUESTION_SORT_NAME,
                        form.getQuestion(),
                        true,
                        form.getQuestionOrder()
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.requete.resultat.dossierNature",
                        ProfilUtilisateurConstants.UserColumnEnum.NATURE.name(),
                        lstUSerColonnes,
                        true,
                        NATURE_SORT_NAME,
                        form.getNature(),
                        true,
                        form.getNatureOrder()
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.datePublication",
                        null,
                        lstUSerColonnes,
                        true,
                        DATE_SORT_NAME,
                        form.getDate(),
                        true,
                        form.getDateOrder()
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.author",
                        null,
                        lstUSerColonnes,
                        true,
                        AUTEUR_SORT_NAME,
                        form.getAuteur(),
                        true,
                        form.getAuteurOrder()
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.ministere.attributaire",
                        ProfilUtilisateurConstants.UserColumnEnum.MINISTERE_ATTRIBUTAIRE.name(),
                        lstUSerColonnes,
                        !fromEspTravail,
                        MINISTERE_ATTRIBUTAIRE_SORT_NAME,
                        form.getMinAttr(),
                        true,
                        form.getMinAttrOrder()
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.direction.runningStep",
                        ProfilUtilisateurConstants.UserColumnEnum.DIR_ETAPE_COURANTE.name(),
                        lstUSerColonnes,
                        true
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.ministere.interroge",
                        ProfilUtilisateurConstants.UserColumnEnum.MINISTERE_INTERROGE.name(),
                        lstUSerColonnes,
                        !fromEspTravail,
                        MINISTERE_INTERROGE_SORT_NAME,
                        form.getMinInter(),
                        true,
                        form.getMinInterOrder()
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.requete.resultat.dossierIndexationPrincipale",
                        null,
                        lstUSerColonnes,
                        true,
                        INDEXATION_PRINCIPALE_SORT_NAME,
                        form.getIndexPrinc(),
                        true,
                        form.getIndexPrincOrder()
                    )
                );
            getListeColonnes().add(buildColonne("label.content.header.delai", null, lstUSerColonnes, true));
            getListeColonnes().add(buildColonne("label.requete.resultat.dossierEtat", null, lstUSerColonnes, true));
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.dateSignalement.question",
                        ProfilUtilisateurConstants.UserColumnEnum.DATE_SIGNALEMENT.name(),
                        lstUSerColonnes,
                        true,
                        DATE_SIGNALEMENT_SORT_NAME,
                        form.getDateSignal(),
                        true,
                        form.getDateSignalOrder()
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.etapes",
                        ProfilUtilisateurConstants.UserColumnEnum.ETAPE_COURANTE.name(),
                        lstUSerColonnes,
                        true
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.legislature",
                        ProfilUtilisateurConstants.UserColumnEnum.LEGISLATURE.name(),
                        lstUSerColonnes,
                        true
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.date.effet.renouvellement",
                        ProfilUtilisateurConstants.UserColumnEnum.DATE_EFFET_RENOUVELLEMENT.name(),
                        lstUSerColonnes,
                        true
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.date.rappel",
                        ProfilUtilisateurConstants.UserColumnEnum.DATE_RAPPEL.name(),
                        lstUSerColonnes,
                        true
                    )
                );
            getListeColonnes()
                .add(
                    buildColonne(
                        "label.content.header.qe.rappel",
                        ProfilUtilisateurConstants.UserColumnEnum.QE_RAPPEL.name(),
                        lstUSerColonnes,
                        true
                    )
                );
            getListeColonnes().add(buildColonne("label.content.header.infos", null, lstUSerColonnes, false));
        }
    }
}
