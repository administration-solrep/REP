package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesSuivi.REPONSE_CHAMP_CONTRIB_NAME;
import static fr.dila.ss.core.enumeration.TypeChampEnum.SIMPLE_SELECT_BOOLEAN;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static java.util.Collections.emptyList;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.bean.RequetePersoForm;
import fr.dila.reponses.ui.contentview.RechercheResultPageProvider;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.helper.RepDossierListHelper;
import fr.dila.reponses.ui.helper.RepDossierListProviderHelper;
import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.ss.api.constant.SSRechercheChampConstants;
import fr.dila.ss.core.enumeration.OperatorEnum;
import fr.dila.ss.core.enumeration.TypeChampEnum;
import fr.dila.ss.core.util.NXQLUtils;
import fr.dila.ss.ui.bean.RequeteLigneDTO;
import fr.dila.ss.ui.enums.EtapeLifeCycle;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.Requeteur;
import fr.dila.st.core.query.translation.TranslatedStatement;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.query.QueryParseException;

public class RequeteUIServiceImpl implements RequeteUIService {
    private static final String REQUETE_RESULT_NUMBER_RESULTS = "recherche.result.numberResults";
    private static final STLogger LOGGER = STLogFactory.getLog(RechercheUIServiceImpl.class);

    @Override
    public RepDossierList getDossiersByRequeteExperte(SpecificContext context) {
        String idRequete = context.getFromContextData(ID);
        DossierListForm listForm = context.getFromContextData(ReponsesContextDataKey.DOSSIER_LIST_FORM);
        CoreSession session = context.getSession();

        DocumentModel requeteDoc = session.getDocument(new IdRef(idRequete));
        RequeteExperte requete = requeteDoc.getAdapter(RequeteExperte.class);
        String query = ReponsesActionsServiceLocator.getRequeteurActionService().getFullQuery(session, requete);

        return getDossiersByQuery(session, query, listForm, requeteDoc);
    }

    /**
     * Retourne les résultats d'une requête
     *
     * @param session
     * @param query
     *            la requête à lancer
     * @param listForm
     * @param requeteDoc
     *            le DocumentModel de la RequeteExperte si elle existe
     *            (peut être null)
     * @return
     */
    protected RepDossierList getDossiersByQuery(
        CoreSession session,
        String query,
        DossierListForm listForm,
        DocumentModel requeteDoc
    ) {
        RechercheResultPageProvider provider = RepDossierListProviderHelper.initProvider(
            listForm,
            "requeteCompositePageProvider",
            session,
            emptyList(),
            "q."
        );

        provider.getDefinition().setPattern(query);

        List<Map<String, Serializable>> searchResults = provider.getCurrentPage();
        int resultsCount = (int) provider.getResultsCount();

        return RepDossierListHelper.buildDossierList(
            searchResults,
            Optional.ofNullable(requeteDoc).map(DocumentModel::getTitle).orElse(""),
            resultsCount + ResourceHelper.getString(REQUETE_RESULT_NUMBER_RESULTS),
            listForm,
            provider.getLstUserVisibleColumns(),
            resultsCount,
            false
        );
    }

    @Override
    public boolean isRequeteGenerale(CoreSession session, String idRequete) {
        DocumentModel requeteDoc = session.getDocument(new IdRef(idRequete));
        DocumentModel parentDoc = session.getDocument(requeteDoc.getParentRef());
        return "BibliothequeRequetesRoot".equals(parentDoc.getType());
    }

    @Override
    public RepDossierList getDossiersByRequeteCriteria(SpecificContext context) {
        List<RequeteLigneDTO> requeteCriteria = context.getFromContextData(ReponsesContextDataKey.REQUETE_PERSO_LIGNES);
        DossierListForm listForm = context.getFromContextData(ReponsesContextDataKey.DOSSIER_LIST_FORM);

        String where = getWhereClause(requeteCriteria);

        String query = ReponsesActionsServiceLocator
            .getReponsesSmartNXQLQueryActionService()
            .getFullQuery(context.getSession(), where);

        return getDossiersByQuery(context.getSession(), query, listForm, null);
    }

    /**
     * Retourne une requête NXQL à partie d'une RequeteLigneDTO
     *
     * @param ligne
     * @return
     */
    private String getWhereConditionFromRequeteLigne(RequeteLigneDTO ligne) {
        StringBuilder condition = new StringBuilder();
        if (StringUtils.isNotBlank(ligne.getAndOr())) {
            condition.append("ET".equals(ligne.getAndOr()) ? "AND " : "OR ");
        }
        condition.append(ligne.getChamp().getField() + " ");
        condition.append(ligne.getOperator().getOperator() + " ");
        if (!SIMPLE_SELECT_BOOLEAN.name().equals(ligne.getChamp().getTypeChamp())) {
            condition.append(ligne.getOperator().getDisplayFunction().apply(ligne.getValue()));
        } else {
            condition.append(ligne.getValue().get(0));
        }
        return condition.toString();
    }

