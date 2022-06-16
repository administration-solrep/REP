package fr.dila.reponses.rest.jaxb;

import fr.dila.st.rest.client.helper.CdataCharacterEscapeHandler;
import fr.sword.xsd.reponses.ChercherErrataQuestionsResponse;
import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * This context resolver register a provider, which returns a custom marshaller for specific classes. That custom marshaller is CDATA compliant.
 *
 * @author sly
 * @author fbarmes
 *
 */
@Provider
public class CustomMarshallerProvider implements ContextResolver<Marshaller> {
    private static final String PROPERTY_CHARACTER_ESCAPE_HANDLER =
        "com.sun.xml.bind.marshaller.CharacterEscapeHandler";

    private Map<Class<?>, Marshaller> marshallerMap;
    private Map<Class<?>, JAXBContext> jaxBContextMap;

    private static final Log LOGGER = LogFactory.getLog(CustomMarshallerProvider.class);

    /**
     * Generate the marshaller for the given class
     */
    @Override
    public Marshaller getContext(Class<?> clazz) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("CustomMarshallerProvider - getContext for" + clazz.getName());
        }

        //--- do not execute for type that do not contains CDATA elements
        if (
            clazz != ChercherQuestionsResponse.class &&
            clazz != ChercherErrataQuestionsResponse.class &&
            clazz != ChercherReponsesResponse.class &&
            clazz != ChercherErrataReponsesResponse.class
        ) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("CustomMarshallerProvider - returning null");
            }

            return null;
        }

        //--- JAX-B Context Map conditional creation
        if (jaxBContextMap == null) {
            jaxBContextMap = new HashMap<>();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("CustomMarshallerProvider - created new jaxBContextMap ");
            }
        }

        //--- Marshaller Map conditional creation
        if (marshallerMap == null) {
            marshallerMap = new HashMap<>();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("CustomMarshallerProvider - created new marshallerMap ");
            }
        }

        //--- Create Context
        if (!jaxBContextMap.containsKey(clazz)) {
            try {
                jaxBContextMap.put(clazz, JAXBContext.newInstance(clazz));

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(
                        "CustomMarshallerProvider - added context for " + clazz.getName() + " to jaxBContextMap"
                    );
                }
            } catch (JAXBException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        }

        //--- Create Marshaller
        if (!marshallerMap.containsKey(clazz)) {
            Marshaller marshaller = null;
            try {
                marshaller = jaxBContextMap.get(clazz).createMarshaller();

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("created Marshaller for " + clazz.getName());
                    LOGGER.trace("Marshaller is of type " + marshaller.getClass().getName());
                    LOGGER.trace("set Marshaller property " + PROPERTY_CHARACTER_ESCAPE_HANDLER);
                }

                marshaller.setProperty(PROPERTY_CHARACTER_ESCAPE_HANDLER, new CdataCharacterEscapeHandler());

                if (LOGGER.isTraceEnabled()) {
                    Object property;
                    LOGGER.trace("test retrieve the ESCAPE_HANDLER");
                    property = marshaller.getProperty(PROPERTY_CHARACTER_ESCAPE_HANDLER);

                    if (property == null) {
                        LOGGER.trace("property is null");
                    } else {
                        LOGGER.trace("property is of type " + property.getClass().getName());
                    }
                }
            } catch (PropertyException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            } catch (JAXBException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }

            marshallerMap.put(clazz, marshaller);
        }

        return marshallerMap.get(clazz);
    }
}
