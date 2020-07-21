
-- Si l'application n'a pas été démarrée, les 4 lignes suivantes sont inutiles :
ALTER TABLE QUESTION DROP COLUMN idMinistereInterroge;
ALTER TABLE DOSSIER_REPONSES_LINK DROP COLUMN idMinistereAttributaire;
ALTER TABLE DOSSIER_REPONSE DROP COLUMN ministereAttributairePrecedent;
ALTER TABLE DOSSIER_REPONSE DROP COLUMN ministereAttributaireCourant;


-- schema question : renommage de idMinistereInterpelle en idMinistereInterroge
ALTER TABLE QUESTION RENAME COLUMN idMinistereInterpelle TO idMinistereInterroge;

-- schema dossier_reponses_link : renommage de idMinistereInterpelle en idMinistereAttributaire
ALTER TABLE DOSSIER_REPONSES_LINK RENAME COLUMN idMinistereInterpelle TO idMinistereAttributaire;

-- schema dossier_reponses : renommage de ministereInterpellePrecedent en ministereAttributairePrecedent
ALTER TABLE DOSSIER_REPONSE RENAME COLUMN ministereInterpellePrecedent TO ministereAttributairePrecedent;
-- schema dossier_reponses : renommage de ministereInterpelleCourant en ministereAttributaireCourant
ALTER TABLE DOSSIER_REPONSE RENAME COLUMN ministereInterpelleCourant TO ministereAttributaireCourant;

-- schema dossier_reponses_link : suppresion origineQuestion
ALTER TABLE DOSSIER_REPONSES_LINK DROP COLUMN ORIGINEQUESTION;
-- schema dossier_reponses_link : suppresion titreJo
ALTER TABLE DOSSIER_REPONSES_LINK DROP COLUMN TITREJOMINISTERE;
