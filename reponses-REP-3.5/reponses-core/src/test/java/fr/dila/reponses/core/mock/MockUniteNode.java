package fr.dila.reponses.core.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.NorDirection;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;

public class MockUniteNode implements UniteStructurelleNode {

	@Override
	public String getId() {
		return null;
	}

	@Override
	public void setId(final String anid) {

	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void setLabel(final String label) {

	}

	@Override
	public OrganigrammeType getType() {
		return null;
	}

	@Override
	public Date getDateDebut() {
		return null;
	}

	@Override
	public void setDateDebut(final Date dateDebut) {

	}

	@Override
	public void setDateDebut(final Calendar dateDebut) {

	}

	@Override
	public Date getDateFin() {
		return null;
	}

	@Override
	public void setDateFin(final Date dateFin) {

	}

	@Override
	public void setDateFin(final Calendar dateFin) {

	}

	@Override
	public int getParentListSize() throws ClientException {
		return 0;
	}

	@Override
	public void setParentList(final List<OrganigrammeNode> parentList) {

	}

	@Override
	public boolean getDeleted() {
		return false;
	}

	@Override
	public void setDeleted(final boolean deleted) {

	}

	@Override
	public String getLockUserName() {
		return null;
	}

	@Override
	public void setLockUserName(final String lockUserName) {

	}

	@Override
	public Date getLockDate() {
		return null;
	}

	@Override
	public void setLockDate(final Date lockDate) {

	}

	@Override
	public void setLockDate(final Calendar lockDate) {

	}

	@Override
	public List<String> getFunctionRead() {
		return null;
	}

	@Override
	public void setFunctionRead(final List<String> functions) {

	}

	@Override
	public boolean isReadGranted(final CoreSession coreSession) {
		return false;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isNext() {
		return false;
	}

	@Override
	public List<PosteNode> getSubPostesList() throws ClientException {
		return null;
	}

	@Override
	public void setSubPostesList(final List<PosteNode> subPostesList) {

	}

	@Override
	public List<UniteStructurelleNode> getSubUnitesStructurellesList() throws ClientException {
		return null;
	}

	@Override
	public void setSubUnitesStructurellesList(final List<UniteStructurelleNode> subUnitesStructurellesList) {

	}

	@Override
	public List<EntiteNode> getEntiteParentList() throws ClientException {
		return null;
	}

	@Override
	public void setEntiteParentList(final List<EntiteNode> entiteParentList) {

	}

	@Override
	public List<UniteStructurelleNode> getUniteStructurelleParentList() throws ClientException {
		return null;
	}

	@Override
	public void setUniteStructurelleParentList(final List<UniteStructurelleNode> parentList) {

	}

	@Override
	public boolean containsBDC() throws ClientException {
		return false;
	}

	@Override
	public void setType(final OrganigrammeType type) {

	}

	@Override
	public void setTypeValue(final String type) {

	}

	@Override
	public String getTypeValue() {
		return null;
	}

	@Override
	public String getParentId() {
		return null;
	}

	@Override
	public String getParentEntiteId() {
		return null;
	}

	@Override
	public void setParentEntiteId(final String parentEntiteId) {

	}

	@Override
	public String getParentUniteId() {
		return null;
	}

	@Override
	public void setParentUniteId(final String parentUniteId) {

	}

	@Override
	public void setParentEntiteIds(final List<String> list) {

	}

	@Override
	public List<String> getParentEntiteIds() throws ClientException {
		return null;
	}

	@Override
	public void setParentUnitIds(final List<String> list) {

	}

	@Override
	public List<String> getParentUnitIds() throws ClientException {
		return null;
	}

	@Override
	public List<NorDirection> getNorDirectionList() {
		return null;
	}

	@Override
	public void setNorDirectionList(final List<NorDirection> norDirectionList) {

	}

	@Override
	public String getNorDirectionForMinistereId(final String ministereId) {
		return null;
	}

	@Override
	public String getNorDirection(final String ministereId) {
		return null;
	}

	@Override
	public void setNorDirectionForMinistereId(final String ministereId, final String nor) {

	}

	@Override
	public List<InstitutionNode> getInstitutionParentList() {
		return null;
	}

	@Override
	public void setInstitutionParentList(final List<InstitutionNode> instututionParentList) {

	}

	@Override
	public List<String> getParentInstitIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentInstitIds(List<String> list) {
		// TODO Auto-generated method stub

	}

}
