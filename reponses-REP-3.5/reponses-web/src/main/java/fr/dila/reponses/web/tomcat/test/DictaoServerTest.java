package fr.dila.reponses.web.tomcat.test;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.dictao.DictaoUtils;
import fr.dila.dictao.d2s.proxy.D2SServiceCaller;
import fr.dila.dictao.dvs.proxy.DVSServiceCaller;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.tomcat.test.ServerTest;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

/**
 * Test le fonctionnement de Dictao.
 * 
 * @author bgamard
 */
public class DictaoServerTest implements ServerTest {

	long time = 0;
	
    @Override
    public boolean runTest(CoreSession session) {
    	time = Calendar.getInstance().getTimeInMillis();
        try {
            final ConfigService configService = STServiceLocator.getConfigService();
            
            String data = "sign me please";
            String dossierKey = "15_SENAT_QE_30";
           
            //---- Build SignatureEx request
            String requestId = ReponsesConfigConstant.D2S_SERVICE_REQUEST_ID;
            String d2sTransactionId = configService.getValue(ReponsesConfigConstant.D2S_TRANSACTION_ID);
            String d2sKeyAlias = configService.getValue(ReponsesConfigConstant.D2S_KEY_ALIAS);
            
            //--- build service caller
            String d2sService = configService.getValue(ReponsesConfigConstant.D2S_SERVICE);
            
            D2SServiceCaller d2sServiceCaller = new D2SServiceCaller(d2sService);
            d2sServiceCaller.setClientKeyAlias(d2sKeyAlias);
            SignatureExResponse signatureResponse = d2sServiceCaller.signatureEx(requestId, data, d2sTransactionId, dossierKey);
            
            //--- extract actual signature
            String signature = D2SServiceCaller.extractSignatureFromResponse(signatureResponse);
            if (signature == null || signature.length() == 0) {
                return false;
            }
            
            //--- verify response
            String dvsTransactionId = configService.getValue(ReponsesConfigConstant.DVS_TRANSACTION_ID);
            String dvsKeyAlias = configService.getValue(ReponsesConfigConstant.DVS_KEY_ALIAS);
            
            String dvsService = configService.getValue(ReponsesConfigConstant.DVS_SERVICE);
            
            DVSServiceCaller dvsServiceCaller = new DVSServiceCaller(dvsService);
            dvsServiceCaller.setClientKeyAlias(dvsKeyAlias);
            VerifySignatureExResponse verifyResponse = dvsServiceCaller.verifySignatureEx(requestId, data, signature, dvsTransactionId);
        
            return DictaoUtils.isSignatureValid(verifyResponse);
        } catch (Exception e) {
            return false;
        } finally {
        	time = Calendar.getInstance().getTimeInMillis() - time;
        }
    }

    @Override
    public String getName() {
        final ConfigService configService = STServiceLocator.getConfigService();
        String useStub = configService.getValue(ReponsesConfigConstant.DICTAO_USE_STUB);
        String stub = "";
        if (useStub.equals("true")) {
            stub = " - Non testé (stub = true)";
        }
        
        return "DICTAO" + stub;
    }
    
    @Override
    public long getElapsedTime() {
    	return time;
    }
}
