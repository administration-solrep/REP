package fr.dila.reponses.core.event.batch;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.api.extraction.Auteur;
import fr.dila.reponses.api.extraction.ExtractionQuestionsResponse;
import fr.dila.reponses.api.extraction.Question;
import fr.dila.reponses.api.extraction.QuestionJAXB;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ExtractionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * 
 * Listener d'extraction des questions 
 * evenement : extractionQuestionsEvent
 *
 */
public class ExtractionQuestionsListener extends AbstractBatchEventListener {
	
	private static final STLogger LOGGER = STLogFactory.getLog(ExtractionQuestionsListener.class);
	
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static final String QTYPE_PROPERTY = "QTYPE";
	private static final String QSTATUT_PROPERTY = "QSTATUT";
	private static final String QSOURCE_PROPERTY = "QSOURCE";
	
	enum StatutsEnum {
		EN_COURS("ENCOURS_ADISPO"),
		CLOSES("CLOSES");
		
		private String statut;
		
		StatutsEnum(String status) {
			this.statut = status;
		}
		
		@Override
		public String toString() {
			return this.statut;
		}
	}

	public ExtractionQuestionsListener() {
		super(LOGGER, ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT);
	}

	@Override
	protected void processEvent(final CoreSession session,final Event event) throws ClientException {
		LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_EXTRACTION_QUESTIONS_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();

        final ExtractionService repExtract = ReponsesServiceLocator.getExtractionService();
        final String type = (String) event.getContext().getProperty(QTYPE_PROPERTY);

        if (!QuestionTypesEnum.isValueInEnum(type)) {
        	StringBuilder message = new StringBuilder("Le paramètre QTYPE doit être égal à l'une des valeurs suivantes : ");
			message.append(StringUtil.join(QuestionTypesEnum.toStrings(), ",", "'"));
        	LOGGER.error(session, ReponsesLogEnumImpl.PROCESS_B_EXTRACTION_QUESTIONS_TEC, message.toString());
        	return;
        }
        
        String source = (String) event.getContext().getProperty(QSOURCE_PROPERTY);;
		if (source == null || (!"SENAT".equals(source) && !"AN".equals(source))) {
			LOGGER.error(session, ReponsesLogEnumImpl.PROCESS_B_EXTRACTION_QUESTIONS_TEC, "Le paramètre QSOURCE doit être égal à l'une des valeurs suivantes : SENAT ou AN");
			return;
		}

		String statut = (String) event.getContext().getProperty(QSTATUT_PROPERTY);
        StatutsEnum statutEnum = null;
        List<Question> result = null;
        if ("OUVERTES".equals(statut)) {
        	statutEnum = StatutsEnum.EN_COURS;
        	result = repExtract.extractQuestionsOuvertes(session, type, source);
        } else if ("FERMEES".equals(statut)) {
        	statutEnum = StatutsEnum.CLOSES;
        	result = repExtract.extractQuestionsCloses(session, type, source);
        } else {
        	LOGGER.error(session, ReponsesLogEnumImpl.PROCESS_B_EXTRACTION_QUESTIONS_TEC, "Le paramètre QSTATUT doit être égal à OUVERTES ou FERMEES");
        	return;
        }

        ExtractionQuestionsResponse xmlResult = generateXML(result, type, source, statutEnum);
        marshallFile(session, xmlResult, source, statutEnum);

		long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
        	suiviBatchService.createBatchResultFor(batchLoggerModel, "Extraction de questions", endTime-startTime);
        } catch (Exception exc) {
        	LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, exc);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_EXTRACTION_QUESTIONS_TEC);
	}
	
	/**
	 * Génération de l'extraction
	 * @param session
	 * @param result la liste de question à insérer dans le xml
	 * @param type type de question (QE, QO,...)
	 * @param source source des questions (AN, SENAT)
	 * @param statutEnum statut des questions 
	 * @return
	 */
	private ExtractionQuestionsResponse generateXML(final List<Question> result, final String type, final String source, final StatutsEnum statutEnum) {
		ExtractionQuestionsResponse xmlResult = new ExtractionQuestionsResponse();
		xmlResult.setType(type);
		xmlResult.setSource(source); 
		for (Question question : result) {
			QuestionJAXB questionXML = new QuestionJAXB();
			questionXML.setId_question(question.getNumeroQuestion());
			questionXML.setLegislature(question.getLegislatureQuestion());
			questionXML.setPage_jo(question.getPageJO());
			questionXML.setDate_publication_jo(DateUtil.formatYYYYMMdd(question.getDatePublicationJO()));
			if (StatutsEnum.EN_COURS.equals(statutEnum)) {
				questionXML.setStatut(statutEnum.toString());
			} else if ("repondu".equals(question.getEtatQuestion())) {
				questionXML.setStatut("TRANSMIS_PUBLIE");
			} else if ("caduque".equals(question.getEtatQuestion())) {
				questionXML.setStatut("CADUQUE");
			} else if ("retiree".equals(question.getEtatQuestion())) {
				questionXML.setStatut("RETIRE");
			} else if ("cloture_autre".equals(question.getEtatQuestion())) {
				questionXML.setStatut("CLOTURE_AUTRE");
			}
			Auteur auteur = new Auteur();
			if ("MME".equals(question.getCiviliteAuteur())) {
				auteur.setCivilite("Mme");
			} else if ("MLLE".equals(question.getCiviliteAuteur())) {
				auteur.setCivilite("Mlle");
			} else if ("M".equals(question.getCiviliteAuteur())){
				auteur.setCivilite("M.");
			} else {
				auteur.setCivilite("*Civilité inconnue*");
			}
			auteur.setId_auteur(question.getIdMandat());
			auteur.setNom(question.getNomAuteur());
			auteur.setPrenom(question.getPrenomAuteur());
			questionXML.setAuteur(auteur);
			questionXML.setId_ministre_depot(Integer.parseInt(question.getIdMinistereInterroge()));
			questionXML.setId_ministre_attributaire(Integer.parseInt(question.getIdMinistereAttributaire()));

			questionXML.setErratum_question(question.hasErratum());
			questionXML.setErratum_reponse(question.hasRepErratum());
			
			if (question.getDateRenouvellementQuestion() != null) {
				questionXML.setDate_renouvellement_question(DateUtil.formatYYYYMMdd(question.getDateRenouvellementQuestion()));
			}
			if (question.getDateSignalementQuestion() != null) {
				questionXML.setDate_signalement_question(DateUtil.formatYYYYMMdd(question.getDateSignalementQuestion()));
			}
			Date dateCloture = question.getDateClotureQuestion();
			Date dateCaducite = question.getDateCaduciteQuestion();
			Date dateRetrait = question.getDateRetraitQuestion();
			// Si l'un d'entre eux est non null, on renseigne la date d'abandon avec
			if (dateCloture != null) {
				questionXML.setDate_abandon_question(DateUtil.formatYYYYMMdd(dateCloture));
			} else if (dateCaducite != null) {
				questionXML.setDate_abandon_question(DateUtil.formatYYYYMMdd(dateCaducite));
			} else if (dateRetrait != null) {
				questionXML.setDate_abandon_question(DateUtil.formatYYYYMMdd(dateRetrait));
			}
			xmlResult.getQuestion().add(questionXML);
		}
		
		return xmlResult;
	}
	
	/**
	 * génère le fichier sur le serveur à partir des données passées 
	 * @param session
	 * @param xmlResult données des questions à insérer dans le xml
	 * @param source source des questions
	 * @param statutEnum statut des questions
	 */
	private void marshallFile(final CoreSession session, final ExtractionQuestionsResponse xmlResult, final String source, final StatutsEnum statutEnum) {
		try {
			JAXBContext context = JAXBContext.newInstance(ExtractionQuestionsResponse.class);
			Marshaller marshaller = context.createMarshaller();
			String statutStr = null;
			if (StatutsEnum.EN_COURS.equals(statutEnum)) {
				statutStr = "encours";
			} else {
				statutStr = "closes";
			}

			String dirPath = STServiceLocator.getSTParametreService().getParametreValue(session, STParametreConstant.EXTRACTION_REPERTOIRE);
						
	        // génération nom de fichier
			StringBuilder filePath = new StringBuilder(source)
				.append("_")
				.append(statutStr)
				.append("_")
				.append(DateUtil.simpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime()))
				.append(".xml");

	        File resultFile = null;
	        if (dirPath != null) {
	        	File resultDir = new File(dirPath);
				if (!resultDir.exists()) {
					resultDir.mkdirs();
				}	
				resultFile = new File(dirPath + FILE_SEPARATOR + filePath.toString());
	        } else {
	        	resultFile = new File(filePath.toString());
	        }
			resultFile.createNewFile();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(xmlResult, resultFile);
		} catch (JAXBException exc) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_EXTRACT_QUESTION_TEC, exc);
		} catch (IOException exc) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_EXTRACT_QUESTION_TEC, exc);
		} catch (ClientException exc) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_EXTRACT_QUESTION_TEC, exc);
		}
	}

}
