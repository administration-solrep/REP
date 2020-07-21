package fr.dila.reponses.core.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ReponsesInjectionGouvernementService;
import fr.dila.reponses.core.util.ExcelUtil;
import fr.dila.ss.api.client.InjectionGvtDTO;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * 
 * @author jbrunet
 * 
 */
public class ReponsesInjectionGouvernementServiceImpl implements ReponsesInjectionGouvernementService {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1639423217943873818L;

	private static final STLogger LOGGER = STLogFactory.getLog(ReponsesInjectionGouvernementServiceImpl.class);

	@Override
	public InjectionGvtDTO getNewGovernment(List<InjectionGvtDTO> listInjection) {
		return listInjection.get(0);
	}

	@Override
	public List<InjectionGvtDTO> getAllNewEntities(List<InjectionGvtDTO> listInjection) {
		List<InjectionGvtDTO> formattedList = new ArrayList<InjectionGvtDTO>();
		for (InjectionGvtDTO entity : listInjection) {
			if (!entity.isGvt() && (entity.isaCreerReponses() || entity.isaModifierReponses())) {
				formattedList.add(entity);
			}
		}
		return formattedList;
	}

	/**
	 * Crée le nouveau gouvernement
	 * 
	 * @param session
	 * @param newGov
	 * @throws ClientException
	 */
	private void createNewGovernment(CoreSession session, InjectionGvtDTO newGov) throws ClientException {
		GouvernementNode gouv = STServiceLocator.getSTGouvernementService().getBareGouvernementModel();

		Calendar cal = Calendar.getInstance();
		cal.setTime(newGov.getDateDeDebut());
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		gouv.setDateDebut(cal.getTime());
		gouv.setLabel(newGov.getLibelleLong());

		STServiceLocator.getSTGouvernementService().createGouvernement(gouv);
		// Récupération de l'id du gouvernement
		newGov.setId(gouv.getId().toString());
	}

	/**
	 * Au sein du nouveau gouvernement créé :
	 * <ul>
	 * <li>crée les items suivants pour chaque ministère: entité du ministère
	 * (timbre) entité structurelle de même nom poste BDC de même nom</li>
	 * <li>Modifie l'ordre protocolaire des entités identifiées comme à modifier
	 * Réponses
	 * </ul>
	 * 
	 * @param session
	 * @param newEntities
	 * @param newGov
	 * @throws ClientException
	 */
	private void createNewEntities(CoreSession session, List<InjectionGvtDTO> newEntities, InjectionGvtDTO newGov)
			throws ClientException {
		final STMinisteresService minService = STServiceLocator.getSTMinisteresService();
		final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
		final STPostesService postesService = STServiceLocator.getSTPostesService();
		EntiteNode entiteNode = null;

		final Calendar cal = Calendar.getInstance();

		for (InjectionGvtDTO newEntity : newEntities) {
			if (newEntity.isaModifierReponses()) {
				// Modification immédiate de l'ordre protocolaire (on a toutes les infos)
				entiteNode = minService.getEntiteNode(newEntity.getIdOrganigramme());
				Long newOrdreProtocolaire = new Long(newEntity.getOrdreProtocolaireReponses());
				entiteNode.setOrdre(newOrdreProtocolaire);
				minService.updateEntite(entiteNode);
			} else {
				// Création du ministère
				entiteNode = minService.getBareEntiteModel();
				// ajout du parent
				entiteNode.setParentGouvernement(newGov.getId());
				// Création
				cal.setTime(newEntity.getDateDeDebut());
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				entiteNode.setDateDebut(cal.getTime());
				entiteNode.setLabel(newEntity.getLibelleLong());
				entiteNode.setFormule(newEntity.getFormule());
				entiteNode.setEdition(newEntity.getLibelleCourt());
				entiteNode.setMembreGouvernementCivilite(newEntity.getCivilite());
				entiteNode.setMembreGouvernementPrenom(newEntity.getPrenom());
				entiteNode.setMembreGouvernementNom(newEntity.getNom());
				entiteNode.setOrdre(Long.parseLong(newEntity.getOrdreProtocolaireReponses()));
				entiteNode = minService.createEntite(entiteNode);

				// Création de l'entité structurelle
				UniteStructurelleNode ustNode = usService.getBareUniteStructurelleModel();

				// ajout du parent
				ustNode.setParentEntiteId(entiteNode.getId());
				// Création
				ustNode.setDateDebut(cal.getTime());
				ustNode.setLabel(entiteNode.getEdition());
				ustNode.setType(OrganigrammeType.DIRECTION);
				ustNode = usService.createUniteStructurelle(ustNode);

				// Création du poste BDC
				PosteNode posteNode = postesService.getBarePosteModel();

				// ajout du parent
				posteNode.setParentUniteId(ustNode.getId());
				// Création
				posteNode.setChargeMissionSGG(false);
				posteNode.setConseillerPM(false);
				posteNode.setPosteBdc(true);
				posteNode.setPosteWs(false);
				posteNode.setDateDebut(cal.getTime());
				posteNode.setLabel("BDC " + entiteNode.getEdition());
				postesService.createPoste(session, posteNode);
			}
		}

	}

	@Override
	public List<InjectionGvtDTO> prepareInjection(CoreSession session, File file) throws ClientException {
		List<InjectionGvtDTO> listInjectionResult = ExcelUtil.prepareImportGvt(session, file);
		verifierDTO(listInjectionResult, session);
		return listInjectionResult;
	}

	@Override
	public void executeInjection(CoreSession session, List<InjectionGvtDTO> listInjection) throws ClientException {
		try {
			createNewGovernment(session, getNewGovernment(listInjection));
			createNewEntities(session, getAllNewEntities(listInjection), getNewGovernment(listInjection));
		} catch (ClientException e) {
			throw new ClientException("Echec lors de l'injection du gouvernement", e);
		}
	}

	/**
	 * Permet de vérifier la présence des données obligatoires dans le DTO
	 * 
	 * @param listInjection
	 * @throws ClientException
	 */
	private void verifierDTO(List<InjectionGvtDTO> listInjection, CoreSession session) throws ClientException {
		// Vérification du Gouvernement
		InjectionGvtDTO injGouv = getNewGovernment(listInjection);
		if (injGouv.getLibelleLong() == null || injGouv.getDateDeDebut() == null) {
			throw new ClientException("Données manquantes pour le gouvernement");
		}
		// Vérification des entités
		for (InjectionGvtDTO injMin : getAllNewEntities(listInjection)) {
			if (injMin.getCivilite() == null || injMin.getPrenom() == null || injMin.getNom() == null
					|| injMin.getFormule() == null || injMin.getOrdreProtocolaireReponses() == null
					|| injMin.getDateDeDebut() == null || injMin.getLibelleCourt() == null
					|| injMin.getLibelleLong() == null) {
				String entiteName = (injMin.getLibelleLong() == null ? (injMin.getLibelleCourt() == null ? "Inconnue"
						: injMin.getLibelleCourt()) : injMin.getLibelleLong());
				throw new ClientException("Données manquantes pour l'entité : " + entiteName);
			}
			
			if (injMin.isaModifierReponses()) {
				// On vérifie et on renseigne l'ordre protocolaire avant
				// modification de l'entité
				injMin.setOldOrdreProtocolaireReponses(STServiceLocator.getSTMinisteresService()
						.getEntiteNode(injMin.getIdOrganigramme()).getOrdre().toString());
			}
		}
	}

}
