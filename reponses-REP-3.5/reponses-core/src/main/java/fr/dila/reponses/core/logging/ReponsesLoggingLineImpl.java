package fr.dila.reponses.core.logging;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Represente une ligne de log
 * @author asatre
 *
 */
public class ReponsesLoggingLineImpl extends STDomainObjectImpl implements ReponsesLoggingLine {
    
    private static final long serialVersionUID = 688337909736841034L;

    public ReponsesLoggingLineImpl(DocumentModel document){
        super(document);
    }

    @Override
    public Calendar getStartDate() {
        return PropertyUtil.getCalendarProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_START_DATE);
    }

    @Override
    public void setStartDate(Calendar startDate) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_START_DATE, startDate);
    }

    @Override
    public Calendar getEndDate() {
        return PropertyUtil.getCalendarProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_END_DATE);
    }

    @Override
    public void setEndDate(Calendar endDate) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_END_DATE, endDate);
    }

    @Override
    public List<String> getReponsesLoggingLineDetails() {
        return PropertyUtil.getStringListProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_LINES_DETAILS);
    }

    @Override
    public void setReponsesLoggingLineDetails(List<String> reponsesLoggingLineDetails) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_LINES_DETAILS, reponsesLoggingLineDetails);        
    }

    @Override
    public String getMessage() {
        return PropertyUtil.getStringProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_MESSAGE);
    }

    @Override
    public void setMessage(String message) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_MESSAGE, message);        
    }

    @Override
    public ReponsesLoggingStatusEnum getStatus() {
        return ReponsesLoggingStatusEnum.findById(PropertyUtil.getStringProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_STATUS));
    }

    @Override
    public void setStatus(ReponsesLoggingStatusEnum status) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_STATUS, status.name());
    }

    @Override
    public List<String> getFullLog() {
        return PropertyUtil.getStringListProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_FULL_LOG);
    }

    @Override
    public void setFullLog(List<String> fullLog) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_FULL_LOG, fullLog);        
    }

    @Override
    public Long getEndCount() {
        return PropertyUtil.getLongProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_END_COUNT);
    }

    @Override
    public void setEndCount(Long endCount) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_END_COUNT, endCount);
    }
    
    @Override
    public Long getPrevisionalCount() {
        return PropertyUtil.getLongProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_PREVISIONNAL_COUNT);
    }

    @Override
    public void setPrevisionalCount(Long previsionalCount) {
        PropertyUtil.setProperty(document, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA, ReponsesLoggingConstant.REPONSES_LOGGING_PREVISIONNAL_COUNT, previsionalCount);
    }

}
