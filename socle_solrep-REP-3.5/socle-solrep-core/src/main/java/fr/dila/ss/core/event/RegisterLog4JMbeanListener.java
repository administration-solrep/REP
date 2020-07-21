package fr.dila.ss.core.event;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.jmx.LoggerDynamicMBean;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.management.api.AdministrativeStatusManager;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

import fr.dila.st.core.event.AbstractEventListener;

/**
 * Listener qui enregistrer le MBean de monitoring Log4J.
 * 
 * @author jtremeaux
 */
public class RegisterLog4JMbeanListener extends AbstractEventListener {
	private static final Log	LOG	= LogFactory.getLog(RegisterLog4JMbeanListener.class);

	/**
	 * Default constructor
	 */
	public RegisterLog4JMbeanListener() {
		super();
	}

	@Override
	public void doHandleEvent(Event event) throws ClientException {
		final String eventId = event.getName();
		final String serviceId = (String) event.getContext().getProperty(
				AdministrativeStatusManager.ADMINISTRATIVE_EVENT_SERVICE);

		if (serviceId.equals(AdministrativeStatusManager.GLOBAL_INSTANCE_AVAILABILITY)) {
			if (eventId.equals(AdministrativeStatusManager.ACTIVATED_EVENT)) {
				final String instanceId = (String) event.getContext().getProperty(
						AdministrativeStatusManager.ADMINISTRATIVE_EVENT_INSTANCE);
				registerLog4JMBean(instanceId);
			}
		}
	}

	/**
	 * Déclare les MBean de monitoring Log4J
	 * 
	 * @param instanceId
	 *            ID de l'instance
	 */
	private void registerLog4JMBean(String instanceId) {
		final List<MBeanServer> list = MBeanServerFactory.findMBeanServer(null);

		// Recherche le serveur MBean du JRE
		for (MBeanServer server : list) {
			if (server instanceof JmxMBeanServer) {
				final Collection<Logger> loggerList = this.getLoggerList();
				for (Logger logger : loggerList) {
					final String name = "Type=Logger,logger=" + logger.getName();
					final LoggerDynamicMBean mBean = new LoggerDynamicMBean(logger);

					try {
						final ObjectName oName = new ObjectName(name);
						if (!server.isRegistered(oName)) {
							server.registerMBean(mBean, oName);
						}
					} catch (Exception e) {
						LOG.error("Problem instanciating Log4J MBean", e);
					}
				}
			}
		}
	}

	/**
	 * Construit une collection de tous les loggers de l'application.
	 * 
	 * @return Collection de Logger.
	 */
	private Collection<Logger> getLoggerList() {
		Enumeration<?> e = LogManager.getCurrentLoggers();
		Set<Logger> loggerSet = new HashSet<Logger>();
		while (e.hasMoreElements()) {
			loggerSet.add((Logger) ((Logger) e.nextElement()).getParent());
		}

		return loggerSet;
	}
}
