ALTER TABLE Question ADD "CONNEXITE" NUMBER(19,0) DEFAULT 0;

create or replace
PROCEDURE "UPDATE_CONNEXITE"
-- Met à jour le champ connexité contenu dans la table question
IS  
BEGIN
	update question set connexite=0;
  DBMS_OUTPUT.PUT_LINE('start updateConnexite');
  FOR connexitean in (SELECT COUNT(1) AS nbre_doublon, hashconnexitean as texte FROM question where hashconnexitean is not null GROUP BY hashconnexitean) LOOP
    IF connexitean.nbre_doublon > 1 THEN
     update question q set q.connexite = q.connexite + connexitean.nbre_doublon where q.hashconnexitean = connexitean.texte;
    END IF;
  END LOOP;

  FOR connexitetitre in (SELECT COUNT(1) AS nbre_doublon, hashconnexitetitre as texte FROM question where hashconnexitetitre is not null GROUP BY hashconnexitetitre) LOOP
    IF connexitetitre.nbre_doublon > 1 THEN
      update question q set q.connexite = q.connexite + connexitetitre.nbre_doublon where q.hashconnexitean = connexitetitre.texte;
    END IF;
   END LOOP;
   
  FOR connexitetexte in (SELECT COUNT(1) AS nbre_doublon, hashconnexitetexte as texte FROM question where hashconnexitetexte is not null GROUP BY hashconnexitetexte) LOOP
    IF connexitetexte.nbre_doublon > 1 THEN
     update question q set q.connexite = q.connexite + connexitetexte.nbre_doublon where q.hashconnexitean = connexitetexte.texte;
    END IF;
   END LOOP;
   DBMS_OUTPUT.PUT_LINE('end updateConnexite');
END;
/

commit;
