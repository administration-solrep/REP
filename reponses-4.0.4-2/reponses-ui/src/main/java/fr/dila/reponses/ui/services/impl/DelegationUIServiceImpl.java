package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.core.constant.DelegationConstant.DELEGATION_SCHEMA_PREFIX;

import fr.dila.reponses.api.domain.user.Delegation;
import fr.dila.reponses.core.constant.DelegationConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.DelegationsDonneesList;
import fr.dila.reponses.ui.bean.DelegationsRecuesList;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.DelegationUIService;
import fr.dila.reponses.ui.th.bean.DelegationForm;
import fr.dila.reponses.ui.th.bean.DelegationsDonneesListForm;
import fr.dila.reponses.ui.th.bean.DelegationsRecuesListForm;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class DelegationUIServiceImpl implements DelegationUIService {
    /**
     * Profil sélectionné pour l'ajout d'un profil à une liste de valeurs.
     */
    protected String currentProfile;

    @Override
    public DocumentModel createDelegation(SpecificContext context) {
        DelegationForm form = context.getFromContextData(ReponsesContextDataKey.DELEGATION_FORM);
        Objects.requireNonNull(form, "Un formulaire de type [DelegationForm] depuis le context est requis");

        CoreSession session = context.getSession();
        validateDelegation(session, form);

        DocumentModel userDelegationRoot = ReponsesServiceLocator.getDelegationService().getDelegationRoot(session);
        DocumentModel doc = session.createDocumentModel(
            userDelegationRoot.getPathAsString(),
            form.getDelegue(),
            DelegationConstant.DELEGATION_DOCUMENT_TYPE
        );

        MapDoc2Bean.beanToDoc(form, doc);

        STServiceLocator.getSTUserService().clearUserFromCache(form.getDelegue());

        return session.createDocument(doc);
    }

    @Override
    public DocumentModel updateDelegation(SpecificContext context) {
        DelegationForm form = context.getFromContextData(ReponsesContextDataKey.DELEGATION_FORM);
        Objects.requireNonNull(form, "Un formulaire de type [DelegationForm] depuis le context est requis");

        CoreSession session = context.getSession();
        validateDelegation(session, form);

        DocumentModel doc = session.getDocument(new IdRef(form.getId()));
        validateUpdateDelegation(form, doc.getAdapter(Delegation.class));

        MapDoc2Bean.beanToDoc(form, doc);

        STServiceLocator.getSTUserService().clearUserFromCache(form.getDelegue());

        return session.saveDocument(doc);
    }

    private void validateDelegation(CoreSession session, DelegationForm form) {
        Calendar today = DateUtil.dateToGregorianCalendar(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));

        if (form.getDateDebut() == null || form.getDateDebut().before(today)) {
            throw new STValidationException("reponses.delegation.start.date.not.valid");
        }

        if (form.getDateFin() == null || form.getDateDebut().compareTo(form.getDateFin()) >= 0) {
            throw new STValidationException("reponses.delegation.dates.not.valid");
        }

        if (form.getProfils().isEmpty()) {
            throw new STValidationException("reponses.delegation.profils.empty");
        }

        if (StringUtils.isBlank(form.getDelegue())) {
            throw new STValidationException("reponses.delegation.delegue.empty");
        }

        NuxeoPrincipal principal = session.getPrincipal();
        String loggedUsername = principal.getName();
        if (form.getDelegue().equals(loggedUsername)) {
            throw new STValidationException("reponses.delegation.self");
        }

        UserManager userManager = STServiceLocator.getUserManager();
        if (userManager.getUserModel(form.getDelegue()) == null) {
            throw new STValidationException("reponses.delegation.delegue.inexistant");
        }

        if (
            !userManager
                .getUserModel(loggedUsername)
                .getAdapter(STUser.class)
                .getGroups()
                .containsAll(form.getProfils())
        ) {
            throw new STValidationException("reponses.delegation.profils.notMember");
        }
    }

    private void validateUpdateDelegation(DelegationForm form, Delegation delegation) {
        if (!Objects.equals(form.getDelegue(), delegation.getDestinataireId())) {
            throw new STValidationException("reponses.delegation.delegue.nonUpdatable");
        }
    }

    @Override
    public DelegationForm fetchDelegation(SpecificContext context) {
        String id = context.getFromContextData(STContextDataKey.ID);
        Objects.requireNonNull(id, "Un id de type [String] depuis le context est requis");

        CoreSession session = context.getSession();
        DocumentModel doc = session.getDocument(new IdRef(id));

        return MapDoc2Bean.docToBean(doc, DelegationForm.class);
    }

    @Override
    public void deleteDelegation(SpecificContext context) {
        String id = context.getFromContextData(STContextDataKey.ID);
        Objects.requireNonNull(id, "Un id de type [String] depuis le context est requis");

        CoreSession session = context.getSession();
        String delegue = fetchDelegation(context).getDelegue();
        session.removeDocument(new IdRef(id));
        STServiceLocator.getSTUserService().clearUserFromCache(delegue);
    }

    @Override
    public List<SelectValueDTO> getProfilsForCurrentUser(SpecificContext context) {
        DocumentModel userDoc = STServiceLocator
            .getUserManager()
            .getUserModel(context.getSession().getPrincipal().getName());
        STUser user = userDoc.getAdapter(STUser.class);
        return user.getGroups().stream().map(s -> new SelectValueDTO(s, s)).collect(Collectors.toList());
    }

    @Override
    public DelegationsDonneesList fetchDelegationsDonnees(SpecificContext context) {
        CoreSession session = context.getSession();

        DelegationsDonneesListForm form = context.getFromContextData(ReponsesContextDataKey.DELEGATIONS_DONNEES);
        Objects.requireNonNull(form, "Un object de type [DelegationsDonneesListForm] du context est attendu");

        List<Object> params = Arrays.asList(
            ReponsesServiceLocator.getDelegationService().getDelegationRoot(session).getId()
        );
        CoreQueryDocumentPageProvider pageProvider = form.getPageProvider(
            session,
            "CURRENT_DOC_CHILDREN",
            DELEGATION_SCHEMA_PREFIX + ":",
            params
        );

        List<DocumentModel> delegationsDonneesDocs = pageProvider.getCurrentPage();
        List<DelegationForm> delegationsDonnees = delegationsDonneesDocs
            .stream()
            .map(doc -> MapDoc2Bean.docToBean(doc, DelegationForm.class))
            .collect(Collectors.toList());

        DelegationsDonneesList delegationsDonneesList = new DelegationsDonneesList();
        delegationsDonneesList.setListe(delegationsDonnees);
        delegationsDonneesList.setNbTotal(Math.toIntExact(pageProvider.getNumberOfPages()));
        delegationsDonneesList.buildColonnes(form);

        return delegationsDonneesList;
    }

    @Override
    public DelegationsRecuesList fetchDelegationsRecues(SpecificContext context) {
        CoreSession session = context.getSession();

        DelegationsRecuesListForm form = context.getFromContextData(ReponsesContextDataKey.DELEGATIONS_RECUES);
        Objects.requireNonNull(form, "Un object de type [DelegationsRecuesListForm] du context est attendu");

        List<Object> params = Arrays.asList(context.getSession().getPrincipal().getName());
        CoreQueryDocumentPageProvider pageProvider = form.getPageProvider(
            session,
            "PP_DELEGATIONS_RECUES",
            null,
            params
        );

        List<DocumentModel> delegationsRecuesDocs = pageProvider.getCurrentPage();
        List<DelegationForm> delegationsRecues = delegationsRecuesDocs
            .stream()
            .map(doc -> MapDoc2Bean.docToBean(doc, DelegationForm.class))
            .collect(Collectors.toList());

        DelegationsRecuesList delegationsRecuesList = new DelegationsRecuesList();
        delegationsRecuesList.setListe(delegationsRecues);
        delegationsRecuesList.setNbTotal(Math.toIntExact(pageProvider.getNumberOfPages()));
        delegationsRecuesList.buildColonnes(form);

        return delegationsRecuesList;
    }
}
