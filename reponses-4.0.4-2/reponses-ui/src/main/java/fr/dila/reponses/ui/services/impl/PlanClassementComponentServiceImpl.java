package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.helper.TreeElementDTOHelper.setLink;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.PlanClassementDTO;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.PlanClassementComponentService;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.services.impl.FragmentServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;

public class PlanClassementComponentServiceImpl extends FragmentServiceImpl implements PlanClassementComponentService {
    public static final String ASSEMBLEE_KEY = "assembleeVal";
    public static final String SELECTED_KEY = "planClassementSelected";
    public static final String ACTIVE_KEY = "planClassementActivated";
    private static final String SESSION_KEY = "planClassement";
    private static final String JS_ACTION = "onClickPlanItem";
    private static final Integer SIZE_LIMIT = 20;
    private static final String LABEL = "%s (%s)";
    private static final Pattern NOT_ASCII_CHARACTER_PATTERN = Pattern.compile("[^\\p{ASCII}]");

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        PlanClassementDTO dto = new PlanClassementDTO();
        String assemblee = null;

        //On récupère l'assemblée dans le contexte
        if (context.getContextData().get(ASSEMBLEE_KEY) != null) {
            assemblee = (String) context.getContextData().get(ASSEMBLEE_KEY);
        }

        //On récupère l'item actif en session puis on vérifie s'il a changé dans le contexte
        String activeKey = (String) context.getWebcontext().getUserSession().get(ACTIVE_KEY);
        if (context.getContextData().get(ACTIVE_KEY) != null) {
            activeKey = (String) context.getContextData().get(ACTIVE_KEY);
        }

        //On récupère le DTO en session s'il existe
        if (context.getWebcontext().getUserSession().containsKey(SESSION_KEY)) {
            dto = (PlanClassementDTO) context.getWebcontext().getUserSession().get(SESSION_KEY);
        }

        //Si le premier niveau est vide ou si l'assemblée sélectionnée est différente de celle sauvegardée
        //On met à jour notre premier niveau
        if (dto.getChilds().isEmpty() || (assemblee != null && !dto.getAssemblee().equals(assemblee))) {
            if (assemblee != null) {
                dto.setAssemblee(assemblee);
            }
            Map<String, Integer> map = ReponsesServiceLocator
                .getPlanClassementService()
                .getPlanClassementNiveau1(context.getSession(), dto.getAssemblee());

            dto.setChilds(constructListElementFromMap(map, false, "", dto.getAssemblee()));
        } else {
            //Sinon si un élément est sélectionné on tente de le retrouver dans l'arbre et on met à jour ses enfants
            //On récupère l'élément sélectionné dans le contexte
            String selectedItem = (String) context.getContextData().get(SELECTED_KEY);
            if (selectedItem != null) {
                //Si un élément est sélectionné on le cherche dans l'arbre
                TreeElementDTO element = searchInTreeElement(dto.getChilds(), selectedItem);
                if (element != null) {
                    //Si on a retrouvé l'élement sélectionné
                    //Si il était fermé on l'ouvre
                    if (!element.getIsOpen()) {
                        if (element.getChilds().isEmpty()) {
                            //Si sa liste d'enfant n'est pas chargé on l'a charge
                            Map<String, Integer> map = ReponsesServiceLocator
                                .getPlanClassementService()
                                .getPlanClassementNiveau2(context.getSession(), dto.getAssemblee(), element.getKey());
                            element.setChilds(
                                constructListElementFromMap(map, true, element.getKey(), dto.getAssemblee())
                            );
                        }
                        element.setIsOpen(true);
                    } else {
                        //Sinon on le ferme
                        element.setIsOpen(false);
                    }
                }
            }
        }

