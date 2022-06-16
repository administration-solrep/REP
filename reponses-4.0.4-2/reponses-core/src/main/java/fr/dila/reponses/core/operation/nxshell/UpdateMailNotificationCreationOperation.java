package fr.dila.reponses.core.operation.nxshell;

import com.google.common.collect.ImmutableList;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.operation.utils.AbstractUpdateParametersOperation;
import fr.dila.st.core.operation.utils.ParametreBean;
import fr.dila.st.core.operation.utils.UpdateUrlApplicationOperation;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;

/**
 * Opération pour mettre à jour le texte du mail de notification de création d'un utilisateur.
 *
 */
@Operation(
    id = UpdateMailNotificationCreationOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    description = UpdateUrlApplicationOperation.DESCRIPTION
)
@STVersion(version = "4.0.4")
public class UpdateMailNotificationCreationOperation extends AbstractUpdateParametersOperation {
    public static final String ID = "Parametre.Update.Mail.Notification.Creation.Application";

    public static final String DESCRIPTION =
        "Mise à jour du texte du mail de notification de création d'un utilisateur";

    @Context
    private OperationContext context;

    @Override
    protected OperationContext getContext() {
        return context;
    }

    @Override
    protected List<ParametreBean> getParametreBeans() {
        String urlApplication = STServiceLocator
            .getConfigService()
            .getValue(STConfigConstants.SOLON_MAIL_URL_APPLICATION);

        return ImmutableList.of(
            new ParametreBean(
                ReponsesParametreConstant.TEXTE_MEL_NOTIFICATION_CREATION_UTILISATEUR_KEY,
                ReponsesParametreConstant.TEXTE_MEL_NOTIFICATION_CREATION_UTILISATEUR_TITLE,
                ReponsesParametreConstant.TEXTE_MEL_NOTIFICATION_CREATION_UTILISATEUR_DESC,
                ReponsesParametreConstant.TEXTE_MEL_NOTIFICATION_CREATION_UTILISATEUR_UNIT,
                "Bienvenue dans l'application REPONSES. Vous trouverez ci-dessous les identifiant et mot de passe qui vous permettront de travailler dans cet outil interministériel de traitement des questions écrites. L'adresse de l'application est : " +
                urlApplication
            )
        );
    }
}
