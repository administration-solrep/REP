ALTER TABLE "DOSSIER_REPONSE" ADD "HASPJ" NUMBER(1,0) DEFAULT 0;

UPDATE "DOSSIER_REPONSE"
SET "DOSSIER_REPONSE"."HASPJ" = 1
WHERE "DOSSIER_REPONSE"."ID" IN (
SELECT d.id AS id FROM "DOSSIER_REPONSE" d, "FONDDOSSIER" f, "FILE" fi, "HIERARCHY" h
WHERE d.iddocumentfdd = f.id
AND (h.parentid = f.repertoire_ministere OR h.parentid = f.repertoire_parlement OR h.parentid = f.repertoire_sgg)
AND h.id = fi.id)

commit;