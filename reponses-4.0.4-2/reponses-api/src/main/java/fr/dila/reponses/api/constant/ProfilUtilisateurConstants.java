package fr.dila.reponses.api.constant;

import static fr.dila.ss.api.constant.SSProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA_PREFIX;

import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class ProfilUtilisateurConstants {
    public static final String PROFIL_UTILISATEUR_SCHEMA = "profil_utilisateur";

    /**
     * Propriété parametreMail du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY = "parametreMail";

    public static final String PROFIL_UTILISATEUR_LISTE_DERNIERS_DOSSIERS_INTERVENTION = "derniersDossiersIntervention";

    /**
     * Propriété columns du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_COLUMNS = "columns";

    public static final String PROFIL_UTILISATEUR_COLUMNS_XPATH =
        PROFIL_UTILISATEUR_SCHEMA_PREFIX + ":" + PROFIL_UTILISATEUR_COLUMNS;

    /**
     * Xpath de la Propriété parametreMail du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_PARAMETRE_MAIL_XPATH =
        PROFIL_UTILISATEUR_SCHEMA_PREFIX + ":" + PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY;

    /**
     * Xpath de la Propriété dernierChangementMotDePasse du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_XPATH =
        PROFIL_UTILISATEUR_SCHEMA_PREFIX +
        ":" +
        STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY;

    /**
     * Valeur possible pour parametreMail
     */
    public static final String PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_AUTO = "AUTO";

    /**
     * Valeur possible pour parametreMail
     */
    public static final String PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_JOURNALIER = "JOURNALIER";

    /**
     * Valeur possible pour parametreMail
     */
    public static final String PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_AUCUN = "AUCUN";

    /**
     * Propriété masquerCorbeilles du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_MASQUER_CORBEILLES_PROPERTY = "masquerCorbeilles";

    /**
     * Xpath de la Propriété masquerCorbeilles du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_MASQUER_CORBEILLES_XPATH =
        PROFIL_UTILISATEUR_SCHEMA_PREFIX + ":" + PROFIL_UTILISATEUR_MASQUER_CORBEILLES_PROPERTY;

    public enum UserColumnEnum {
        MINISTERE_ATTRIBUTAIRE("Ministère attributaire"),
        ETAPE_COURANTE("Etape en cours"),
        NATURE("Nature"),
        // DATE_PUBLICATION_REPONSE_AU_JO("Date publication réponse", false),
        MINISTERE_INTERROGE("Ministère interrogé"),
        LEGISLATURE("Législature"),
        DATE_SIGNALEMENT("Date de signalement"),
        DATE_EFFET_RENOUVELLEMENT("Date d’effet de renouvellement"),
        DATE_RAPPEL("Date de rappel"),
        QE_RAPPEL("QE de rappel"),
        DIR_ETAPE_COURANTE("Direction de l'étape en cours");

        private String label;

        UserColumnEnum(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static List<UserColumnEnum> findAll() {
            return Arrays.asList(UserColumnEnum.values());
        }

        public static UserColumnEnum findByName(String name) {
            for (UserColumnEnum userColumnEnum : UserColumnEnum.values()) {
                if (userColumnEnum.name().equals(name)) {
                    return userColumnEnum;
                }
            }
            return null;
        }

        public static List<UserColumnEnum> findAllWithExcludeList(Set<String> listIdToExcude) {
            List<UserColumnEnum> list = new ArrayList<>();
            for (UserColumnEnum userColumnEnum : UserColumnEnum.values()) {
                if (listIdToExcude == null || !listIdToExcude.contains(userColumnEnum.name())) {
                    list.add(userColumnEnum);
                }
            }
            return list;
        }
    }

    private ProfilUtilisateurConstants() {}
}
