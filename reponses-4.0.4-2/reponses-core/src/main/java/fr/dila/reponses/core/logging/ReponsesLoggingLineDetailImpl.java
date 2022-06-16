package fr.dila.reponses.core.logging;

import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Detail d'une ligne de log
 * @author asatre
 *
 */
public class ReponsesLoggingLineDetailImpl extends STDomainObjectImpl implements ReponsesLoggingLineDetail {
    private static final long serialVersionUID = -3802350723046701684L;

    public ReponsesLoggingLineDetailImpl(DocumentModel document) {
        super(document);
    }

    @Override
    public String getMessage() {
        return PropertyUtil.getStringProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_MESSAGE
        );
    }

    @Override
    public void setMessage(String message) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_MESSAGE,
            message
        );
    }

    @Override
    public ReponsesLoggingStatusEnum getStatus() {
        return ReponsesLoggingStatusEnum.findById(
            PropertyUtil.getStringProperty(
                document,
                ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA,
                ReponsesLoggingConstant.REPONSES_LOGGING_STATUS
            )
        );
    }

    @Override
    public void setStatus(ReponsesLoggingStatusEnum status) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_STATUS,
            status.name()
        );
    }

    @Override
    public List<String> getFullLog() {
        return PropertyUtil.getStringListProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_FULL_LOG
        );
    }

    @Override
    public void setFullLog(List<String> fullLog) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_FULL_LOG,
            fullLog
        );
    }
}
