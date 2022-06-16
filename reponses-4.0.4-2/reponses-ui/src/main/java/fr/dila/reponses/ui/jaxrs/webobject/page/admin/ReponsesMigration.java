package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.ss.ui.jaxrs.webobject.page.admin.SSMigration;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Migration")
public class ReponsesMigration extends SSMigration {
    public static final Map<String, String> MIGRATION_TYPES = ImmutableMap.of(
        OrganigrammeType.DIRECTION.getValue(),
        "migration.structure.direction",
        OrganigrammeType.UNITE_STRUCTURELLE.getValue(),
        "migration.structure.autre.ust",
        OrganigrammeType.POSTE.getValue(),
        "migration.structure.poste"
    );

    public static final Map<String, List<String>> ACTIONS = ImmutableMap.of(
        OrganigrammeType.DIRECTION.getValue(),
        Arrays.asList(MIGRATION_DEPLACER_ELEMENT_FILS, MIGRATION_DEPLACER_DIRECTION_PILOTES_MODELES),
        OrganigrammeType.UNITE_STRUCTURELLE.getValue(),
        Arrays.asList(MIGRATION_DEPLACER_ELEMENT_FILS),
        OrganigrammeType.POSTE.getValue(),
        Arrays.asList(
            MIGRATION_DEPLACER_ELEMENT_FILS,
            MIGRATION_MIGRER_ETAPES_FDR_MODELES,
            MIGRATION_MISE_A_JOUR_DROITS_QE,
            MIGRATION_MISE_A_JOUR_CORBEILLE_POSTE
        )
    );

    public ReponsesMigration() {
        super();
    }

    @Override
    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        return ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService();
    }

    @Override
    public Map<String, String> getMigrationTypes() {
        return MIGRATION_TYPES;
    }

    @Override
    public Map<String, List<String>> getActions() {
        return ACTIONS;
    }

    @Override
    protected ThTemplate getMyTemplate(SpecificContext context) {
        if (context.getWebcontext().getPrincipal().isMemberOf("EspaceAdministrationReader")) {
            return new ReponsesAdminTemplate();
        } else {
            return new ReponsesUtilisateurTemplate();
        }
    }
}
