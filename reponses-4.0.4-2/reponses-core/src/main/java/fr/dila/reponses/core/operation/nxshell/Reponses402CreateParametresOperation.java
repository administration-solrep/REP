package fr.dila.reponses.core.operation.nxshell;

import static fr.dila.st.api.constant.STParametreConstant.MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_DESCRIPTION;
import static fr.dila.st.api.constant.STParametreConstant.MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_NAME;
import static fr.dila.st.api.constant.STParametreConstant.MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_TITRE;
import static fr.dila.st.api.constant.STParametreConstant.MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_UNIT;
import static fr.dila.st.api.constant.STParametreConstant.MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_VALUE;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.operation.utils.AbstractCreateParametersOperation;
import fr.dila.st.core.operation.utils.ParametreBean;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;

/**
 * Opération pour ajouter des nouveaux paramètres dans réponses 4.0.2.
 */
@Operation(id = Reponses402CreateParametresOperation.ID, category = STConstant.PARAMETRE_DOCUMENT_TYPE)
@STVersion(version = "4.0.2")
public class Reponses402CreateParametresOperation extends AbstractCreateParametersOperation {
    public static final String ID = "Reponses402.Parametre.Creation";

    @Context
    private OperationContext context;

    public Reponses402CreateParametresOperation() {
        super();
    }

    @Override
    protected List<ParametreBean> getParametreBeans() {
        return ImmutableList.of(
            new ParametreBean(
                MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_NAME,
                MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_TITRE,
                MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_DESCRIPTION,
                MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_UNIT,
                MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_VALUE
            )
        );
    }

    @Override
    protected OperationContext getContext() {
        return context;
    }
}
