package fr.dila.reponses.rest.api;

import fr.dila.reponses.rest.helper.PasswordHelper;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.reponses.rest.management.ControleDelegate;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.logger.AbstractLogger;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;
import fr.sword.xsd.reponses.ResultatControlePublication;
import fr.sword.xsd.reponses.TraitementStatut;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

@WebObject(type = WSControle.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSControleImpl extends DefaultObject implements WSControle {
    private static final Logger LOGGER = LogManager.getLogger(WSControle.class);

    @Override
    @GET
    @Path(WSControle.METHOD_NAME_TEST)
    @Produces("text/plain")
    public String test() {
        return SERVICE_NAME;
    }

    @Override
    @GET
    @Path(WSControle.METHOD_NAME_VERSION)
    public VersionResponse version() throws Exception {
        return VersionHelper.getVersionForWSControle();
    }

    @Override
    @POST
    @Path(WSControle.METHOD_NAME_CONTROLE_PUBLICATION)
    public ControlePublicationResponse controlePublication(ControlePublicationRequest request) throws Exception {
        long startTime = System.nanoTime();
        ControlePublicationResponse response = new ControlePublicationResponse();
        if (isPasswordOutdated()) {
            ResultatControlePublication rt = new ResultatControlePublication();
            rt.setStatut(TraitementStatut.KO);
            rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
            response.getResultatControleQuestion().add(rt);
        } else if (isPasswordTemporary()) {
            ResultatControlePublication rt = new ResultatControlePublication();
            rt.setStatut(TraitementStatut.KO);
            rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
            response.getResultatControleQuestion().add(rt);
        } else {
            try {
                ControleDelegate delegate = new ControleDelegate(ctx.getCoreSession());
                response = delegate.controlePublication(request);
            } catch (Exception e) {
                LOGGER.info(
                    JaxBHelper.logInWsTransaction(
                        WSControle.SERVICE_NAME,
                        WSControle.METHOD_NAME_CONTROLE_PUBLICATION,
                        ctx.getCoreSession().getPrincipal().getName(),
                        request,
                        ControlePublicationRequest.class,
                        null,
                        null
                    )
                );
                throw e;
            }
        }
        if (LOGGER.isInfoEnabled()) {
            long duration = AbstractLogger.getDurationInMs(startTime);
            LOGGER.info(
                STConstant.LOG_MSG_DURATION,
                JaxBHelper.logInWsTransaction(
                    WSControle.SERVICE_NAME,
                    WSControle.METHOD_NAME_CONTROLE_PUBLICATION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ControlePublicationRequest.class,
                    response,
                    ControlePublicationResponse.class
                ),
                duration
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSControle.METHOD_NAME_CHERCHER_RETOUR_PUBLICATION)
    public ChercherRetourPublicationResponse chercherRetourPublication(ChercherRetourPublicationRequest request)
        throws Exception {
        long startTime = System.nanoTime();
        ChercherRetourPublicationResponse response = new ChercherRetourPublicationResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                ControleDelegate delegate = new ControleDelegate(ctx.getCoreSession());
                response = delegate.chercherRetourPublication(request);
            } catch (Exception e) {
                LOGGER.info(
                    JaxBHelper.logInWsTransaction(
                        WSControle.SERVICE_NAME,
                        WSControle.METHOD_NAME_CHERCHER_RETOUR_PUBLICATION,
                        ctx.getCoreSession().getPrincipal().getName(),
                        request,
                        ChercherRetourPublicationRequest.class,
                        null,
                        null
                    )
                );
                throw e;
            }
        }
        if (LOGGER.isInfoEnabled()) {
            long duration = AbstractLogger.getDurationInMs(startTime);
            LOGGER.info(
                STConstant.LOG_MSG_DURATION,
                JaxBHelper.logInWsTransaction(
                    WSControle.SERVICE_NAME,
                    WSControle.METHOD_NAME_CHERCHER_RETOUR_PUBLICATION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherRetourPublicationRequest.class,
                    response,
                    ChercherRetourPublicationResponse.class
                ),
                duration
            );
        }
        return response;
    }

    private boolean isPasswordOutdated() {
        CoreSession session = ctx.getCoreSession();
        return PasswordHelper.isPasswordOutdated(session, LOGGER);
    }

    private boolean isPasswordTemporary() {
        CoreSession session = ctx.getCoreSession();
        return PasswordHelper.isPasswordTemporary(session, LOGGER);
    }
}
