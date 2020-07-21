create or replace
PROCEDURE generateDocumentRouteId AS
 pragma autonomous_transaction;
  idfdr VARCHAR2(2000);
 BEGIN
  DBMS_OUTPUT.PUT_LINE('start');
	FOR fr in (select fr.id as id from feuille_route fr  ) LOOP
		FOR s_H_0 in (SELECT s_H_0."ID" as id  FROM HIERARCHY s_H_0  WHERE s_H_0."PRIMARYTYPE" IN ( 'DocumentRouteStep', 'RouteStep', 'GenericDistributionTask', 'DistributionStep', 'DistributionTask', 'PersonalDistributionTask') START WITH s_H_0."ID" IN (SELECT f_H_0."ID" FROM HIERARCHY f_H_0 WHERE f_H_0."PRIMARYTYPE" IN ( 'FeuilleRoute', 'DocumentRoute' ) AND f_H_0."ID" = fr.id) CONNECT BY PRIOR s_H_0."ID" = s_H_0."PARENTID") LOOP    
			UPDATE ROUTING_TASK SET documentrouteid = fr.id WHERE id = s_H_0.id;
		END LOOP;
	END LOOP;
  DBMS_OUTPUT.PUT_LINE('end');
  commit;
END;
/

set serveroutput on
BEGIN
  GENERATEDOCUMENTROUTEID();
END;
/