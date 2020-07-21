-- Création de la table pour les documents d'exports
CREATE TABLE "EXPORT_DOCUMENT"
	(	"ID" VARCHAR2(36 BYTE) NOT NULL,
		"DATEREQUEST" TIMESTAMP(6),
		"OWNER" NVARCHAR2(2000)
   ) ;
   
commit;

