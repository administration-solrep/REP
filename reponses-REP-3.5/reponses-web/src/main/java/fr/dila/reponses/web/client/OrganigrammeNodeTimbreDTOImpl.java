package fr.dila.reponses.web.client;

import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;

/**
 * Implémentation de la classe de transport des informations d'un noeud de l'organigramme pour la migration
 * 
 */
public class OrganigrammeNodeTimbreDTOImpl implements OrganigrammeNodeTimbreDTO {

	private static final long	serialVersionUID	= 6935730105607012088L;

	private String				nodeId;
	private String				label;
	private Long				countClose;
	private Long				countSigne;
	private Long				countMigrable;
	private Long				countModelFDR;
	private long				order;
	private boolean				breakingSeal;
	private boolean				migratingDossiersClos;
	private String				labelNextTimbre;
	private OrganigrammeType	type;

	public OrganigrammeNodeTimbreDTOImpl(OrganigrammeNode node, Long countClose, Long countSigne, Long countMigrable,
			Long countModelFDR) {
		this.nodeId = node.getId().toString();
		this.label = node.getLabel();
		this.order = ((EntiteNode) node).getOrdre();

		this.countClose = countClose != null ? countClose : 0;
		this.countSigne = countSigne != null ? countSigne : 0;
		this.countMigrable = countMigrable != null ? countMigrable : 0;
		this.countModelFDR = countModelFDR != null ? countModelFDR : 0;
		this.breakingSeal = true;
		this.migratingDossiersClos = true;
		this.type = node.getType();
	}

	public OrganigrammeNodeTimbreDTOImpl(Long countClose, Long countSigne, Long countMigrable, Long countModelFDR) {
		this.countClose = countClose;
		this.countSigne = countSigne;
		this.countMigrable = countMigrable;
		this.countModelFDR = countModelFDR;
		this.breakingSeal = true;
		this.migratingDossiersClos = true;
	}

	@Override
	public String getId() {
		return nodeId;
	}

	@Override
	public void setId(String nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Long getCountClose() {
		return countClose;
	}

	@Override
	public void setCountClose(Long countClose) {
		this.countClose = countClose;
	}

	@Override
	public Long getCountSigne() {
		return countSigne;
	}

	@Override
	public void setCountSigne(Long countSigne) {
		this.countSigne = countSigne;
	}

	@Override
	public Long getCountMigrable() {
		return countMigrable;
	}

	@Override
	public void setCountMigrable(Long countMigrable) {
		this.countMigrable = countMigrable;
	}

	@Override
	public Long getCountModelFDR() {
		return countModelFDR;
	}

	@Override
	public void setCountModelFDR(Long countModelFDR) {
		this.countModelFDR = countModelFDR;
	}

	@Override
	public long getOrder() {
		return this.order;
	}

	@Override
	public void setOrder(long order) {
		this.order = order;
	}

	@Override
	public boolean getBreakingSeal() {
		return breakingSeal;
	}

	@Override
	public void setBreakingSeal(boolean breaking) {
		this.breakingSeal = breaking;
	}

	@Override
	public String getBreakingSealLabel() {
		if (breakingSeal) {
			return "reponses.migration.recap.value.yes";
		}
		return "";
	}

	@Override
	public boolean getMigratingDossiersClos() {
		return migratingDossiersClos;
	}

	@Override
	public void setMigratingDossiersClos(boolean migrate) {
		this.migratingDossiersClos = migrate;
	}

	@Override
	public String getMigratingDossiersClosLabel() {
		if (migratingDossiersClos) {
			return "reponses.migration.recap.value.yes";
		}
		return "";
	}

	@Override
	public void setLabelNextTimbre(String labelNextTimbre) {
		this.labelNextTimbre = labelNextTimbre;
	}

	@Override
	public String getLabelNextTimbre() {
		return labelNextTimbre;
	}

	public OrganigrammeType getType() {
		return type;
	}

	public void setType(OrganigrammeType type) {
		this.type = type;
	}

}
