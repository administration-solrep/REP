package fr.dila.reponses.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.dictao.DictaoUtils;
import fr.dila.dictao.DictaoUtilsException;
import fr.dila.dictao.d2s.proxy.D2SServiceCaller;
import fr.dila.dictao.d2s.proxy.D2SServiceCallerException;
import fr.dila.dictao.dvs.proxy.DVSServiceCaller;
import fr.dila.dictao.dvs.proxy.DVSServiceCallerException;
import fr.dila.dictao.proxy.DictaoServiceCallerException;
import fr.dila.reponses.api.Exception.SignatureException;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

/**
 * Service de signature des dossier de l'application Réponses
 * 
 * @author sly
 */
//TODO: JGZ Beaucoup de logs sont mis en info, une fois le service validé, les traces repasseront en DEBUG
public class DossierSignatureServiceImpl  implements DossierSignatureService {

    // ///////////////////////////////////////
    // DossierDistributionService Interface method
    // ///////////////////////////////////////

    private static final Log LOGGER = LogFactory.getLog(DossierSignatureServiceImpl.class);
    
    /**
     * Default constructor
     */
    public DossierSignatureServiceImpl(){
    	// do nothing
    }
    
    @Override
    public Boolean signerDossier(Dossier dossier, CoreSession session) throws ClientException {
        
		try {
			LOGGER.info("Signature du dossier - start.");
	        
	    	
	    	final ConfigService configService = STServiceLocator.getConfigService();
	        final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
	        
	        //--- gather data
	        Reponse reponse = dossier.getReponse(session);
	        
	        if(reponse == null) {
	        	LOGGER.warn("D2S : can not sign, reponse == null");
	        	throw new SignatureException("label.reponses.feuilleRoute.message.etape.signature.error");
	        }
	        
	        String dataToSign = reponse.getTexteReponse();
	       
	        if (dataToSign == null || dataToSign.length() == 0) {
	        	LOGGER.warn("D2S : can not sign, dataToSign == " + dataToSign);
	            throw new SignatureException("label.reponses.feuilleRoute.message.etape.signature.error");
	        }
			
			//---- Build SignatureEx request
			String requestId = ReponsesConfigConstant.D2S_SERVICE_REQUEST_ID;
			String transactionId = configService.getValue(ReponsesConfigConstant.D2S_TRANSACTION_ID);
			String keyAlias = configService.getValue(ReponsesConfigConstant.D2S_KEY_ALIAS);
	        
			//--- build service caller
			String serviceUrl = null;
	        if(useStub()) {
	            LOGGER.info("Use D2S service stub");
	            serviceUrl = null;
	        } else {
	        	serviceUrl = configService.getValue(ReponsesConfigConstant.D2S_SERVICE);
	        	LOGGER.info("Use D2S service at " + serviceUrl);	            
	        }
	        
	        D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(serviceUrl);
	        d2sServiceCaller.setClientKeyAlias(keyAlias);
			SignatureExResponse response = d2sServiceCaller.signatureEx(requestId, dataToSign, transactionId,
					generateKey(dossier, session));
	        
	        //--- extract actual signature
	        String signature = D2SServiceCaller.extractSignatureFromResponse(response);
	        if (signature == null || signature.length() == 0) {
	            LOGGER.warn("D2S signature is null or empty. Signature has failed");
	        	return false;
	        }

	       	        
	        //--- Sauvegarde de la signature
	        reponse.setSignature(signature);
            reponse.setIsSignatureValide(false);
            session.saveDocument(reponse.getDocument());
            session.save();
            
	        if(allotissementService.isAllotit(dossier, session)) {
	            allotissementService.updateSignatureLinkedReponses(session, reponse);
	        }
	        
	        LOGGER.info("Le dossier a été signé");
	        return true;
		} catch (D2SServiceCallerException e) {
		    LOGGER.error("Le dossier n'a pas été signé à cause d'erreur " + e.getMessage(), e);
			throw new ClientException(e);
		} catch (DictaoServiceCallerException e) {
			LOGGER.error("Le dossier n'a pas été signé à cause d'erreur " + e.getMessage(), e);
			throw new ClientException(e);
		}
    }
    
