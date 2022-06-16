package fr.dila.reponses.ui.helper;

import fr.dila.reponses.api.enumeration.IndexationTypeEnum;
import fr.dila.reponses.ui.bean.RechercheDTO;
import fr.dila.reponses.ui.th.bean.recherche.BlocDatesForm;
import fr.dila.reponses.ui.th.bean.recherche.FeuilleRouteForm;
import fr.dila.reponses.ui.th.bean.recherche.MotsClesForm;
import fr.dila.reponses.ui.th.bean.recherche.RechercheGeneraleForm;
import fr.dila.reponses.ui.th.bean.recherche.TexteIntegralForm;
import fr.dila.st.core.util.ResourceHelper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public final class RechercheHelper {

    private RechercheHelper() {
        // Default constructor
    }

    public static RechercheDTO convertTo(Map<String, Object> sessionSearchForms) {
        return Optional.ofNullable(sessionSearchForms).map(RechercheHelper::create).orElse(null);
    }

    private static RechercheDTO create(Map<String, Object> sessionSearchForms) {
        RechercheDTO dto = new RechercheDTO();
        dto.setRechercheGeneraleForm((RechercheGeneraleForm) sessionSearchForms.get("general"));
        dto.setBlocDatesForm((BlocDatesForm) sessionSearchForms.get("dates"));
        dto.setMotsClesForm((MotsClesForm) sessionSearchForms.get("motsCles"));
        dto.setFeuilleRouteForm((FeuilleRouteForm) sessionSearchForms.get("feuilleRoute"));
        dto.setTexteIntegralForm((TexteIntegralForm) sessionSearchForms.get("texteIntegral"));
        return dto;
    }

    public static List<String> getMotsClesByIndexationType(List<String> motsCles, IndexationTypeEnum indexationType) {
        String indexationTypeLabel = ResourceHelper.getString(indexationType.getLabelKey());
        return motsCles
            .stream()
            .filter(motCle -> motCle.startsWith(indexationTypeLabel))
            .map(motCle -> StringUtils.substringAfter(motCle, indexationTypeLabel + " : "))
            .collect(Collectors.toList());
    }
}
