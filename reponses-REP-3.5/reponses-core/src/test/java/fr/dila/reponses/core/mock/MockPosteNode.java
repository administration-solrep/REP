package fr.dila.reponses.core.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.user.STUser;

public class MockPosteNode implements PosteNode {

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

	@Override
	public List<String> getMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMembers(List<String> members) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<STUser> getUserList() throws ClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUserListSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPosteBdc() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPosteBdc(boolean posteBdc) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isChargeMissionSGG() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setChargeMissionSGG(boolean chargeMissionSGG) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isConseillerPM() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConseillerPM(boolean conseillerPM) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWsUrl(String wsUrl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWsUser(String wsUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWsPassword(String wsPassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWsKeyAlias(String keyAlias) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getWsUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWsUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWsPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWsKeyAlias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPosteWs() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPosteWs(boolean posteWs) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSuperviseurSGG() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSuperviseurSGG(boolean superviseurSGG) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getParentEntiteIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParentUnitIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentUnitIds(List<String> list) {
		// TODO Auto-generated method stub

	}

}
