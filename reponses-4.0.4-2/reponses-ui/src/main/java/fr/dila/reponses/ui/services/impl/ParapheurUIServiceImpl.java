package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getReponseService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getParapheurActionService;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static java.util.stream.Collectors.toList;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.services.ParapheurUIService;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ParapheurUIServiceImpl implements ParapheurUIService {

    @Override
    public ParapheurDTO getParapheur(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        Reponse reponse = dossier.getReponse(session);
        Question question = dossier.getQuestion(session);

        //Création du DTO avec les données de la question
        ParapheurDTO dto = MapDoc2Bean.docToBean(question.getDocument(), ParapheurDTO.class);

        MapDoc2Bean.docToBean(reponse.getDocument(), dto);
        dto.setIsEdit(getParapheurActionService().canSaveDossier(context, dossierDoc));
        dto.setVersion(toSelectValueDTO(reponse.getDocument()));
        dto.setPublished(getReponseService().isReponsePublished(session, dossierDoc));
        dto.setHasErrata(CollectionUtils.isNotEmpty(reponse.getErrata()));
        dto.setReponseType(getString(getTypeReponse(dto, question, reponse)));

        return dto;
    }

    @Override
    public List<SelectValueDTO> getVersionDTOs(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        CoreSession session = context.getSession();
        Reponse reponse = dossier.getReponse(session);

        List<DocumentModel> versionDocs = getReponseService()
            .getReponseVersionDocumentList(session, reponse.getDocument());

        List<SelectValueDTO> versions = versionDocs
            .stream()
            .map(ParapheurUIServiceImpl::toSelectValueDTO)
            .collect(toList());
        SelectValueDTO defaultVersion = toSelectValueDTO(reponse.getDocument()); // version en cours
        versions.add(versions.size(), defaultVersion); // mettre la version en cours à la fin de la liste
        return versions;
    }

    public static SelectValueDTO toSelectValueDTO(DocumentModel reponseDoc) {
        String label = reponseDoc.isVersion()
            ? getVersionLabel(reponseDoc)
            : getString("parapheur.reponse.version.en.cours");
        return new SelectValueDTO(reponseDoc.getId(), label);
    }

    private static String getVersionLabel(DocumentModel reponseDoc) {
        int versionNum = getReponseService().getReponseMajorVersionNumber(reponseDoc.getCoreSession(), reponseDoc);
        String auteur = reponseDoc.getAdapter(Reponse.class).getAuteur();
        return String.format("%d - %s", versionNum, auteur);
    }

    private static String getTypeReponse(ParapheurDTO dto, Question question, Reponse reponse) {
        String reponseType = "";
        if (dto.isHasErrata()) {
            if (reponse.getErrata().stream().allMatch(erratum -> erratum.getDatePublication() != null)) {
                reponseType = "parapheur.type.reponses.erratum.publie";
            } else {
                reponseType = "parapheur.type.reponses.erratum.enCours";
            }
        } else {
            if (dto.getPublished()) {
                reponseType = "parapheur.type.reponses.publie";
            } else if (Boolean.TRUE.equals(question.isRepondue())) {
                reponseType = "parapheur.type.reponses.transmise";
            } else {
                reponseType = "parapheur.type.reponses.enCours";
            }
        }
        return reponseType;
    }
}
