package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesSuivi.REPONSE_CHAMP_CONTRIB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.core.enumeration.OperatorEnum;
import fr.dila.ss.core.enumeration.TypeChampEnum;
import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.ss.ui.bean.RequeteExperteForm;
import fr.dila.ss.ui.bean.RequeteLigneDTO;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ServiceUtil.class, WebEngine.class, UserSessionHelper.class, ReponsesSuiviAjax.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesSuiviAjaxTest {
    private static final String REQUETE_EXPERTE_TABLE_FRAGMENT_NAME = "fragments/components/requeteExperteTable";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesSuiviAjax reponsesSuiviAjax;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private SpecificContext context;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RechercheChampService champService;

    @Mock
    private RequeteUIService requeteUIService;

    @Before
    public void setUp() {
        reponsesSuiviAjax = new ReponsesSuiviAjax();
        Whitebox.setInternalState(reponsesSuiviAjax, "context", context);

        PowerMockito.mockStatic(ServiceUtil.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(webContext.getRequest()).thenReturn(request);
        when(STServiceLocator.getRechercheChampService()).thenReturn(champService);
        when(ReponsesUIServiceLocator.getRequeteUIService()).thenReturn(requeteUIService);
    }

    @Test
    public void testGetChoixChamp() {
        ChampDescriptor champ = new ChampDescriptor();
        List<Parametre> parametres = new ArrayList<>(
            Arrays.asList(new Parametre("param1", "value1"), new Parametre("param2", "value2"))
        );
        champ.setTypeChamp("DATES");
        champ.setParametres(parametres);
        when(champService.getChamp(REPONSE_CHAMP_CONTRIB_NAME, "id")).thenReturn(champ);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("param1", "value1");
        paramMap.put("param2", "value2");

        ThTemplate template = reponsesSuiviAjax.getChoixChamp("id");

        assertTemplate(template, "fragments/components/requeteExperteChamp");

        Map<String, Object> data = template.getData();
        assertEquals(champ, data.get("champ"));
        assertEquals(TypeChampEnum.DATES, data.get("typeChamp"));
        assertEquals(paramMap, data.get("parameters"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddLine() {
        RequeteLigneDTO existingRequeteLigne = new RequeteLigneDTO();
        existingRequeteLigne.setOperator(OperatorEnum.EGAL);

        RequeteExperteDTO requeteExperteDTO = new RequeteExperteDTO();
        requeteExperteDTO.getRequetes().add(existingRequeteLigne);

        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(requeteExperteDTO);

        RequeteExperteForm form = new RequeteExperteForm();
        form.setOperator("IN");

        ThTemplate template = reponsesSuiviAjax.addLineToTable(form);

        assertTemplate(template, REQUETE_EXPERTE_TABLE_FRAGMENT_NAME);

        List<RequeteLigneDTO> requetesTemplate = (List<RequeteLigneDTO>) template.getData().get("requetes");
        assertThat(requetesTemplate)
            .extracting(RequeteLigneDTO::getOperator)
            .containsExactly(OperatorEnum.EGAL, OperatorEnum.DANS);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(
            context,
            reponsesSuiviAjax.getDtoSessionKey(context),
            requeteExperteDTO
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveLine() {
        RequeteExperteDTO requeteExperteDTO = initRequeteExperteDto();

        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(requeteExperteDTO);

        ThTemplate template = reponsesSuiviAjax.removeLine(0);

        assertTemplate(template, REQUETE_EXPERTE_TABLE_FRAGMENT_NAME);

        List<RequeteLigneDTO> requetesTemplate = (List<RequeteLigneDTO>) template.getData().get("requetes");
        assertEquals(2, requetesTemplate.size());
        assertEquals(Arrays.asList("3", "valeur3"), requetesTemplate.get(1).getValue());
        assertEquals(Arrays.asList("2", "valeur2"), requetesTemplate.get(0).getValue());
        assertNull(requetesTemplate.get(0).getAndOr());

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(
            eq(context),
            eq(reponsesSuiviAjax.getDtoSessionKey(context)),
            any(RequeteExperteDTO.class)
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMoveLine() {
        RequeteExperteDTO requeteExperteDTO = initRequeteExperteDto();

        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(requeteExperteDTO);

        ThTemplate template = reponsesSuiviAjax.moveLine(1, -1);

        assertTemplate(template, REQUETE_EXPERTE_TABLE_FRAGMENT_NAME);

        List<RequeteLigneDTO> requetesTemplate = (List<RequeteLigneDTO>) template.getData().get("requetes");
        assertEquals(3, requetesTemplate.size());
        assertEquals(Arrays.asList("2", "valeur2"), requetesTemplate.get(0).getValue());
        assertEquals(Arrays.asList("1", "valeur1"), requetesTemplate.get(1).getValue());
        assertEquals("AND", requetesTemplate.get(1).getAndOr());
        assertNull(requetesTemplate.get(0).getAndOr());
        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(
            eq(context),
            eq(reponsesSuiviAjax.getDtoSessionKey(context)),
            any(RequeteExperteDTO.class)
        );
    }

    @Test
    public void testReinit() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());

        JsonResponse reponse = (JsonResponse) reponsesSuiviAjax.reinitRequete().getEntity();

        PowerMockito.verifyStatic();
        UserSessionHelper.clearUserSessionParameter(context, reponsesSuiviAjax.getDtoSessionKey(context));
        UserSessionHelper.clearUserSessionParameter(context, reponsesSuiviAjax.getResultsSessionKey(context));
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testGetResultsFromRequete() {
        RequeteExperteDTO requeteExperteDTO = new RequeteExperteDTO();
        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(requeteExperteDTO);
        RepDossierList resultList = new RepDossierList();
        when(requeteUIService.getDossiersByRequeteCriteria(context)).thenReturn(resultList);

        ThTemplate template = reponsesSuiviAjax.getResultsFromRequete(new DossierListForm());

        assertTemplate(template, "fragments/recherche/result-list");

        PowerMockito.verifyStatic();
        UserSessionHelper.getUserSessionParameter(
            context,
            reponsesSuiviAjax.getDtoSessionKey(context),
            RequeteExperteDTO.class
        );

        assertEquals(resultList, template.getData().get(STTemplateConstants.RESULT_LIST));
        assertEquals("/suivi/resultats", template.getData().get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/suivi/resultats", template.getData().get(STTemplateConstants.DATA_AJAX_URL));

        assertThat(template.getData())
            .hasSize(15)
            .containsKeys(
                STTemplateConstants.GENERALE_ACTIONS,
                SSTemplateConstants.FDR_ACTIONS,
                SSTemplateConstants.PRINT_ACTIONS,
                ReponsesTemplateConstants.RENVOI_ACTIONS,
                SSTemplateConstants.DIVERS_ACTIONS
            );
    }

    private static RequeteExperteDTO initRequeteExperteDto() {
        RequeteExperteDTO requeteExperteDTO = new RequeteExperteDTO();
        RequeteLigneDTO ligne1 = new RequeteLigneDTO();
        RequeteLigneDTO ligne2 = new RequeteLigneDTO();
        RequeteLigneDTO ligne3 = new RequeteLigneDTO();

        ligne1.setValue(Arrays.asList("1", "valeur1"));
        ligne2.setValue(Arrays.asList("2", "valeur2"));
        ligne2.setAndOr("AND");
        ligne3.setValue(Arrays.asList("3", "valeur3"));
        requeteExperteDTO.getRequetes().add(ligne1);
        requeteExperteDTO.getRequetes().add(ligne2);
        requeteExperteDTO.getRequetes().add(ligne3);
        return requeteExperteDTO;
    }

    private void assertTemplate(ThTemplate template, String name) {
        assertThat(template).isNotNull();
        assertThat(template).isExactlyInstanceOf(AjaxLayoutThTemplate.class);
        assertThat(template.getName()).isEqualTo(name);
        assertThat(template.getContext()).isEqualTo(context);
    }
}
