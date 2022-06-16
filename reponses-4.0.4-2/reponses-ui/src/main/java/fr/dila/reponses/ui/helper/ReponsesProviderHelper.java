package fr.dila.reponses.ui.helper;

import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_LABEL;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_SCHEMA;
import static fr.dila.reponses.core.service.ReponsesServiceLocator.getDossierService;
import static java.util.Optional.ofNullable;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.flux.DelaiCalculateur;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

public final class ReponsesProviderHelper {

    private ReponsesProviderHelper() {
        // Pour cacher le constructeur public
    }

    public static Set<String> getIdsQuestionsWithErratum(final CoreSession session, final Collection<String> idsList) {
        // Récupération des errata en une seule fois : on vérifie quels documents sont parents d'un
        // erratum#anonymousType dans hierarchy
        return getIdsQuestionsParentOf(session, idsList, "erratum#anonymousType");
    }

    public static Map<String, String> getLegislatureNames() {
        return ReponsesServiceLocator
            .getVocabularyService()
            .getAllEntry(VocabularyConstants.LEGISLATURE)
            .stream()
            .collect(
                Collectors.toMap(
                    DocumentModel::getId,
                    legDoc -> (String) legDoc.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_LABEL)
                )
            );
    }

    public static void updateLegislatureInfo(
        RepDossierListingDTO rrdto,
        Map<String, String> mapLegislatureNames,
        Question question
    ) {
        if (
            !mapLegislatureNames.isEmpty() &&
            question.getLegislatureQuestion() != null &&
            mapLegislatureNames.get(question.getLegislatureQuestion().toString()) != null
        ) {
            rrdto.setLegislature(
                question.getLegislatureQuestion() != null
                    ? mapLegislatureNames.get(question.getLegislatureQuestion().toString())
                    : null
            );
        }
    }

    public static void buildDTOFromQuestion(
        final RepDossierListingDTO rrdto,
        final Question question,
        final Integer reponseDureeTraitement,
        final Set<String> reponseIdsWithErratum,
        String reponseId
    ) {
        final String id = question.getDocument().getId();

        rrdto.setQuestionId(id);
        rrdto.setTypeQuestion(question.getTypeQuestion());
        rrdto.setSourceNumeroQuestion(question.getSourceNumeroQuestion());
        rrdto.setOrigineQuestion(question.getOrigineQuestion());

        if (question.getDatePublicationJO() != null) {
            rrdto.setDatePublicationJO(question.getDatePublicationJO().getTime());
        }

        if (question.getDateSignalementQuestion() != null) {
            rrdto.setDateSignalement(question.getDateSignalementQuestion().getTime());
        }

        rrdto.setSignale(question.getEtatSignale());

        rrdto.setMinistereInterroge(question.getIntituleMinistere());
        rrdto.setAuteur(question.getNomCompletAuteur());
        rrdto.setMotCles(question.getMotsCles());
        rrdto.setUrgent(question.getEtatRappele());

        rrdto.setRenouvelle(question.getEtatRenouvele());

        rrdto.setConnexite(question.hasConnexite());

        rrdto.setDelai(
            DelaiCalculateur.computeDelaiExpirationFdr(
                question,
                question.getEtatQuestionSimple(),
                reponseDureeTraitement
            )
        );

        if (reponseIdsWithErratum != null) {
            rrdto.setErrata(reponseIdsWithErratum.contains(reponseId) ? "Err" : null);
        }

        rrdto.setLegislature(
            question.getLegislatureQuestion() != null ? String.valueOf(question.getLegislatureQuestion()) : null
        );

        rrdto.setMinistereAttributaire(question.getIntituleMinistereAttributaire());

        // la date de renouvellement de la question est égale à la date d'effet
        // du dernier renouvellementQuestion
        rrdto.setDateEffetRenouvellement(getDate(question.getDateRenouvellementQuestion()));

        rrdto.setDateRappel(getDate(question.getDateRappelQuestion()));
    }

    private static Date getDate(Calendar cal) {
        return ofNullable(cal).map(Calendar::getTime).orElse(null);
    }

    public static void buildDTOFromDossier(
        final RepDossierListingDTO rrdto,
        final Dossier dossier,
        final Set<String> dossiersDirecteurs,
        boolean isLocked,
        boolean updateForSelection,
        CoreSession session,
        List<String> lstUserVisibleColumns
    ) {
        String dossierId = dossier.getDocument().getId();

        if (updateForSelection) {
            rrdto.setDocIdForSelection(dossierId);
        }
        rrdto.setDossierId(dossierId);
        rrdto.setLot(dossier.getDossierLot() != null);
        rrdto.setLocked(isLocked);
        if (isLocked) {
            rrdto.setLockOwner(STServiceLocator.getSTUserService().getUserFullName(rrdto.getLockOwner()));
        }

        // Analyse pour voir si le dossier a des pièces jointes
        rrdto.setAttachement(Boolean.TRUE.equals(dossier.hasPJ()));

        rrdto.setRedemarre(dossier.isRedemarre() ? " Rdm" : null);

        boolean hasFdR = (dossier.hasFeuilleRoute() != null && dossier.hasFeuilleRoute());
        List<String> userVisibleColumns = ObjectHelper.requireNonNullElseGet(lstUserVisibleColumns, ArrayList::new);
        if (
            userVisibleColumns.contains(ProfilUtilisateurConstants.UserColumnEnum.DIR_ETAPE_COURANTE.name()) && hasFdR
        ) {
            ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
            rrdto.setDirectionRunningStep(feuilleRouteService.getDirectionsRunningSteps(session, dossier));
        }

        // Gestion du cas dossier directeur
        rrdto.setDirecteur(dossiersDirecteurs.contains(dossierId) ? " * " : null);

        if (userVisibleColumns.contains(ProfilUtilisateurConstants.UserColumnEnum.QE_RAPPEL.name())) {
            rrdto.setQERappel(getJoinedQERappel(session, dossier));
        }
    }

    public static void buildDTOFromDossierLink(final RepDossierListingDTO rrdto, final DossierLink dossierLink) {
        rrdto.setDocIdForSelection(dossierLink.getId());

        // Normalement déjà renseigné via le buildFromQuestion
        if (StringUtils.isEmpty(rrdto.getSourceNumeroQuestion())) {
            rrdto.setSourceNumeroQuestion(dossierLink.getSourceNumeroQuestion());
        }

        // Normalement déjà renseigné via le buildFromQuestion
        if (dossierLink.getDatePublicationJO() != null && rrdto.getDatePublicationJO() == null) {
            rrdto.setDatePublicationJO(dossierLink.getDatePublicationJO().getTime());
        }

        // Normalement déjà renseigné via le buildFromQuestion
        if (StringUtils.isEmpty(rrdto.getAuteur())) {
            rrdto.setAuteur(dossierLink.getNomCompletAuteur());
        }

        // Normalement déjà renseigné via le buildFromQuestion
        if (StringUtils.isEmpty(rrdto.getMotCles())) {
            rrdto.setMotCles(dossierLink.getMotsCles());
        }

        rrdto.setCaseLinkId(dossierLink.getId());
        rrdto.setDossierId(dossierLink.getDossierId());
        rrdto.setRead(dossierLink.isRead());

        // etats question

        // Normalement déjà renseigné via le buildFromQuestion
        if (rrdto.isUrgent() == null) {
            rrdto.setUrgent(dossierLink.isUrgent());
        }

        // Normalement déjà renseigné via le buildFromQuestion
        if (rrdto.isSignale() == null) {
            rrdto.setSignale(dossierLink.isSignale());
        }

        // Normalement déjà renseigné via le buildFromQuestion
        if (rrdto.isRenouvelle() == null) {
            rrdto.setRenouvelle(dossierLink.isRenouvelle());
        }
    }

    private static Set<String> getIdsQuestionsParentOf(
        final CoreSession session,
        final Collection<String> idsList,
        final String primaryType
    ) {
        // Récupération des questions signalées en une seule fois : on vérifie quelles questions sont parentes d'un
        // erratum#anonymousType dans hierarchy
        final StringBuilder querySignalement = new StringBuilder(
            "select distinct(h.parentId) as id from hierarchy h where h.parentId in ("
        )
            .append(StringUtil.genMarksSuite(idsList.size()))
            .append(") and h.primarytype = ?");
        final ArrayList<String> params = new ArrayList<>(idsList);
        params.add(primaryType);
        return getIdsFromQuery(session, querySignalement.toString(), params);
    }

    private static Set<String> getIdsFromQuery(
        final CoreSession session,
        final String query,
        final Collection<String> params
    ) {
        final Set<String> idsSet = new HashSet<>();
        try (
            IterableQueryResult res = QueryUtils.doSqlQuery(
                session,
                new String[] { FlexibleQueryMaker.COL_ID },
                query,
                params.toArray(new Object[0])
            )
        ) {
            Iterator<Map<String, Serializable>> iterator = res.iterator();
            if (iterator.hasNext()) {
                while (iterator.hasNext()) {
                    Map<String, Serializable> row = iterator.next();
                    String reponseId = (String) row.get(FlexibleQueryMaker.COL_ID);
                    idsSet.add(reponseId);
                }
            }
        }
        return idsSet;
    }

    public static String getReponseIdFromQuestion(CoreSession session, Question question) {
        return question.getDossier(session).getReponseId();
    }

    /**
     * Fonction utilitaire afin de joindre les différentes QE rappel récupéré depuis l'onglet Allotissement
     * à des fins d'affichage<br>
     *
     * L'objectif étant de se conformer à la règle suivante : "Ces numéros sont récupérés de l’onglet « Allotissement »
     * de la QE directrice. Il s’agit du numéro des QE présentes dans cet onglet et qui ne correspondent pas au numéro
     * de la QE directrice et dont l'indéxation principale contient la valeur 'Question de rappel'."
     *
     * @param session
     *            CoreSession
     * @param dossier
     *            le dossier concerné
     * @return la liste sous forme de chaîne de caractères des QE rappel
     */
    private static String getJoinedQERappel(CoreSession session, Dossier dossier) {
        return String.join(", ", getDossierService().getSourceNumeroQERappels(session, dossier));
    }
}