        //On renvoie dans la map de données le DTO et l'item actif
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("planClassementMap", dto);
        returnMap.put(ACTIVE_KEY, activeKey);
        //Ajout de l'action ajouter aux favoris
        returnMap.put("actionAdd", context.getAction(ReponsesActionEnum.ADD_FAVORIS_PC));
        //On sauvegarde en session le DTO et l'item actif
        context.getWebcontext().getUserSession().put(ACTIVE_KEY, activeKey);
        context.getWebcontext().getUserSession().put(SESSION_KEY, dto);
        return returnMap;
    }

    protected TreeElementDTO searchInTreeElement(List<? extends TreeElementDTO> liste, String searchKey) {
        TreeElementDTO returnElement = null;
        //Recherche récurente d'un item dans l'arbre via la clé
        for (TreeElementDTO element : liste) {
            if (searchKey.equals(element.getCompleteKey())) {
                return element;
            } else {
                returnElement = searchInTreeElement(element.getChilds(), searchKey);
                if (returnElement != null) {
                    break;
                }
            }
        }
        return returnElement;
    }

    protected List<TreeElementDTO> constructListElementFromMap(
        Map<String, Integer> map,
        Boolean isLastLevel,
        String parentIndexationKey,
        String origin
    ) {
        List<TreeElementDTO> listElement = new ArrayList<>();
        if (MapUtils.isNotEmpty(map)) {
            //Si le nombre d'éléments est supérieur à la limite, on ajoute un niveau alphabétique
            if (map.entrySet().size() > SIZE_LIMIT) {
                listElement = addElementsWithSizeGreaterThanLimit(map, isLastLevel, parentIndexationKey, origin);
            } else {
                //On est inférieur à la limite donc on créé chaque élément tel quel
                for (Entry<String, Integer> entry : map.entrySet()) {
                    TreeElementDTO element = createTreeElementDTO(isLastLevel, parentIndexationKey, origin, entry);

                    if (ResourceHelper.getString("plan.classement.tous.label").equals(entry.getKey())) {
                        listElement.add(0, element);
                    } else {
                        listElement.add(element);
                    }
                }
            }
        }
        return listElement;
    }

    private static List<TreeElementDTO> addElementsWithSizeGreaterThanLimit(
        Map<String, Integer> map,
        Boolean isLastLevel,
        String parentIndexationKey,
        String origin
    ) {
        String parentKey = "";
        Integer count = 0;
        TreeElementDTO parentElement = new TreeElementDTO();
        List<TreeElementDTO> listParentChild = new ArrayList<>();
        List<TreeElementDTO> listElement = new ArrayList<>();
        for (Entry<String, Integer> entry : map.entrySet()) {
            String firstLetter = NOT_ASCII_CHARACTER_PATTERN
                .matcher(Normalizer.normalize(entry.getKey().substring(0, 1).toUpperCase(), Normalizer.Form.NFD))
                .replaceAll("");
            //On vérifie si l'élément en cours à le même parent que le précédent
            //Si ce n'est pas le cas on enregistre l'ancien parent et on en crée un nouveau
            if (!firstLetter.equals(parentKey)) {
                parentKey = firstLetter;
                //On met à jour l'ancien parent
                if (!listParentChild.isEmpty()) {
                    parentElement.setLabel(String.format(LABEL, parentElement.getKey(), count));
                    parentElement.setChilds(listParentChild);
                    listParentChild = new ArrayList<>();
                    count = 0;
                }

                //On crée le nouveau parent
                parentElement = createTreeElementDTO(firstLetter.toUpperCase(), parentIndexationKey, false);
                parentElement.setAction(getAction(parentElement));
                listElement.add(parentElement);
            }
            //On met à jour le count pour le parent
            count += entry.getValue();

            TreeElementDTO element = createTreeElementDTO(isLastLevel, parentIndexationKey, origin, entry);
            if (ResourceHelper.getString("plan.classement.tous.label").equals(entry.getKey())) {
                listElement.add(0, element);
            } else {
                //On ajoute l'élément au parent
                listParentChild.add(element);
            }
        }
        //On met à jour le parent pour le dernier élément
        if (!listParentChild.isEmpty()) {
            parentElement.setLabel(String.format(LABEL, parentElement.getKey(), count));
            parentElement.setChilds(listParentChild);
        }

        return listElement;
    }

    private static TreeElementDTO createTreeElementDTO(
        Boolean isLastLevel,
        String parentIndexationKey,
        String origin,
        Entry<String, Integer> entry
    ) {
        //Pour chaque entrée on crée l'élément
        TreeElementDTO element = createTreeElementDTO(entry.getKey(), parentIndexationKey, isLastLevel);
        element.setLabel(String.format(LABEL, entry.getKey(), entry.getValue()));

        //Si on est au dernier niveau on crée le lien sinon on créé l'action du bouton
        if (BooleanUtils.isTrue(isLastLevel)) {
            setLink(element, origin, entry.getKey(), parentIndexationKey);
        } else {
            element.setAction(getAction(element));
        }
        return element;
    }

    private static TreeElementDTO createTreeElementDTO(String key, String parentIndexationKey, Boolean isLastLevel) {
        TreeElementDTO element = new TreeElementDTO();
        element.setKey(key);
        element.setCompleteKey(parentIndexationKey + "__" + key);
        element.setIsLastLevel(isLastLevel);
        return element;
    }

    private static String getAction(TreeElementDTO element) {
        return JS_ACTION + "(\'" + element.getCompleteKey() + "\')";
    }
}
