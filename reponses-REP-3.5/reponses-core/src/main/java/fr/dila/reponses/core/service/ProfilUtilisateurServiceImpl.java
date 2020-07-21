package fr.dila.reponses.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.directory.VocabularyEntry;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants.UserColumnEnum;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.user.STProfilUtilisateur;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.AbstractSTProfilUtilisateurServiceImpl;
import fr.dila.st.core.util.StringUtil;

/**
 * Service profil utilisateur
 *
 */
public class ProfilUtilisateurServiceImpl extends AbstractSTProfilUtilisateurServiceImpl implements ProfilUtilisateurService {

    /**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 4607880778403935312L;

	private static final String WORKSPACE_NAME_LABEL = "workspaceName";

    private static final String QUERY_FILTERED_USER_LIST = "SELECT w." + STSchemaConstant.ECM_NAME_XPATH + " as " + WORKSPACE_NAME_LABEL + " FROM "
            + ProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE + " AS w" + " WHERE w." + STSchemaConstant.ECM_NAME_XPATH + " IN (%s) AND w."
            + ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_XPATH + " = ?";
    
    /**
     * Default constructor
     */
    public ProfilUtilisateurServiceImpl(){
    	// do nothing
    }

    @Override
    public List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList) throws ClientException {
        return getFilteredUserList(session, userList, ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_AUTO);
    }

    @Override
    public List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList, String mailMode) throws ClientException {
        if (userList == null) {
            return null;
        }
        if (userList.isEmpty()) {
            return new ArrayList<STUser>();
        }
        
        final String query = String.format(QUERY_FILTERED_USER_LIST, StringUtil.getQuestionMark(userList.size()));

        final List<String> paramList = new ArrayList<String>();
        for (STUser stUser : userList) {
            paramList.add(stUser.getUsername());
        }
        paramList.add(mailMode);

        List<String> workspaceNameList = QueryUtils.doUFNXQLQueryAndMapping(session, query, paramList.toArray(), new QueryUtils.RowMapper<String>() {
            @Override
			public String doMapping(Map<String, Serializable> rowData) {
                return (String) rowData.get(WORKSPACE_NAME_LABEL);
            }
        });

        Set<String> workspaceNameSet = new HashSet<String>(workspaceNameList);
        List<STUser> filteredList = new ArrayList<STUser>();
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
    public List<VocabularyEntry> getVocEntryAllowedColumn(CoreSession session) throws ClientException {
        List<VocabularyEntry> list = new LinkedList<VocabularyEntry>();

        List<VocabularyEntry> currentUserColumn = getVocEntryUserColumn(session);
        Set<String> listIdToExcude = new HashSet<String>();
        for (VocabularyEntry vocabularyEntry : currentUserColumn) {
            listIdToExcude.add(vocabularyEntry.getId());
        }

        for (UserColumnEnum userColumnEnum : UserColumnEnum.findAllWithExcludeList(listIdToExcude)) {
            VocabularyEntry vocabularyEntry = new VocabularyEntry(userColumnEnum.name(), userColumnEnum.getLabel());
            list.add(vocabularyEntry);
        }

        return list;
    }

    @Override
    public List<VocabularyEntry> getVocEntryUserColumn(CoreSession session) throws ClientException {
    	List<String> userColumns = getUserColumn(session);
        List<VocabularyEntry> list = new LinkedList<VocabularyEntry>();

        for (String entry : userColumns) {
            UserColumnEnum userColumnEnum = UserColumnEnum.findByName(entry);
            if (userColumnEnum != null) {
                VocabularyEntry vocabularyEntry = new VocabularyEntry(userColumnEnum.name(), userColumnEnum.getLabel());
                list.add(vocabularyEntry);
            }
        }

        return list;
    }

    @Override
    public List<String> getUserColumn(CoreSession session) throws ClientException {
        List<String> listColumns = new ArrayList<String>();
    	ProfilUtilisateur profilUtilisateur = (ProfilUtilisateur) getProfilUtilisateurForCurrentUser(session);
        listColumns.addAll(profilUtilisateur.getUserColumns());
        return listColumns;
    }

	@Override
	protected String getReminderMDPQuery(String nxqlDateInf, String nxqlDateSup) {
		StringBuilder query = new StringBuilder("SELECT * FROM ").append(STProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE).append(" WHERE ");
        query.append("(pru:dernierChangementMotDePasse >= DATE '").append(nxqlDateInf).append("')");
        query.append(" AND (pru:dernierChangementMotDePasse <= DATE '").append(nxqlDateSup).append("')");
        return query.toString();
	}
	
	@Override 
	protected DocumentModel getProfilUtilisateurDocFromWorkspace(CoreSession session, DocumentModel userWorkspaceDoc) throws ClientException {
		return userWorkspaceDoc;
	}
	
	@Override
    protected DocumentModel initProfilUtilisateurFromUserWorkspace(CoreSession session, DocumentModel userWorkspaceDoc) throws ClientException {
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
