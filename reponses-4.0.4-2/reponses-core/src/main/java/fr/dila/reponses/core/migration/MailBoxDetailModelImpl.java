package fr.dila.reponses.core.migration;

import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "MailBoxDetail")
@DiscriminatorValue(value = MigrationDiscriminatorConstants.MAILBOX)
public class MailBoxDetailModelImpl extends MigrationDetailModelImpl {
    private static final long serialVersionUID = 1911904197107085940L;
}
