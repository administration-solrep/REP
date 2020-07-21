create or replace
PROCEDURE generateDenormalisation AS
  pragma autonomous_transaction;
  qcount INTEGER :=0;
  mots VARCHAR2(2000);
  etat VARCHAR2(2000);
 BEGIN
	
	FOR q in (select q.id from question q  ) LOOP
		DBMS_OUTPUT.PUT_LINE('question start');
		DBMS_OUTPUT.PUT_LINE(q.id);
		
		DBMS_OUTPUT.PUT_LINE('maj mots cles start');
		mots := '';
		
		UPDATE QUESTION SET MOTSCLES='' where id=q.id;
		
		FOR ind in (SELECT item from IXA_SE_THEME ind where id=q.id ORDER BY POS) LOOP
			mots := CONCAT(mots,ind.item);		
			mots := CONCAT(mots,' ');
		END LOOP;
		
		FOR ind in (SELECT item from IXA_SE_RUBRIQUE ind where id=q.id ORDER BY POS) LOOP
			mots := CONCAT(mots,ind.item);		
			mots := CONCAT(mots,' ');
		END LOOP;
		
		FOR ind in (SELECT item from IXA_SE_RENVOI ind where id=q.id ORDER BY POS) LOOP
			mots := CONCAT(mots,ind.item);		
			mots := CONCAT(mots,' ');
		END LOOP;
		
		FOR ind in (SELECT item from IXA_AN_RUBRIQUE ind where id=q.id ORDER BY POS) LOOP
			mots := CONCAT(mots,ind.item);		
			mots := CONCAT(mots,' ');
		END LOOP;
		
		FOR ind in (SELECT item from IXA_TA_RUBRIQUE ind where id=q.id ORDER BY POS) LOOP
			mots := CONCAT(mots,ind.item);		
			mots := CONCAT(mots,' ');
		END LOOP;
		
		FOR ind in (SELECT item from IXA_AN_ANALYSE ind where id=q.id ORDER BY POS) LOOP
			mots := CONCAT(mots,ind.item);		
			mots := CONCAT(mots,' ');
		END LOOP;
		
		FOR ind in (SELECT item from IXA_MOTCLEF_MINISTERE ind where id=q.id ORDER BY POS) LOOP
			mots := CONCAT(mots,ind.item);		
			mots := CONCAT(mots,' ');
		END LOOP;
		
		UPDATE QUESTION SET MOTSCLES=mots WHERE ID=q.id;
		
		DBMS_OUTPUT.PUT_LINE('maj mots cles end');
		
		DBMS_OUTPUT.PUT_LINE('maj etat start');
		
		UPDATE QUESTION SET ETATSQUESTION='' where id=q.id;
		
		UPDATE QUESTION SET ETATSQUESTION=CONCAT(ETATSQUESTION, 
			CASE WHEN ((SELECT PRIORITE FROM QUESTION WHERE id=q.id)='Urgent')
				THEN 't'
				ELSE 'f'
			END
		) WHERE ID=q.id;
		
		UPDATE QUESTION SET ETATSQUESTION=CONCAT(ETATSQUESTION, 
			CASE WHEN ((SELECT count(id) FROM HIERARCHY WHERE PARENTID=q.id AND PRIMARYTYPE='signalementQuestion')>0)
				THEN 't'
				ELSE 'f'
			END
		) WHERE ID=q.id;
		
		UPDATE QUESTION SET ETATSQUESTION=CONCAT(ETATSQUESTION, 
			CASE WHEN ((SELECT count(id) FROM HIERARCHY WHERE PARENTID=q.id AND PRIMARYTYPE='renouvellementQuestion')>0) 
				THEN 't'
				ELSE 'f'
			END
		) WHERE ID=q.id;

		DBMS_OUTPUT.PUT_LINE('maj etat end');
		
		qcount:=qcount+1;
		DBMS_OUTPUT.PUT_LINE(qcount);
		DBMS_OUTPUT.PUT_LINE('question END');
	END LOOP;
	DBMS_OUTPUT.PUT_LINE(qcount);
	DBMS_OUTPUT.PUT_LINE('end');
	commit;
END;
/
BEGIN
  generateDenormalisation();
END ;
/
commit;