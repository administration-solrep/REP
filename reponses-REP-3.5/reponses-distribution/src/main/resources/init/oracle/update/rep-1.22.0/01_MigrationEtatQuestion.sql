    CREATE TABLE "ETATQUESTION"
   (	"ID" VARCHAR2(36) NOT NULL ENABLE,
	"DATE_CHANGEMENT_ETAT" TIMESTAMP (6),
	"ETATQUESTION" NVARCHAR2(2000)	 
   );
   CREATE UNIQUE INDEX "ETATQUESTION_PK" ON "ETATQUESTION" ("ID") TABLESPACE INDX;
   ALTER TABLE "ETATQUESTION" ADD CONSTRAINT "ETATQUESTION_PK" PRIMARY KEY ("ID") ENABLE;
   ALTER TABLE "ETATQUESTION" ADD CONSTRAINT "ETATQUESTION_PARENT_FK" FOREIGN KEY ("ID") REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;
   
   INSERT INTO "ETATQUESTION" ("ID","DATE_CHANGEMENT_ETAT","ETATQUESTION") SELECT e."ID",e."DATE_CHANGEMENT_ETAT",e."ETATQUESTION" FROM "ETATQUESTION#ANONYMOUSTYPE" e;

update "HIERARCHY" 
SET "ISPROPERTY"=0,
"NAME"='EtatQuestion' || "POS",
"POS"=null,
"PRIMARYTYPE"='EtatQuestion' 
WHERE "PRIMARYTYPE"='etatQuestion#anonymousType';

commit;
CALL SW_REBUILD_MISSING_READ_ACLS();
commit;
/
