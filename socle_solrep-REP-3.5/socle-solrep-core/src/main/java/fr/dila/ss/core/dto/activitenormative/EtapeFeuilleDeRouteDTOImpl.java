package fr.dila.ss.core.dto.activitenormative;

import java.util.UUID;

import fr.dila.ss.api.dto.EtapeFeuilleDeRouteDTO;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.core.client.AbstractMapDTO;

public class EtapeFeuilleDeRouteDTOImpl extends AbstractMapDTO implements EtapeFeuilleDeRouteDTO {

	private static final long serialVersionUID = 1L;
	
	public EtapeFeuilleDeRouteDTOImpl() {
		this.setId(UUID.randomUUID().toString());
	}
	
	public EtapeFeuilleDeRouteDTOImpl(STRouteStep routeStep) {
		if (routeStep != null) {			
			if (routeStep.getDocument().getId()==null) {
				setId(UUID.randomUUID().toString());
			} else {
				setId(routeStep.getDocument().getId());
			}
			
			if (routeStep.getDeadLine()!=null) {
				this.setDeadLine(routeStep.getDeadLine());
			}
		}
	}

	@Override
	public String getDocIdForSelection() {
		
		return getId();
	}
	
	@Override
	public String getType() {
		return "RouteStep";
	}

	@Override
	public String getTypeEtape() {
		return getString(STSchemaConstant.ROUTING_TASK_TYPE_PROPERTY);
	}
	
	@Override
	public void setTypeEtape(String typeEtape) {
		put(STSchemaConstant.ROUTING_TASK_TYPE_PROPERTY, typeEtape);
	}

	@Override
	public String getDeadLine() {
		return (String) get(STSchemaConstant.ROUTING_TASK_DEADLINE_PROPERTY);
	}

	@Override
	public void setDeadLine(Long echeanceIndicative) {
		put(STSchemaConstant.ROUTING_TASK_DEADLINE_PROPERTY, echeanceIndicative);
	}

	@Override
	public Boolean getObligatoireSGG() {
		return getBoolean(STSchemaConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY);
	}

	@Override
	public void setObligatoireSGG(Boolean validationSGG) {
		put(STSchemaConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY, validationSGG);
	}

	@Override
	public Boolean getObligatoireMinistere() {
		return getBoolean(STSchemaConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY);
	}

	@Override
	public void setObligatoireMinistere(Boolean obligatoireMinistere) {
		put(STSchemaConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY,obligatoireMinistere);
	}
	
	@Override
	public String getId() {
		return getString(STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY);
	}

	@Override
	public void setId(String id) {
		put(STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY,id);
	}
	
	@Override
    public String toString() {
		return this.getId();
    }

	@Override
	public String getDistributionMailboxId() {
		return getString(STSchemaConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY);
	}

	@Override
	public void setDistributionMailboxId(String distributionMailboxId) {
		put(STSchemaConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY, distributionMailboxId);
	}

	@Override
	public Boolean getAutomaticValidation() {
		return getBoolean(STSchemaConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY);
	}

	@Override
	public void setAutomaticValidation(Boolean validationAutomatique) {
		put(STSchemaConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY, validationAutomatique);
	}
	

}
