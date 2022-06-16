package fr.dila.reponses.core.caselink;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.core.caselink.ActionableCaseLinkImpl;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.caselink.ReponsesActionableCaseLink;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.StringHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper.ActionableValidator;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

public class ReponsesActionableCaseLinkImpl extends ActionableCaseLinkImpl implements ReponsesActionableCaseLink {
    private static final long serialVersionUID = 724359104615922683L;

    public ReponsesActionableCaseLinkImpl(final DocumentModel doc) {
        super(doc);
    }

    @Override
    public void validate(final CoreSession session) {
        validate(session, true);
    }

    @Override
    public void validate(final CoreSession session, final boolean sendMail) {
        setDone(session);

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final Map<String, Serializable> map = new HashMap<>();
                map.put(CaseConstants.OPERATION_CASE_LINK_KEY, ReponsesActionableCaseLinkImpl.this);
                map.put(CaseConstants.OPERATION_CASE_LINKS_KEY, (Serializable) fetchCaseLinksFromStep(session));
                map.put(STEventConstant.OPERATION_SEND_MAIL_KEY, sendMail);
                final ActionableValidator validator = new ActionableValidator(
                    ReponsesActionableCaseLinkImpl.this,
                    session,
                    map
                );
                validator.validate();
            }
        }
            .runUnrestricted();
    }

    @Override
    public void refuse(final CoreSession session) {
        setDone(session);

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final Map<String, Serializable> map = new HashMap<>();
                map.put(CaseConstants.OPERATION_CASE_LINK_KEY, ReponsesActionableCaseLinkImpl.this);
                map.put(CaseConstants.OPERATION_CASE_LINKS_KEY, (Serializable) fetchCaseLinksFromStep(session));
                final ActionableValidator validator = new ActionableValidator(
                    ReponsesActionableCaseLinkImpl.this,
                    session,
                    map
                );
                validator.refuse();
            }
        }
            .runUnrestricted();
    }

    private List<STDossierLink> fetchCaseLinksFromStep(final CoreSession session) {
        final FeuilleRouteStep routeStep = getDocumentRouteStep(session);
        final String routeStepId = getStepId();
        final List<STDossierLink> l = new ArrayList<>();

        final List<String> attachedDocument = routeStep.getFeuilleRoute(session).getAttachedDocuments();

        if (attachedDocument != null && attachedDocument.size() > 1) {
            final StringBuilder sb = new StringBuilder("SELECT l.");
            sb.append(STSchemaConstant.ECM_UUID_XPATH);
            sb.append(" AS id FROM ");
            sb.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
            sb.append(" AS l WHERE l.");
            sb.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
            sb.append(" = '");
            sb.append(STDossierLink.CaseLinkState.todo.name());
            sb.append("' AND l.");
            sb.append(STSchemaConstant.CASE_LINK_CASE_DOCUMENT_ID_XPATH);
            sb.append(" IN (");
            sb.append(StringHelper.getQuestionMark(attachedDocument.size()));
            sb.append(") AND l.");
            sb.append(STSchemaConstant.ACTIONABLE_CASE_LINK_STEP_DOCUMENT_ID_XPATH);
            sb.append(" = ? AND l.");
            sb.append(STSchemaConstant.ECM_UUID_XPATH);
            sb.append(" != ? ");

            final List<String> paramList = new ArrayList<>();
            paramList.addAll(attachedDocument);
            paramList.add(routeStepId);
            paramList.add(getId());
            final List<DocumentModel> dossierLinkList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
                sb.toString(),
                paramList.toArray()
            );

            for (final DocumentModel dossierLinkDoc : dossierLinkList) {
                l.add(dossierLinkDoc.getAdapter(DossierLink.class));
            }
        }

        return l;
    }

    @Override
    public String getComment() {
        return PropertyUtil.getStringProperty(
            document,
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY
        );
    }
}
