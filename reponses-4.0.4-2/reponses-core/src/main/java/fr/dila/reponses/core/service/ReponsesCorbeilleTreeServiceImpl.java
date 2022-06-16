package fr.dila.reponses.core.service;

import static fr.dila.cm.mailbox.MailboxConstants.ID_FIELD;
import static fr.dila.reponses.api.constant.ReponsesConstant.REPONSES_MAILBOX_PRECALCUL_LIST_XPATH;
import static fr.dila.reponses.core.service.ReponsesServiceLocator.getCorbeilleService;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.mailbox.PreComptage;
import fr.dila.reponses.api.mailbox.ReponsesMailbox;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService;
import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.core.util.SSMailboxUtils;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.core.api.CoreSession;

public class ReponsesCorbeilleTreeServiceImpl implements ReponsesCorbeilleTreeService {

    @Override
    public Map<String, Integer> getCorbeilleTreeNiveau1(
        CoreSession session,
        TypeRegroupement regroupement,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        Map<String, Integer> tree = new LinkedHashMap<>();

        if (regroupement == TypeRegroupement.PAR_MINISTERE) {
            Map<String, Integer> tempTree = getCorbeilleTreeNiveau1Ministeres(
                session,
                corbeilleSelectionPoste,
                corbeilleSelectionUser
            );
            SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
            if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER)) {
                tree = tempTree;
            } else {
                tree = filterMinistereTreeForUser(session, corbeilleSelectionPoste, corbeilleSelectionUser, tempTree);
            }
        } else if (regroupement == TypeRegroupement.PAR_POSTE) {
            tree = getCorbeilleTreeNiveau1Postes(session, corbeilleSelectionPoste, corbeilleSelectionUser);
        } else if (regroupement == TypeRegroupement.PAR_SIGNALE) {
            tree = getCorbeilleTreeNiveau1Signale(session, corbeilleSelectionPoste, corbeilleSelectionUser);
        }

        return tree;
    }

    @Override
    public Map<String, Integer> getCorbeilleTreeNiveau2(
        CoreSession session,
        TypeRegroupement regroupement,
        String parentId,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        if (regroupement == TypeRegroupement.PAR_MINISTERE) {
            Collection<Map<String, List<PreComptage>>> precomptageMapList = retrievePrecomptageForSelectedPostes(
                session,
                corbeilleSelectionPoste,
                corbeilleSelectionUser
            )
                .values();
            return getCorbeilleTreeNiveau2Etapes(parentId, precomptageMapList);
        }

        Map<String, Integer> tree = new LinkedHashMap<>();
        Map<String, Integer> tempTree = new LinkedHashMap<>();
        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();

        if (regroupement == TypeRegroupement.PAR_POSTE) {
            Map<String, Map<String, List<PreComptage>>> precomptageMap = retrievePrecomptageForSelectedPostes(
                session,
                corbeilleSelectionPoste,
                corbeilleSelectionUser
            );
            tempTree = computePrecomptageForMinisteresByPoste(precomptageMap, parentId);
        } else if (regroupement == TypeRegroupement.PAR_SIGNALE) {
            tempTree =
                getCorbeilleTreeNiveau2MinisteresParSignale(
                    session,
                    EtatSignalement.QUESTIONS_SIGNALEES.toString().equals(parentId),
                    corbeilleSelectionPoste,
                    corbeilleSelectionUser
                );
        }

        List<EntiteNode> ministereList = ministeresService.getCurrentMinisteres();
        Collections.sort(ministereList, new ProtocolarOrderComparator());
        final Set<String> postesId = SSMailboxUtils.getSelectedPostes(
            (SSPrincipal) session.getPrincipal(),
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );
        List<EntiteNode> ministeresUser = ministeresService.getMinistereParentFromPostes(postesId);
        Set<String> tempTreeUser = new HashSet<>();
        for (EntiteNode ministereNode : ministeresUser) {
            tempTreeUser.add(ministereNode.getId());
        }
        // Récupération de la liste des ministères
        for (EntiteNode entiteNode : ministereList) {
            if (tempTreeUser.contains(entiteNode.getId()) || tempTree.containsKey(entiteNode.getId())) {
                Integer count = tempTree.get(entiteNode.getId());
                tree.put(entiteNode.getId(), count != null ? count.intValue() : 0);
                tempTree.remove(entiteNode.getId());
            }
        }
        if (!tempTree.isEmpty()) {
            for (Entry<String, Integer> entry : tempTree.entrySet()) {
                tree.put(entry.getKey(), entry.getValue());
            }
        }
        return tree;
    }

    /**
     * Retourne une map avec tous les ministères du gouvernement actuel + les
     * ministères d'anciens gouvernements qui ont des données de précomptage
     *
     * @param session
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    private Map<String, Integer> getCorbeilleTreeNiveau1Ministeres(
        CoreSession session,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();

        Map<String, Integer> tree = new LinkedHashMap<>();
        List<EntiteNode> ministereList = ministeresService.getCurrentMinisteres();
        Collections.sort(ministereList, new ProtocolarOrderComparator());

        Map<String, Map<String, List<PreComptage>>> preComptageMap = retrievePrecomptageForSelectedPostes(
            session,
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );
        Map<String, Long> precomptageMapList = computePrecomptageForSelectedPostesByMinistere(preComptageMap);

        // Récupération de la liste des ministères
        for (EntiteNode entiteNode : ministereList) {
            Long preComptage = precomptageMapList.get(entiteNode.getId());
            tree.put(entiteNode.getId(), preComptage != null ? preComptage.intValue() : 0);
            precomptageMapList.remove(entiteNode.getId());
        }

        for (String ministereId : precomptageMapList.keySet()) {
            for (Map<String, List<PreComptage>> map : preComptageMap.values()) {
                for (Entry<String, List<PreComptage>> entry : map.entrySet()) {
                    if (entry.getKey().equals(ministereId)) {
                        Long count = 0L;
                        for (PreComptage preComptage : entry.getValue()) {
                            count += preComptage.getCount();
                        }
                        OrganigrammeNode node = ministeresService.getEntiteNode(ministereId);
                        if (node != null) {
                            tree.put(node.getId(), count.intValue());
                        }
                    }
                }
            }
        }

        return tree;
    }

    /**
     * Filtre la map des précomptages par ministère en gardant uniquement les
     * ministères des postes de l'utilisateur (même si vides) + les ministères
     * attributaires du reste des dossierLinks
     *
     * @param session
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @param tempTree
     * @return
     */
    private Map<String, Integer> filterMinistereTreeForUser(
        CoreSession session,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser,
        Map<String, Integer> tempTree
    ) {
        Map<String, Integer> result = new LinkedHashMap<>();
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();

        Set<String> tempTreeUser = getMinisteresParentsFromPostes(
            ssPrincipal,
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );

        // Remplissage de l'arbre avec les ministeres accessibles par l'utilisateur
        for (Entry<String, Integer> entry : tempTree.entrySet()) {
            if (tempTreeUser.contains(entry.getKey())) {
                // ajout du ministère du user
                result.put(entry.getKey(), entry.getValue());
            }
            if (entry.getValue() != 0 && !result.containsKey(entry.getKey())) {
                // ajout du ministère accessible car assigné au poste du user
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    @Override
    public Set<String> getMinisteresParentsFromPostes(
        SSPrincipal ssPrincipal,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        Set<String> result = new HashSet<>();
        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
        Set<String> postesId = SSMailboxUtils.getSelectedPostes(
            ssPrincipal,
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );

        List<EntiteNode> ministeresUser = ministeresService.getMinistereParentFromPostes(postesId);

        for (EntiteNode ministereNode : ministeresUser) {
            result.add(ministereNode.getId());
        }
        return result;
    }

    /**
     * Retourne une map avec le nombre de dossierLink par poste de l'utilisateur
     *
     * @param session
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    private Map<String, Integer> getCorbeilleTreeNiveau1Postes(
        CoreSession session,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        Map<String, Integer> tree = new LinkedHashMap<>();
        Map<String, Map<String, List<PreComptage>>> preComptageMap = retrievePrecomptageForSelectedPostes(
            session,
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );
        Set<String> postesId = SSMailboxUtils.getSelectedPostes(
            (SSPrincipal) session.getPrincipal(),
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );
        for (String posteId : postesId) {
            Long count = 0L;
            Map<String, List<PreComptage>> postePreComptage = preComptageMap.get(posteId);
            if (postePreComptage != null) {
                for (List<PreComptage> preComptages : postePreComptage.values()) {
                    for (PreComptage preComptage : preComptages) {
                        count += preComptage.getCount();
                    }
                }
            }
            tree.put(posteId, count.intValue());
        }
        return tree;
    }

    /**
     * Retourne une map avec le nombre de dossierLink signalés et non signalés
     *
     * @param session
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    private Map<String, Integer> getCorbeilleTreeNiveau1Signale(
        CoreSession session,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        Map<String, Integer> tree = new LinkedHashMap<>();
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        Set<String> postesId = SSMailboxUtils.getSelectedPostes(
            ssPrincipal,
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );

        int countSignale = corbeilleService.countDossierLinksSignalesForPostes(session, postesId);
        int countNonSignale = corbeilleService.countDossierLinksNonSignalesForPostes(session, postesId);

        tree.put(EtatSignalement.QUESTIONS_SIGNALEES.toString(), countSignale);
        tree.put(EtatSignalement.QUESTIONS_NON_SIGNALEES.toString(), countNonSignale);

        return tree;
    }

    /**
     * Retourne une map avec le nombre de dossierLink par étape du ministères
     *
     * @param ministereId
     * @param precomptageMapList
     * @return
     */
    private Map<String, Integer> getCorbeilleTreeNiveau2Etapes(
        String ministereId,
        Collection<Map<String, List<PreComptage>>> precomptageMapList
    ) {
        return precomptageMapList
            .stream()
            .map(precomptageMap -> precomptageMap.get(ministereId))
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(
                Collectors.toMap(
                    PreComptage::getRoutingTaskType,
                    preComptage -> preComptage.getCount() != null ? preComptage.getCount().intValue() : 0,
                    Integer::sum,
                    LinkedHashMap::new
                )
            );
    }

    /**
     * Retourne une map avec le nombre de dossierLink par ministère selon l'état de
     * signalement
     *
     * @param session
     * @param signale
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    private Map<String, Integer> getCorbeilleTreeNiveau2MinisteresParSignale(
        CoreSession session,
        Boolean signale,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        final Set<String> postesId = SSMailboxUtils.getSelectedPostes(
            (SSPrincipal) session.getPrincipal(),
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );
        if (BooleanUtils.isTrue(signale)) {
            return getCorbeilleService().mapCountDossierLinksSignalesToMinisteres(session, postesId);
        } else {
            return getCorbeilleService().mapCountDossierLinksNonSignalesToMinisteres(session, postesId);
        }
    }

    /**
     * Retourne les informations de précomptage pour les postes sélectionnés.
     *
     * @param session
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    private Map<String, Map<String, List<PreComptage>>> retrievePrecomptageForSelectedPostes(
        CoreSession session,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        Set<String> postesId = SSMailboxUtils.getSelectedPostes(
            (SSPrincipal) session.getPrincipal(),
            corbeilleSelectionPoste,
            corbeilleSelectionUser
        );

        List<Mailbox> mailboxes;
        if (corbeilleSelectionPoste != null || corbeilleSelectionUser != null) {
            mailboxes =
                mailboxPosteService.getMailboxPosteListUnrestricted(
                    session,
                    postesId,
                    REPONSES_MAILBOX_PRECALCUL_LIST_XPATH,
                    ID_FIELD
                );
        } else {
            mailboxes =
                mailboxPosteService.getMailboxPosteList(
                    session,
                    postesId,
                    REPONSES_MAILBOX_PRECALCUL_LIST_XPATH,
                    ID_FIELD
                );
        }

        Map<String, Map<String, List<PreComptage>>> precomptageMapList = new LinkedHashMap<>();

        for (Mailbox mailbox : mailboxes) {
            Map<String, List<PreComptage>> precomptageMap =
                ((ReponsesMailbox) mailbox).getPreComptagesGroupByMinistereId();
            if (!precomptageMap.isEmpty()) {
                precomptageMapList.put(mailboxPosteService.getPosteIdFromMailboxId(mailbox.getId()), precomptageMap);
            }
        }
        return precomptageMapList;
    }

    /**
     * Retourne le nombre de dossiers par ministère à partir des données de
     * précomptage.
     *
     * @param retrievePrecomptage
     * @return
     */
    private Map<String, Long> computePrecomptageForSelectedPostesByMinistere(
        Map<String, Map<String, List<PreComptage>>> retrievePrecomptage
    ) {
        Map<String, Long> result = new HashMap<>();
        for (Map<String, List<PreComptage>> map : retrievePrecomptage.values()) {
            for (Entry<String, List<PreComptage>> entry : map.entrySet()) {
                Long comptage = result.get(entry.getKey()) != null ? result.get(entry.getKey()) : 0L;
                for (PreComptage preComptage : entry.getValue()) {
                    comptage += preComptage.getCount() != null ? preComptage.getCount() : 0L;
                }
                result.put(entry.getKey(), comptage);
            }
        }

        return result;
    }

    /**
     * Retourne le nombre de dossiers par ministère pour un poste donné à partir des
     * données de précomptage.
     *
     * @param retrievePrecomptage
     * @param posteId
     * @return
     */
    private Map<String, Integer> computePrecomptageForMinisteresByPoste(
        Map<String, Map<String, List<PreComptage>>> retrievePrecomptage,
        String posteId
    ) {
        Map<String, Integer> result = new HashMap<>();
        Map<String, List<PreComptage>> postePreComptage = retrievePrecomptage.get(posteId);
        if (postePreComptage != null) {
            for (Entry<String, List<PreComptage>> entry : postePreComptage.entrySet()) {
                Long count = 0L;
                for (PreComptage preComptage : entry.getValue()) {
                    count += preComptage.getCount();
                }
                result.put(entry.getKey(), count.intValue());
            }
        }
        return result;
    }
}
