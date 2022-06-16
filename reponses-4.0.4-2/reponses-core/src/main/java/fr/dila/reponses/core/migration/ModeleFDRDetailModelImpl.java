package fr.dila.reponses.core.migration;

import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ModeleFDRDetail")
@DiscriminatorValue(value = MigrationDiscriminatorConstants.FDR)
public class ModeleFDRDetailModelImpl extends MigrationDetailModelImpl {
    private static final long serialVersionUID = -5282990752404203710L;
}
