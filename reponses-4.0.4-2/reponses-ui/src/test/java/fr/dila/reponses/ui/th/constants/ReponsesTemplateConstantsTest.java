package fr.dila.reponses.ui.th.constants;

import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.AbstractTestConstants;
import org.junit.Test;

public class ReponsesTemplateConstantsTest extends AbstractTestConstants<ReponsesTemplateConstants> {

    @Override
    protected Class<ReponsesTemplateConstants> getConstantClass() {
        return ReponsesTemplateConstants.class;
    }

    @Test
    public void constantsShouldNotHaveDuplicates() {
        verifyDuplicatesWithSTTemplateConstants();
        verifyDuplicatesWithClass(SSTemplateConstants.class);
    }
}
