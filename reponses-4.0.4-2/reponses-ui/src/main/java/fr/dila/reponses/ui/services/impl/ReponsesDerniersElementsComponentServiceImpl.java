package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.ReponsesDerniersElementsComponentService;
import fr.dila.ss.api.recherche.IdLabel;
import fr.dila.ss.ui.bean.DernierElementDTO;
import fr.dila.ss.ui.services.impl.SSDerniersElementsComponentServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ReponsesDerniersElementsComponentServiceImpl
    extends SSDerniersElementsComponentServiceImpl
    implements ReponsesDerniersElementsComponentService {

    @Override
    public List<DernierElementDTO> getDernierElementsDTOFromDocs(SpecificContext context) {
        List<DocumentModel> docs = context.getFromContextData(SESSION_KEY);
        Set<String> dossiersDirecteur = ReponsesServiceLocator
            .getAllotissementService()
            .extractDossiersDirecteurs(docs, context.getSession());
        return docs
            .stream()
            .map(doc -> getDernierElementDTOFromDoc(context, dossiersDirecteur, doc))
            .collect(Collectors.toList());
    }

    private DernierElementDTO getDernierElementDTOFromDoc(
        SpecificContext context,
        Set<String> dossiersDirecteur,
        DocumentModel doc
    ) {
        DernierElementDTO dto = new DernierElementDTO();
        Dossier dossier = doc.getAdapter(Dossier.class);
        Question question = dossier.getQuestion(context.getSession());
        Reponse rep = dossier.getReponse(context.getSession());
        dto.setLabel(question.getSourceNumeroQuestion());
        dto.setId(doc.getId());
        dto.setCategorie("parapheur");
        dto.setCaseLinkIdsLabels(
            ReponsesServiceLocator
                .getCorbeilleService()
                .findDossierLinkUnrestricted(context.getSession(), doc.getId())
                .stream()
                .map(dm -> new IdLabel(dm.getId(), dm.getAdapter(DossierLink.class).getRoutingTaskLabel(), doc.getId()))
                .collect(Collectors.toList())
        );

        dto.setExposant(getExposant(dossier, rep, dossiersDirecteur));
        return dto;
    }

    private String getExposant(Dossier dossier, Reponse rep, Set<String> dossiersDirecteur) {
        String err = CollectionUtils.isNotEmpty(rep.getErrata()) ? "Err" : StringUtils.EMPTY;
        String rdm = dossier.isRedemarre() ? "Rdm" : StringUtils.EMPTY;
        String directeur = dossiersDirecteur.contains(dossier.getDocument().getId()) ? "*" : StringUtils.EMPTY;
        return Stream.of(err, directeur, rdm).collect(Collectors.joining(StringUtils.SPACE));
    }
}
