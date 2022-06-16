package fr.dila.reponses.core.service;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants.UserColumnEnum;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.ss.api.constant.SSProfilUtilisateurConstants;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.user.STProfilUtilisateur;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.AbstractSTProfilUtilisateurServiceImpl;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.commons.core.helper.VocabularyHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.RowMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service profil utilisateur
 *
 */
public class ProfilUtilisateurServiceImpl
    extends AbstractSTProfilUtilisateurServiceImpl<ProfilUtilisateur>
    implements ProfilUtilisateurService {
    private static final String WORKSPACE_NAME_LABEL = "workspaceName";

    private static final String QUERY_FILTERED_USER_LIST =
        "SELECT w." +
        STSchemaConstant.ECM_NAME_XPATH +
        " as " +
        WORKSPACE_NAME_LABEL +
        " FROM " +
        SSProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE +
        " AS w" +
        " WHERE w." +
        STSchemaConstant.ECM_NAME_XPATH +
        " IN (%s) AND w." +
        ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_XPATH +
        " = ?";

    /**
     * Default constructor
     */
    public ProfilUtilisateurServiceImpl() {
        // do nothing
    }

    @Override
    public List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList) {
        return getFilteredUserList(
            session,
            userList,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_AUTO
        );
    }

    @Override
    public List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList, String mailMode) {
        if (userList == null) {
            return null;
        }
        if (userList.isEmpty()) {
            return new ArrayList<>();
        }

        final String query = String.format(QUERY_FILTERED_USER_LIST, StringHelper.getQuestionMark(userList.size()));

        final List<String> paramList = new ArrayList<>();
        for (STUser stUser : userList) {
            paramList.add(stUser.getUsername());
        }
        paramList.add(mailMode);

        List<String> workspaceNameList = QueryUtils.doUFNXQLQueryAndMapping(
            session,
            query,
            paramList.toArray(),
            new RowMapper<String>() {

                @Override
                public String doMapping(Map<String, Serializable> rowData) {
                    return (String) rowData.get(WORKSPACE_NAME_LABEL);
                }
            }
        );

        Set<String> workspaceNameSet = new HashSet<>(workspaceNameList);
        List<STUser> filteredList = new ArrayList<>();
        for (STUser current : userList) {
            final String username = current.getUsername();
            final String workspaceName = getUserWorkspaceNameForUser(username);
            if (workspaceNameSet.contains(workspaceName)) {
                filteredList.add(current);
            }
        }

        return filteredList;
    }

    @Override
    public List<VocabularyHelper.Entry> getVocEntryAllowedColumn(CoreSession session) {
        List<VocabularyHelper.Entry> list = new LinkedList<>();

        List<VocabularyHelper.Entry> currentUserColumn = getVocEntryUserColumn(session);
        Set<String> listIdToExcude = new HashSet<>();
        for (VocabularyHelper.Entry vocabularyEntry : currentUserColumn) {
            listIdToExcude.add(vocabularyEntry.getId());
        }

        for (UserColumnEnum userColumnEnum : UserColumnEnum.findAllWithExcludeList(listIdToExcude)) {
            VocabularyHelper.Entry vocabularyEntry = new VocabularyHelper.Entry(
                userColumnEnum.name(),
                userColumnEnum.getLabel(),
                0L,
                0L
            );
            list.add(vocabularyEntry);
        }

        return list;
    }

    @Override
    public List<VocabularyHelper.Entry> getVocEntryUserColumn(CoreSession session) {
        List<String> userColumns = getUserColumn(session);
        List<VocabularyHelper.Entry> list = new LinkedList<>();

        for (String entry : userColumns) {
            UserColumnEnum userColumnEnum = UserColumnEnum.findByName(entry);
            if (userColumnEnum != null) {
                VocabularyHelper.Entry vocabularyEntry = new VocabularyHelper.Entry(
                    userColumnEnum.name(),
                    userColumnEnum.getLabel(),
                    0L,
                    0L
                );
                list.add(vocabularyEntry);
            }
        }

        return list;
    }

    @Override
    public List<String> getUserColumn(CoreSession session) {
        List<String> listColumns = new ArrayList<>();
        ProfilUtilisateur profilUtilisateur = (ProfilUtilisateur) getProfilUtilisateurForCurrentUser(session);
        listColumns.addAll(profilUtilisateur.getUserColumns());
        return listColumns;
    }

    @Override
    protected String getReminderMDPQuery(String nxqlDateInf, String nxqlDateSup) {
        StringBuilder query = new StringBuilder("SELECT * FROM ")
            .append(STProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE)
            .append(" WHERE ");
        query.append("(pru:dernierChangementMotDePasse >= DATE '").append(nxqlDateInf).append("')");
        query.append(" AND (pru:dernierChangementMotDePasse <= DATE '").append(nxqlDateSup).append("')");
        return query.toString();
    }

    @Override
    protected DocumentModel getProfilUtilisateurDocFromWorkspace(CoreSession session, DocumentModel userWorkspaceDoc) {
        return userWorkspaceDoc;
    }

    @Override
    protected DocumentModel initProfilUtilisateurFromUserWorkspace(
        CoreSession session,
        DocumentModel userWorkspaceDoc
    ) {
        final DocumentModel profilUtilisateurDocument = userWorkspaceDoc;
        // Initialisation de la date de changement de mot de passe
        if (profilUtilisateurDocument != null) {
            STProfilUtilisateur profilUtilisateur = profilUtilisateurDocument.getAdapter(STProfilUtilisateur.class);
            if (profilUtilisateur != null && profilUtilisateur.getDernierChangementMotDePasse() == null) {
                profilUtilisateur.setDernierChangementMotDePasse(Calendar.getInstance());
                session.saveDocument(profilUtilisateur.getDocument());
                session.save();
            }
        }
        return profilUtilisateurDocument;
    }
}
