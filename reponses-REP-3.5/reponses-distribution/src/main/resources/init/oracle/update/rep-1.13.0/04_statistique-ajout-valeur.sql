
Drop table "STATISTIQUE_VALEUR";

CREATE TABLE "STATISTIQUE_VALEUR"
   (	"ID" NUMBER(19,0) NOT NULL ENABLE,
	"IDRAPPORT" VARCHAR2(255 CHAR),
	"LIBELLE" VARCHAR2(255 CHAR),
	"REQUETE" VARCHAR2(255 CHAR),
    "VALEUR" VARCHAR2(255 CHAR),
	 PRIMARY KEY ("ID")
   );

Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (2,'1a','Nombre total de réponses en attente',13,'select sum(nbquestion)-(sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur)) from statistique_question_reponse where ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (3,'1a','Nombre total de réponses publiées',76,'select sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur) from statistique_question_reponse where ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (4,'1b','Réponse à 1 mois',45,'select sum(nbrepondu1mois) from statistique_question_reponse where ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (5,'1b','Réponse de 1 à 2 mois',51,'select sum(nbrepondu2mois) from statistique_question_reponse where ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (6,'1b','Réponse supérieur à 2 mois',72,'select sum(nbrepondusuperieur) from statistique_question_reponse where ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (7,'2a','Nombre total de réponses en attente',13,'select sum(nbquestion)-(sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur)) from statistique_question_reponse where origine=''AN'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (8,'2a','Nombre total de réponses publiées',76,'select sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur) from statistique_question_reponse where origine=''AN'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (9,'2b','Réponse à 1 mois',45,'select sum(nbrepondu1mois) from statistique_question_reponse where origine=''AN'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (10,'2b','Réponse de 1 à 2 mois',51,'select sum(nbrepondu2mois) from statistique_question_reponse where origine=''AN'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (11,'2b','Réponse supérieur à 2 mois',72,'select sum(nbrepondusuperieur) from statistique_question_reponse where origine=''AN'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (12,'3a','Nombre total de réponses en attente',13,'select sum(nbquestion)-(sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur)) from statistique_question_reponse where origine=''SENAT'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (13,'3a','Nombre total de réponses publiées',76,'select sum(nbrepondu1mois)+sum(nbrepondu2mois)+sum(nbrepondusuperieur) from statistique_question_reponse where origine=''SENAT'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (14,'3b','Réponse à 1 mois',45,'select sum(nbrepondu1mois) from statistique_question_reponse where origine=''SENAT'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (15,'3b','Réponse de 1 à 2 mois',51,'select sum(nbrepondu2mois) from statistique_question_reponse where origine=''SENAT'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (16,'3b','Réponse supérieur à 2 mois',72,'select sum(nbrepondusuperieur) from statistique_question_reponse where origine=''SENAT'' and ministere=''GLOBAL''');
Insert into STATISTIQUE_VALEUR (id,IDRAPPORT,LIBELLE,VALEUR,REQUETE) values (17,'','Date de l''Etat','','SELECT to_char(sysdate,''dd/MM/YYYY'') from dual');
commit;

