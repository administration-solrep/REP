package fr.dila.reponses.core.export;

import fr.dila.reponses.core.export.enums.RepExcelSheetName;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.st.core.export.AbstractEnumExportConfig;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public class RepRechercheDossierConfig extends AbstractEnumExportConfig<RepDossierListingDTO> {

    public RepRechercheDossierConfig(List<RepDossierListingDTO> dossiers) {
        super(dossiers, RepExcelSheetName.RECHERCHE_DOSSIER);
    }

    @Override
    protected String[] getDataCells(CoreSession session, RepDossierListingDTO item) {
        return new String[] {
            item.getOrigineQuestion(),
            item.getSourceNumeroQuestion(),
            item.getTypeQuestion(),
            item.getDatePublicationJO() != null
                ? SolonDateConverter.DATE_SLASH.format(item.getDatePublicationJO())
                : null,
            item.getAuteur(),
            item.getMinistereAttributaire(),
            item.getDirectionRunningStep(),
            item.getMotCles(),
            item.getDelai(),
            item.getEtatQuestion(),
            item.getRoutingTaskType()
        };
    }
}
