package fr.dila.reponses.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants.UserColumnEnum;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.ss.api.constant.SSProfilUtilisateurConstants;
import fr.sword.naiad.nuxeo.commons.core.helper.VocabularyHelper.Entry;
import java.util.List;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.api")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
public class TestProfilUtilisateurService {
    @Inject
    private ProfilUtilisateurService profilService;

    @Inject
    private CoreSession session;

    @Inject
    private UserWorkspaceService userService;

    @Test
    public void testGetUserColumn() {
        // On vérifie que l'utilisateur n'a aucune colonne de présente
        List<String> lstUserColumn = profilService.getUserColumn(session);

        assertNotNull(lstUserColumn);
        assertTrue(lstUserColumn.isEmpty());

        // On rajoute une colonne à l'utilisateur
        DocumentModel userWorkspace = userService.getCurrentUserPersonalWorkspace(session);
        userWorkspace.setPropertyValue(
            SSProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA_PREFIX +
            ":" +
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS,
            Lists.newArrayList(UserColumnEnum.LEGISLATURE.toString())
        );
        session.saveDocument(userWorkspace);

        // On vérifie quela modification est bien répercutée
        lstUserColumn = profilService.getUserColumn(session);

        assertNotNull(lstUserColumn);
        assertFalse(lstUserColumn.isEmpty());
        assertEquals(1, lstUserColumn.size());
        assertEquals(UserColumnEnum.LEGISLATURE.toString(), lstUserColumn.get(0));
    }

    private void checkEntriesValue(List<Entry> lstEntries) {
        for (Entry value : lstEntries) {
            try {
                assertNotNull(UserColumnEnum.valueOf(value.getId()));
            } catch (Exception e) {
                fail(String.format("La propriété %s est inconnue", value.getId()));
            }
        }
    }

    @Test
    public void testGetVocEntryAllowedColumn() {
        // On vérifie que l'utilisateur peut sélectionner toutes les colonnes
        List<Entry> lstAllowedSelection = profilService.getVocEntryAllowedColumn(session);
        assertNotNull(lstAllowedSelection);
        assertEquals(UserColumnEnum.values().length, lstAllowedSelection.size());
        checkEntriesValue(lstAllowedSelection);

        // On rajoute un document au profil utilisateur
        DocumentModel userWorkspace = userService.getCurrentUserPersonalWorkspace(session);
        userWorkspace.setPropertyValue(
            SSProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA_PREFIX +
            ":" +
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS,
            Lists.newArrayList(UserColumnEnum.LEGISLATURE.toString())
        );
        session.saveDocument(userWorkspace);

        // On vérifie qu'il manque une colonne après l'ajout
        lstAllowedSelection = profilService.getVocEntryAllowedColumn(session);
        assertNotNull(lstAllowedSelection);
        assertEquals(UserColumnEnum.values().length - 1, lstAllowedSelection.size());
        checkEntriesValue(lstAllowedSelection);
    }

    @Test
    public void testGetVocEntryUserColumn() {
        // On vérifie que l'utilisateur n'a aucune colonne de présente
        List<Entry> lstCurUserColumn = profilService.getVocEntryUserColumn(session);
        assertNotNull(lstCurUserColumn);
        assertEquals(0, lstCurUserColumn.size());

        // On rajoute un document au profil utilisateur
        DocumentModel userWorkspace = userService.getCurrentUserPersonalWorkspace(session);
        userWorkspace.setPropertyValue(
            SSProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA_PREFIX +
            ":" +
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS,
            Lists.newArrayList(UserColumnEnum.LEGISLATURE.toString())
        );
        session.saveDocument(userWorkspace);

        // On vérifie qu'on retrouve bien la nouvelle colonne
        lstCurUserColumn = profilService.getVocEntryUserColumn(session);
        assertNotNull(lstCurUserColumn);
        assertEquals(1, lstCurUserColumn.size());
        assertEquals(UserColumnEnum.LEGISLATURE.toString(), lstCurUserColumn.get(0).getId());
    }
}
