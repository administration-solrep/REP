DROP TABLE "V_STATISTIQUE_DATE_PARCOURS";
CREATE TABLE "V_STATISTIQUE_DATE_PARCOURS" 
   (	
	"IDMINISTERE" NVARCHAR2(50), 
	"MINISTERE" NVARCHAR2(2000), 
	"ORIGINE" NVARCHAR2(50),
	"NUMERO" NUMBER(19,0), 
	"DATEJO" TIMESTAMP(6),
	"DATEJOREPONSE" TIMESTAMP(6),
	"DATETRANSMISSIONMINISTERE" TIMESTAMP(6),
	"DATETRANSMISSIONPARLEMENT" TIMESTAMP(6)
   );