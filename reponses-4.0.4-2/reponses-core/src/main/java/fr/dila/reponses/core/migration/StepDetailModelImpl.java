package fr.dila.reponses.core.migration;

import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "StepDetail")
@DiscriminatorValue(value = MigrationDiscriminatorConstants.STEP)
public class StepDetailModelImpl extends MigrationDetailModelImpl {
    private static final long serialVersionUID = -1985271719571753180L;
}