    @Override
    public List<EtapeLifeCycle> getRequeteurStepStates() {
        return Arrays.asList(
            EtapeLifeCycle.RUNNING,
            EtapeLifeCycle.VALIDATED,
            EtapeLifeCycle.DONE,
            EtapeLifeCycle.READY
        );
    }

    /**
     * Retourne la clause where à partir d'une liste de RequeteLigneDTO
     *
     * @param requeteCriteria
     * @return
     */
    private String getWhereClause(List<RequeteLigneDTO> requeteCriteria) {
        return requeteCriteria.stream().map(this::getWhereConditionFromRequeteLigne).collect(Collectors.joining(" "));
    }

    /**
     * Retourne le formulaire de sauvegarde de requetes à partir d'une liste de critères
     *
     * @param lignes
     * @return
     */
    @Override
    public RequetePersoForm getRequetePersoForm(List<RequeteLigneDTO> lignes) {
        RequetePersoForm form = new RequetePersoForm();
        form.setRequete(getWhereClause(lignes));
        return form;
    }

    @Override
    public String saveRequetePerso(SpecificContext context) {
        String result = StringUtils.EMPTY;
        RequetePersoForm form = context.getFromContextData(ReponsesContextDataKey.REQUETE_PERSO_FORM);
        CoreSession session = context.getSession();
        try {
            String parentPath = STServiceLocator
                .getUserWorkspaceService()
                .getCurrentUserPersonalWorkspace(session)
                .getPathAsString();

            boolean isCreation = false;
            DocumentModel doc = context.getCurrentDocument();
            if (doc == null) {
                doc =
                    session.createDocumentModel(
                        parentPath,
                        form.getTitre(),
                        STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE
                    );
                isCreation = true;
            }

            RequeteExperte requeteExperte = doc.getAdapter(RequeteExperte.class);
            requeteExperte.setWhereClause(form.getRequete());
            DublincoreSchemaUtils.setTitle(doc, form.getTitre());
            DublincoreSchemaUtils.setDescription(doc, form.getCommentaire());

            String query = ReponsesActionsServiceLocator
                .getReponsesSmartNXQLQueryActionService()
                .getFullQuery(session, form.getRequete());

            //On lance la requête pour vérifier qu'elle est valide
            getDossiersByQuery(session, query, DossierListForm.newForm(), doc);

            DocumentModel savedDoc;
            String message = "";
            if (isCreation) {
                savedDoc = session.createDocument(doc);
                message = ResourceHelper.getString("suivi.requete.sauvegardee.alert");
            } else {
                savedDoc = session.saveDocument(doc);
                message = ResourceHelper.getString("suivi.requete.modifiee");
            }

            SecurityService service = STServiceLocator.getSecurityService();
            NuxeoPrincipal principal = session.getPrincipal();
            service.addAceToAcl(savedDoc, ACL.LOCAL_ACL, principal.getName(), SecurityConstants.EVERYTHING);

            result = savedDoc.getId();
            context.getMessageQueue().addToastSuccess(message);
        } catch (QueryParseException qpe) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("suivi.erreur.requete.perso.invalide"));
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_EXECUTE_UFNXQL_TEC,
                ResourceHelper.getString("suivi.erreur.requete.perso.invalide"),
                qpe
            );
        } catch (Exception e) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("suivi.erreur.creation.requete.perso"));
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_UPDATE_REQUETE_TEC,
                ResourceHelper.getString("suivi.erreur.creation.requete.perso"),
                e
            );
        }
        return result;
    }

    @Override
    public RequetePersoForm getRequetePerso(SpecificContext context) {
        RequetePersoForm form = new RequetePersoForm();
        DocumentModel requeteDoc = context.getCurrentDocument();
        RequeteExperte requete = requeteDoc.getAdapter(RequeteExperte.class);
        form.setRequete(requete.getWhereClause());
        form.setTitre(DublincoreSchemaUtils.getTitle(requeteDoc));
        form.setCommentaire(DublincoreSchemaUtils.getDescription(requeteDoc));
        return form;
    }

    @Override
    public void deleteRequetePerso(SpecificContext context) {
        ReponsesActionsServiceLocator
            .getSuiviActionService()
            .delete(context, context.getSession(), context.getCurrentDocument());
    }

    @Override
    public List<RequeteLigneDTO> getRequeteLignesFromRequete(SpecificContext context) {
        Requeteur requeteur = new Requeteur();
        requeteur.setQuery("SELECT * FROM Dossier AS d WHERE " + getRequetePerso(context).getRequete());
        List<TranslatedStatement> statements = ReponsesActionsServiceLocator
            .getReponsesSmartNXQLQueryActionService()
            .getUserInfo(context, requeteur);
        // Décalage des operateurs et/ou
        if (CollectionUtils.isNotEmpty(statements)) {
            for (int i = 0; i < statements.size(); i++) {
                if (i > 0) {
                    statements.get(i).setLogicalOperator(statements.get(i - 1).getLogicalOperator());
                }
            }
            statements.get(0).setLogicalOperator(StringUtils.EMPTY);
        }
        return statements.stream().map(RequeteUIServiceImpl::getRequeteFromStatement).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static RequeteLigneDTO getRequeteFromStatement(TranslatedStatement statement) {
        RequeteLigneDTO requete = new RequeteLigneDTO();

        RechercheChampService champService = STServiceLocator.getRechercheChampService();

        if (StringUtils.isNotBlank(statement.getLogicalOperator())) {
            requete.setAndOr("AND".equals(statement.getLogicalOperator()) ? "ET" : "OU");
        }
        requete.setOperator(OperatorEnum.getByOperator(statement.getConditionalOperator()));
        requete.setChamp(
            champService
                .getChamps(REPONSE_CHAMP_CONTRIB_NAME)
                .stream()
                .filter(c -> statement.getSearchField().equals(c.getField()))
                .findFirst()
                .orElse(null)
        );
        requete.setValue(
            Stream.of(statement.getValue().split(",")).map(v -> StringUtils.strip(v, "'")).collect(Collectors.toList())
        );

        ChampDescriptor champ = requete.getChamp();
        switch (TypeChampEnum.valueOf(champ.getTypeChamp())) {
            case DATES:
                getDisplayValueFromDates(statement, requete);
                break;
            case MULTIPLE_SELECT:
            case SIMPLE_SELECT:
            case SIMPLE_SELECT_BOOLEAN:
                getDisplayValueFromSelect(requete, champService, champ);
                break;
            case ORGANIGRAMME:
                getDisplayValueFromOrganigramme(requete, champService, champ);
                break;
            default:
                requete.setDisplayValue(requete.getValue());
                break;
        }
        return requete;
    }

    private static void getDisplayValueFromDates(TranslatedStatement statement, RequeteLigneDTO requete) {
        requete.setValue(
            Stream.of(statement.getValue().split(",")).map(NXQLUtils::parseTimeStamp).collect(Collectors.toList())
        );
    }

    private static void getDisplayValueFromSelect(
        RequeteLigneDTO requete,
        RechercheChampService champService,
        ChampDescriptor champ
    ) {
        Parametre parametre = getParametre(champService, champ, SSRechercheChampConstants.OPTIONS_PARAMETER_NAME);
        if (parametre != null) {
            List<SelectValueDTO> options = (List<SelectValueDTO>) parametre.getValue();
            requete.setDisplayValue(
                requete
                    .getValue()
                    .stream()
                    .map(
                        v ->
                            options
                                .stream()
                                .filter(o -> o.getId().equals(v))
                                .findFirst()
                                .orElse(new SelectValueDTO())
                                .getLabel()
                    )
                    .collect(Collectors.toList())
            );
        }
    }

    private static void getDisplayValueFromOrganigramme(
        RequeteLigneDTO requete,
        RechercheChampService champService,
        ChampDescriptor champ
    ) {
        Parametre parametre = getParametre(champService, champ, SSRechercheChampConstants.TYPE_SELECTION_PARAMETER_NAME);
        if (parametre != null) {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
            OrganigrammeType organigrammeType = OrganigrammeType.getEnum(
                StringUtils.substring(parametre.getValue().toString(), 0, 3)
            );
            String value = CollectionUtils.isNotEmpty(requete.getValue())
                ? requete.getValue().get(0)
                : StringUtils.EMPTY;
            String nodeId = organigrammeType == OrganigrammeType.POSTE
                ? StringUtils.replace(value, STConstant.PREFIX_POSTE, StringUtils.EMPTY)
                : value;
            OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(nodeId, organigrammeType);
            requete.setDisplayValue(
                Collections.singletonList(
                    Optional.ofNullable(node).map(OrganigrammeNode::getLabel).orElse(StringUtils.EMPTY)
                )
            );
        }
    }

    private static Parametre getParametre(
        RechercheChampService champService,
        ChampDescriptor champ,
        String parametreName
    ) {
        if (CollectionUtils.isEmpty(champ.getParametres())) {
            champService.getChamp(REPONSE_CHAMP_CONTRIB_NAME, champ.getName());
        }
        return champ.getParametres().stream().filter(c -> parametreName.equals(c.getName())).findFirst().orElse(null);
    }

    @Override
    public RequetePersoForm getRequetePersoFormWithId(SpecificContext context) {
        RequetePersoForm form = new RequetePersoForm();
        List<RequeteLigneDTO> lignes = context.getFromContextData(ReponsesContextDataKey.REQUETE_PERSO_LIGNES);

        form.setRequete(getWhereClause(lignes));
        form.setTitre(DublincoreSchemaUtils.getTitle(context.getCurrentDocument()));
        form.setCommentaire(DublincoreSchemaUtils.getDescription(context.getCurrentDocument()));

        return form;
    }
}
