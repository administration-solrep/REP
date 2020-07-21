package fr.dila.reponses.web.client;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;

import fr.dila.reponses.api.client.ReponseDossierListingDTO;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.reponses.core.recherche.ReponseDossierListingDTOImpl;

public class TestCorbeilleDTO extends TestCase{

    public void testGetterSetters(){
        
        final String strValue = "string value";
        final Boolean boolValue = Boolean.TRUE;
        final Date dateValue = new Date();
        
        ReponseDossierListingDTO cdto = new ReponseDossierListingDTOImpl();
        
        cdto.setSourceNumeroQuestion(strValue);
        Assert.assertEquals(strValue, cdto.getSourceNumeroQuestion());
        Assert.assertEquals(strValue, cdto.get(ReponseDossierListingConstants.SOURCE_NUMERO_QUESTION));
        cdto.setSourceNumeroQuestion(null);
        Assert.assertEquals(null, cdto.getSourceNumeroQuestion());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.SOURCE_NUMERO_QUESTION));

        cdto.setDatePublicationJO(dateValue);
        Assert.assertEquals(dateValue, cdto.getDatePublicationJO());
        Assert.assertEquals(dateValue, cdto.get(ReponseDossierListingConstants.DATE_PUBLICATION_JO));
        cdto.setDatePublicationJO(null);
        Assert.assertEquals(null, cdto.getDatePublicationJO());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.DATE_PUBLICATION_JO));
        
        cdto.setAuteur(strValue);
        Assert.assertEquals(strValue, cdto.getAuteur());
        Assert.assertEquals(strValue, cdto.get(ReponseDossierListingConstants.AUTEUR));
        cdto.setAuteur(null);        
        Assert.assertEquals(null, cdto.getAuteur());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.AUTEUR));
        
        cdto.setMotCles(strValue);
        Assert.assertEquals(strValue, cdto.getMotCles());
        Assert.assertEquals(strValue, cdto.get(ReponseDossierListingConstants.MOT_CLES));
        cdto.setMotCles(null);
        Assert.assertEquals(null, cdto.getMotCles());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.MOT_CLES));

        cdto.setLocked(boolValue);
        Assert.assertEquals(boolValue, cdto.isLocked());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.IS_LOCKED));
        cdto.setLocked(null);
        Assert.assertEquals(null, cdto.isLocked());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.IS_LOCKED));

        cdto.setConnexite(boolValue);
        Assert.assertEquals(boolValue, cdto.hasConnexite());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.HAS_CONNEXITE));
        cdto.setConnexite(null);
        Assert.assertEquals(null, cdto.hasConnexite());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.HAS_CONNEXITE));
        
        cdto.setAttachement(boolValue);
        Assert.assertEquals(boolValue, cdto.hasAttachement());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.HAS_ATTACHEMENT));
        cdto.setAttachement(null);
        Assert.assertEquals(null, cdto.hasAttachement());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.HAS_ATTACHEMENT));

        cdto.setDelai(strValue);
        Assert.assertEquals(strValue, cdto.getDelai());
        Assert.assertEquals(strValue, cdto.get(ReponseDossierListingConstants.DELAI));
        cdto.setDelai(null);
        Assert.assertEquals(null, cdto.getDelai());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.DELAI));

        cdto.setLot(boolValue);
        Assert.assertEquals(boolValue, cdto.hasLot());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.HAS_LOT));
        cdto.setLot(null);
        Assert.assertEquals(null, cdto.hasLot());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.HAS_LOT));

        cdto.setRead(boolValue);
        Assert.assertEquals(boolValue, cdto.getRead());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.IS_READ));
        cdto.setRead(null);
        Assert.assertEquals(null, cdto.getRead());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.IS_READ));

        cdto.setCaseLinkId(strValue);
        Assert.assertEquals(strValue, cdto.getCaseLinkId());
        Assert.assertEquals(strValue, cdto.get(ReponseDossierListingConstants.CASE_LINK_ID));
        cdto.setCaseLinkId(null);
        Assert.assertEquals(null, cdto.getCaseLinkId());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.CASE_LINK_ID));

        cdto.setDossierId(strValue);
        Assert.assertEquals(strValue, cdto.getDossierId());
        Assert.assertEquals(strValue, cdto.get(ReponseDossierListingConstants.DOSSIER_ID));
        cdto.setDossierId(null);
        Assert.assertEquals(null, cdto.getDossierId());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.DOSSIER_ID));

        cdto.setUrgent(boolValue);
        Assert.assertEquals(boolValue, cdto.isUrgent());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.IS_URGENT));
        cdto.setUrgent(null);
        Assert.assertEquals(null, cdto.isUrgent());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.IS_URGENT));

        cdto.setRenouvelle(boolValue);
        Assert.assertEquals(boolValue, cdto.isRenouvelle());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.IS_RENOUVELLE));
        cdto.setRenouvelle(null);
        Assert.assertEquals(null, cdto.isRenouvelle());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.IS_RENOUVELLE));

        cdto.setSignale(boolValue);
        Assert.assertEquals(boolValue, cdto.isSignale());
        Assert.assertEquals(boolValue, cdto.get(ReponseDossierListingConstants.IS_SIGNALE));
        cdto.setSignale(null);
        Assert.assertEquals(null, cdto.isSignale());
        Assert.assertEquals(null, cdto.get(ReponseDossierListingConstants.IS_SIGNALE));

    }
    
}
