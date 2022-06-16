package fr.dila.reponses.core.migration;

import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "LancerDetail")
@DiscriminatorValue(value = MigrationDiscriminatorConstants.LANCE)
public class LancerDetailModelImpl extends MigrationDetailModelImpl {
    private static final long serialVersionUID = -2781120053887590699L;
}
