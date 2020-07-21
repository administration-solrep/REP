CREATE OR REPLACE PROCEDURE computePrecomptageMailbox(mailboxDocId VARCHAR2) AS
BEGIN	
	DBMS_OUTPUT.PUT_LINE('genPrecomptage for '||mailboxDocId);
	DELETE FROM RMLBX_PRECALCULLIST WHERE ID = mailboxDocId;
	INSERT INTO RMLBX_PRECALCULLIST(ID,ITEM) SELECT H.PARENTID, idMinistereAttributaire  || ':' || routingTaskType || ':' || count(*) 
		FROM HIERARCHY H, DOSSIER_REPONSES_LINK L, MISC M WHERE H.ID = L.ID AND H.PARENTID = mailboxDocId
		AND H.ID = M.ID AND M.LIFECYCLESTATE = 'todo'
		GROUP BY H.PARENTID, L.idMinistereAttributaire, L.routingTaskType;
	INSERT INTO CLUSTER_INVALS (SELECT NODEID, mailboxDocId, 'rmlbx:preCalculList', 1 FROM CLUSTER_NODES);
END;
/

CREATE OR REPLACE PROCEDURE computePrecomptage AS
BEGIN
	delete from RMLBX_PRECALCULLIST;
	DBMS_OUTPUT.PUT_LINE('start genPrecomptage');
	FOR rep in (select rep.id from hierarchy rep where rep.primarytype = 'ReponsesMailbox') LOOP
		computePrecomptageMailbox(rep.id);
	END LOOP;
END;
/

commit;

