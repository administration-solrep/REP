package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getFavorisDossierService;
import static fr.dila.reponses.core.service.ReponsesServiceLocator.getProfilUtilisateurService;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_IDS;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_IDS_STRING;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.FAVORIS_ID;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.FAVORIS_TRAVAIL_FORM;
import static fr.dila.reponses.ui.helper.RepDossierListHelper.buildDossierList;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.ui.bean.FavorisTravailDTO;
import fr.dila.reponses.ui.bean.FavorisTravailList;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.FavorisDossierUIService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.FavorisTravailForm;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class FavorisDossierUIServiceImpl implements FavorisDossierUIService {
    private static final String NULL_MSG_FMT = "null %s";

    @Override
    public FavorisTravailList getFavoris(SpecificContext context) {
        List<DocumentModel> reps = getFavorisDossierService().getFavorisRepertoires(context.getSession());
        FavorisTravailList list = new FavorisTravailList();
        list.setListe(
            reps.stream().map(this::toDto).sorted(Comparator.comparing(FavorisTravailDTO::getNom)).collect(toList())
        );
        return list;
    }

    @Override
    public List<SelectValueDTO> getFavorisSelectValueDtos(SpecificContext context) {
        return getFavorisDossierService()
            .getFavorisRepertoires(context.getSession())
            .stream()
            .map(f -> new SelectValueDTO(f.getId(), f.getTitle()))
            .collect(toList());
    }

    private FavorisTravailDTO toDto(DocumentModel favoriDoc) {
        FavorisTravailDTO dto = new FavorisTravailDTO();
        dto.setId(favoriDoc.getId());
        dto.setNom(favoriDoc.getTitle());
        dto.setLink("/favoris/listeDossiers?idFavori=" + favoriDoc.getId() + "#main_content");
        return dto;
    }

    @Override
    public RepDossierList getDossierList(SpecificContext context) {
        String idFavori = context.getFromContextData("idFavori");
        DossierListForm form = context.getFromContextData("form");

        CoreSession session = context.getSession();
        AbstractDTOPageProvider provider = form.getPageProvider(
            session,
            "favorisdossier_repertoire_content",
            "q.",
            asList(idFavori)
        );
        List<Map<String, Serializable>> dtos = provider.getCurrentPage();

        return buildDossierList(
            dtos,
            ResourceHelper.getString("label.favoris.travail.title"),
            session.getDocument(new IdRef(idFavori)).getTitle(),
            form,
            getProfilUtilisateurService().getUserColumn(session),
            (int) provider.getResultsCount(),
            false
        );
    }

    @Override
    public void createAndAddFavoris(SpecificContext context) {
        FavorisTravailForm form = context.getFromContextData(FAVORIS_TRAVAIL_FORM);
        validateForm(form);

        FavorisDossierService favService = getFavorisDossierService();
        CoreSession session = context.getSession();
        String nomFavori = form.getNomFavoris();
        Calendar dateFin = isNotBlank(form.getDateFin())
            ? SolonDateConverter.DATE_SLASH.parseToCalendar(form.getDateFin())
            : null;

        if (favService.isNomFavoriLibre(session, nomFavori)) {
            favService.createFavorisRepertoire(session, nomFavori, dateFin);
        } else {
            context.getMessageQueue().addErrorToQueue(getString("favoris.erreur.name"));
            return;
        }

        List<String> ids = asList(form.getIdDossiers().split(";"));
        favService.add(session, ids, nomFavori);

        context.getMessageQueue().addToastSuccess(getString("favoris.add.success"));
    }

    @Override
    public void addFavoris(SpecificContext context) {
        String favoriId = context.getFromContextData(FAVORIS_ID);
        String idDossiers = context.getFromContextData(DOSSIER_IDS_STRING);
        requireNonNull(favoriId, format(NULL_MSG_FMT, FAVORIS_ID.getName()));
        requireNonNull(idDossiers, format(NULL_MSG_FMT, DOSSIER_IDS_STRING.getName()));

        CoreSession session = context.getSession();

        List<String> ids = asList(idDossiers.split(";"));
        String favoriName = session.getDocument(new IdRef(favoriId)).getTitle();
        getFavorisDossierService().add(session, ids, favoriName);

        context.getMessageQueue().addToastSuccess(getString("favoris.add.success"));
    }

    @Override
    public void removeFavoris(SpecificContext context) {
        String favoriId = context.getFromContextData(FAVORIS_ID);
        requireNonNull(favoriId, format(NULL_MSG_FMT, FAVORIS_ID.getName()));
        List<String> idDossiers = context.getFromContextData(DOSSIER_IDS);
        requireNonNull(idDossiers, format(NULL_MSG_FMT, DOSSIER_IDS.getName()));

        getFavorisDossierService().delete(context.getSession(), favoriId, idDossiers);

        context.getMessageQueue().addToastSuccess(getString("label.favoris.travail.dossier.remove.success"));
    }

    private void validateForm(FavorisTravailForm form) {
        if (isBlank(form.getNomFavoris())) {
            throw new STValidationException("favoris.error.name.mandatory");
        }
        if (isBlank(form.getIdDossiers())) {
            throw new STValidationException(getString("favoris.error.ids.dossiers.mandatory"));
        }
    }
}
