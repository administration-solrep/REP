package fr.dila.reponses.core.logging;

import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.st.api.domain.ComplexeType;
import fr.dila.st.core.domain.ComplexeTypeImpl;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * classe de gestion des logs en bdd
 *
 * @author asatre
 *
 */
public class ReponsesLoggingImpl extends STDomainObjectImpl implements ReponsesLogging {
    private static final long serialVersionUID = 7848901104436007575L;

    private static final String NEXT_MIN = "nextMin";
    private static final String CURRENT_MIN = "currentMin";

    public ReponsesLoggingImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public Calendar getStartDate() {
        return PropertyUtil.getCalendarProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_START_DATE
        );
    }

    @Override
    public void setStartDate(Calendar startDate) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_START_DATE,
            startDate
        );
    }

    @Override
    public Calendar getEndDate() {
        return PropertyUtil.getCalendarProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_END_DATE
        );
    }

    @Override
    public void setEndDate(Calendar endDate) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_END_DATE,
            endDate
        );
    }

    @Override
    public Long getPrevisionalCount() {
        return PropertyUtil.getLongProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_PREVISIONNAL_COUNT
        );
    }

    @Override
    public void setPrevisionalCount(Long previsionalCount) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_PREVISIONNAL_COUNT,
            previsionalCount
        );
    }

    @Override
    public List<String> getReponsesLoggingLines() {
        return PropertyUtil.getStringListProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_LINES
        );
    }

    @Override
    public void setReponsesLoggingLines(List<String> reponsesLoggingLines) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_LINES,
            reponsesLoggingLines
        );
    }

    @Override
    public String getMessage() {
        return PropertyUtil.getStringProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_MESSAGE
        );
    }

    @Override
    public void setMessage(String message) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_MESSAGE,
            message
        );
    }

    @Override
    public ReponsesLoggingStatusEnum getStatus() {
        return ReponsesLoggingStatusEnum.findById(
            PropertyUtil.getStringProperty(
                document,
                ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
                ReponsesLoggingConstant.REPONSES_LOGGING_STATUS
            )
        );
    }

    @Override
    public void setStatus(ReponsesLoggingStatusEnum status) {
        String statusStr = null;
        if (status != null) {
            statusStr = status.name();
        }
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_STATUS,
            statusStr
        );
    }

    @Override
    public Long getEndCount() {
        return PropertyUtil.getLongProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_END_COUNT
        );
    }

    @Override
    public void setEndCount(Long endCount) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_END_COUNT,
            endCount
        );
    }

    @Override
    public Long getClosePrevisionalCount() {
        return PropertyUtil.getLongProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_CLOSE_PREVISIONNAL_COUNT
        );
    }

    @Override
    public void setClosePrevisionalCount(Long previsionalCount) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_CLOSE_PREVISIONNAL_COUNT,
            previsionalCount
        );
    }

    @Override
    public Long getCloseEndCount() {
        return PropertyUtil.getLongProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_CLOSE_END_COUNT
        );
    }

    @Override
    public void setCloseEndCount(Long closeEndCount) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_CLOSE_END_COUNT,
            closeEndCount
        );
    }

    @Override
    public List<ComplexeType> getTimbres() {
        return getComplexeTypeProperty(
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_TIMBRE
        );
    }

    @Override
    public void setTimbres(List<ComplexeType> timbres) {
        List<Map<String, Serializable>> value = timbres
            .stream()
            .map(ct -> ct.getSerializableMap())
            .collect(Collectors.toList());
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_TIMBRE,
            value
        );
    }

    @Override
    public void addtimbre(String currentMin, String nextMin) {
        List<ComplexeType> list = getTimbres();
        Map<String, Serializable> serializableMap = new HashMap<>();
        serializableMap.put(CURRENT_MIN, currentMin);
        serializableMap.put(NEXT_MIN, nextMin);
        ComplexeType newtimbre = new ComplexeTypeImpl(serializableMap);
        list.add(newtimbre);
        setTimbres(list);
    }

    public Map<String, String> geTimbresAsMap() {
        HashMap<String, String> map = new HashMap<>();
        List<ComplexeType> list = getTimbres();
        for (ComplexeType complexeType : list) {
            map.put(
                (String) complexeType.getSerializableMap().get(CURRENT_MIN),
                (String) complexeType.getSerializableMap().get(NEXT_MIN)
            );
        }
        return map;
    }

    protected List<ComplexeType> getComplexeTypeProperty(String schema, String value) {
        List<ComplexeType> complexeTypeList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Serializable>> complexeTypes = (List<Map<String, Serializable>>) document.getProperty(
            schema,
            value
        );
        if (complexeTypes != null) {
            for (Map<String, Serializable> map : complexeTypes) {
                complexeTypeList.add(new ComplexeTypeImpl(map));
            }
        }
        return complexeTypeList;
    }

    @Override
    public String getCurrentGouvernement() {
        return PropertyUtil.getStringProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_CURRENT_GOUVERNEMENT
        );
    }

    @Override
    public void setCurrentGouvernement(String currentGouvernement) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_CURRENT_GOUVERNEMENT,
            currentGouvernement
        );
    }

    @Override
    public String getNextGouvernement() {
        return PropertyUtil.getStringProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_NEXT_GOUVERNEMENT
        );
    }

    @Override
    public void setNextGouvernement(String nextGouvernement) {
        PropertyUtil.setProperty(
            document,
            ReponsesLoggingConstant.REPONSES_LOGGING_SHEMA,
            ReponsesLoggingConstant.REPONSES_LOGGING_NEXT_GOUVERNEMENT,
            nextGouvernement
        );
    }

    @Override
    public boolean isAllCloseQuestionsMigrate() {
        if (getCloseEndCount() != null && getClosePrevisionalCount() != null) {
            return getCloseEndCount().compareTo(getClosePrevisionalCount()) == 0;
        }
        return false;
    }
}
