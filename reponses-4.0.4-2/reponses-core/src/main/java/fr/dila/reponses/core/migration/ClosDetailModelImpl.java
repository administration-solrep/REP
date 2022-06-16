package fr.dila.reponses.core.migration;

import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ClosDetail")
@DiscriminatorValue(value = MigrationDiscriminatorConstants.CLOS)
public class ClosDetailModelImpl extends MigrationDetailModelImpl {
    private static final long serialVersionUID = 7263210755328333820L;
}
