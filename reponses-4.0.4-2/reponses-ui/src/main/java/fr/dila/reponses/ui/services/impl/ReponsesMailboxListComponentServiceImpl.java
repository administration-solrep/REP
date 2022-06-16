package fr.dila.reponses.ui.services.impl;

import static fr.dila.st.core.service.STServiceLocator.getSTMinisteresService;
import static fr.dila.st.core.util.ObjectHelper.requireNonNullElse;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService.EtatSignalement;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepMailboxListDTO;
import fr.dila.reponses.ui.services.ReponsesMailboxListComponentService;
import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.MailboxListDTO;
import fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.session.UserSession;

public class ReponsesMailboxListComponentServiceImpl
    extends SSMailboxListComponentServiceImpl
    implements ReponsesMailboxListComponentService {

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        TypeRegroupement modeTri = null;
        String selectionPoste = null;
        String selectionUser = null;
        Boolean isSelectionValidee = false;
        Boolean masquerCorbeilles = null;
        final CoreSession session = context.getSession();
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        DocumentModel profilUtilisateurDoc = ReponsesServiceLocator
            .getProfilUtilisateurService()
            .getProfilUtilisateurForCurrentUser(session)
            .getDocument();
        RepMailboxListDTO dto = MapDoc2Bean.docToBean(profilUtilisateurDoc, RepMailboxListDTO.class);
        if (dto.getModeTri() == null) {
            dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        }

        // On récupère le mode de tri dans le contexte
        modeTri = context.getFromContextData(MODE_TRI_KEY);

        // On récupère le poste sélectionné dans le contexte
        selectionPoste = context.getFromContextData(POSTE_KEY);

        // On récupère l'utilisateur sélectionné dans le contexte
        selectionUser = context.getFromContextData(USER_KEY);

        // On récupère si la sélection a été validée dans le contexte
        isSelectionValidee = context.getFromContextData(SELECTION_VALIDEE_KEY);

        // On récupère le choix de masquer les corbeilles dans le contexte
        masquerCorbeilles = context.getFromContextData(MASQUER_CORBEILLES_KEY);

        final UserSession userSession = context.getWebcontext().getUserSession();

        // On récupère l'item actif en session puis on vérifie s'il a changé dans le contexte
        String activeKey = (String) userSession.get(ACTIVE_KEY);
        if (context.getFromContextData(ACTIVE_KEY) != null) {
            activeKey = context.getFromContextData(ACTIVE_KEY);
        }

        // On récupère le DTO en session s'il existe
        if (userSession.containsKey(SESSION_KEY)) {
            dto = (RepMailboxListDTO) userSession.get(SESSION_KEY);
        }

        Boolean refreshCorbeille = requireNonNullElse((Boolean) userSession.get(REFRESH_CORBEILLE_KEY), TRUE);

        // On récupère le droit d'afficher la sélection de corbeilles en session s'il existe
        Boolean isDisplayCorbeilleSelection = (Boolean) userSession.get(DISPLAY_CORBEILLE_SELECTION_KEY);
        if (isDisplayCorbeilleSelection == null) {
            isDisplayCorbeilleSelection = ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ALL_CORBEILLE_READER);
        }

        if (CollectionUtils.isNotEmpty(dto.getChilds())) {
            TypeRegroupement mTriRefresh = modeTri;
            if (mTriRefresh == null) {
                mTriRefresh = dto.getModeTri();
            }
            Map<String, Integer> map1erNiveau = ReponsesServiceLocator
                .getCorbeilleTreeService()
                .getCorbeilleTreeNiveau1(session, mTriRefresh, dto.getSelectionPoste(), dto.getSelectionUser());
            updateValues(
                session,
                map1erNiveau,
                dto.getChilds(),
                false,
                mTriRefresh,
                getSTMinisteresService().getCurrentMinisteres(),
                dto.getSelectionPoste(),
                dto.getSelectionUser()
            );
        }

        // Si le premier niveau est vide ou si le mode de tri sélectionné est différent de celui sauvegardé
        // On met à jour notre premier niveau
        if (
            isTrue(refreshCorbeille) ||
            CollectionUtils.isEmpty(dto.getChilds()) ||
            modeTriChanged(modeTri, dto) ||
            masquerCorbeillesChanged(masquerCorbeilles, dto) ||
            isTrue(isSelectionValidee)
        ) {
            if (modeTri != null) {
                dto.setModeTri(modeTri);
            }
            if (isTrue(isSelectionValidee)) {
                dto.setSelectionPoste(selectionPoste);
                dto.setSelectionUser(selectionUser);
            }
            if (masquerCorbeilles != null) {
                dto.setMasquerCorbeilles(masquerCorbeilles);
                MapDoc2Bean.beanToDoc(dto, profilUtilisateurDoc);
                session.saveDocument(profilUtilisateurDoc);
            }
            Map<String, Integer> map1erNiveau = ReponsesServiceLocator
                .getCorbeilleTreeService()
                .getCorbeilleTreeNiveau1(session, dto.getModeTri(), dto.getSelectionPoste(), dto.getSelectionUser());
            dto.setChilds(constructListElementFromMap(map1erNiveau, false, "", dto, ssPrincipal));
        } else {
            // Sinon si un élément est sélectionné on tente de le retrouver dans l'arbre et
            // on met à jour ses enfants
            // On récupère l'élément sélectionné dans le contexte
            String selectedItem = context.getFromContextData(SELECTED_KEY);
            if (selectedItem != null) {
                // Si un élément est sélectionné on le cherche dans l'arbre
                TreeElementDTO element = searchInTreeElement(dto.getChilds(), selectedItem);
                if (element != null) {
                    // Si on a retrouvé l'élement sélectionné
                    // Si il était fermé on l'ouvre
                    if (isFalse(element.getIsOpen())) {
                        if (element.getChilds().isEmpty()) {
                            // Si sa liste d'enfant n'est pas chargé on l'a charge
                            Map<String, Integer> map2emeNiveau = ReponsesServiceLocator
                                .getCorbeilleTreeService()
                                .getCorbeilleTreeNiveau2(
                                    session,
                                    dto.getModeTri(),
                                    element.getKey(),
                                    dto.getSelectionPoste(),
                                    dto.getSelectionUser()
                                );
                            element.setChilds(
                                constructListElementFromMap(map2emeNiveau, true, element.getKey(), dto, ssPrincipal)
                            );
                        }
                        element.setIsOpen(true);
                    } else {
                        // Sinon on le ferme
                        element.setIsOpen(false);
                    }
                }
            }
        }

        // On sauvegarde en session le DTO et l'item actif
        userSession.put(ACTIVE_KEY, activeKey);
        userSession.put(SESSION_KEY, dto);
        userSession.put(DISPLAY_CORBEILLE_SELECTION_KEY, isDisplayCorbeilleSelection);
        userSession.put(REFRESH_CORBEILLE_KEY, FALSE);

        return buildReturnMap(dto, activeKey, isDisplayCorbeilleSelection);
    }

    private void updateValues(
        CoreSession session,
        Map<String, Integer> map,
        List<? extends TreeElementDTO> dtos,
        boolean lastLevel,
        TypeRegroupement modeTri,
        List<EntiteNode> currentMinisteres,
        String selectionPoste,
        String selectionUser
    ) {
        for (TreeElementDTO elem : dtos) {
            String key = elem.getKey();
            int val = defaultIfNull(map.get(key), 0);
            elem.setLabel(fetchLabel(key, modeTri, lastLevel, currentMinisteres, val));
            if (!lastLevel && isTrue(elem.getIsOpen()) && val != 0) {
                Map<String, Integer> map2emeNiveau = ReponsesServiceLocator
                    .getCorbeilleTreeService()
                    .getCorbeilleTreeNiveau2(session, modeTri, key, selectionPoste, selectionUser);
                updateValues(
                    session,
                    map2emeNiveau,
                    elem.getChilds(),
                    true,
                    modeTri,
                    currentMinisteres,
                    selectionPoste,
                    selectionUser
                );
            }
        }
    }

    private boolean masquerCorbeillesChanged(Boolean masquerCorbeilles, RepMailboxListDTO dto) {
        return masquerCorbeilles != null && !dto.getMasquerCorbeilles().equals(masquerCorbeilles);
    }

    private boolean modeTriChanged(TypeRegroupement modeTri, MailboxListDTO dto) {
        return modeTri != null && dto.getModeTri() != modeTri;
    }

    private Map<String, Object> buildReturnMap(
        MailboxListDTO dto,
        String activeKey,
        Boolean isDisplayCorbeilleSelection
    ) {
        // On renvoie dans la map de données le DTO et l'item actif
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("mailboxListMap", dto);
        returnMap.put(POSTE_LABEL_KEY, getPosteLabel(dto.getSelectionPoste()));
        if (StringUtils.isNotEmpty(dto.getSelectionPoste())) {
            returnMap.put(
                SELECTED_POSTE_KEY,
                Collections.singletonMap(dto.getSelectionPoste(), getPosteLabel(dto.getSelectionPoste()))
            );
        }
        returnMap.put(USER_LABEL_KEY, getUserLabel(dto.getSelectionUser()));
        if (StringUtils.isNotEmpty(dto.getSelectionUser())) {
            returnMap.put(
                SELECTED_USER_KEY,
                Collections.singletonMap(dto.getSelectionUser(), getUserLabel(dto.getSelectionUser()))
            );
        }
        returnMap.put(ACTIVE_KEY, activeKey);
        returnMap.put(DISPLAY_CORBEILLE_SELECTION_KEY, isDisplayCorbeilleSelection);

        return returnMap;
    }

    protected List<TreeElementDTO> constructListElementFromMap(
        Map<String, Integer> map,
        Boolean isLastLevel,
        String parentKey,
        RepMailboxListDTO dto,
        SSPrincipal ssPrincipal
    ) {
        List<TreeElementDTO> listElement = new ArrayList<>();
        List<EntiteNode> currentMinisteres = getSTMinisteresService().getCurrentMinisteres();
        Set<String> userMinisteres = new HashSet<>();
        if (TypeRegroupement.PAR_MINISTERE.equals(dto.getModeTri())) {
            userMinisteres =
                ReponsesServiceLocator
                    .getCorbeilleTreeService()
                    .getMinisteresParentsFromPostes(ssPrincipal, dto.getSelectionPoste(), dto.getSelectionUser());
        }

        if (MapUtils.isNotEmpty(map)) {
            for (Entry<String, Integer> entry : map.entrySet()) {
                if (isFalse(dto.getMasquerCorbeilles()) || entry.getValue() != 0) {
                    // Pour chaque entrée on crée l'élément
                    TreeElementDTO element = new TreeElementDTO();
                    element.setKey(entry.getKey());
                    element.setCompleteKey(parentKey + "__" + entry.getKey());
                    String label = fetchLabel(
                        entry.getKey(),
                        dto.getModeTri(),
                        isLastLevel,
                        currentMinisteres,
                        entry.getValue()
                    );
                    element.setLabel(label);
                    element.setIsLastLevel(isLastLevel);

                    // Si on est au dernier niveau on crée le lien sinon on créé l'action du bouton
                    if (isTrue(isLastLevel)) {
                        element.setLink(
                            fetchLink(
                                parentKey,
                                entry.getKey(),
                                dto.getModeTri(),
                                dto.getSelectionPoste(),
                                dto.getSelectionUser()
                            )
                        );
                    } else {
                        element.setAction(JS_ACTION + "(\'" + element.getCompleteKey() + "\')");
                        // Les ministères qui ne font pas partie des ministères parents ne sont pas affichés en gras
                        if (
                            TypeRegroupement.PAR_MINISTERE.equals(dto.getModeTri()) &&
                            !userMinisteres.contains(entry.getKey())
                        ) {
                            element.setIsBold(false);
                        }
                    }
                    listElement.add(element);
                }
            }
        }
        return listElement;
    }

    protected String fetchLabel(
        String key,
        TypeRegroupement modeTri,
        Boolean isLastLevel,
        List<EntiteNode> currentMinisteres,
        Integer value
    ) {
        String label = key;
        if (TypeRegroupement.PAR_POSTE.equals(modeTri) && isFalse(isLastLevel)) {
            PosteNode poste = STServiceLocator
                .getOrganigrammeService()
                .getOrganigrammeNodeById(key, OrganigrammeType.POSTE);
            if (poste != null) {
                label = poste.getLabel();
            }
        } else if (TypeRegroupement.PAR_SIGNALE.equals(modeTri) && isFalse(isLastLevel)) {
            label = key.equals(EtatSignalement.QUESTIONS_SIGNALEES.toString()) ? "Signalées" : "Non signalées";
        } else if (TypeRegroupement.PAR_MINISTERE.equals(modeTri) && isTrue(isLastLevel)) {
            label = VocabularyConstants.LIST_LIBELLE_ROUTING_TASK_PAR_ID.get(key);
        } else {
            EntiteNode entite = STServiceLocator
                .getOrganigrammeService()
                .getOrganigrammeNodeById(key, OrganigrammeType.MINISTERE);
            if (entite != null) {
                label = entite.getEdition();
                if (!currentMinisteres.contains(entite)) {
                    label += " [Ancien gouvernement]";
                }
            }
        }
        return label + " - (" + value + ")";
    }

    protected String fetchLink(
        String parentKey,
        String entryKey,
        TypeRegroupement modeTri,
        String selectionPoste,
        String selectionUser
    ) {
        StringBuilder link = new StringBuilder();
        if (TypeRegroupement.PAR_MINISTERE.equals(modeTri)) {
            link.append(LINK_URL_MIN + "?minAttribId=" + parentKey + "&routingTaskType=" + entryKey);
        } else if (TypeRegroupement.PAR_POSTE.equals(modeTri)) {
            link.append(LINK_URL_POSTE + "?posteId=" + parentKey + "&minAttribId=" + entryKey);
        } else {
            link.append(
                LINK_URL_SIGNAL +
                "?isSignale=" +
                EtatSignalement.QUESTIONS_SIGNALEES.toString().equals(parentKey) +
                "&minAttribId=" +
                entryKey
            );
        }
        if (selectionPoste != null && !selectionPoste.isEmpty()) {
            link.append("&selectionPoste=" + selectionPoste);
        }
        if (selectionUser != null && !selectionUser.isEmpty()) {
            link.append("&selectionUser=" + selectionUser);
        }
        return link.toString();
    }
}
