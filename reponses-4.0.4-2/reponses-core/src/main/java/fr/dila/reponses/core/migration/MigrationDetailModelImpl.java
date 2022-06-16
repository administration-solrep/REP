package fr.dila.reponses.core.migration;

import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.st.core.util.DateUtil;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "MigrationDetail")
@Table(name = "MIGRATION_DETAIL")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_", discriminatorType = DiscriminatorType.STRING)
public class MigrationDetailModelImpl implements MigrationDetailModel {
    private static final long serialVersionUID = 1534289917599102939L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, columnDefinition = "integer")
    private long id;

    @Column(name = "EXEC_DETAIL")
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = MigrationLoggerModelImpl.class)
    @JoinColumn(name = "ID_LOGGER")
    private MigrationLoggerModel migration;

    @Column(name = "STATUT")
    private String statut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    private Date endDate;

    @Override
    public void setMigration(MigrationLoggerModel migration) {
        this.migration = migration;
    }

    @Override
    public MigrationLoggerModel getMigration() {
        return migration;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getStatut() {
        return statut;
    }

    @Override
    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public Date getStartDate() {
        return DateUtil.copyDate(startDate);
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = DateUtil.copyDate(startDate);
    }

    @Override
    public Date getEndDate() {
        return DateUtil.copyDate(endDate);
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = DateUtil.copyDate(endDate);
    }
}
