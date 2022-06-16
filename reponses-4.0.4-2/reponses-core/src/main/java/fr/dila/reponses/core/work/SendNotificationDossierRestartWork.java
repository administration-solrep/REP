package fr.dila.reponses.core.work;

import static fr.dila.st.core.service.STServiceLocator.getSTMailService;
import static fr.dila.st.core.service.STServiceLocator.getSTUserService;
import static org.nuxeo.runtime.api.Framework.getProperty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.core.work.SolonWork;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class SendNotificationDossierRestartWork extends SolonWork {
    private static final long serialVersionUID = -586569010072346619L;

    private static final STLogger LOG = STLogFactory.getLog(SendNotificationDossierRestartWork.class);
    private static final String NOTIFICATION_MAIL_AN = getProperty(
        ReponsesConfigConstant.SEND_MAIL_REDEMARRAGE_FDR_AN,
        ""
    );
    private static final String NOTIFICATION_MAIL_SENAT = getProperty(
        ReponsesConfigConstant.SEND_MAIL_REDEMARRAGE_FDR_SENAT,
        ""
    );
    private static final Map<String, String> NOTIFICATION_MAIL = ImmutableMap.of(
        DossierConstants.ORIGINE_QUESTION_AN,
        NOTIFICATION_MAIL_AN,
        DossierConstants.ORIGINE_QUESTION_SENAT,
        NOTIFICATION_MAIL_SENAT
    );

    private final String questionId;
    private final String origine;
    private final String userName;

    public SendNotificationDossierRestartWork(String questionId, String origine, String userName) {
        super();
        this.questionId = questionId;
        this.origine = origine;
        this.userName = userName;
    }

    @Override
    protected void doWork() {
        openSystemSession();

        Date date = new Date();

        // Récupération des mails de diffusion
        String strMails = STServiceLocator.getConfigService().getValue(STConfigConstants.SEND_MAIL_REDEMARRAGE_FDR);
        List<String> mailList = Lists.newArrayList(strMails.split(";"));
        String additionalMail = NOTIFICATION_MAIL.get(origine);
        if (StringUtils.isNotBlank(additionalMail)) {
            mailList.add(additionalMail);
        }

        // Détermine l'objet du mail
        final STParametreService parametreService = STServiceLocator.getSTParametreService();
        final String mailObjet = parametreService.getParametreValue(
            session,
            STParametreConstant.NOTIFICATION_MAIL_REDEMARRAGE_FDR_OBJET
        );

        // Détermine le corps du mail
        final String mailTexte = parametreService.getParametreValue(
            session,
            STParametreConstant.NOTIFICATION_MAIL_REDEMARRAGE_FDR_TEXTE
        );
        final Map<String, Object> mailTexteMap = new HashMap<>();
        mailTexteMap.put("user", getSTUserService().getUserFullName(userName));
        Question question = QueryHelper.getDocument(session, questionId).getAdapter(Question.class);
        mailTexteMap.put("numero_question", question.getSourceNumeroQuestion());
        mailTexteMap.put("libelle_ministere", question.getIntituleMinistereAttributaire());
        mailTexteMap.put("date_heure_redemarrage", SolonDateConverter.DATETIME_DASH_A_MINUTE_COLON.format(date));

        // Envoie l'email au destinataire de la délégation
        try {
            getSTMailService().sendTemplateMail(mailList, mailObjet, mailTexte, mailTexteMap);
        } catch (final Exception e) {
            LOG.error(
                STLogEnumImpl.DEFAULT,
                e,
                "Erreur lors de l'envoi de mail signalant le redémarrage de la feuille de route."
            );
        }
    }
}
