ALTER TABLE REPONSES_LOGGING ADD CLOSEENDCOUNT NUMBER(19,0);
ALTER TABLE REPONSES_LOGGING ADD CLOSEPREVISIONALCOUNT NUMBER(19,0);
ALTER TABLE REPONSES_LOGGING ADD CURRENTGOUVERNEMENT VARCHAR2(255);
ALTER TABLE REPONSES_LOGGING ADD NEXTGOUVERNEMENT VARCHAR2(255);


CREATE TABLE "TIMBRE"
(	"ID" VARCHAR2(36) NOT NULL ENABLE,
	"CURRENTMIN" NVARCHAR2(2000),
	"NEXTMIN" NVARCHAR2(2000),
	CONSTRAINT "TIMBRE_PK" PRIMARY KEY ("ID"),
	CONSTRAINT "TIMBRE_00C5451D_FK" FOREIGN KEY ("ID")
		REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE
);

commit;