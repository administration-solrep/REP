-- Identification des dossiers nécessitant une réparation de l'historique pour la législature 14

UPDATE "DOSSIER_REPONSE" SET "HISTORIQUEDOSSIERTRAITE" = 1;

UPDATE DOSSIER_REPONSE SET HISTORIQUEDOSSIERTRAITE = 0 where id in (select dr.id from dossier_reponse dr, question qu where not exists (select * from hierarchy h, historiqueattribution ha 
where dr.id=h.parentid and h.primarytype='historiqueAttribution' and h.id=ha.id and ha.typeattribution='instanciation') and
dr.iddocumentquestion=qu.id and qu.legislaturequestion=14);

commit;
