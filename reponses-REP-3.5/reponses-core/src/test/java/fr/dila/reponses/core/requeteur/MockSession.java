package fr.dila.reponses.core.requeteur;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.local.LocalSession;

import fr.dila.ss.api.security.principal.SSPrincipal;

/**
 * Une session factice utilisée pour tester le requêteur.
 * @author jgomez
 *
 */
public class MockSession extends LocalSession{
    private static final long serialVersionUID = 1L;

    public Principal getPrincipal(){
        return new MockSSPrincipal();
    }
}


// On ne peut pas surcharger SSPrincipal (trop compliqué)
class MockSSPrincipal implements SSPrincipal{

    private static final long serialVersionUID = 1L;

    @Override
    public Set<String> getBaseFunctionSet() {
        return null;
    }

    @Override
    public void setBaseFunctionSet(Set<String> baseFunctionSet) {
        
    }

    @Override
    public Set<String> getPosteIdSet() {
        return null;
    }

    @Override
    public void setPosteIdSet(Set<String> posteIdSet) {
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getCompany() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public List<String> getGroups() {
        return null;
    }

    @Override
    public List<String> getAllGroups() {
        return null;
    }

    @Override
    public boolean isMemberOf(String group) {
        return false;
    }

    @Override
    public List<String> getRoles() {
        return null;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public void setFirstName(String firstName) {
        
    }

    @Override
    public void setLastName(String lastName) {
        
    }

    @Override
    public void setGroups(List<String> groups) {
        
    }

    @Override
    public void setRoles(List<String> roles) {
        
    }

    @Override
    public void setCompany(String company) {
        
    }

    @Override
    public void setPassword(String password) {
        
    }

    @Override
    public void setEmail(String email) {
        
    }

    @Override
    public String getPrincipalId() {
        return null;
    }

    @Override
    public void setPrincipalId(String principalId) {
        
    }

    @Override
    public DocumentModel getModel() {
        return null;
    }

    @Override
    public void setModel(DocumentModel model) throws ClientException {
        
    }

    @Override
    public boolean isAdministrator() {
        return false;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public String getOriginatingUser() {
        return null;
    }

    @Override
    public void setOriginatingUser(String originatingUser) {
        
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<String> getMinistereIdSet() {
        Set<String> ministereIdSet = new HashSet<String>();
        ministereIdSet.add("502");
        ministereIdSet.add("503");
        return ministereIdSet;
    }

    @Override
    public void setMinistereIdSet(Set<String> ministereIdSet) {
        
    }

    @Override
    public Set<String> getDirectionIdSet() {
        return null;
    }

    @Override
    public void setDirectionIdSet(Set<String> directionIdSet) {
        
    }
    
}

