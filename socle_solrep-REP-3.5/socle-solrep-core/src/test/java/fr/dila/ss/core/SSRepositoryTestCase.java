package fr.dila.ss.core;

import fr.dila.ss.core.constant.SSTestConstant;
import fr.dila.st.core.STRepositoryTestCase;

public class SSRepositoryTestCase extends STRepositoryTestCase {

	@Override
	protected void deployRepositoryContrib () throws Exception {
		super.deployRepositoryContrib();

		// deploy api and core bundles
		deployContrib(SSTestConstant.SS_CORE_BUNDLE, "OSGI-INF/ss-operation-contrib.xml");
		deployContrib(SSTestConstant.SS_CORE_BUNDLE, "OSGI-INF/service/mailbox-poste-framework.xml");
		deployContrib(SSTestConstant.SS_CORE_BUNDLE, "OSGI-INF/ss-adapter-contrib.xml");
		deployContrib(SSTestConstant.SS_CORE_BUNDLE, "OSGI-INF/ss-life-cycle-contrib.xml");
		deployContrib(SSTestConstant.SS_CORE_BUNDLE, "OSGI-INF/ss-life-cycle-type-contrib.xml");
		deployContrib(SSTestConstant.SS_CORE_BUNDLE, "OSGI-INF/service/fond-dossier-framework.xml");
	}
}
