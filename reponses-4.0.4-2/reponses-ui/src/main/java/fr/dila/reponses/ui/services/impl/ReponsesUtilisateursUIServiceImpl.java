package fr.dila.reponses.ui.services.impl;

import static fr.dila.st.ui.utils.ValidationHelper.date;
import static fr.dila.st.ui.utils.ValidationHelper.email;
import static fr.dila.st.ui.utils.ValidationHelper.future;
import static fr.dila.st.ui.utils.ValidationHelper.minSize;
import static fr.dila.st.ui.utils.ValidationHelper.notBlank;
import static fr.dila.st.ui.utils.ValidationHelper.past;

import fr.dila.reponses.api.service.ReponsesUserManager;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.ReponsesUtilisateursUIService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.impl.STUtilisateursUIServiceImpl;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;
import org.nuxeo.runtime.api.Framework;

public class ReponsesUtilisateursUIServiceImpl
    extends STUtilisateursUIServiceImpl
    implements ReponsesUtilisateursUIService {
    private static final String DATE_DEBUT = "date début";
    private static final String DATE_FIN = "date fin";

    @Override
    public void createOccasionalUser(SpecificContext context) {
        UserForm userForm = context.getFromContextData(STContextDataKey.USER_FORM);
        Objects.requireNonNull(userForm, "un objet du context de type [UserForm] est attendu");

        validateOccasionalUserForm(userForm);

        ReponsesUserManager userManager = ReponsesServiceLocator.getReponsesUserManager();
        DocumentModel newUserDoc = userManager.getBareUserModel();
        updateDocWithUserForm(newUserDoc.getAdapter(STUser.class), userForm);

        try {
            Framework.doPrivileged(
                () ->
                    ReponsesServiceLocator
                        .getReponsesUserManager()
                        .createUserOccasional(context.getSession(), newUserDoc)
            );
            context.getMessageQueue().addSuccessToQueue("utilisateurs.tempUser.add");
        } catch (UserAlreadyExistsException e) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("admin.user.already.exist"));
        }
    }

    private void validateOccasionalUserForm(UserForm form) {
        notBlank("identifiant", form.getUtilisateur());
        minSize("identifiant", form.getUtilisateur(), 8);
        notBlank("civilité", form.getCivilite());
        notBlank("nom", form.getNom());
        notBlank("prénom", form.getPrenom());
        notBlank("téléphone", form.getTelephone());
        notBlank("mél", form.getMel());
        email("mél", form.getMel());
        notBlank("utilisateur temporaire", form.getTemporaire());

        date(DATE_DEBUT, form.getDateDebut());
        notBlank(DATE_FIN, form.getDateFin());
        date(DATE_FIN, form.getDateFin());

        future(DATE_FIN, form.getDateFin(), DATE_DEBUT, form.getDateDebut());

        Calendar endDate = DateUtil.localDateToGregorianCalendar(LocalDate.now().plusMonths(6));
        String endDateString = SolonDateConverter.DATE_SLASH.format(endDate);

        past(DATE_FIN, form.getDateFin(), endDateString, endDateString);
    }
}
