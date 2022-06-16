package fr.dila.reponses.core.recherche.traitement;

import fr.dila.st.core.util.FullTextUtil;
import junit.framework.TestCase;

public class TestTexteIntegralTraitement extends TestCase {

    public void testIndexHelperReponses() {
        ReponsesFulltextIndexEnum index = ReponsesFulltextIndexEnum.TXT_REPONSE;
        index.activateUFNXQL();
        assertEquals("r.ecm:fulltext_txtReponse", index.getIndexFullName());
        assertEquals("r.ecm:fulltext_txtReponse = \"${to-to}\"", index.getFullText("to-to"));
    }

    public void testIndexHelperQuestion() {
        ReponsesFulltextIndexEnum index = ReponsesFulltextIndexEnum.TXT_QUESTION;
        index.activateUFNXQL();
        assertEquals("q.ecm:fulltext_txtQuestion", index.getIndexFullName());
        assertEquals("q.ecm:fulltext_txtQuestion = \"${toto-'''''',--d}\"", index.getFullText("toto-''',--d"));
    }

    public void testIndexHelperSenatTitre() {
        ReponsesFulltextIndexEnum index = ReponsesFulltextIndexEnum.SENAT_TITRE;
        index.activateUFNXQL();
        assertEquals("q.ecm:fulltext_senatTitre", index.getIndexFullName());
        assertEquals(
            "q.ecm:fulltext_senatTitre = \"${l''oiural} ${''est} ${-un} ${pet-ieo#,}\"",
            index.getFullText("l'oiural 'est -un pet-ieo#,")
        );
    }

    public void testOeuvre() {
        ReponsesFulltextIndexEnum index = ReponsesFulltextIndexEnum.SENAT_TITRE;
        index.activateUFNXQL();
        assertEquals("q.ecm:fulltext_senatTitre", index.getIndexFullName());
        assertEquals("q.ecm:fulltext_senatTitre = \"${oeuvre}\"", index.getFullText("œuvre"));
    }

    public void testIndexHelperMotsVide() {
        ReponsesFulltextIndexEnum index = ReponsesFulltextIndexEnum.SENAT_TITRE;
        index.activateUFNXQL();
        assertEquals(
            "q.ecm:fulltext_senatTitre = \"${Une} ${voiture} ${175} ${000} ${euros}\"",
            index.getFullText("Une voiture à 175 000 euros")
        );
    }

    public void testIndexHelperMotsVide_2() {
        ReponsesFulltextIndexEnum index = ReponsesFulltextIndexEnum.SENAT_TITRE;
        index.activateUFNXQL();
        assertEquals(
            "q.ecm:fulltext_senatTitre = \"${Une} ${croissance} ${15.5%}\"",
            index.getFullText("Une croissance à 15.5%")
        );
    }

    /**
     * Test à déplacer dans TestFullTextUtil
     *  avec la plupart de ces tests là
     */
    public void testTruncature() {
        String input_1 = "chat ois*";
        assertEquals("${chat} ois%", FullTextUtil.parseContent(input_1));
        String input_2 = "ch?t oiseaux";
        assertEquals("ch_t ${oiseaux}", FullTextUtil.parseContent(input_2));
    }
}
