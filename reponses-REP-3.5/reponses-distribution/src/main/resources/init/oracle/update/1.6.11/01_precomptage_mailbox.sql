
CREATE OR REPLACE PROCEDURE computePrecomptage AS
BEGIN
	delete from RMLBX_PRECALCULLIST;
	DBMS_OUTPUT.PUT_LINE('start genPrecomptage');
	FOR rep in (select rep.id from hierarchy rep where rep.primarytype = 'ReponsesMailbox') LOOP
		DBMS_OUTPUT.PUT_LINE('process mailbox '||rep.id);
		INSERT INTO RMLBX_PRECALCULLIST(ID,ITEM) SELECT H.PARENTID, idMinistereAttributaire  || ':' || routingTaskType || ':' || count(*) 
			FROM HIERARCHY H, DOSSIER_REPONSES_LINK L, MISC M WHERE H.ID = L.ID AND H.PARENTID = rep.id
			AND H.ID = M.ID AND M.LIFECYCLESTATE = 'todo'
			GROUP BY H.PARENTID, L.idMinistereAttributaire, L.routingTaskType;
	END LOOP;
END;
/

