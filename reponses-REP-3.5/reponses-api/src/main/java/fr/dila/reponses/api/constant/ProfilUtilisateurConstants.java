package fr.dila.reponses.api.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.dila.st.api.constant.STProfilUtilisateurConstants;

public class ProfilUtilisateurConstants {

    public static final String WORKSPACE_DOCUMENT_TYPE = "Workspace";

    public static final String PROFIL_UTILISATEUR_SCHEMA = "profil_utilisateur";

    public static final String PROFIL_UTILISATEUR_SCHEMA_PREFIX = "pru";

    /**
     * Propriété parametreMail du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY = "parametreMail";

    /**
     * Propriété columns du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_COLUMNS = "columns";

    /**
     * Xpath de la Propriété parametreMail du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_PARAMETRE_MAIL_XPATH = PROFIL_UTILISATEUR_SCHEMA_PREFIX + ":"
            + PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY;

    /**
     * Xpath de la Propriété dernierChangementMotDePasse du schema profil_utilisateur
     */
    public static final String PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_XPATH = PROFIL_UTILISATEUR_SCHEMA_PREFIX + ":"
            + STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY;

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

    public enum UserColumnEnum {
        MINISTERE_ATTRIBUTAIRE("Ministère attributaire"),
        ETAPE_COURANTE("Etape en cours"),
        NATURE("Nature"),
        // DATE_PUBLICATION_REPONSE_AU_JO("Date publication réponse", false),
        MINISTERE_INTERROGE("Ministère interrogé"),
        LEGISLATURE("Législature"),
        DATE_SIGNALEMENT("Date de signalement");

        private String label;

        UserColumnEnum(String label) {
            this.label = label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
        

        public static List<UserColumnEnum> findAll() {
            List<UserColumnEnum> list = new ArrayList<UserColumnEnum>();
            for (UserColumnEnum userColumnEnum : UserColumnEnum.values()) {
                list.add(userColumnEnum);
            }
            return list;
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
            List<UserColumnEnum> list = new ArrayList<UserColumnEnum>();
            for (UserColumnEnum userColumnEnum : UserColumnEnum.values()) {
                if (listIdToExcude == null || !listIdToExcude.contains(userColumnEnum.name())) {
                    list.add(userColumnEnum);
                }
            }
            return list;
        }
        
    }

    private ProfilUtilisateurConstants() {

    }
}
