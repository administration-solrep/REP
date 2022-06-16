package fr.dila.reponses.core.migration;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.migration.MigrationInfo;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.st.core.util.DateUtil;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "MigrationLogger")
@Table(name = "MIGRATION_LOGGER")
public class MigrationLoggerModelImpl implements MigrationLoggerModel {
    private static final long serialVersionUID = 4746590541344319754L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, columnDefinition = "integer")
    private long id;

    @Column(name = "PRINCIPAL_NAME")
    private String principalName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "ELT_FILS", columnDefinition = "integer")
    private long elementsFils = 2;

    @Column(name = "MODEL_FDR", columnDefinition = "integer")
    private long modeleFdr = 2;

    @Column(name = "DOSSIER_LANCE", columnDefinition = "integer")
    private long norDossierLanceInite = 2;

    @Column(name = "DOSSIER_CLOS", columnDefinition = "integer")
    private long norDossierClos = 2;

    @Column(name = "BULETIN_OFF", columnDefinition = "integer")
    private long bulletinOfficiel = 2;

    @Column(name = "MOTS_CLES", columnDefinition = "integer")
    private long motsCles = 2;

    @Column(name = "FDR_STEP", columnDefinition = "integer")
    private long fdrStep = 2;

    @Column(name = "CREATOR_POSTE", columnDefinition = "integer")
    private long creatorPoste = 2;

    @Column(name = "MAILBOX", columnDefinition = "integer")
    private long mailboxPoste = 2;

    @Column(name = "MODEL_FDR_COUNT", columnDefinition = "integer")
    private long modeleFdrCount;

    @Column(name = "DOSSIER_LANCE_COUNT", columnDefinition = "integer")
    private long norDossierLanceIniteCount;

    @Column(name = "DOSSIER_CLOS_COUNT", columnDefinition = "integer")
    private long norDossierClosCount;

    @Column(name = "BULETIN_OFF_COUNT", columnDefinition = "integer")
    private long bulletinOfficielCount;

    @Column(name = "MOTS_CLES_COUNT", columnDefinition = "integer")
    private long motsClesCount;

    @Column(name = "FDR_STEP_COUNT", columnDefinition = "integer")
    private long fdrStepCount;

    @Column(name = "CREATOR_POSTE_COUNT", columnDefinition = "integer")
    private long creatorPosteCount;

    @Column(name = "MAILBOX_COUNT", columnDefinition = "integer")
    private long mailboxPosteCount;

    @Column(name = "MODEL_FDR_CUR", columnDefinition = "integer")
    private long modeleFdrCurrent;

    @Column(name = "DOSSIER_LANCE_CUR", columnDefinition = "integer")
    private long norDossierLanceIniteCurrent;

    @Column(name = "DOSSIER_CLOS_CUR", columnDefinition = "integer")
    private long norDossierClosCurrent;

    @Column(name = "BULETIN_OFF_CUR", columnDefinition = "integer")
    private long bulletinOfficielCurrent;

    @Column(name = "MOTS_CLES_CUR", columnDefinition = "integer")
    private long motsClesCurrent;

    @Column(name = "FDR_STEP_CUR", columnDefinition = "integer")
    private long fdrStepCurrent;

    @Column(name = "CREATOR_POSTE_CUR", columnDefinition = "integer")
    private long creatorPosteCurrent;

    @Column(name = "MAILBOX_CUR", columnDefinition = "integer")
    private long mailboxPosteCurrent;

    @Column(name = "TABLE_REF", columnDefinition = "integer")
    private long tableRef = 2;

    @Column(name = "TABLE_REF_COUNT", columnDefinition = "integer")
    private long tableRefCount;

    @Column(name = "TABLE_REF_CUR", columnDefinition = "integer")
    private long tableRefCurrent;

    @Column(name = "DELETE_OLD", columnDefinition = "integer")
    private long deleteOld = 2;

    @Column(name = "TYPE_MIG")
    private String typeMigration;

    @Column(name = "DELETE_VAL")
    private boolean deleteOldValue;

    @Column(name = "OLD_ELT")
    private String oldElement;

    @Column(name = "NEW_ELT")
    private String newElement;

    @Column(name = "OLD_MIN")
    private String oldMinistere;

    @Column(name = "NEW_MIN")
    private String newMinistere;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "MIGR_MODEL")
    private Boolean migrationModeleFdr;

    @Column(name = "MIGR_DOS_CLOS")
    private Boolean migrationWithDossierClos;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getPrincipalName() {
        return principalName;
    }

    @Override
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = DateUtil.copyDate(startDate);
    }

    @Override
    public Date getStartDate() {
        return DateUtil.copyDate(startDate);
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = DateUtil.copyDate(endDate);
    }

    @Override
    public Date getEndDate() {
        return DateUtil.copyDate(endDate);
    }

    @Override
    public long getElementsFils() {
        return elementsFils;
    }

    @Override
    public void setElementsFils(long elementsFils) {
        this.elementsFils = elementsFils;
    }

    @Override
    public long getModeleFdr() {
        return modeleFdr;
    }

    @Override
    public void setModeleFdr(long modeleFdr) {
        this.modeleFdr = modeleFdr;
    }

    @Override
    public long getNorDossierLanceInite() {
        return norDossierLanceInite;
    }

    @Override
    public void setNorDossierLanceInite(long norDossierLanceInite) {
        this.norDossierLanceInite = norDossierLanceInite;
    }

    @Override
    public long getNorDossierClos() {
        return norDossierClos;
    }

    @Override
    public void setNorDossierClos(long norDossierClos) {
        this.norDossierClos = norDossierClos;
    }

    @Override
    public long getBulletinOfficiel() {
        return bulletinOfficiel;
    }

    @Override
    public void setBulletinOfficiel(long bulletinOfficiel) {
        this.bulletinOfficiel = bulletinOfficiel;
    }

    @Override
    public long getMotsCles() {
        return motsCles;
    }

    @Override
    public void setMotsCles(long motsCles) {
        this.motsCles = motsCles;
    }

    @Override
    public long getFdrStep() {
        return fdrStep;
    }

    @Override
    public void setFdrStep(long fdrStep) {
        this.fdrStep = fdrStep;
    }

    @Override
    public long getCreatorPoste() {
        return creatorPoste;
    }

    @Override
    public void setCreatorPoste(long creatorPoste) {
        this.creatorPoste = creatorPoste;
    }

    @Override
    public long getMailboxPoste() {
        return mailboxPoste;
    }

    @Override
    public void setMailboxPoste(long mailboxPoste) {
        this.mailboxPoste = mailboxPoste;
    }

    @Override
    public long getDeleteOld() {
        return deleteOld;
    }

    @Override
    public void setDeleteOld(long deleteOld) {
        this.deleteOld = deleteOld;
    }

    @Override
    public long getModeleFdrCount() {
        return modeleFdrCount;
    }

    @Override
    public void setModeleFdrCount(long modeleFdrCount) {
        this.modeleFdrCount = modeleFdrCount;
    }

    @Override
    public long getNorDossierLanceIniteCount() {
        return norDossierLanceIniteCount;
    }

    @Override
    public void setNorDossierLanceIniteCount(long norDossierLanceIniteCount) {
        this.norDossierLanceIniteCount = norDossierLanceIniteCount;
    }

    @Override
    public long getNorDossierClosCount() {
        return norDossierClosCount;
    }

    @Override
    public void setNorDossierClosCount(long norDossierClosCount) {
        this.norDossierClosCount = norDossierClosCount;
    }

    @Override
    public long getBulletinOfficielCount() {
        return bulletinOfficielCount;
    }

    @Override
    public void setBulletinOfficielCount(long bulletinOfficielCount) {
        this.bulletinOfficielCount = bulletinOfficielCount;
    }

    @Override
    public long getMotsClesCount() {
        return motsClesCount;
    }

    @Override
    public void setMotsClesCount(long motsClesCount) {
        this.motsClesCount = motsClesCount;
    }

    @Override
    public long getFdrStepCount() {
        return fdrStepCount;
    }

    @Override
    public void setFdrStepCount(long fdrStepCount) {
        this.fdrStepCount = fdrStepCount;
    }

    @Override
    public long getCreatorPosteCount() {
        return creatorPosteCount;
    }

    @Override
    public void setCreatorPosteCount(long creatorPosteCount) {
        this.creatorPosteCount = creatorPosteCount;
    }

    @Override
    public long getMailboxPosteCount() {
        return mailboxPosteCount;
    }

    @Override
    public void setMailboxPosteCount(long mailboxPosteCount) {
        this.mailboxPosteCount = mailboxPosteCount;
    }

    @Override
    public long getModeleFdrCurrent() {
        return modeleFdrCurrent;
    }

    @Override
    public void setModeleFdrCurrent(long modeleFdrCurrent) {
        this.modeleFdrCurrent = modeleFdrCurrent;
    }

    @Override
    public long getNorDossierLanceIniteCurrent() {
        return norDossierLanceIniteCurrent;
    }

    @Override
    public void setNorDossierLanceIniteCurrent(long norDossierLanceIniteCurrent) {
        this.norDossierLanceIniteCurrent = norDossierLanceIniteCurrent;
    }

    @Override
    public long getNorDossierClosCurrent() {
        return norDossierClosCurrent;
    }

    @Override
    public void setNorDossierClosCurrent(long norDossierClosCurrent) {
        this.norDossierClosCurrent = norDossierClosCurrent;
    }

    @Override
    public long getBulletinOfficielCurrent() {
        return bulletinOfficielCurrent;
    }

    @Override
    public void setBulletinOfficielCurrent(long bulletinOfficielCurrent) {
        this.bulletinOfficielCurrent = bulletinOfficielCurrent;
    }

    @Override
    public long getMotsClesCurrent() {
        return motsClesCurrent;
    }

    @Override
    public void setMotsClesCurrent(long motsClesCurrent) {
        this.motsClesCurrent = motsClesCurrent;
    }

    @Override
    public long getFdrStepCurrent() {
        return fdrStepCurrent;
    }

    @Override
    public void setFdrStepCurrent(long fdrStepCurrent) {
        this.fdrStepCurrent = fdrStepCurrent;
    }

    @Override
    public long getCreatorPosteCurrent() {
        return creatorPosteCurrent;
    }

    @Override
    public void setCreatorPosteCurrent(long creatorPosteCurrent) {
        this.creatorPosteCurrent = creatorPosteCurrent;
    }

    @Override
    public long getMailboxPosteCurrent() {
        return mailboxPosteCurrent;
    }

    @Override
    public void setMailboxPosteCurrent(long mailboxPosteCurrent) {
        this.mailboxPosteCurrent = mailboxPosteCurrent;
    }

    @Override
    public long getTableRef() {
        return tableRef;
    }

    @Override
    public void setTableRef(long tableRef) {
        this.tableRef = tableRef;
    }

    @Override
    public long getTableRefCount() {
        return tableRefCount;
    }

    @Override
    public void setTableRefCount(long tableRefCount) {
        this.tableRefCount = tableRefCount;
    }

    @Override
    public long getTableRefCurrent() {
        return tableRefCurrent;
    }

    @Override
    public void setTableRefCurrent(long tableRefCurrent) {
        this.tableRefCurrent = tableRefCurrent;
    }

    @Override
    public String getTypeMigration() {
        return typeMigration;
    }

    @Override
    public void setTypeMigration(String typeMigration) {
        this.typeMigration = typeMigration;
    }

    @Override
    public boolean isDeleteOldValue() {
        return deleteOldValue;
    }

    @Override
    public void setDeleteOldValue(boolean deleteOldValue) {
        this.deleteOldValue = deleteOldValue;
    }

    @Override
    public String getOldElement() {
        return oldElement;
    }

    @Override
    public void setOldElement(String oldElement) {
        this.oldElement = oldElement;
    }

    @Override
    public String getNewElement() {
        return newElement;
    }

    @Override
    public void setNewElement(String newElement) {
        this.newElement = newElement;
    }

    @Override
    public String getOldMinistere() {
        return oldMinistere;
    }

    @Override
    public void setOldMinistere(String oldMinistere) {
        this.oldMinistere = oldMinistere;
    }

    @Override
    public String getNewMinistere() {
        return newMinistere;
    }

    @Override
    public void setNewMinistere(String newMinistere) {
        this.newMinistere = newMinistere;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void assignMigrationInfo(MigrationInfo migrationInfo) {
        migrationInfo.setLoggerId(id);
        migrationInfo.setDeleteOldElementOrganigramme(deleteOld == 1);
        migrationInfo.setNewElementOrganigramme(newElement);
        migrationInfo.setNewMinistereElementOrganigramme(newMinistere);
        migrationInfo.setOldElementOrganigramme(oldElement);
        migrationInfo.setOldMinistereElementOrganigramme(oldMinistere);
    }

    @Override
    public boolean enCours() {
        return this.getStatus() != null && this.getStatus().equals(SSConstant.EN_COURS_STATUS);
    }

    @Override
    public boolean terminee() {
        return this.getStatus() != null && this.getStatus().equals(SSConstant.TERMINEE_STATUS);
    }

    @Override
    public boolean failed() {
        return this.getStatus() != null && this.getStatus().equals(SSConstant.FAILED_STATUS);
    }

    @Override
    public Boolean isMigrationModeleFdr() {
        return migrationModeleFdr;
    }

    @Override
    public void setMigrationModeleFdr(Boolean migrationModeleFdr) {
        this.migrationModeleFdr = migrationModeleFdr;
    }

    @Override
    public Boolean isMigrationWithDossierClos() {
        return migrationWithDossierClos;
    }

    @Override
    public void setMigrationWithDossierClos(Boolean migrationWithDossierClos) {
        this.migrationWithDossierClos = migrationWithDossierClos;
    }
}
