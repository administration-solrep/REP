-- Ajout colonne visibility
ALTER TABLE BIRTREPORTMODEL ADD DROITVISIBILITERESTRAINTSGG NUMBER(1,0) DEFAULT 0 ;
COMMIT;

-- Statistique valeur
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

-- Ministère étape
ALTER TABLE STATISTIQUE_ETAPE 
ADD
  (
    "IDMINISTEREETAPE" VARCHAR2(2000)
  )
;
COMMIT;

-- modificaiton table statistique
alter table statistique_ministere add ( nbRenouvelleEC number(10,0));

ALTER TABLE STATISTIQUE_ETAPE ADD IDMINISTERERATTACHEMENT NVARCHAR2(50);
ALTER TABLE STATISTIQUE_ETAPE ADD INTITULEMINISTERERATTACHEMENT NVARCHAR2(2000);


-- Vue statistique


CREATE OR REPLACE VIEW V_STATISTIQUE_DATE_PARCOURS AS
select t3.IDMINISTERERATTACHEMENT as idministere, t3.INTITULEMINISTERERATTACHEMENT as ministere, t3.origine as origine,t3.numero as numero,t3.datejo as datejo, 
t3.datejoreponse as datejoreponse,
t2.datefinMin as DATETRANSMISSIONMINISTERE, t1.DateTransmissionParlement as DateTransmissionParlement
from
(select origine,numero,
max(case when idtype=11 then datefin else null end) DateTransmissionParlement
from statistique_etape
group by origine,numero,datejo,datejoreponse,IDMINISTERERATTACHEMENT,INTITULEMINISTERERATTACHEMENT
order by numero) t1,
(select origine, numero,  
  (case when( max(nvl(datefin, date '9999-12-31')))=date '9999-12-31' then null else max(nvl(datefin, date '9999-12-31')) end) as datefinMin
            from statistique_etape
            where IDMINISTERERATTACHEMENT =  idministereetape
            group by origine, numero,datejo, datejoreponse,IDMINISTERERATTACHEMENT order by numero) t2,
(select origine,numero,datejo,IDMINISTERERATTACHEMENT,INTITULEMINISTERERATTACHEMENT,datejoreponse 
from statistique_etape 
group by origine, numero,datejo, datejoreponse,IDMINISTERERATTACHEMENT,INTITULEMINISTERERATTACHEMENT,datejoreponse 
order by numero) t3
where
t3.origine = t1.origine (+) and t3.origine=t2.origine (+) and
t3.numero = t1.numero (+) and t3.numero=t2.numero (+);

commit;