    /**
     * Génère la clé du dossier à insérer dans l'URI.
     * 
     * @param dossier
     * @return [legislature]_[source]_[type]_[numero]
     */
    @Override
    public String generateKey(Dossier dossier, CoreSession session) {
    	StringBuilder builder = new StringBuilder();
    	
    	Question question = dossier.getQuestion(session);
    	builder.append(question.getLegislatureQuestion()).append("_");
    	builder.append(question.getOrigineQuestion()).append("_");
    	builder.append(question.getTypeQuestion()).append("_");
    	builder.append(question.getNumeroQuestion());
    	
    	return builder.toString();
    }


    /**
     * Utilisation du service bouchon pour dictao
     * @return
     */
    private Boolean useStub() {
        ConfigService configService = STServiceLocator.getConfigService();
        Boolean useStub = configService.getBooleanValue(ReponsesConfigConstant.DICTAO_USE_STUB);
        LOGGER.info("Utilisation du service de signature avec use_stub = " + useStub);
        return useStub;
    }

   
    @Override
    public Boolean verifierDossier(Dossier dossier, CoreSession session) throws ClientException {
        
        try {
        	
        	final ConfigService configService = STServiceLocator.getConfigService();
            LOGGER.info("Vérification du dossier - start");
        	
            if(dossier == null) {
            	LOGGER.warn("DVS: can not verify : null dossier");
            	return false;
            }
            
            //--- gather data
            Reponse reponse = dossier.getReponse(session);
            
            if(reponse == null) {
            	LOGGER.warn("DVS: can not verify : null response numero question : " + dossier.getNumeroQuestion());
            	return false;
            }
            
            String data = reponse.getTexteReponse();
            
            if(data == null) {
            	LOGGER.warn("DVS: can not verify : data is null (no TexteReponse) numero question : " + dossier.getNumeroQuestion());
            	return false;
            }
            
            String signature = reponse.getSignature();
            if(signature == null) {
                LOGGER.info("Le dossier n'a pas été validé : signature inexistante");
            	return false;
            }
            
            
            //--- si la signature est déjà valide -> on ignore l'appel
            if(reponse.getIsSignatureValide()) {
            	LOGGER.info("La signature est déjà valide, on ignore la vérification");
                return true;
            }
            
            //--- build serviceCaller
            String requestId = ReponsesConfigConstant.D2S_SERVICE_REQUEST_ID;
            String transactionId = configService.getValue(ReponsesConfigConstant.DVS_TRANSACTION_ID);
            String keyAlias = configService.getValue(ReponsesConfigConstant.DVS_KEY_ALIAS);

            String serviceUrl = null;
            if(useStub()) {
	            LOGGER.info("Use DVS service stub");
	            serviceUrl = null;
            } else {
            	serviceUrl = configService.getValue(ReponsesConfigConstant.DVS_SERVICE);            	
            	LOGGER.info("Use DVS service at " + serviceUrl);
            }
            
            DVSServiceCaller dvsServiceCaller = new DVSServiceCaller(serviceUrl);
            dvsServiceCaller.setClientKeyAlias(keyAlias);
            VerifySignatureExResponse response = dvsServiceCaller.verifySignatureEx(requestId, data, signature, transactionId);
            
            boolean isValid = DictaoUtils.isSignatureValid(response);

            reponse.setIsSignatureValide(isValid);
            session.saveDocument(reponse.getDocument());
            session.save();
            
            if (isValid) {
                LOGGER.info("Le dossier a été validé");
            } else {
                LOGGER.info("Le dossier n'a pas été validé : signature invalide");
            }
            return isValid;
        } catch (DVSServiceCallerException e) {
        	LOGGER.error("Le dossier n'a pas été validé à cause d'une erreur " + e.getMessage(), e);
        	return false;
        } catch (DictaoServiceCallerException e) {
        	LOGGER.error("Le dossier n'a pas été validé à cause d'une erreur " + e.getMessage(), e);
        	return false;
		} catch (DictaoUtilsException e) {
			LOGGER.error("Le dossier n'a pas été validé à cause d'une erreur " + e.getMessage(), e);
        	throw new ClientException(e);
		} 
    }
}
