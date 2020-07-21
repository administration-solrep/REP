package fr.dila.reponses.core.cases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.flux.RErratumImpl;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;

public class ReponseImpl implements Reponse {

    private DocumentModel document;
    private static final long serialVersionUID = 1L;
    
    public ReponseImpl(DocumentModel document) {
        this.document = document;
    }
        
    
    @Override
    public DocumentModel getDocument(){
        return document;
    }
    
    /**
     * Overrides equality to check documents equality
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ReponseImpl)) {
            return false;
        }
        ReponseImpl otherItem = (ReponseImpl) other;
        return document.equals(otherItem.document);
    }
    
    @Override
    public int hashCode() {
        return document.hashCode();
    }

    @Override
    public String getTexteReponse() {
        // Mise à jour du texte de la reponse sur le schema note
        return PropertyUtil.getStringProperty(document, STSchemaConstant.NOTE_SCHEMA, DossierConstants.DOSSIER_TEXTE_REPONSE);
    }

    @Override
    public void setTexteReponse(String texteReponse) {
        // Nettoyage de la chaine de caractère donnée
        texteReponse = StringUtil.deleteCharNotUTF8(texteReponse);
        texteReponse = StringUtil.deleteHtmlComment(texteReponse);
        // Mise à jour du texte de la reponse sur le schema note
        PropertyUtil.setProperty(document, STSchemaConstant.NOTE_SCHEMA, DossierConstants.DOSSIER_TEXTE_REPONSE, texteReponse);
    }

    @Override
    public String getIdAuteurReponse() {
        return PropertyUtil.getStringProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_AUTEUR_REPONSE);
    }

    @Override
    public void setIdAuteurReponse(String idAuteurReponse) {
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_AUTEUR_REPONSE, idAuteurReponse);
    }

    @Override
    public Long getNumeroJOreponse() {
        return PropertyUtil.getLongProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_NUMERO_JO_REPONSE);
    }

    @Override
    public void setNumeroJOreponse(Long numeroJOreponse) {
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_NUMERO_JO_REPONSE, numeroJOreponse);
    }

    @Override
    public Calendar getDateJOreponse() {
        return PropertyUtil.getCalendarProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_JO_REPONSE);
    }

    @Override
    public void setDateJOreponse(Calendar dateJOreponse) {
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_JO_REPONSE, dateJOreponse);
    }

    @Override
    public Long getPageJOreponse() {
        return PropertyUtil.getLongProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_PAGE_JO_REPONSE);
    }

    @Override
    public void setPageJOreponse(Long pageJOreponse) {
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_PAGE_JO_REPONSE, pageJOreponse);
    }
    
    @Override
    public String getSignature() {
        return PropertyUtil.getStringProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SIGNATURE_REPONSE);
    }

    @Override
    public void setSignature(String signature) {
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SIGNATURE_REPONSE, signature);
    }
    
    @Override
    public List<RErratum> getErrata() {
        return getErrataProperty(DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ERRATA_PROPERTY);
    }
    
    @Override
    public void setErrata(List<RErratum> errata) {
        ArrayList<Map<String, Serializable>> listeErratum = new ArrayList<Map<String, Serializable>>();
        for(RErratum erratum : errata)
        {
            Map<String, Serializable> erratumMap = new HashMap<String, Serializable>();
            erratumMap.put(DossierConstants.DOSSIER_ERRATUM_DATE_PUBLICATION_PROPERTY, erratum.getDatePublication());
            erratumMap.put(DossierConstants.DOSSIER_ERRATUM_PAGE_JO_PROPERTY, erratum.getPageJo());
            erratumMap.put(DossierConstants.DOSSIER_ERRATUM_TEXTE_CONSOLIDE_PROPERTY, erratum.getTexteConsolide());
            erratumMap.put(DossierConstants.DOSSIER_ERRATUM_TEXTE_ERRATUM_PROPERTY, erratum.getTexteErratum());
            listeErratum.add(erratumMap);
        }
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ERRATA_PROPERTY, listeErratum);
    }
        
    protected List<RErratum> getErrataProperty(String schema, String value) {
        List<RErratum> erratumList = new ArrayList<RErratum>();
        List<Map<String, Serializable>> errata = PropertyUtil.getMapStringSerializableListProperty(document, schema, value);
        if (errata != null) {
            for (Map<String, Serializable> erratum : errata) {
                erratumList.add(new RErratumImpl(erratum));
            }
        }
        return erratumList;
    }

    @Override
    public Boolean isPublished() {
        return ((getDateJOreponse() != null) || (getPageJOreponse() != null));
    }

    @Override
    public void setIsSignatureValide(Boolean isSignatureValide) {
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SIGNATURE_VALIDE_REPONSE, isSignatureValide);
    }

    @Override
    public Boolean getIsSignatureValide() {
        return PropertyUtil.getBooleanProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SIGNATURE_VALIDE_REPONSE);
    }

	@Override
	public String getAuthorRemoveSignature() {
		return PropertyUtil.getStringProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.REPONSE_REMOVE_SIGNATURE_AUTHOR);
	}

	@Override
	public void setAuthorRemoveSignature(String signature) {
	    PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.REPONSE_REMOVE_SIGNATURE_AUTHOR, signature);
	}

	@Override
	public Boolean isSignee(){
	    String signature = getSignature();
        return signature != null && !signature.isEmpty();
	}
	
    @Override
    public void setAuteur(String reponseAuthor){
        PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_AUTEUR_REPONSE, reponseAuthor);
    }
	
   @Override
   public String getAuteur(){
      return PropertyUtil.getStringProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_AUTEUR_REPONSE);
   }
   
   @Override
   public void setCurrentErratum(String currentErratum){
       PropertyUtil.setProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.REPONSE_CURRENT_ERRATUM, currentErratum);
   }
   
  @Override
  public String getCurrentErratum(){
     return PropertyUtil.getStringProperty(document, DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.REPONSE_CURRENT_ERRATUM);
  }

}
