update "HIERARCHY" 
SET "ISPROPERTY"=0,
"PARENTID"=(select "IDDOCUMENTQUESTION" from "DOSSIER_REPONSE" WHERE DOSSIER_REPONSE."ID"="PARENTID"),
"NAME"='Attribution' || "POS",
"POS"=null,
"PRIMARYTYPE"='HistoriqueAttribution' 
WHERE "PRIMARYTYPE"='historiqueAttribution';

commit;
CALL SW_REBUILD_MISSING_READ_ACLS();
commit;
/
