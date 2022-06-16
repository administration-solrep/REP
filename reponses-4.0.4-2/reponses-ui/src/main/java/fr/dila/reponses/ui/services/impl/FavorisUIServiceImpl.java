package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.api.constant.ReponsesSchemaConstant.INDEXATION_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.ReponsesSchemaConstant.INDEXATION_NIVEAU2;
import static fr.dila.reponses.api.constant.ReponsesSchemaConstant.INDEXATION_SCHEMA;
import static fr.dila.reponses.api.favoris.FavorisIndexation.TypeIndexation.AN;
import static fr.dila.reponses.api.favoris.FavorisIndexation.TypeIndexation.SENAT;
import static fr.dila.reponses.ui.helper.TreeElementDTOHelper.setLink;
import static fr.dila.st.core.util.ObjectHelper.requireNonNullElseGet;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.nuxeo.ecm.core.api.DocumentModelComparator.ORDER_ASC;

import fr.dila.reponses.api.favoris.FavorisIndexation;
import fr.dila.reponses.api.service.FavorisIndexationService;
import fr.dila.reponses.ui.bean.FavorisPlanClassementDTO;
import fr.dila.reponses.ui.services.FavorisUIService;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

public class FavorisUIServiceImpl implements FavorisUIService {

    @Override
    public FavorisPlanClassementDTO getFavorisDTO(CoreSession session) {
        Map<String, List<FavorisIndexation>> mapAN = new HashMap<>();
        Map<String, List<FavorisIndexation>> mapSE = new HashMap<>();

        DocumentRef favorisIndexationRootRef = ServiceUtil
            .getRequiredService(FavorisIndexationService.class)
            .getFavorisIndexationRootRef(session);
        DocumentModelComparator comp = new DocumentModelComparator(
            INDEXATION_SCHEMA,
            singletonMap(INDEXATION_NIVEAU2, ORDER_ASC)
        );
        DocumentModelList favorisDocList = session.getChildren(
            favorisIndexationRootRef,
            INDEXATION_DOCUMENT_TYPE,
            null,
            comp
        );
        favorisDocList.forEach(
            f -> {
                FavorisIndexation favorisIndexation = f.getAdapter(FavorisIndexation.class);
                String niv1 = favorisIndexation.getNiveau1();
                if (AN.name().equals(favorisIndexation.getTypeIndexation())) {
                    addFavDocToMap(favorisIndexation, mapAN, niv1);
                } else if (SENAT.name().equals(favorisIndexation.getTypeIndexation())) {
                    addFavDocToMap(favorisIndexation, mapSE, niv1);
                }
            }
        );

        FavorisPlanClassementDTO root = new FavorisPlanClassementDTO();

        TreeElementDTO anDto = new TreeElementDTO();
        anDto.setKey("AN");
        anDto.setLabel("Assemblée nationale");
        anDto.setChilds(getChildren(mapAN));
        root.setAssemblee(anDto);

        TreeElementDTO senatDto = new TreeElementDTO();
        senatDto.setKey("SENAT");
        senatDto.setLabel("Sénat");
        senatDto.setChilds(getChildren(mapSE));
        root.setSenat(senatDto);

        return root;
    }

    private void addFavDocToMap(FavorisIndexation fav, Map<String, List<FavorisIndexation>> map, String key) {
        List<FavorisIndexation> favNiv1 = requireNonNullElseGet(map.get(key), ArrayList::new);
        favNiv1.add(fav);
        map.put(key, favNiv1);
    }

    private List<TreeElementDTO> getChildren(Map<String, List<FavorisIndexation>> map) {
        List<TreeElementDTO> children = new ArrayList<>();
        for (Entry<String, List<FavorisIndexation>> niveau1 : map.entrySet()) {
            TreeElementDTO dtoN1 = new TreeElementDTO();
            dtoN1.setKey(niveau1.getKey());
            dtoN1.setLabel(niveau1.getKey());
            dtoN1.setChilds(niveau1.getValue().stream().map(this::toElementDto).collect(toList()));
            children.add(dtoN1);
        }
        return children;
    }

    private TreeElementDTO toElementDto(FavorisIndexation fav) {
        TreeElementDTO elem = new TreeElementDTO();
        elem.setKey(fav.getDocument().getId());
        String niv2 = fav.getNiveau2();
        elem.setLabel(niv2);
        setLink(elem, fav.getTypeIndexation(), niv2, fav.getNiveau1());
        return elem;
    }
}
