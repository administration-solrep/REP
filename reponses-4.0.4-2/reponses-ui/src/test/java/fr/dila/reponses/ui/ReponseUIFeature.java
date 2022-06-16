package fr.dila.reponses.ui;

import fr.dila.reponses.core.ReponseFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features(ReponseFeature.class)
@Deploy("fr.dila.ss.ui")
@Deploy("fr.dila.reponses.ui")
public class ReponseUIFeature implements RunnerFeature {}
