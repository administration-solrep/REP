-- ATTENTION: ce script met plusieurs heures à s'exécuter

-- ALTER TRIGGER "NX_TRIG_HIERARCHY_UPDATE" DISABLE;

-- mise a jour du parent des documents Question
UPDATE HIERARCHY H SET(PARENTID) = (SELECT D.ID FROM DOSSIER_REPONSE D WHERE D.idDocumentQuestion = H.ID) WHERE H.PRIMARYTYPE='Question';

-- mise a jour du parent des document Reponse
UPDATE HIERARCHY H SET(PARENTID) = (SELECT D.ID FROM DOSSIER_REPONSE D WHERE D.idDocumentReponse = H.ID) WHERE H.PRIMARYTYPE='Reponse';

commit;
-- ALTER TRIGGER "NX_TRIG_HIERARCHY_UPDATE" ENABLE;
