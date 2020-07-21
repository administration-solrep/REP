create or replace PROCEDURE "UPDATE_CONNEXITE"
IS 
BEGIN  
     DBMS_OUTPUT.PUT_LINE('start updateConnexite');
      update question set connexite=0;
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
   

    FOR connexitese in (SELECT COUNT(1) AS doublette,HASHCONNEXITESE as hashSE FROM question where HASHCONNEXITESE  is not null GROUP BY HASHCONNEXITESE) LOOP
    IF connexitese.doublette > 1 THEN
      update question p set p.connexite = p.connexite + connexitese.doublette where p.HASHCONNEXITESE = CONNEXITESE.hashSE;
    END IF;
   END LOOP;

 FOR connexitetitre in (SELECT COUNT(1) AS nbre_doublon, hashconnexitetitre as hashSE FROM question where hashconnexitetitre is not null GROUP BY hashconnexitetitre) LOOP
    IF connexitetitre.nbre_doublon > 1 THEN
      update question p set p.connexite = p.connexite + connexitetitre.nbre_doublon  where  p.HASHCONNEXITESE  = connexitetitre.hashSE;
    END IF;
   END LOOP;
   
  FOR connexitetexte in (SELECT COUNT(1) AS nbre_doublon, hashconnexitetexte as hashSE FROM question where hashconnexitetexte is not null GROUP BY hashconnexitetexte) LOOP
    IF connexitetexte.nbre_doublon > 1 THEN
    update question p set p.connexite = p.connexite + connexitetexte.nbre_doublon  where  p.HASHCONNEXITESE  = connexitetexte.hashSE;
    END IF;
   END LOOP;
   DBMS_OUTPUT.PUT_LINE('end updateConnexite');
END;