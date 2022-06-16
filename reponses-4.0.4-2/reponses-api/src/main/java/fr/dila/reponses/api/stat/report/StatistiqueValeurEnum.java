package fr.dila.reponses.api.stat.report;

public enum StatistiqueValeurEnum {
    SV_2(
        2L,
        "1a",
        "select sum(nbquestion)-(sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur)) from statistique_question_reponse where ministere='GLOBAL'"
    ),
    SV_3(
        3L,
        "1a",
        "select sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur) from statistique_question_reponse where ministere='GLOBAL'"
    ),
    SV_4(4L, "1b", "select sum(nbrepondu1mois) from statistique_question_reponse where ministere='GLOBAL'"),
    SV_5(5L, "1b", "select sum(nbrepondu2mois) from statistique_question_reponse where ministere='GLOBAL'"),
    SV_6(6L, "1b", "select sum(nbrepondusuperieur) from statistique_question_reponse where ministere='GLOBAL'"),
    SV_7(
        7L,
        "2a",
        "select sum(nbquestion)-(sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur)) from statistique_question_reponse where origine='AN' and ministere='GLOBAL'"
    ),
    SV_8(
        8L,
        "2a",
        "select sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur) from statistique_question_reponse where origine='AN' and ministere='GLOBAL'"
    ),
    SV_9(
        9L,
        "2b",
        "select sum(nbrepondu1mois) from statistique_question_reponse where origine='AN' and ministere='GLOBAL'"
    ),
    SV_10(
        10L,
        "2b",
        "select sum(nbrepondu2mois) from statistique_question_reponse where origine='AN' and ministere='GLOBAL'"
    ),
    SV_11(
        11L,
        "2b",
        "select sum(nbrepondusuperieur) from statistique_question_reponse where origine='AN' and ministere='GLOBAL'"
    ),
    SV_12(
        12L,
        "3a",
        "select sum(nbquestion)-(sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur)) from statistique_question_reponse where origine='SENAT' and ministere='GLOBAL'"
    ),
    SV_13(
        13L,
        "3a",
        "select sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur) from statistique_question_reponse where origine='SENAT' and ministere='GLOBAL'"
    ),
    SV_14(
        14L,
        "3b",
        "select sum(nbrepondu1mois) from statistique_question_reponse where origine='SENAT' and ministere='GLOBAL'"
    ),
    SV_15(
        15L,
        "3b",
        "select sum(nbrepondu2mois) from statistique_question_reponse where origine='SENAT' and ministere='GLOBAL'"
    ),
    SV_16(
        16L,
        "3b",
        "select sum(nbrepondusuperieur) from statistique_question_reponse where origine='SENAT' and ministere='GLOBAL'"
    ),
    SV_17(17L, "", "SELECT to_char(sysdate,'dd/MM/YYYY') from dual");

    /** Clé de l'enregistrement dans la table STATISTIQUE_VALEUR */
    private final Long id;

    /** IDRAPPORT concerné en BDD */
    private final String idRapport;

    /** Requête SQL associée permettant de calculer la valeur à mettre en BDD */
    private final String requete;

    StatistiqueValeurEnum(Long id, String idRapport, String requete) {
        this.id = id;
        this.idRapport = idRapport;
        this.requete = requete;
    }

    public Long getId() {
        return id;
    }

    public String getIdRapport() {
        return idRapport;
    }

    public String getRequete() {
        return requete;
    }
}
