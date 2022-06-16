package fr.dila.reponses.core.operation.nxshell;

import static fr.dila.reponses.api.constant.ReponsesParametreConstant.DELAI_TRAITEMENT_ETAPE_PARAMETER_DESCRIPTION;
import static fr.dila.reponses.api.constant.ReponsesParametreConstant.DELAI_TRAITEMENT_ETAPE_PARAMETER_NAME;
import static fr.dila.reponses.api.constant.ReponsesParametreConstant.DELAI_TRAITEMENT_ETAPE_PARAMETER_TITRE;
import static fr.dila.reponses.api.constant.ReponsesParametreConstant.DELAI_TRAITEMENT_ETAPE_PARAMETER_UNIT;
import static fr.dila.reponses.api.constant.ReponsesParametreConstant.DELAI_TRAITEMENT_ETAPE_PARAMETER_VALUE;
import static fr.dila.st.api.constant.STParametreConstant.ADRESSE_MEL_EMISSION_PARAMETER_NAME;
import static fr.dila.st.api.constant.STParametreConstant.ADRESSE_MEL_EMISSION_PARAMETER_NAME_DESCRIPTION;
import static fr.dila.st.api.constant.STParametreConstant.ADRESSE_MEL_EMISSION_PARAMETER_NAME_TITRE;
import static fr.dila.st.api.constant.STParametreConstant.ADRESSE_MEL_EMISSION_PARAMETER_NAME_UNIT;
import static fr.dila.st.api.constant.STParametreConstant.ADRESSE_MEL_EMISSION_PARAMETER_NAME_VALUE;
import static fr.dila.st.api.constant.STParametreConstant.PAGE_INTERNET_DILA_PARAMETER_DESCRIPTION;
import static fr.dila.st.api.constant.STParametreConstant.PAGE_INTERNET_DILA_PARAMETER_NAME;
import static fr.dila.st.api.constant.STParametreConstant.PAGE_INTERNET_DILA_PARAMETER_TITRE;
import static fr.dila.st.api.constant.STParametreConstant.PAGE_INTERNET_DILA_PARAMETER_UNIT;
import static fr.dila.st.api.constant.STParametreConstant.PAGE_INTERNET_DILA_PARAMETER_VALUE;

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
 * Opération pour ajouter des nouveaux paramètres dans réponses.
 *
 */
@Operation(id = ReponsesCreateParametresOperation.ID, category = STConstant.PARAMETRE_DOCUMENT_TYPE)
@STVersion(version = "4.0.0")
public class ReponsesCreateParametresOperation extends AbstractCreateParametersOperation {
    public static final String ID = "Reponses.Parametre.Creation";

    @Context
    private OperationContext context;

    @Override
    protected List<ParametreBean> getParametreBeans() {
        return ImmutableList.of(
            new ParametreBean(
                DELAI_TRAITEMENT_ETAPE_PARAMETER_NAME,
                DELAI_TRAITEMENT_ETAPE_PARAMETER_TITRE,
                DELAI_TRAITEMENT_ETAPE_PARAMETER_DESCRIPTION,
                DELAI_TRAITEMENT_ETAPE_PARAMETER_UNIT,
                DELAI_TRAITEMENT_ETAPE_PARAMETER_VALUE
            ),
            new ParametreBean(
                ADRESSE_MEL_EMISSION_PARAMETER_NAME,
                ADRESSE_MEL_EMISSION_PARAMETER_NAME_TITRE,
                ADRESSE_MEL_EMISSION_PARAMETER_NAME_DESCRIPTION,
                ADRESSE_MEL_EMISSION_PARAMETER_NAME_UNIT,
                ADRESSE_MEL_EMISSION_PARAMETER_NAME_VALUE
            ),
            new ParametreBean(
                PAGE_INTERNET_DILA_PARAMETER_NAME,
                PAGE_INTERNET_DILA_PARAMETER_TITRE,
                PAGE_INTERNET_DILA_PARAMETER_DESCRIPTION,
                PAGE_INTERNET_DILA_PARAMETER_UNIT,
                PAGE_INTERNET_DILA_PARAMETER_VALUE
            )
        );
    }

    @Override
    protected OperationContext getContext() {
        return context;
    }
}
