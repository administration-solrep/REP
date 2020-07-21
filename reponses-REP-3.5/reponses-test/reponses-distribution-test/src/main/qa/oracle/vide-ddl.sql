--------------------------------------------------------
--  Fichier créé - mercredi-août-05-2015   
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Procedure INIT_INDEX
--------------------------------------------------------

  CREATE OR REPLACE PROCEDURE "INIT_INDEX" 
IS
BEGIN
	CTXSYS.CTX_DDL.CREATE_PREFERENCE ('REP_LEXER', 'BASIC_LEXER');
	CTXSYS.CTX_DDL.SET_ATTRIBUTE ('REP_LEXER', 'base_letter', 'YES');
	CTXSYS.CTX_DDL.SET_ATTRIBUTE ('REP_LEXER', 'index_stems', 'FRENCH');
 	CTXSYS.CTX_DDL.CREATE_PREFERENCE('REP_WORDLIST', 'BASIC_WORDLIST');
	CTXSYS.CTX_DDL.SET_ATTRIBUTE('REP_WORDLIST', 'stemmer', 'FRENCH');
	CTXSYS.CTX_DDL.SET_ATTRIBUTE('REP_WORDLIST', 'substring_index', 'NO');
	CTXSYS.CTX_DDL.SET_ATTRIBUTE('REP_WORDLIST', 'prefix_index', 'NO');
END;



call INIT_INDEX();



--------------------------------------------------------
--  DDL for Type NX_STRING_ARRAY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "NX_STRING_ARRAY" AS VARRAY(200) OF VARCHAR2(32767);


--------------------------------------------------------
--  DDL for Type NX_STRING_TABLE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "NX_STRING_TABLE" AS TABLE OF VARCHAR2(4000);


--------------------------------------------------------
--  DDL for Table VOC_AN_ANALYSE
--------------------------------------------------------

  CREATE TABLE "VOC_AN_ANALYSE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_AN_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "VOC_AN_RUBRIQUE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_BOOLEAN
--------------------------------------------------------

  CREATE TABLE "VOC_BOOLEAN" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_BOOLEAN_REQUETEUR
--------------------------------------------------------

  CREATE TABLE "VOC_BOOLEAN_REQUETEUR" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_BORDEREAU_LABEL
--------------------------------------------------------

  CREATE TABLE "VOC_BORDEREAU_LABEL" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_CARACT_QUESTION
--------------------------------------------------------

  CREATE TABLE "VOC_CARACT_QUESTION" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_CM_ROUTING_TASK_TYPE
--------------------------------------------------------

  CREATE TABLE "VOC_CM_ROUTING_TASK_TYPE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_ETAT_ETAPE
--------------------------------------------------------

  CREATE TABLE "VOC_ETAT_ETAPE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_ETAT_QUESTION
--------------------------------------------------------

  CREATE TABLE "VOC_ETAT_QUESTION" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_FEUILLEROUTE_TYPE_CREATION
--------------------------------------------------------

  CREATE TABLE "VOC_FEUILLEROUTE_TYPE_CREATION" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"parent" NVARCHAR2(2000), 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_GROUPE_POLITIQUE
--------------------------------------------------------

  CREATE TABLE "VOC_GROUPE_POLITIQUE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_LEGISLATURE
--------------------------------------------------------

  CREATE TABLE "VOC_LEGISLATURE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"dateFin" TIMESTAMP (6), 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000), 
	"dateDebut" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_ORIGINE_QUESTION
--------------------------------------------------------

  CREATE TABLE "VOC_ORIGINE_QUESTION" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_SE_RENVOI
--------------------------------------------------------

  CREATE TABLE "VOC_SE_RENVOI" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_SE_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "VOC_SE_RUBRIQUE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_SE_THEME
--------------------------------------------------------

  CREATE TABLE "VOC_SE_THEME" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_STATUT_ETAPE_RECHERCHE
--------------------------------------------------------

  CREATE TABLE "VOC_STATUT_ETAPE_RECHERCHE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_TA_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "VOC_TA_RUBRIQUE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"parent" NVARCHAR2(2000), 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_TITLE
--------------------------------------------------------

  CREATE TABLE "VOC_TITLE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_TYPE_QUESTION_AN
--------------------------------------------------------

  CREATE TABLE "VOC_TYPE_QUESTION_AN" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_TYPE_QUESTION_RECHERCHE
--------------------------------------------------------

  CREATE TABLE "VOC_TYPE_QUESTION_RECHERCHE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_TYPE_UNITE_STRUCTURELLE
--------------------------------------------------------

  CREATE TABLE "VOC_TYPE_UNITE_STRUCTURELLE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VOC_VALIDATION_STATUT_ETAPE
--------------------------------------------------------

  CREATE TABLE "VOC_VALIDATION_STATUT_ETAPE" 
   (	"id" NVARCHAR2(2000), 
	"obsolete" NUMBER(19,0) DEFAULT 0, 
	"ordering" NUMBER(19,0) DEFAULT 10000000, 
	"label" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table ACLR
--------------------------------------------------------

  CREATE TABLE "ACLR" 
   (	"ACL_ID" VARCHAR2(34), 
	"ACL" VARCHAR2(4000)
   ) ;


--------------------------------------------------------
--  DDL for Table ACLR_MODIFIED
--------------------------------------------------------

  CREATE TABLE "ACLR_MODIFIED" 
   (	"HIERARCHY_ID" VARCHAR2(36), 
	"IS_NEW" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table ACLR_PERMISSION
--------------------------------------------------------

  CREATE TABLE "ACLR_PERMISSION" 
   (	"PERMISSION" VARCHAR2(250)
   ) ;


--------------------------------------------------------
--  DDL for Table ACLS
--------------------------------------------------------

  CREATE TABLE "ACLS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"NAME" VARCHAR2(250), 
	"GRANT" NUMBER(1,0), 
	"PERMISSION" VARCHAR2(250), 
	"USER" VARCHAR2(250), 
	"GROUP" VARCHAR2(250)
   ) ;


--------------------------------------------------------
--  DDL for Table ACTIONNABLE_CASE_LINK
--------------------------------------------------------

  CREATE TABLE "ACTIONNABLE_CASE_LINK" 
   (	"ID" VARCHAR2(36), 
	"STEPDOCUMENTID" VARCHAR2(50), 
	"VALIDATIONOPERATIONCHAINID" NVARCHAR2(2000), 
	"REFUSALOPERATIONCHAINID" NVARCHAR2(2000), 
	"DUEDATE" TIMESTAMP (6), 
	"TASKTYPE" NVARCHAR2(2000), 
	"AUTOMATICVALIDATION" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table ADVANCED_SEARCH
--------------------------------------------------------

  CREATE TABLE "ADVANCED_SEARCH" 
   (	"ID" VARCHAR2(36), 
	"TITLE" NVARCHAR2(2000), 
	"VALID_MAX" TIMESTAMP (6), 
	"EXPIRED_MIN" TIMESTAMP (6), 
	"FULLTEXT_NONE" NVARCHAR2(2000), 
	"FORMAT" NVARCHAR2(2000), 
	"ISCHECKEDINVERSION" NUMBER(1,0), 
	"VALID_MIN" TIMESTAMP (6), 
	"ISSUED_MAX" TIMESTAMP (6), 
	"EXPIRED_MAX" TIMESTAMP (6), 
	"CREATED_MIN" TIMESTAMP (6), 
	"CREATED_MAX" TIMESTAMP (6), 
	"FULLTEXT_PHRASE" NVARCHAR2(2000), 
	"MODIFIED_MAX" TIMESTAMP (6), 
	"FULLTEXT_ALL" NVARCHAR2(2000), 
	"SORTCOLUMN" NVARCHAR2(2000), 
	"DESCRIPTION" NVARCHAR2(2000), 
	"CURRENTLIFECYCLESTATE" NVARCHAR2(2000), 
	"SOURCE" NVARCHAR2(2000), 
	"SORTASCENDING" NUMBER(1,0), 
	"RIGHTS" NVARCHAR2(2000), 
	"LANGUAGE" NVARCHAR2(2000), 
	"ISSUED_MIN" TIMESTAMP (6), 
	"MODIFIED_MIN" TIMESTAMP (6), 
	"FULLTEXT_ONE_OF" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table ALERT
--------------------------------------------------------

  CREATE TABLE "ALERT" 
   (	"ID" VARCHAR2(36), 
	"DATEVALIDITYBEGIN" TIMESTAMP (6), 
	"ISACTIVATED" NUMBER(1,0), 
	"PERIODICITY" NVARCHAR2(2000), 
	"REQUETEID" NVARCHAR2(2000), 
	"DATEVALIDITYEND" TIMESTAMP (6), 
	"DATEDEMANDECONFIRMATION" TIMESTAMP (6), 
	"HASDEMANDECONFIRMATION" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table ALLOT_IDDOSSIERS
--------------------------------------------------------

  CREATE TABLE "ALLOT_IDDOSSIERS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table ALLOTISSEMENT
--------------------------------------------------------

  CREATE TABLE "ALLOTISSEMENT" 
   (	"ID" VARCHAR2(36), 
	"NOM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table ALTR_RECIPIENTS
--------------------------------------------------------

  CREATE TABLE "ALTR_RECIPIENTS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;
   
--------------------------------------------------------
--  DDL for Table ALTR_EXTERNALRECIPIENTS
--------------------------------------------------------
CREATE TABLE "ALTR_EXTERNALRECIPIENTS"
   (
        "ID"  VARCHAR2(36),
        "POS" NUMBER(10,0),
        "ITEM" NVARCHAR2(2000)
   ) ;



--------------------------------------------------------
--  DDL for Table ANCESTORS
--------------------------------------------------------

  CREATE TABLE "ANCESTORS" 
   (	"HIERARCHY_ID" VARCHAR2(36), 
	"ANCESTORS" "NX_STRING_TABLE" 
   ) 
 NESTED TABLE "ANCESTORS" STORE AS "ANCESTORS_ANCESTORS"
 RETURN AS VALUE;



--------------------------------------------------------
--  DDL for Table ATTRIBUTION#ANONYMOUSTYPE
--------------------------------------------------------

  CREATE TABLE "ATTRIBUTION#ANONYMOUSTYPE" 
   (	"ID" VARCHAR2(36), 
	"TITREJOMINISTERE" NVARCHAR2(2000), 
	"INTITULEMINISERE" NVARCHAR2(2000), 
	"ID_MINISTERE_ATTRIBUTAIRE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table BATCH_LOG
--------------------------------------------------------

  CREATE TABLE "BATCH_LOG" 
   (	"ID_LOG" NUMBER(19,0), 
	"END_TIME" TIMESTAMP (6), 
	"ERROR_COUNT" NUMBER(19,0), 
	"NAME" NVARCHAR2(255), 
	"PARENT_ID" NUMBER(19,0), 
	"SERVER" NVARCHAR2(255), 
	"START_TIME" TIMESTAMP (6), 
	"TOMCAT" NUMBER(19,0), 
	"TYPE" NVARCHAR2(255)
   ) ;


--------------------------------------------------------
--  DDL for Table BATCH_RESULT
--------------------------------------------------------

  CREATE TABLE "BATCH_RESULT" 
   (	"ID_RESULT" NUMBER(19,0), 
	"ID_LOG" NUMBER(19,0), 
	"EXECUTION_RESULT" NUMBER(19,0), 
	"EXECUTION_TIME" NUMBER(19,0), 
	"TEXT" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table BIRTREPORT
--------------------------------------------------------

  CREATE TABLE "BIRTREPORT" 
   (	"ID" VARCHAR2(36), 
	"REPORTKEY" NVARCHAR2(2000), 
	"OLDMODELREF" NVARCHAR2(2000), 
	"MODELREF" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table BIRTREPORTMODEL
--------------------------------------------------------

  CREATE TABLE "BIRTREPORTMODEL" 
   (	"ID" VARCHAR2(36), 
	"REPORTNAME" NVARCHAR2(2000), 
	"PARAMETREORGANIGRAMME" NVARCHAR2(2000), 
	"DROITVISIBILITERESTRAINTSGG" NUMBER(1,0) DEFAULT 0
   ) ;


--------------------------------------------------------
--  DDL for Table CASE_DOCUMENTSID
--------------------------------------------------------

  CREATE TABLE "CASE_DOCUMENTSID" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CASE_ITEM
--------------------------------------------------------

  CREATE TABLE "CASE_ITEM" 
   (	"ID" VARCHAR2(36), 
	"RECEIVE_DATE" TIMESTAMP (6), 
	"BODY" NVARCHAR2(2000), 
	"TYPE" NVARCHAR2(2000), 
	"REFERENCE" NVARCHAR2(2000), 
	"DEFAULTCASEID" NVARCHAR2(2000), 
	"DOCUMENT_DATE" TIMESTAMP (6), 
	"IMPORT_DATE" TIMESTAMP (6), 
	"SENDING_DATE" TIMESTAMP (6), 
	"CONFIDENTIALITY" NVARCHAR2(2000), 
	"ORIGIN" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CASE_LINK
--------------------------------------------------------

  CREATE TABLE "CASE_LINK" 
   (	"ID" VARCHAR2(36), 
	"COMMENT" NVARCHAR2(2000), 
	"TYPEINFO" NVARCHAR2(2000), 
	"CASEDOCUMENTID" VARCHAR2(50), 
	"DRAFT" NUMBER(1,0), 
	"CASEITEMID" NVARCHAR2(2000), 
	"DATE" TIMESTAMP (6), 
	"ISACTIONABLE" NUMBER(1,0), 
	"SENDERMAILBOXID" NVARCHAR2(2000), 
	"SENTDATE" TIMESTAMP (6), 
	"CASEREPOSITORYNAME" NVARCHAR2(2000), 
	"TYPE" NVARCHAR2(2000), 
	"ISSENT" NUMBER(1,0), 
	"SENDER" NVARCHAR2(2000), 
	"ISREAD" NUMBER(1,0),
	"DATEFIELDDEBUTVALIDATION" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table CLASSIFICATION_TARGETS
--------------------------------------------------------

  CREATE TABLE "CLASSIFICATION_TARGETS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CLUSTER_INVALS
--------------------------------------------------------

  CREATE TABLE "CLUSTER_INVALS" 
   (	"NODEID" VARCHAR2(25), 
	"ID" VARCHAR2(36), 
	"FRAGMENTS" VARCHAR2(4000), 
	"KIND" NUMBER(3,0)
   ) ;


--------------------------------------------------------
--  DDL for Table CLUSTER_NODES
--------------------------------------------------------

  CREATE TABLE "CLUSTER_NODES" 
   (	"NODEID" VARCHAR2(25), 
	"CREATED" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table CMDIST_ALL_ACTION_PAR_6B4BBED8
--------------------------------------------------------

  CREATE TABLE "CMDIST_ALL_ACTION_PAR_6B4BBED8" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CMDIST_ALL_COPY_PARTI_21AB3C5B
--------------------------------------------------------

  CREATE TABLE "CMDIST_ALL_COPY_PARTI_21AB3C5B" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CMDIST_INITIAL_ACTION_4CD43708
--------------------------------------------------------

  CREATE TABLE "CMDIST_INITIAL_ACTION_4CD43708" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(100)
   ) ;


--------------------------------------------------------
--  DDL for Table CMDIST_INITIAL_ACTION_88A481B7
--------------------------------------------------------

  CREATE TABLE "CMDIST_INITIAL_ACTION_88A481B7" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CMDIST_INITIAL_COPY_E_B3610C04
--------------------------------------------------------

  CREATE TABLE "CMDIST_INITIAL_COPY_E_B3610C04" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CMDIST_INITIAL_COPY_I_D6588F7E
--------------------------------------------------------

  CREATE TABLE "CMDIST_INITIAL_COPY_I_D6588F7E" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CMDOC_SENDERS
--------------------------------------------------------

  CREATE TABLE "CMDOC_SENDERS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table COMMENT
--------------------------------------------------------

  CREATE TABLE "COMMENT" 
   (	"ID" VARCHAR2(36), 
	"AUTHOR" NVARCHAR2(2000), 
	"CREATIONDATE" TIMESTAMP (6), 
	"COMMENTEDDOCID" VARCHAR2(50), 
	"TEXT" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table COMMON
--------------------------------------------------------

  CREATE TABLE "COMMON" 
   (	"ID" VARCHAR2(36), 
	"ICON" NVARCHAR2(2000), 
	"ICON-EXPANDED" NVARCHAR2(2000), 
	"SIZE" NUMBER(19,0)
   ) ;


--------------------------------------------------------
--  DDL for Table CONTENT
--------------------------------------------------------

  CREATE TABLE "CONTENT" 
   (	"ID" VARCHAR2(36), 
	"NAME" NVARCHAR2(2000), 
	"LENGTH" NUMBER(19,0), 
	"DATA" VARCHAR2(40), 
	"ENCODING" NVARCHAR2(2000), 
	"DIGEST" NVARCHAR2(2000), 
	"MIME-TYPE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table CONTENT_VIEW_DISPLAY
--------------------------------------------------------

  CREATE TABLE "CONTENT_VIEW_DISPLAY" 
   (	"ID" VARCHAR2(36), 
	"PAGESIZE" NUMBER(19,0)
   ) ;


--------------------------------------------------------
--  DDL for Table CVD_SELECTEDLAYOUTCOLUMNS
--------------------------------------------------------

  CREATE TABLE "CVD_SELECTEDLAYOUTCOLUMNS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table DEFAULTSETTINGS
--------------------------------------------------------

  CREATE TABLE "DEFAULTSETTINGS" 
   (	"ID" VARCHAR2(36), 
	"LANGUAGE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table DELEGATION
--------------------------------------------------------

  CREATE TABLE "DELEGATION" 
   (	"ID" VARCHAR2(36), 
	"SOURCEID" NVARCHAR2(2000), 
	"DATEDEBUT" TIMESTAMP (6), 
	"DATEFIN" TIMESTAMP (6), 
	"DESTINATAIREID" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table DEL_PROFILLIST
--------------------------------------------------------

  CREATE TABLE "DEL_PROFILLIST" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table DOCRI_PARTICIPATINGDOCUMENTS
--------------------------------------------------------

  CREATE TABLE "DOCRI_PARTICIPATINGDOCUMENTS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(50)
   ) ;


--------------------------------------------------------
--  DDL for Table documentsLists
--------------------------------------------------------

  CREATE TABLE "documentsLists" 
   (	"id" NVARCHAR2(2000), 
	"ref" NVARCHAR2(2000), 
	"repo" NVARCHAR2(2000), 
	"userid" NVARCHAR2(2000), 
	"listid" NVARCHAR2(2000), 
	"reftype" NUMBER(19,0)
   ) ;


--------------------------------------------------------
--  DDL for Table DOSSIER_REPONSE
--------------------------------------------------------

  CREATE TABLE "DOSSIER_REPONSE" 
   (	"ID" VARCHAR2(36), 
	"NUMEROQUESTION" NUMBER(19,0), 
	"IDDOCUMENTQUESTION" VARCHAR2(50), 
	"LASTDOCUMENTROUTE" VARCHAR2(50), 
	"LISTEELIMINATION" VARCHAR2(50), 
	"IDDOCUMENTREPONSE" VARCHAR2(50), 
	"ETAPEREDACTIONATTEINTE" NUMBER(1,0), 
	"IDDOSSIERLOT" VARCHAR2(50), 
	"IDDOCUMENTFDD" VARCHAR2(50), 
	"MINISTEREATTRIBUTAIRECOURANT" NVARCHAR2(50), 
	"REAFFECTATIONCOUNT" NUMBER(19,0), 
	"ETAPESIGNATUREATTEINTE" NUMBER(1,0), 
	"MINISTEREATTRIBUTAIREPRECEDENT" NVARCHAR2(50), 
	"MINISTEREREATTRIBUTION" NVARCHAR2(50), 
	"LABELETAPESUIVANTE" NVARCHAR2(2000), 
	"HISTORIQUEDOSSIERTRAITE" NUMBER(1,0), 
	"ISARBITRATED" NUMBER(1,0) DEFAULT 0
   ) ;


--------------------------------------------------------
--  DDL for Table DOSSIER_REPONSES_LINK
--------------------------------------------------------

  CREATE TABLE "DOSSIER_REPONSES_LINK" 
   (	"ID" VARCHAR2(36), 
	"ETATQUESTION" NVARCHAR2(100), 
	"AUTOMATICVALIDATED" NUMBER(1,0), 
	"MOTSCLES" NVARCHAR2(2000), 
	"IDMINISTEREATTRIBUTAIRE" VARCHAR2(50), 
	"SOURCENUMEROQUESTION" NVARCHAR2(100), 
	"ROUTINGTASKLABEL" NVARCHAR2(2000), 
	"TYPEQUESTION" NVARCHAR2(100), 
	"ROUTINGTASKTYPE" NVARCHAR2(50), 
	"DATEPUBLICATIONJO" TIMESTAMP (6), 
	"ETATSQUESTION" NVARCHAR2(10), 
	"ROUTINGTASKID" NVARCHAR2(50), 
	"NOMCOMPLETAUTEUR" NVARCHAR2(201), 
	"ROUTINGTASKMAILBOXLABEL" NVARCHAR2(2000), 
	"INTITULEMINISTERE" NVARCHAR2(2000), 
	"SORTFIELD" NVARCHAR2(100), 
	"NUMEROQUESTION" NUMBER(19,0), 
	"ISMAILSEND" NUMBER(1,0), 
	"DATESIGNALEMENTQUESTION" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_E17194F7_IDX$I
--------------------------------------------------------

  CREATE TABLE "DR$FULL_E17194F7_IDX$I" 
   (	"TOKEN_TEXT" VARCHAR2(64), 
	"TOKEN_TYPE" NUMBER(10,0), 
	"TOKEN_FIRST" NUMBER(10,0), 
	"TOKEN_LAST" NUMBER(10,0), 
	"TOKEN_COUNT" NUMBER(10,0), 
	"TOKEN_INFO" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_E17194F7_IDX$K
--------------------------------------------------------

  CREATE TABLE "DR$FULL_E17194F7_IDX$K" 
   (	"DOCID" NUMBER(38,0), 
	"TEXTKEY" ROWID, 
	 PRIMARY KEY ("TEXTKEY") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_E17194F7_IDX$N
--------------------------------------------------------

  CREATE TABLE "DR$FULL_E17194F7_IDX$N" 
   (	"NLT_DOCID" NUMBER(38,0), 
	"NLT_MARK" CHAR(1), 
	 PRIMARY KEY ("NLT_DOCID") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_E17194F7_IDX$R
--------------------------------------------------------

  CREATE TABLE "DR$FULL_E17194F7_IDX$R" 
   (	"ROW_NO" NUMBER(3,0), 
	"DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULLTEXT_FULLTEXT_IDX$I
--------------------------------------------------------

  CREATE TABLE "DR$FULLTEXT_FULLTEXT_IDX$I" 
   (	"TOKEN_TEXT" VARCHAR2(64), 
	"TOKEN_TYPE" NUMBER(10,0), 
	"TOKEN_FIRST" NUMBER(10,0), 
	"TOKEN_LAST" NUMBER(10,0), 
	"TOKEN_COUNT" NUMBER(10,0), 
	"TOKEN_INFO" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULLTEXT_FULLTEXT_IDX$K
--------------------------------------------------------

  CREATE TABLE "DR$FULLTEXT_FULLTEXT_IDX$K" 
   (	"DOCID" NUMBER(38,0), 
	"TEXTKEY" ROWID, 
	 PRIMARY KEY ("TEXTKEY") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULLTEXT_FULLTEXT_IDX$N
--------------------------------------------------------

  CREATE TABLE "DR$FULLTEXT_FULLTEXT_IDX$N" 
   (	"NLT_DOCID" NUMBER(38,0), 
	"NLT_MARK" CHAR(1), 
	 PRIMARY KEY ("NLT_DOCID") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULLTEXT_FULLTEXT_IDX$R
--------------------------------------------------------

  CREATE TABLE "DR$FULLTEXT_FULLTEXT_IDX$R" 
   (	"ROW_NO" NUMBER(3,0), 
	"DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_406097D3_IDX$I
--------------------------------------------------------

  CREATE TABLE "DR$FULL_406097D3_IDX$I" 
   (	"TOKEN_TEXT" VARCHAR2(64), 
	"TOKEN_TYPE" NUMBER(10,0), 
	"TOKEN_FIRST" NUMBER(10,0), 
	"TOKEN_LAST" NUMBER(10,0), 
	"TOKEN_COUNT" NUMBER(10,0), 
	"TOKEN_INFO" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_406097D3_IDX$K
--------------------------------------------------------

  CREATE TABLE "DR$FULL_406097D3_IDX$K" 
   (	"DOCID" NUMBER(38,0), 
	"TEXTKEY" ROWID, 
	 PRIMARY KEY ("TEXTKEY") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_406097D3_IDX$N
--------------------------------------------------------

  CREATE TABLE "DR$FULL_406097D3_IDX$N" 
   (	"NLT_DOCID" NUMBER(38,0), 
	"NLT_MARK" CHAR(1), 
	 PRIMARY KEY ("NLT_DOCID") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_406097D3_IDX$R
--------------------------------------------------------

  CREATE TABLE "DR$FULL_406097D3_IDX$R" 
   (	"ROW_NO" NUMBER(3,0), 
	"DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_80158323_IDX$I
--------------------------------------------------------

  CREATE TABLE "DR$FULL_80158323_IDX$I" 
   (	"TOKEN_TEXT" VARCHAR2(64), 
	"TOKEN_TYPE" NUMBER(10,0), 
	"TOKEN_FIRST" NUMBER(10,0), 
	"TOKEN_LAST" NUMBER(10,0), 
	"TOKEN_COUNT" NUMBER(10,0), 
	"TOKEN_INFO" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_80158323_IDX$K
--------------------------------------------------------

  CREATE TABLE "DR$FULL_80158323_IDX$K" 
   (	"DOCID" NUMBER(38,0), 
	"TEXTKEY" ROWID, 
	 PRIMARY KEY ("TEXTKEY") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_80158323_IDX$N
--------------------------------------------------------

  CREATE TABLE "DR$FULL_80158323_IDX$N" 
   (	"NLT_DOCID" NUMBER(38,0), 
	"NLT_MARK" CHAR(1), 
	 PRIMARY KEY ("NLT_DOCID") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_80158323_IDX$R
--------------------------------------------------------

  CREATE TABLE "DR$FULL_80158323_IDX$R" 
   (	"ROW_NO" NUMBER(3,0), 
	"DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_989918B1_IDX$I
--------------------------------------------------------

  CREATE TABLE "DR$FULL_989918B1_IDX$I" 
   (	"TOKEN_TEXT" VARCHAR2(64), 
	"TOKEN_TYPE" NUMBER(10,0), 
	"TOKEN_FIRST" NUMBER(10,0), 
	"TOKEN_LAST" NUMBER(10,0), 
	"TOKEN_COUNT" NUMBER(10,0), 
	"TOKEN_INFO" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DR$FULL_989918B1_IDX$K
--------------------------------------------------------

  CREATE TABLE "DR$FULL_989918B1_IDX$K" 
   (	"DOCID" NUMBER(38,0), 
	"TEXTKEY" ROWID, 
	 PRIMARY KEY ("TEXTKEY") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_989918B1_IDX$N
--------------------------------------------------------

  CREATE TABLE "DR$FULL_989918B1_IDX$N" 
   (	"NLT_DOCID" NUMBER(38,0), 
	"NLT_MARK" CHAR(1), 
	 PRIMARY KEY ("NLT_DOCID") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;


--------------------------------------------------------
--  DDL for Table DR$FULL_989918B1_IDX$R
--------------------------------------------------------

  CREATE TABLE "DR$FULL_989918B1_IDX$R" 
   (	"ROW_NO" NUMBER(3,0), 
	"DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table DUBLINCORE
--------------------------------------------------------

  CREATE TABLE "DUBLINCORE" 
   (	"ID" VARCHAR2(36), 
	"CREATOR" NVARCHAR2(2000), 
	"NATURE" NVARCHAR2(2000), 
	"SOURCE" NVARCHAR2(2000), 
	"CREATED" TIMESTAMP (6), 
	"DESCRIPTION" NVARCHAR2(2000), 
	"RIGHTS" NVARCHAR2(2000), 
	"VALID" TIMESTAMP (6), 
	"FORMAT" NVARCHAR2(2000), 
	"ISSUED" TIMESTAMP (6), 
	"MODIFIED" TIMESTAMP (6), 
	"LANGUAGE" NVARCHAR2(2000), 
	"COVERAGE" NVARCHAR2(2000), 
	"EXPIRED" TIMESTAMP (6), 
	"LASTCONTRIBUTOR" NVARCHAR2(2000), 
	"TITLE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table ELEMENTFONDDOSSIER
--------------------------------------------------------

  CREATE TABLE "ELEMENTFONDDOSSIER" 
   (	"ID" VARCHAR2(36), 
	"MINISTEREAJOUTE" NVARCHAR2(2000), 
	"FILEPATH" NVARCHAR2(2000), 
	"TYPE" NVARCHAR2(2000), 
	"NUMEROVERSION" NVARCHAR2(2000), 
	"NIVEAUVISIBILITE" NVARCHAR2(2000), 
	"USERNAME" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table ERRATUM#ANONYMOUSTYPE
--------------------------------------------------------

  CREATE TABLE "ERRATUM#ANONYMOUSTYPE" 
   (	"ID" VARCHAR2(36), 
	"TEXTE_ERRATUM" NCLOB, 
	"DATEPUBLICATION" TIMESTAMP (6), 
	"PAGEJO" NUMBER(19,0), 
	"TEXTE_CONSOLIDE" NCLOB
   ) ;


--------------------------------------------------------
--  DDL for Table ETAT_APPLICATION
--------------------------------------------------------

  CREATE TABLE "ETAT_APPLICATION" 
   (	"ID" VARCHAR2(36), 
	"DESCRIPTION_RESTRICTION" NVARCHAR2(2000), 
	"RESTRICTION_ACCES" NUMBER(1,0),
        "MESSAGE" CLOB,
	"AFFICHAGE" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table ETATQUESTION
--------------------------------------------------------

  CREATE TABLE "ETATQUESTION" 
   (	"ID" VARCHAR2(36), 
	"DATE_CHANGEMENT_ETAT" TIMESTAMP (6), 
	"ETATQUESTION" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table EXPORT_DOCUMENT
--------------------------------------------------------

  CREATE TABLE "EXPORT_DOCUMENT" 
   (	"ID" VARCHAR2(36), 
	"DATEREQUEST" TIMESTAMP (6), 
	"OWNER" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FACETED_SEARCH
--------------------------------------------------------

  CREATE TABLE "FACETED_SEARCH" 
   (	"ID" VARCHAR2(36), 
	"CONTENT_VIEW_NAME" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FACETED_SEARCH_DEFAULT
--------------------------------------------------------

  CREATE TABLE "FACETED_SEARCH_DEFAULT" 
   (	"ID" VARCHAR2(36), 
	"ECM_FULLTEXT" NVARCHAR2(2000), 
	"DC_CREATED_MAX" TIMESTAMP (6), 
	"DC_CREATED_MIN" TIMESTAMP (6), 
	"DC_MODIFIED_MAX" TIMESTAMP (6), 
	"DC_MODIFIED_MIN" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table FAVORISDOSSIER
--------------------------------------------------------

  CREATE TABLE "FAVORISDOSSIER" 
   (	"ID" VARCHAR2(36), 
	"TARGETDOCUMENT" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FAVORISINDEXATION
--------------------------------------------------------

  CREATE TABLE "FAVORISINDEXATION" 
   (	"ID" VARCHAR2(36), 
	"TYPEINDEXATION" NVARCHAR2(2000), 
	"NIVEAU1" NVARCHAR2(2000), 
	"NIVEAU2" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FEUILLE_ROUTE
--------------------------------------------------------

  CREATE TABLE "FEUILLE_ROUTE" 
   (	"ID" VARCHAR2(36), 
	"TITREQUESTION" NVARCHAR2(2000), 
	"DEMANDEVALIDATION" NUMBER(1,0), 
	"MINISTERE" NVARCHAR2(50), 
	"FEUILLEROUTEDEFAUT" NUMBER(1,0), 
	"TYPECREATION" NVARCHAR2(50), 
	"IDDIRECTIONPILOTE" NVARCHAR2(50), 
	"INTITULEDIRECTIONPILOTE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FILE
--------------------------------------------------------

  CREATE TABLE "FILE" 
   (	"ID" VARCHAR2(36), 
	"FILENAME" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FONDDOSSIER
--------------------------------------------------------

  CREATE TABLE "FONDDOSSIER" 
   (	"ID" VARCHAR2(36), 
	"REPERTOIRE_PARLEMENT" NVARCHAR2(2000), 
	"REPERTOIRE_MINISTERE" NVARCHAR2(2000), 
	"REPERTOIRE_SGG" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FSD_DC_COVERAGE
--------------------------------------------------------

  CREATE TABLE "FSD_DC_COVERAGE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FSD_DC_CREATOR
--------------------------------------------------------

  CREATE TABLE "FSD_DC_CREATOR" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FSD_DC_NATURE
--------------------------------------------------------

  CREATE TABLE "FSD_DC_NATURE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FSD_DC_SUBJECTS
--------------------------------------------------------

  CREATE TABLE "FSD_DC_SUBJECTS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FSD_ECM_PATH
--------------------------------------------------------

  CREATE TABLE "FSD_ECM_PATH" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table FULLTEXT
--------------------------------------------------------

  CREATE TABLE "FULLTEXT" 
   (	"ID" VARCHAR2(36), 
	"JOBID" VARCHAR2(250), 
	"FULLTEXT" CLOB, 
	"SIMPLETEXT" NCLOB, 
	"BINARYTEXT" NCLOB, 
	"FULLTEXT_IDQUESTION" CLOB, 
	"SIMPLETEXT_IDQUESTION" NCLOB, 
	"BINARYTEXT_IDQUESTION" NCLOB, 
	"FULLTEXT_TXTREPONSE" CLOB, 
	"SIMPLETEXT_TXTREPONSE" NCLOB, 
	"BINARYTEXT_TXTREPONSE" NCLOB, 
	"FULLTEXT_TXTQUESTION" CLOB, 
	"SIMPLETEXT_TXTQUESTION" NCLOB, 
	"BINARYTEXT_TXTQUESTION" NCLOB, 
	"FULLTEXT_SENATTITRE" CLOB, 
	"SIMPLETEXT_SENATTITRE" NCLOB, 
	"BINARYTEXT_SENATTITRE" NCLOB
   ) ;


--------------------------------------------------------
--  DDL for Table HIERARCHY
--------------------------------------------------------

  CREATE TABLE "HIERARCHY" 
   (	"ID" VARCHAR2(36), 
	"PARENTID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"NAME" NVARCHAR2(2000), 
	"ISPROPERTY" NUMBER(1,0), 
	"PRIMARYTYPE" VARCHAR2(250), 
	"MIXINTYPES" VARCHAR2(250), 
	"ISCHECKEDIN" NUMBER(1,0), 
	"BASEVERSIONID" VARCHAR2(36), 
	"MAJORVERSION" NUMBER(19,0), 
	"MINORVERSION" NUMBER(19,0), 
	"ISVERSION" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table HIERARCHY_READ_ACL
--------------------------------------------------------

  CREATE TABLE "HIERARCHY_READ_ACL" 
   (	"ID" VARCHAR2(36), 
	"ACL_ID" VARCHAR2(34)
   ) ;


--------------------------------------------------------
--  DDL for Table HISTORIQUEATTRIBUTION
--------------------------------------------------------

  CREATE TABLE "HISTORIQUEATTRIBUTION" 
   (	"ID" VARCHAR2(36), 
	"DATEATTRIBUTION" TIMESTAMP (6), 
	"MINATTRIBUTION" NVARCHAR2(2000), 
	"TYPEATTRIBUTION" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table HISTORIQUE_MDP
--------------------------------------------------------

  CREATE TABLE "HISTORIQUE_MDP" 
   (	"ID" NUMBER(19,0), 
	"LOGIN" VARCHAR2(255 CHAR), 
	"DERNIER_MDP" VARCHAR2(255 CHAR), 
	"DATE_ENREGISTREMENT" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table INDEXATION
--------------------------------------------------------

  CREATE TABLE "INDEXATION" 
   (	"ID" VARCHAR2(36), 
	"INDEXATIONREFERENCE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table INDEXATION_COMP
--------------------------------------------------------

  CREATE TABLE "INDEXATION_COMP" 
   (	"ID" VARCHAR2(36), 
	"INDEXATIONREFERENCE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table INFO_COMMENTS
--------------------------------------------------------

  CREATE TABLE "INFO_COMMENTS" 
   (	"ID" VARCHAR2(36), 
	"NUMBEROFCOMMENTS" NUMBER(19,0)
   ) ;


--------------------------------------------------------
--  DDL for Table INFO_UTILISATEUR_CONNECTION
--------------------------------------------------------

  CREATE TABLE "INFO_UTILISATEUR_CONNECTION" 
   (	"ID" VARCHAR2(36), 
	"USERNAME" NVARCHAR2(2000), 
	"ISLOGOUT" NUMBER(1,0), 
	"FIRSTNAME" NVARCHAR2(2000), 
	"DATECONNECTION" TIMESTAMP (6), 
	"LASTNAME" NVARCHAR2(2000),
	"COURRIEL" NVARCHAR2(2000),
	"TELEPHONE" NVARCHAR2(2000),
	"MINISTERERATTACHEMENT" NVARCHAR2(2000),
	"DIRECTION" NVARCHAR2(2000),
	"POSTE" NVARCHAR2(2000),
	"DATECREATION" TIMESTAMP (6),
	"DATEDERNIERECONNEXION" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table IXA_AN_ANALYSE
--------------------------------------------------------

  CREATE TABLE "IXA_AN_ANALYSE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXA_AN_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "IXA_AN_RUBRIQUE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXACOMP_AN_ANALYSE
--------------------------------------------------------

  CREATE TABLE "IXACOMP_AN_ANALYSE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXACOMP_AN_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "IXACOMP_AN_RUBRIQUE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXACOMP_MOTCLEF_MINISTERE
--------------------------------------------------------

  CREATE TABLE "IXACOMP_MOTCLEF_MINISTERE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXACOMP_SE_RENVOI
--------------------------------------------------------

  CREATE TABLE "IXACOMP_SE_RENVOI" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXACOMP_SE_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "IXACOMP_SE_RUBRIQUE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXACOMP_SE_THEME
--------------------------------------------------------

  CREATE TABLE "IXACOMP_SE_THEME" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXACOMP_TA_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "IXACOMP_TA_RUBRIQUE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXA_MOTCLEF_MINISTERE
--------------------------------------------------------

  CREATE TABLE "IXA_MOTCLEF_MINISTERE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXA_SE_RENVOI
--------------------------------------------------------

  CREATE TABLE "IXA_SE_RENVOI" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXA_SE_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "IXA_SE_RUBRIQUE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXA_SE_THEME
--------------------------------------------------------

  CREATE TABLE "IXA_SE_THEME" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table IXA_TA_RUBRIQUE
--------------------------------------------------------

  CREATE TABLE "IXA_TA_RUBRIQUE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_GRAPH
--------------------------------------------------------

  CREATE TABLE "JENA_GRAPH" 
   (	"ID" NUMBER(*,0), 
	"NAME" VARCHAR2(4000)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_G1T0_REIF
--------------------------------------------------------

  CREATE TABLE "JENA_G1T0_REIF" 
   (	"SUBJ" NVARCHAR2(250), 
	"PROP" NVARCHAR2(250), 
	"OBJ" NVARCHAR2(250), 
	"GRAPHID" NUMBER(*,0), 
	"STMT" NVARCHAR2(250), 
	"HASTYPE" CHAR(1)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_G1T1_STMT
--------------------------------------------------------

  CREATE TABLE "JENA_G1T1_STMT" 
   (	"SUBJ" NVARCHAR2(250), 
	"PROP" NVARCHAR2(250), 
	"OBJ" NVARCHAR2(250), 
	"GRAPHID" NUMBER(*,0)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_G2T0_REIF
--------------------------------------------------------

  CREATE TABLE "JENA_G2T0_REIF" 
   (	"SUBJ" NVARCHAR2(250), 
	"PROP" NVARCHAR2(250), 
	"OBJ" NVARCHAR2(250), 
	"GRAPHID" NUMBER(*,0), 
	"STMT" NVARCHAR2(250), 
	"HASTYPE" CHAR(1)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_G2T1_STMT
--------------------------------------------------------

  CREATE TABLE "JENA_G2T1_STMT" 
   (	"SUBJ" NVARCHAR2(250), 
	"PROP" NVARCHAR2(250), 
	"OBJ" NVARCHAR2(250), 
	"GRAPHID" NUMBER(*,0)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_G3T0_REIF
--------------------------------------------------------

  CREATE TABLE "JENA_G3T0_REIF" 
   (	"SUBJ" NVARCHAR2(250), 
	"PROP" NVARCHAR2(250), 
	"OBJ" NVARCHAR2(250), 
	"GRAPHID" NUMBER(*,0), 
	"STMT" NVARCHAR2(250), 
	"HASTYPE" CHAR(1)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_G3T1_STMT
--------------------------------------------------------

  CREATE TABLE "JENA_G3T1_STMT" 
   (	"SUBJ" NVARCHAR2(250), 
	"PROP" NVARCHAR2(250), 
	"OBJ" NVARCHAR2(250), 
	"GRAPHID" NUMBER(*,0)
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_LONG_LIT
--------------------------------------------------------

  CREATE TABLE "JENA_LONG_LIT" 
   (	"ID" NUMBER(*,0), 
	"HEAD" NVARCHAR2(250), 
	"CHKSUM" NUMBER(*,0), 
	"TAIL" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_LONG_URI
--------------------------------------------------------

  CREATE TABLE "JENA_LONG_URI" 
   (	"ID" NUMBER(*,0), 
	"HEAD" NVARCHAR2(250), 
	"CHKSUM" NUMBER(*,0), 
	"TAIL" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_PREFIX
--------------------------------------------------------

  CREATE TABLE "JENA_PREFIX" 
   (	"ID" NUMBER(*,0), 
	"HEAD" NVARCHAR2(250), 
	"CHKSUM" NUMBER(*,0), 
	"TAIL" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table JENA_SYS_STMT
--------------------------------------------------------

  CREATE TABLE "JENA_SYS_STMT" 
   (	"SUBJ" NVARCHAR2(250), 
	"PROP" NVARCHAR2(250), 
	"OBJ" NVARCHAR2(250), 
	"GRAPHID" NUMBER(*,0)
   ) ;


--------------------------------------------------------
--  DDL for Table JETON_DOC
--------------------------------------------------------

  CREATE TABLE "JETON_DOC" 
   (	"ID" VARCHAR2(36), 
	"TYPE_WEBSERVICE" NVARCHAR2(50), 
	"ID_JETON" NUMBER(19,0), 
	"ID_OWNER" VARCHAR2(50), 
	"ID_DOC" VARCHAR2(50), 
	"CREATED" TIMESTAMP (6), 
	"DATE_ATTRIBUTION" TIMESTAMP (6), 
	"MIN_ATTRIBUTION" NVARCHAR2(2000), 
	"TYPE_ATTRIBUTION" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table JETON_MAITRE
--------------------------------------------------------

  CREATE TABLE "JETON_MAITRE" 
   (	"ID" VARCHAR2(36), 
	"ID_PROPRIETAIRE" VARCHAR2(50), 
	"TYPE_WEBSERVICE" NVARCHAR2(50), 
	"NUMERO_JETON" NUMBER(19,0)
   ) ;


--------------------------------------------------------
--  DDL for Table LISTEELIMINATION
--------------------------------------------------------

  CREATE TABLE "LISTEELIMINATION" 
   (	"ID" VARCHAR2(36), 
	"EN_COURS" NUMBER(1,0), 
	"SUPPRESSION_EN_COURS" NUMBER(1,0), 
	"ABANDON_EN_COURS" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table LOCALTHEMECONFIG
--------------------------------------------------------

  CREATE TABLE "LOCALTHEMECONFIG" 
   (	"ID" NUMBER(10,0), 
	"DOCID" VARCHAR2(255 CHAR), 
	"ENGINE" VARCHAR2(255 CHAR), 
	"THEMODE" VARCHAR2(255 CHAR), 
	"PAGE" VARCHAR2(255 CHAR), 
	"PERSPECTIVE" VARCHAR2(255 CHAR), 
	"THEME" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table LOCK_JETON_MAITRE
--------------------------------------------------------

  CREATE TABLE "LOCK_JETON_MAITRE" 
   (	"ID" VARCHAR2(36), 
	"ID_JETON_MAITRE" VARCHAR2(36), 
	"ID_PROPRIETAIRE" NVARCHAR2(2000), 
	"TYPE_WEBSERVICE" NVARCHAR2(2000), 
	"NUMERO_JETON" NUMBER(19,0)
   ) ;


--------------------------------------------------------
--  DDL for Table LOCKS
--------------------------------------------------------

  CREATE TABLE "LOCKS" 
   (	"ID" VARCHAR2(36), 
	"OWNER" VARCHAR2(250), 
	"CREATED" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table MAIL
--------------------------------------------------------

  CREATE TABLE "MAIL" 
   (	"ID" VARCHAR2(36), 
	"HTMLTEXT" NVARCHAR2(2000), 
	"MESSAGEID" NVARCHAR2(2000), 
	"TEXT" NVARCHAR2(2000), 
	"SENDER" NVARCHAR2(2000), 
	"SENDING_DATE" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table MAILBOX
--------------------------------------------------------

  CREATE TABLE "MAILBOX" 
   (	"ID" VARCHAR2(36), 
	"ORIGIN" NVARCHAR2(2000), 
	"LASTSYNCUPDATE" TIMESTAMP (6), 
	"DEFAULTCONFIDENTIALITY" NUMBER(19,0), 
	"MAILBOX_ID" NVARCHAR2(2000), 
	"TYPE" NVARCHAR2(2000), 
	"SYNCHRONIZERID" NVARCHAR2(2000), 
	"AFFILIATED_MAILBOX_ID" NVARCHAR2(2000), 
	"OWNER" NVARCHAR2(2000), 
	"SYNCHRONIZEDSTATE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MAILBOX_ID
--------------------------------------------------------

  CREATE TABLE "MAILBOX_ID" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MAIL_CC_RECIPIENTS
--------------------------------------------------------

  CREATE TABLE "MAIL_CC_RECIPIENTS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MAIL_RECIPIENTS
--------------------------------------------------------

  CREATE TABLE "MAIL_RECIPIENTS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MISC
--------------------------------------------------------

  CREATE TABLE "MISC" 
   (	"ID" VARCHAR2(36), 
	"LIFECYCLEPOLICY" VARCHAR2(250), 
	"LIFECYCLESTATE" VARCHAR2(250)
   ) ;


--------------------------------------------------------
--  DDL for Table MLBX_FAVORITES
--------------------------------------------------------

  CREATE TABLE "MLBX_FAVORITES" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MLBX_GROUPS
--------------------------------------------------------

  CREATE TABLE "MLBX_GROUPS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MLBX_NOTIFIED_USERS
--------------------------------------------------------

  CREATE TABLE "MLBX_NOTIFIED_USERS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MLBX_PROFILES
--------------------------------------------------------

  CREATE TABLE "MLBX_PROFILES" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table MLBX_USERS
--------------------------------------------------------

  CREATE TABLE "MLBX_USERS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table NAV
--------------------------------------------------------

  CREATE TABLE "NAV" 
   (	"ID" VARCHAR2(36), 
	"SE_THEME" NVARCHAR2(2000), 
	"AN_RUBRIQUE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table NAVIGATIONSETTINGS
--------------------------------------------------------

  CREATE TABLE "NAVIGATIONSETTINGS" 
   (	"ID" VARCHAR2(36), 
	"HOMEPAGE" NVARCHAR2(2000), 
	"PAGINATION" NVARCHAR2(2000), 
	"FULLNAVIGATION" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table NOTE
--------------------------------------------------------

  CREATE TABLE "NOTE" 
   (	"ID" VARCHAR2(36), 
	"MIME_TYPE" NVARCHAR2(2000), 
	"NOTE" NCLOB
   ) ;


--------------------------------------------------------
--  DDL for Table NOTIFICATIONS_SUIVI_BATCHS
--------------------------------------------------------

  CREATE TABLE "NOTIFICATIONS_SUIVI_BATCHS" 
   (	"ID" VARCHAR2(36), 
	"ETAT_NOTIFICATION" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table NSB_RECEIVERMAILLIST
--------------------------------------------------------

  CREATE TABLE "NSB_RECEIVERMAILLIST" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table NXP_LOGS
--------------------------------------------------------

  CREATE TABLE "NXP_LOGS" 
   (	"LOG_ID" NUMBER(*,0), 
	"LOG_EVENT_CATEGORY" VARCHAR2(255 CHAR), 
	"LOG_EVENT_COMMENT" VARCHAR2(1024 CHAR), 
	"LOG_DOC_LIFE_CYCLE" VARCHAR2(255 CHAR), 
	"LOG_DOC_PATH" VARCHAR2(1024 CHAR), 
	"LOG_DOC_TYPE" VARCHAR2(255 CHAR), 
	"LOG_DOC_UUID" VARCHAR2(255 CHAR), 
	"LOG_EVENT_DATE" TIMESTAMP (6), 
	"LOG_EVENT_ID" VARCHAR2(255 CHAR), 
	"LOG_PRINCIPAL_NAME" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table NXP_LOGS_EXTINFO
--------------------------------------------------------

  CREATE TABLE "NXP_LOGS_EXTINFO" 
   (	"DISCRIMINATOR" VARCHAR2(31 CHAR), 
	"LOG_EXTINFO_ID" NUMBER(19,0), 
	"LOG_EXTINFO_STRING" VARCHAR2(255 CHAR), 
	"LOG_EXTINFO_BOOLEAN" NUMBER(1,0), 
	"LOG_EXTINFO_DOUBLE" FLOAT(126), 
	"LOG_EXTINFO_LONG" NUMBER(19,0), 
	"LOG_EXTINFO_DATE" TIMESTAMP (6), 
	"LOG_EXTINFO_BLOB" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table NXP_LOGS_MAPEXTINFOS
--------------------------------------------------------

  CREATE TABLE "NXP_LOGS_MAPEXTINFOS" 
   (	"LOG_FK" NUMBER(*,0), 
	"INFO_FK" NUMBER(19,0), 
	"MAPKEY" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table NXP_UIDSEQ
--------------------------------------------------------

  CREATE TABLE "NXP_UIDSEQ" 
   (	"SEQ_ID" NUMBER(10,0), 
	"SEQ_INDEX" NUMBER(10,0), 
	"SEQ_KEY" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table PARAMETER
--------------------------------------------------------

  CREATE TABLE "PARAMETER" 
   (	"ID" VARCHAR2(36), 
	"PVALUE" NVARCHAR2(2000), 
	"PNAME" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table PARAMETRE
--------------------------------------------------------

  CREATE TABLE "PARAMETRE" 
   (	"ID" VARCHAR2(36), 
	"UNIT" NVARCHAR2(2000), 
	"VALUE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table PARTICIPANTLIST
--------------------------------------------------------

  CREATE TABLE "PARTICIPANTLIST" 
   (	"ID" VARCHAR2(36), 
	"TITLE" NVARCHAR2(2000), 
	"DESCRIPTION" NVARCHAR2(2000), 
	"PLID" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table PROFIL_UTILISATEUR
--------------------------------------------------------

  CREATE TABLE "PROFIL_UTILISATEUR" 
   (	"ID" VARCHAR2(36), 
	"PARAMETREMAIL" NVARCHAR2(2000), 
	"DERNIERCHANGEMENTMOTDEPASSE" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table PROTOCOL
--------------------------------------------------------

  CREATE TABLE "PROTOCOL" 
   (	"ID" VARCHAR2(36), 
	"EMAIL" NVARCHAR2(2000), 
	"PORT" NVARCHAR2(2000), 
	"HOST" NVARCHAR2(2000), 
	"EMAILS_LIMIT" NUMBER(19,0), 
	"PASSWORD" NVARCHAR2(2000), 
	"SOCKET_FACTORY_FALLBACK" NUMBER(1,0), 
	"SOCKET_FACTORY_PORT" NVARCHAR2(2000), 
	"SSL_PROTOCOLS" NVARCHAR2(2000), 
	"STARTTLS_ENABLE" NUMBER(1,0), 
	"PROTOCOL_TYPE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table PROXIES
--------------------------------------------------------

  CREATE TABLE "PROXIES" 
   (	"ID" VARCHAR2(36), 
	"TARGETID" VARCHAR2(36), 
	"VERSIONABLEID" VARCHAR2(36)
   ) ;


--------------------------------------------------------
--  DDL for Table PRU_COLUMNS
--------------------------------------------------------

  CREATE TABLE "PRU_COLUMNS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table PUBLISH_SECTIONS
--------------------------------------------------------

  CREATE TABLE "PUBLISH_SECTIONS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_BLOB_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "QRTZ_BLOB_TRIGGERS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"TRIGGER_NAME" VARCHAR2(200), 
	"TRIGGER_GROUP" VARCHAR2(200), 
	"BLOB_DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_CALENDARS
--------------------------------------------------------

  CREATE TABLE "QRTZ_CALENDARS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"CALENDAR_NAME" VARCHAR2(200), 
	"CALENDAR" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_CRON_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "QRTZ_CRON_TRIGGERS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"TRIGGER_NAME" VARCHAR2(200), 
	"TRIGGER_GROUP" VARCHAR2(200), 
	"CRON_EXPRESSION" VARCHAR2(120), 
	"TIME_ZONE_ID" VARCHAR2(80)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_FIRED_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "QRTZ_FIRED_TRIGGERS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"ENTRY_ID" VARCHAR2(95), 
	"TRIGGER_NAME" VARCHAR2(200), 
	"TRIGGER_GROUP" VARCHAR2(200), 
	"INSTANCE_NAME" VARCHAR2(200), 
	"FIRED_TIME" NUMBER(13,0), 
	"PRIORITY" NUMBER(13,0), 
	"STATE" VARCHAR2(16), 
	"JOB_NAME" VARCHAR2(200), 
	"JOB_GROUP" VARCHAR2(200), 
	"IS_NONCONCURRENT" VARCHAR2(1), 
	"REQUESTS_RECOVERY" VARCHAR2(1)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_JOB_DETAILS
--------------------------------------------------------

  CREATE TABLE "QRTZ_JOB_DETAILS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"JOB_NAME" VARCHAR2(200), 
	"JOB_GROUP" VARCHAR2(200), 
	"DESCRIPTION" VARCHAR2(250), 
	"JOB_CLASS_NAME" VARCHAR2(250), 
	"IS_DURABLE" VARCHAR2(1), 
	"IS_NONCONCURRENT" VARCHAR2(1), 
	"IS_UPDATE_DATA" VARCHAR2(1), 
	"REQUESTS_RECOVERY" VARCHAR2(1), 
	"JOB_DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_LOCKS
--------------------------------------------------------

  CREATE TABLE "QRTZ_LOCKS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"LOCK_NAME" VARCHAR2(40)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_PAUSED_TRIGGER_GRPS
--------------------------------------------------------

  CREATE TABLE "QRTZ_PAUSED_TRIGGER_GRPS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"TRIGGER_GROUP" VARCHAR2(200)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_SCHEDULER_STATE
--------------------------------------------------------

  CREATE TABLE "QRTZ_SCHEDULER_STATE" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"INSTANCE_NAME" VARCHAR2(200), 
	"LAST_CHECKIN_TIME" NUMBER(13,0), 
	"CHECKIN_INTERVAL" NUMBER(13,0)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_SIMPLE_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "QRTZ_SIMPLE_TRIGGERS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"TRIGGER_NAME" VARCHAR2(200), 
	"TRIGGER_GROUP" VARCHAR2(200), 
	"REPEAT_COUNT" NUMBER(7,0), 
	"REPEAT_INTERVAL" NUMBER(12,0), 
	"TIMES_TRIGGERED" NUMBER(10,0)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_SIMPROP_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "QRTZ_SIMPROP_TRIGGERS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"TRIGGER_NAME" VARCHAR2(200), 
	"TRIGGER_GROUP" VARCHAR2(200), 
	"STR_PROP_1" VARCHAR2(512), 
	"STR_PROP_2" VARCHAR2(512), 
	"STR_PROP_3" VARCHAR2(512), 
	"INT_PROP_1" NUMBER(10,0), 
	"INT_PROP_2" NUMBER(10,0), 
	"LONG_PROP_1" NUMBER(13,0), 
	"LONG_PROP_2" NUMBER(13,0), 
	"DEC_PROP_1" NUMBER(13,4), 
	"DEC_PROP_2" NUMBER(13,4), 
	"BOOL_PROP_1" VARCHAR2(1), 
	"BOOL_PROP_2" VARCHAR2(1)
   ) ;


--------------------------------------------------------
--  DDL for Table QRTZ_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "QRTZ_TRIGGERS" 
   (	"SCHED_NAME" VARCHAR2(120), 
	"TRIGGER_NAME" VARCHAR2(200), 
	"TRIGGER_GROUP" VARCHAR2(200), 
	"JOB_NAME" VARCHAR2(200), 
	"JOB_GROUP" VARCHAR2(200), 
	"DESCRIPTION" VARCHAR2(250), 
	"NEXT_FIRE_TIME" NUMBER(13,0), 
	"PREV_FIRE_TIME" NUMBER(13,0), 
	"PRIORITY" NUMBER(13,0), 
	"TRIGGER_STATE" VARCHAR2(16), 
	"TRIGGER_TYPE" VARCHAR2(8), 
	"START_TIME" NUMBER(13,0), 
	"END_TIME" NUMBER(13,0), 
	"CALENDAR_NAME" VARCHAR2(200), 
	"MISFIRE_INSTR" NUMBER(2,0), 
	"JOB_DATA" BLOB
   ) ;


--------------------------------------------------------
--  DDL for Table QUERYNAV
--------------------------------------------------------

  CREATE TABLE "QUERYNAV" 
   (	"ID" VARCHAR2(36), 
	"SUBJECTS" NVARCHAR2(2000), 
	"COVERAGE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table QUESTION
--------------------------------------------------------

  CREATE TABLE "QUESTION" 
   (	"ID" VARCHAR2(36), 
	"ETATQUESTION" NVARCHAR2(100), 
	"INTITULEMINISTERE" NVARCHAR2(2000), 
	"ETATRENOUVELE" NUMBER(1,0), 
	"LEGISLATUREQUESTION" NUMBER(19,0), 
	"IDMANDAT" NVARCHAR2(10), 
	"HASHCONNEXITETEXTE" NVARCHAR2(50), 
	"DATECADUCITEQUESTION" TIMESTAMP (6), 
	"ETATSIGNALE" NUMBER(1,0), 
	"NOMCOMPLETAUTEUR" NVARCHAR2(201), 
	"ORIGINEQUESTION" NVARCHAR2(50), 
	"ETATRAPPELE" NUMBER(1,0), 
	"SOURCENUMEROQUESTION" NVARCHAR2(100), 
	"DATEPUBLICATIONJO" TIMESTAMP (6), 
	"ETATNONRETIRE" NUMBER(1,0), 
	"ETATSQUESTION" NVARCHAR2(10), 
	"PAGEJO" NVARCHAR2(19), 
	"ETATRETIRE" NUMBER(1,0), 
	"DATETRANSMISSIONASSEMBLEES" TIMESTAMP (6), 
	"DATERETRAITQUESTION" TIMESTAMP (6), 
	"TITREJOMINISTERE" NVARCHAR2(2000), 
	"TEXTE_JOINT" NVARCHAR2(2000), 
	"DATERENOUVELLEMENTQUESTION" TIMESTAMP (6), 
	"SENATQUESTIONTITRE" NVARCHAR2(2000), 
	"IDMINISTEREINTERROGE" NVARCHAR2(50), 
	"HASHCONNEXITEAN" NVARCHAR2(50), 
	"CIRCONSCRIPTIONAUTEUR" NVARCHAR2(100), 
	"GROUPEPOLITIQUE" NVARCHAR2(200), 
	"HASREPONSEINITIEE" NUMBER(1,0), 
	"DATERECEPTIONQUESTION" TIMESTAMP (6), 
	"HASHCONNEXITESE" NVARCHAR2(50), 
	"TEXTEQUESTION" NCLOB, 
	"MOTSCLES" NVARCHAR2(2000), 
	"CIVILITEAUTEUR" NVARCHAR2(10), 
	"NOMAUTEUR" NVARCHAR2(100), 
	"HASHCONNEXITETITRE" NVARCHAR2(50), 
	"ETATREATTRIBUE" NUMBER(1,0), 
	"PRENOMAUTEUR" NVARCHAR2(100), 
	"TYPEQUESTION" NVARCHAR2(100), 
	"CARACTERISTIQUEQUESTION" NVARCHAR2(50), 
	"NUMEROQUESTION" NUMBER(19,0), 
	"IDMINISTEREATTRIBUTAIRE" NVARCHAR2(50), 
	"INTITULEMINISTEREATTRIBUTAIRE" NVARCHAR2(2000), 
	"DATESIGNALEMENTQUESTION" TIMESTAMP (6), 
	"DATECLOTUREQUESTION" TIMESTAMP (6), 
	"DATERAPPELQUESTION" TIMESTAMP (6), 
	"IDMINISTERERATTACHEMENT" NVARCHAR2(50), 
	"INTITULEMINISTERERATTACHEMENT" NVARCHAR2(2000), 
	"IDDIRECTIONPILOTE" NVARCHAR2(50), 
	"INTITULEDIRECTIONPILOTE" NVARCHAR2(2000),
	 "CONNEXITE" NUMBER(19,0) DEFAULT 0
   ) ;

   COMMENT ON TABLE "QUESTION"  IS 'tst_perf';


--------------------------------------------------------
--  DDL for Table RELATEDTEXTRESOURCE
--------------------------------------------------------

  CREATE TABLE "RELATEDTEXTRESOURCE" 
   (	"ID" VARCHAR2(36), 
	"RELATEDTEXTID" NVARCHAR2(2000), 
	"RELATEDTEXT" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table RELATION
--------------------------------------------------------

  CREATE TABLE "RELATION" 
   (	"ID" VARCHAR2(36), 
	"SOURCE" NVARCHAR2(2000), 
	"TARGET" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table RELATION_SEARCH
--------------------------------------------------------

  CREATE TABLE "RELATION_SEARCH" 
   (	"ID" VARCHAR2(36), 
	"DC_TITLE" NVARCHAR2(2000), 
	"ECM_FULLTEXT" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REL_SRCH_ECM_PATH
--------------------------------------------------------

  CREATE TABLE "REL_SRCH_ECM_PATH" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table RENOUVELLEMENTQUESTION
--------------------------------------------------------

  CREATE TABLE "RENOUVELLEMENTQUESTION" 
   (	"ID" VARCHAR2(36), 
	"DATEDEFFET" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table REPLOGD_FULLLOG
--------------------------------------------------------

  CREATE TABLE "REPLOGD_FULLLOG" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REPLOGL_DETAILS
--------------------------------------------------------

  CREATE TABLE "REPLOGL_DETAILS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REPLOGL_FULLLOG
--------------------------------------------------------

  CREATE TABLE "REPLOGL_FULLLOG" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REPLOG_LINES
--------------------------------------------------------

  CREATE TABLE "REPLOG_LINES" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REPONSE
--------------------------------------------------------

  CREATE TABLE "REPONSE" 
   (	"ID" VARCHAR2(36), 
	"PAGEJOREPONSE" NUMBER(19,0), 
	"IDENTIFIANT" NUMBER(19,0), 
	"NUMEROJOREPONSE" NUMBER(19,0), 
	"AN_RUBRIQUE" NVARCHAR2(2000), 
	"ISSIGNATUREVALIDE" NUMBER(1,0), 
	"SIGNATURE" NCLOB, 
	"IDAUTEURREPONSE" NVARCHAR2(2000), 
	"IDAUTEURREMOVESIGNATURE" NVARCHAR2(2000), 
	"VERROU" NVARCHAR2(2000), 
	"DATEPUBLICATIONJOREPONSE" TIMESTAMP (6), 
	"DATEVALIDATION" TIMESTAMP (6), 
	"ERRATUM" NCLOB
   ) ;


--------------------------------------------------------
--  DDL for Table REPONSES_LOGGING
--------------------------------------------------------

  CREATE TABLE "REPONSES_LOGGING" 
   (	"ID" VARCHAR2(36), 
	"STATUS" NVARCHAR2(2000), 
	"STARTDATE" TIMESTAMP (6), 
	"ENDDATE" TIMESTAMP (6), 
	"ENDCOUNT" NUMBER(19,0), 
	"PREVISIONALCOUNT" NUMBER(19,0), 
	"MESSAGE" NVARCHAR2(2000), 
	"CLOSEENDCOUNT" NUMBER(19,0), 
	"CLOSEPREVISIONALCOUNT" NUMBER(19,0), 
	"CURRENTGOUVERNEMENT" VARCHAR2(255), 
	"NEXTGOUVERNEMENT" VARCHAR2(255)
   ) ;


--------------------------------------------------------
--  DDL for Table REPONSES_LOGGING_DETAIL
--------------------------------------------------------

  CREATE TABLE "REPONSES_LOGGING_DETAIL" 
   (	"ID" VARCHAR2(36), 
	"MESSAGE" NVARCHAR2(2000), 
	"STATUS" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REPONSES_LOGGING_LINE
--------------------------------------------------------

  CREATE TABLE "REPONSES_LOGGING_LINE" 
   (	"ID" VARCHAR2(36), 
	"MESSAGE" NVARCHAR2(2000), 
	"STATUS" NVARCHAR2(2000), 
	"PREVISIONALCOUNT" NUMBER(19,0), 
	"ENDCOUNT" NUMBER(19,0), 
	"ENDDATE" TIMESTAMP (6), 
	"STARTDATE" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table REPOSITORIES
--------------------------------------------------------

  CREATE TABLE "REPOSITORIES" 
   (	"ID" VARCHAR2(36), 
	"NAME" VARCHAR2(250)
   ) ;


--------------------------------------------------------
--  DDL for Table REQUETECOMPLEXE
--------------------------------------------------------

  CREATE TABLE "REQUETECOMPLEXE" 
   (	"ID" VARCHAR2(36), 
	"MINISTEREINTERROGE" NVARCHAR2(2000), 
	"POSTE" NVARCHAR2(2000), 
	"NUMEROQUESTIONFIN" NUMBER(19,0), 
	"DATEJOQUESTIONDEBUT" TIMESTAMP (6), 
	"CLAUSECARACTERISTIQUES" NVARCHAR2(2000), 
	"INDEXASSEMBLEE" NVARCHAR2(2000), 
	"LEGISLATURE" NVARCHAR2(2000), 
	"NOMAUTEUR" NVARCHAR2(2000), 
	"DATEJOREPONSEFIN" TIMESTAMP (6), 
	"INDEXMINISTERE" NVARCHAR2(2000), 
	"GROUPEPOLITIQUE" NVARCHAR2(2000), 
	"DATEJOREPONSEDEBUT" TIMESTAMP (6), 
	"MINISTEREATTRIBUTE" NVARCHAR2(2000), 
	"DATEJOQUESTIONFIN" TIMESTAMP (6), 
	"NUMEROQUESTIONDEBUT" NUMBER(19,0), 
	"DATESIGNALEMENTDEBUT" TIMESTAMP (6), 
	"DATESIGNALEMENTFIN" TIMESTAMP (6), 
	"DIRECTIONPILOTE" NVARCHAR2(2000), 
	"MINISTERERATTACHEMENT" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REQUETEFEUILLEROUTE
--------------------------------------------------------

  CREATE TABLE "REQUETEFEUILLEROUTE" 
   (	"ID" VARCHAR2(36), 
	"RANGEBEGINDATESTEPSTART" TIMESTAMP (6), 
	"RANGEENDDATESTEPSTART" TIMESTAMP (6), 
	"STATUSSTEP" NVARCHAR2(2000), 
	"ISMANDATORY" NUMBER(1,0), 
	"TYPETASK" NVARCHAR2(2000), 
	"HASAUTOMATICVALIDATION" NUMBER(1,0), 
	"TYPESTEP" NVARCHAR2(2000), 
	"RANGEBEGINDATESTEPEND" TIMESTAMP (6), 
	"TITREFEUILLEROUTE" NVARCHAR2(2000), 
	"RANGEENDDATESTEPEND" TIMESTAMP (6), 
	"CURRENTLIFECYCLESTATE" NVARCHAR2(2000), 
	"VALIDATIONSTATUSID" NVARCHAR2(2000), 
	"POSTEID" NVARCHAR2(2000), 
	"DISTRIBUTIONMAILBOXID" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REQUETEMETADONNEES
--------------------------------------------------------

  CREATE TABLE "REQUETEMETADONNEES" 
   (	"ID" VARCHAR2(36), 
	"CLAUSEETATRETIREOUNONRETIRE" NVARCHAR2(2000), 
	"ETATSIGNALE" NUMBER(1,0), 
	"ETATRENOUVELE" NUMBER(1,0), 
	"ETATCLOTUREAUTRE" NUMBER(1,0), 
	"ETATRETIRE" NUMBER(1,0), 
	"ETATCADUQUE" NUMBER(1,0), 
	"ETATRAPPELE" NUMBER(1,0), 
	"ETATNONRETIRE" NUMBER(1,0), 
	"ETATREATTRIBUE" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table REQUETESIMPLE
--------------------------------------------------------

  CREATE TABLE "REQUETESIMPLE" 
   (	"ID" VARCHAR2(36), 
	"CRITERE_REQUETE" NVARCHAR2(2000), 
	"CRITERE_REQUETE_CLAUSE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table REQUETETEXTEINTEGRAL
--------------------------------------------------------

  CREATE TABLE "REQUETETEXTEINTEGRAL" 
   (	"ID" VARCHAR2(36), 
	"APPLIQUERRECHERCHEEXACTE" NUMBER(1,0), 
	"DANSTEXTEREPONSE" NUMBER(1,0), 
	"DANSTEXTEQUESTION" NUMBER(1,0), 
	"CRITERE_REQUETE" NVARCHAR2(2000), 
	"SUBCLAUSE" NVARCHAR2(2000), 
	"DANSTITRE" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table REWRITE_TABLE
--------------------------------------------------------

  CREATE TABLE "REWRITE_TABLE" 
   (	"STATEMENT_ID" VARCHAR2(30), 
	"MV_OWNER" VARCHAR2(30), 
	"MV_NAME" VARCHAR2(30), 
	"SEQUENCE" NUMBER(*,0), 
	"QUERY" VARCHAR2(2000), 
	"QUERY_BLOCK_NO" NUMBER(*,0), 
	"REWRITTEN_TXT" VARCHAR2(2000), 
	"MESSAGE" VARCHAR2(512), 
	"PASS" VARCHAR2(3), 
	"MV_IN_MSG" VARCHAR2(30), 
	"MEASURE_IN_MSG" VARCHAR2(30), 
	"JOIN_BACK_TBL" VARCHAR2(30), 
	"JOIN_BACK_COL" VARCHAR2(30), 
	"ORIGINAL_COST" NUMBER(*,0), 
	"REWRITTEN_COST" NUMBER(*,0), 
	"FLAGS" NUMBER(*,0), 
	"RESERVED1" NUMBER(*,0), 
	"RESERVED2" VARCHAR2(10)
   ) ;


--------------------------------------------------------
--  DDL for Table RMLBX_PRECALCULLIST
--------------------------------------------------------

  CREATE TABLE "RMLBX_PRECALCULLIST" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(150)
   ) ;


--------------------------------------------------------
--  DDL for Table ROUTING_TASK
--------------------------------------------------------

  CREATE TABLE "ROUTING_TASK" 
   (	"ID" VARCHAR2(36), 
	"AUTOMATICVALIDATION" NUMBER(1,0), 
	"POSTELABEL" NVARCHAR2(2000), 
	"VALIDATIONSTATUS" NVARCHAR2(2000), 
	"MINISTERELABEL" NVARCHAR2(2000), 
	"ALREADYDUPLICATED" NUMBER(1,0), 
	"DISTRIBUTIONMAILBOXID" NVARCHAR2(2000), 
	"DOCUMENTROUTEID" VARCHAR2(50), 
	"TYPE" NVARCHAR2(2000), 
	"DATEFINETAPE" TIMESTAMP (6), 
	"DUEDATE" TIMESTAMP (6), 
	"ISMAILSEND" NUMBER(1,0), 
	"ACTIONNABLE" NUMBER(1,0), 
	"DEADLINE" NUMBER(19,0), 
	"VALIDATIONUSERLABEL" NVARCHAR2(2000), 
	"VALIDATIONUSERID" NVARCHAR2(2000), 
	"OBLIGATOIREMINISTERE" NUMBER(1,0), 
	"AUTOMATICVALIDATED" NUMBER(1,0), 
	"DATEDEBUTETAPE" TIMESTAMP (6), 
	"ISREAFFECTATION" NUMBER(1,0), 
	"OBLIGATOIRESGG" NUMBER(1,0), 
	"DIRECTIONLABEL" NVARCHAR2(2000), 
	"DIRECTIONID" VARCHAR2(50), 
	"MINISTEREID" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table RQC_CARACTERISTIQUEQUESTION
--------------------------------------------------------

  CREATE TABLE "RQC_CARACTERISTIQUEQUESTION" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table RQC_ORIGINEQUESTION
--------------------------------------------------------

  CREATE TABLE "RQC_ORIGINEQUESTION" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table RQMET_ETATQUESTION
--------------------------------------------------------

  CREATE TABLE "RQMET_ETATQUESTION" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SEARCH_COVERAGE
--------------------------------------------------------

  CREATE TABLE "SEARCH_COVERAGE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SEARCH_CURRENTLIFECYCLESTATES
--------------------------------------------------------

  CREATE TABLE "SEARCH_CURRENTLIFECYCLESTATES" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SEARCH_NATURE
--------------------------------------------------------

  CREATE TABLE "SEARCH_NATURE" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SEARCH_SEARCHPATH
--------------------------------------------------------

  CREATE TABLE "SEARCH_SEARCHPATH" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SEARCH_SUBJECTS
--------------------------------------------------------

  CREATE TABLE "SEARCH_SUBJECTS" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SIGNALEMENTQUESTION
--------------------------------------------------------

  CREATE TABLE "SIGNALEMENTQUESTION" 
   (	"ID" VARCHAR2(36), 
	"DATEATTENDUE" TIMESTAMP (6), 
	"DATEDEFFET" TIMESTAMP (6)
   ) ;


--------------------------------------------------------
--  DDL for Table SMART_FOLDER
--------------------------------------------------------

  CREATE TABLE "SMART_FOLDER" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(19,0), 
	"QUERYPART" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SORTINFOTYPE
--------------------------------------------------------

  CREATE TABLE "SORTINFOTYPE" 
   (	"ID" VARCHAR2(36), 
	"SORTCOLUMN" NVARCHAR2(2000), 
	"SORTASCENDING" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_DATE_PARCOURS
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_DATE_PARCOURS" 
   (	"IDMINISTERE" NVARCHAR2(50), 
	"MINISTERE" NVARCHAR2(2000), 
	"ORIGINE" NVARCHAR2(50), 
	"NUMERO" NUMBER(19,0), 
	"DATEJO" TIMESTAMP (6), 
	"DATEJOREPONSE" TIMESTAMP (6), 
	"DATETRANSMISSIONMINISTERE" TIMESTAMP (6), 
	"DATETRANSMISSIONPARLEMENT" TIMESTAMP (6), 
	"DATESIGNALEMENTQUESTION" TIMESTAMP (6), 
	"EDITION" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_DIRECTION
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_DIRECTION" 
   (	"ID" NUMBER(19,0), 
	"ACTIF" NUMBER(1,0), 
	"DIRECTION" VARCHAR2(255 CHAR), 
	"EDITION" VARCHAR2(255 CHAR), 
	"MINISTERE" VARCHAR2(255 CHAR), 
	"NBQUESTIONENCOURS" NUMBER(10,0), 
	"ORDRE_PROTOCOLAIRE" NUMBER(10,0)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_ETAPE
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_ETAPE" 
   (	"DATEJO" TIMESTAMP (6), 
	"NUMERO" NUMBER(19,0), 
	"ORIGINE" NVARCHAR2(50), 
	"AUTEUR" NVARCHAR2(201), 
	"IDMINISTERE" VARCHAR2(255), 
	"MINISTERE" NVARCHAR2(2000), 
	"IDDIRECTION" NVARCHAR2(50), 
	"DIRECTION" NVARCHAR2(2000), 
	"DATEJOREPONSE" TIMESTAMP (6), 
	"IDTYPE" NVARCHAR2(2000), 
	"TYPE" VARCHAR2(255), 
	"POSTE" NVARCHAR2(2000), 
	"IDMAILBOX" VARCHAR2(255), 
	"DATEDEBUT" TIMESTAMP (6), 
	"DATEFIN" TIMESTAMP (6), 
	"REPONDU" NUMBER(1,0), 
	"IDMINISTEREETAPE" VARCHAR2(2000), 
	"IDMINISTERERATTACHEMENT" VARCHAR2(255), 
	"INTITULEMINISTERERATTACHEMENT" NVARCHAR2(2000), 
	"VALIDATIONSTATUS" NVARCHAR2(2000), 
	"DATESIGNALEMENTQUESTION" TIMESTAMP (6), 
	"EDITION" NVARCHAR2(2000), 
	"EDITIONMINISTERERATTACHEMENT" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_GROUPE
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_GROUPE" 
   (	"ID" NUMBER(19,0), 
	"ACTIF" NUMBER(1,0), 
	"EDITION" VARCHAR2(255 CHAR), 
	"GROUPE" VARCHAR2(255 CHAR), 
	"MINISTERE" VARCHAR2(255 CHAR), 
	"NBQUESTION" NUMBER(10,0), 
	"ORDRE_PROTOCOLAIRE" NUMBER(10,0), 
	"ORIGINE" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_MINISTERE
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_MINISTERE" 
   (	"ID" NUMBER(19,0), 
	"ACTIF" NUMBER(1,0), 
	"EDITION" VARCHAR2(255 CHAR), 
	"MINISTERE" VARCHAR2(255 CHAR), 
	"NBQUESTIONSSREPONSE" NUMBER(10,0), 
	"NBQUESTIONSSREPONSESUP2MOIS" NUMBER(10,0), 
	"NBRENOUVELLE" NUMBER(10,0), 
	"NBRETIRE" NUMBER(10,0), 
	"ORDRE_PROTOCOLAIRE" NUMBER(10,0), 
	"NBRENOUVELLEEC" NUMBER(10,0)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_MOIS
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_MOIS" 
   (	"ID" NUMBER(19,0), 
	"ACTIF" NUMBER(1,0), 
	"EDITION" VARCHAR2(255 CHAR), 
	"MINISTERE" VARCHAR2(255 CHAR), 
	"MOIS" VARCHAR2(255 CHAR), 
	"NBQUESTION" NUMBER(10,0), 
	"NBREPONSE" NUMBER(10,0), 
	"NBREPONSETOTAL" NUMBER(10,0), 
	"ORDRE_PROTOCOLAIRE" NUMBER(10,0)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_QUESTION_REPONSE
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_QUESTION_REPONSE" 
   (	"ID" NUMBER(19,0), 
	"ACTIF" NUMBER(1,0), 
	"EDITION" VARCHAR2(255 CHAR), 
	"MINISTERE" VARCHAR2(255 CHAR), 
	"NBQUESTION" NUMBER(10,0), 
	"NBREPONDU1MOIS" NUMBER(10,0), 
	"NBREPONDU2MOIS" NUMBER(10,0), 
	"NBREPONDUSUPERIEUR" NUMBER(10,0), 
	"ORDRE_PROTOCOLAIRE" NUMBER(10,0), 
	"ORIGINE" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table STATISTIQUE_VALEUR
--------------------------------------------------------

  CREATE TABLE "STATISTIQUE_VALEUR" 
   (	"ID" NUMBER(19,0), 
	"IDRAPPORT" VARCHAR2(255 CHAR), 
	"LIBELLE" VARCHAR2(255 CHAR), 
	"REQUETE" VARCHAR2(255 CHAR), 
	"VALEUR" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table STATUS
--------------------------------------------------------

  CREATE TABLE "STATUS" 
   (	"ID" VARCHAR2(36), 
	"SERVICEID" NVARCHAR2(2000), 
	"ADMINISTRATIVE_STATUS" NVARCHAR2(2000), 
	"INSTANCEID" NVARCHAR2(2000), 
	"USERLOGIN" NVARCHAR2(2000), 
	"STATUSMESSAGE" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table STEP_FOLDER
--------------------------------------------------------

  CREATE TABLE "STEP_FOLDER" 
   (	"ID" VARCHAR2(36), 
	"EXECUTION" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table SW_ACLR_USER_ACLID
--------------------------------------------------------

  CREATE TABLE "SW_ACLR_USER_ACLID" 
   (	"USERGROUP" VARCHAR2(200), 
	"ACL_ID" VARCHAR2(34)
   ) ;


--------------------------------------------------------
--  DDL for Table SW_ACLR_USERID_USER
--------------------------------------------------------

  CREATE TABLE "SW_ACLR_USERID_USER" 
   (	"USER_ID" VARCHAR2(34), 
	"USERGROUP" VARCHAR2(200)
   ) ;


--------------------------------------------------------
--  DDL for Table TAG
--------------------------------------------------------

  CREATE TABLE "TAG" 
   (	"ID" VARCHAR2(36), 
	"LABEL" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table TIMBRE
--------------------------------------------------------

  CREATE TABLE "TIMBRE" 
   (	"ID" VARCHAR2(36), 
	"CURRENTMIN" NVARCHAR2(2000), 
	"NEXTMIN" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table TYPE_CONTACT
--------------------------------------------------------

  CREATE TABLE "TYPE_CONTACT" 
   (	"ID" VARCHAR2(36), 
	"EMAIL" NVARCHAR2(2000), 
	"MAILBOXID" NVARCHAR2(2000), 
	"NAME" NVARCHAR2(2000), 
	"SERVICE" NVARCHAR2(2000), 
	"SURNAME" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table UID
--------------------------------------------------------

  CREATE TABLE "UID" 
   (	"ID" VARCHAR2(36), 
	"UID" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table UITYPESCONF_ALLOWEDTYPES
--------------------------------------------------------

  CREATE TABLE "UITYPESCONF_ALLOWEDTYPES" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table UITYPESCONF_DENIEDTYPES
--------------------------------------------------------

  CREATE TABLE "UITYPESCONF_DENIEDTYPES" 
   (	"ID" VARCHAR2(36), 
	"POS" NUMBER(10,0), 
	"ITEM" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table UI_TYPES_CONFIGURATION
--------------------------------------------------------

  CREATE TABLE "UI_TYPES_CONFIGURATION" 
   (	"ID" VARCHAR2(36), 
	"DENYALLTYPES" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table USERSETTINGS
--------------------------------------------------------

  CREATE TABLE "USERSETTINGS" 
   (	"ID" VARCHAR2(36), 
	"USER" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table USERSUBSCRIPTION
--------------------------------------------------------

  CREATE TABLE "USERSUBSCRIPTION" 
   (	"ID" NUMBER(10,0), 
	"DOCID" VARCHAR2(255 CHAR), 
	"NOTIFICATION" VARCHAR2(255 CHAR), 
	"USERID" VARCHAR2(255 CHAR)
   ) ;


--------------------------------------------------------
--  DDL for Table VCARD
--------------------------------------------------------

  CREATE TABLE "VCARD" 
   (	"ID" VARCHAR2(36), 
	"EMAIL" NVARCHAR2(2000), 
	"SOUND" NVARCHAR2(2000), 
	"GEO" NVARCHAR2(2000), 
	"TITLE" NVARCHAR2(2000), 
	"BDAY" NVARCHAR2(2000), 
	"NOTE" NVARCHAR2(2000), 
	"REV" NVARCHAR2(2000), 
	"NICKNAME" NVARCHAR2(2000), 
	"MAILER" NVARCHAR2(2000), 
	"LABEL" NVARCHAR2(2000), 
	"TEL" NVARCHAR2(2000), 
	"UID" NVARCHAR2(2000), 
	"FN" NVARCHAR2(2000), 
	"LOGO" NVARCHAR2(2000), 
	"N" NVARCHAR2(2000), 
	"ORG" NVARCHAR2(2000), 
	"AGENT" NVARCHAR2(2000), 
	"ADR" NVARCHAR2(2000), 
	"KEY" NVARCHAR2(2000), 
	"VERSION" NVARCHAR2(2000), 
	"URL" NVARCHAR2(2000), 
	"TZ" NVARCHAR2(2000), 
	"ROLE" NVARCHAR2(2000), 
	"PHOTO" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table VERSIONS
--------------------------------------------------------

  CREATE TABLE "VERSIONS" 
   (	"ID" VARCHAR2(36), 
	"VERSIONABLEID" VARCHAR2(36), 
	"CREATED" TIMESTAMP (6), 
	"LABEL" VARCHAR2(250), 
	"DESCRIPTION" NVARCHAR2(2000), 
	"ISLATEST" NUMBER(1,0), 
	"ISLATESTMAJOR" NUMBER(1,0)
   ) ;


--------------------------------------------------------
--  DDL for Table WEBCONTAINER
--------------------------------------------------------

  CREATE TABLE "WEBCONTAINER" 
   (	"ID" VARCHAR2(36), 
	"USECAPTCHA" NUMBER(1,0), 
	"WELCOMETEXT" NCLOB, 
	"THEME" NVARCHAR2(2000), 
	"ISWEBCONTAINER" NUMBER(1,0), 
	"BASELINE" NVARCHAR2(2000), 
	"TEMPLATE" NVARCHAR2(2000), 
	"THEMEPERSPECTIVE" NVARCHAR2(2000), 
	"THEMEPAGE" NVARCHAR2(2000), 
	"NAME" NVARCHAR2(2000), 
	"MODERATIONTYPE" NVARCHAR2(2000), 
	"URL" NVARCHAR2(2000), 
	"EMAIL" NVARCHAR2(2000)
   ) ;


--------------------------------------------------------
--  DDL for Table WSNOTIFICATION
--------------------------------------------------------

  CREATE TABLE "WSNOTIFICATION" 
   (	"ID" VARCHAR2(36), 
	"NBESSAIS" NUMBER(19,0), 
	"IDQUESTION" NVARCHAR2(2000), 
	"POSTEID" NVARCHAR2(2000), 
	"WEBSERVICE" NVARCHAR2(2000)
   ) ;



-- #########################################################################################
-- DATA
-- #########################################################################################

INSERT INTO "ACLR_PERMISSION" ("PERMISSION") VALUES('Browse');



insert INTO "ACLR_PERMISSION" ("PERMISSION") VALUES('Classify');



insert INTO "ACLR_PERMISSION" ("PERMISSION") VALUES('Read');



insert INTO "ACLR_PERMISSION" ("PERMISSION") VALUES('ReadProperties');



insert INTO "ACLR_PERMISSION" ("PERMISSION") VALUES('ReadRemove');



insert INTO "ACLR_PERMISSION" ("PERMISSION") VALUES('ReadWrite');



insert INTO "ACLR_PERMISSION" ("PERMISSION") VALUES('Everything');



Insert into VOC_BOOLEAN ("id","obsolete","ordering","label") values ('TRUE','0','10000000','Oui');



insert into VOC_BOOLEAN ("id","obsolete","ordering","label") values ('FALSE','0','10000000','Non');



Insert into VOC_BOOLEAN_REQUETEUR ("id","obsolete","ordering","label") values ('1','0','10000000','Oui');



insert into VOC_BOOLEAN_REQUETEUR ("id","obsolete","ordering","label") values ('0','0','10000000','Non');



Insert into VOC_BORDEREAU_LABEL ("id","obsolete","ordering","label") values ('AN_rubrique','0','10000000','Indexation complémentaire : Assemblée nationale : Rubrique');



insert into VOC_BORDEREAU_LABEL ("id","obsolete","ordering","label") values ('TA_rubrique','0','10000000','Indexation complémentaire : Assemblée nationale : Tête d''analyse');



insert into VOC_BORDEREAU_LABEL ("id","obsolete","ordering","label") values ('AN_analyse','0','10000000','Indexation complémentaire : Assemblée nationale : Analyse');



insert into VOC_BORDEREAU_LABEL ("id","obsolete","ordering","label") values ('SE_theme','0','10000000','Indexation complémentaire : Sénat : Thème');



insert into VOC_BORDEREAU_LABEL ("id","obsolete","ordering","label") values ('SE_rubrique','0','10000000','Indexation complémentaire : Sénat : Rubrique');



insert into VOC_BORDEREAU_LABEL ("id","obsolete","ordering","label") values ('SE_renvoi','0','10000000','Indexation complémentaire : Sénat : Renvoi');



insert into VOC_BORDEREAU_LABEL ("id","obsolete","ordering","label") values ('motclef_ministere','0','10000000','Indexation complémentaire : Ministères : Mot-clef');



Insert into VOC_CARACT_QUESTION ("id","obsolete","ordering","label") values ('AvecReponse','0','10000000','Répondu');



insert into VOC_CARACT_QUESTION ("id","obsolete","ordering","label") values ('SansReponse','0','10000000','En attente de réponse');



Insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('1','0','10000000','label.reponses.feuilleRoute.etape.pour.redaction');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('2','0','10000000','label.reponses.feuilleRoute.etape.pour.attribution');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('3','0','10000000','label.reponses.feuilleRoute.etape.pour.visa');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('4','0','10000000','label.reponses.feuilleRoute.etape.pour.signature');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('5','0','10000000','label.reponses.feuilleRoute.etape.pour.information');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('6','0','10000000','label.reponses.feuilleRoute.etape.pour.avis');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('7','0','10000000','label.reponses.feuilleRoute.etape.pour.validationPM');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('8','0','10000000','label.reponses.feuilleRoute.etape.pour.reattribution');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('9','0','10000000','label.reponses.feuilleRoute.etape.pour.reorientation');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('10','0','10000000','label.reponses.feuilleRoute.etape.pour.impression');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('11','0','10000000','label.reponses.feuilleRoute.etape.pour.transmissionAssemblees');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('12','0','10000000','label.reponses.feuilleRoute.etape.pour.arbitrage');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('13','0','10000000','label.reponses.feuilleRoute.etape.pour.redactionInterfacee');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('14','0','10000000','label.reponses.feuilleRoute.etape.pour.actualisation');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('15','0','10000000','label.reponses.feuilleRoute.etape.pour.correction');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('16','0','10000000','label.reponses.feuilleRoute.etape.pour.attente');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('17','0','10000000','label.reponses.feuilleRoute.etape.pour.retour');



insert into VOC_CM_ROUTING_TASK_TYPE ("id","obsolete","ordering","label") values ('18','0','10000000','label.reponses.feuilleRoute.etape.pour.validationRetourPM');



Insert into VOC_ETAT_ETAPE ("id","obsolete","ordering","label") values ('running','0','10000000','étape en cours');



insert into VOC_ETAT_ETAPE ("id","obsolete","ordering","label") values ('validated','0','10000000','étape validée');



insert into VOC_ETAT_ETAPE ("id","obsolete","ordering","label") values ('done','0','10000000','étape validée manuellement');



insert into VOC_ETAT_ETAPE ("id","obsolete","ordering","label") values ('ready','0','10000000','étape à venir');



Insert into VOC_ETAT_QUESTION ("id","obsolete","ordering","label") values ('retiree','0','10000000','Retirée');



insert into VOC_ETAT_QUESTION ("id","obsolete","ordering","label") values ('caduque','0','10000000','Caduque');



insert into VOC_ETAT_QUESTION ("id","obsolete","ordering","label") values ('en cours','0','10000000','En cours');



insert into VOC_ETAT_QUESTION ("id","obsolete","ordering","label") values ('repondu','0','10000000','Répondu');



insert into VOC_ETAT_QUESTION ("id","obsolete","ordering","label") values ('cloture_autre','0','10000000','Clos autre');



Insert into VOC_FEUILLEROUTE_TYPE_CREATION ("id","obsolete","parent","ordering","label") values ('substitution','0',null,'10000000','Substitution');



insert into VOC_FEUILLEROUTE_TYPE_CREATION ("id","obsolete","parent","ordering","label") values ('instanciation','0',null,'10000000','Attribution initiale');



insert into VOC_FEUILLEROUTE_TYPE_CREATION ("id","obsolete","parent","ordering","label") values ('reattribution','0',null,'10000000','Réattribution');



insert into VOC_FEUILLEROUTE_TYPE_CREATION ("id","obsolete","parent","ordering","label") values ('reaffectation','0',null,'10000000','Réaffectation');



Insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Nouveau Centre','0','10000000','Nouveau Centre');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Union pour un Mouvement Populaire','0','10000000','Union pour un Mouvement Populaire');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Députés n''appartenant à aucun groupe','0','10000000','Députés n''appartenant à aucun groupe');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Gauche démocrate et républicaine','0','10000000','Gauche démocrate et républicaine');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Socialiste, radical, citoyen et divers gauche','0','10000000','Socialiste, radical, citoyen et divers gauche');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Non connu','0','10000000','Non connu');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Groupe Socialiste','0','10000000','Groupe Socialiste');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Groupe UMP','0','10000000','Groupe UMP');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Groupe du RDSE','0','10000000','Groupe du RDSE');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Réunion administrative des non-inscrits','0','10000000','Réunion administrative des non-inscrits');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Groupe UC','0','10000000','Groupe UC');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Groupe CRC-SPG','0','10000000','Groupe CRC-SPG');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Groupe UC - UDF','0','10000000','Groupe UC - UDF');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Groupe CRC','0','10000000','Groupe CRC');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('SOC (SENAT)','0','10000000','SOC (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UMP (SENAT)','0','10000000','UMP (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UMP (SENAT)','0','10000000','UMP (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('RDSE (SENAT)','0','10000000','RDSE (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('NI (SENAT)','0','10000000','NI (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UC-UDF (SENAT)','0','10000000','UC-UDF (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UC (SENAT)','0','10000000','UC (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('CRC-SPG (SENAT)','0','10000000','CRC-SPG (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('CRC (SENAT)','0','10000000','CRC (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UCR (SENAT)','0','10000000','UCR (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('SOC-EELVr (SENAT)','0','10000000','SOC-EELVr (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Députés n''appartenant à aucun groupe (AN)','0','10000000','Députés n''appartenant à aucun groupe (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Socialiste, radical, citoyen et divers gauche (AN)','0','10000000','Socialiste, radical, citoyen et divers gauche (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Union pour un Mouvement Populaire (AN)','0','10000000','Union pour un Mouvement Populaire (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Nouveau Centre (AN)','0','10000000','Nouveau Centre (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Gauche démocrate et républicaine (AN)','0','10000000','Gauche démocrate et républicaine (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Non inscrit (AN)','0','10000000','Non inscrit (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Non connu (SENAT)','0','10000000','Non connu (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('ECOLO (SENAT)','0','10000000','ECOLO (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('RRDP (AN)','0','10000000','RRDP (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UDI-UC (SENAT)','0','10000000','UDI-UC (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Rsbt-UMP (AN)','0','10000000','Rsbt-UMP (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UMP (AN)','0','10000000','UMP (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('GDR (AN)','0','10000000','GDR (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('NC (AN)','0','10000000','NC (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('NI (AN)','0','10000000','NI (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('S.R.C. (AN)','0','10000000','S.R.C. (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('SRC (AN)','0','10000000','SRC (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('UDI (AN)','0','10000000','UDI (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('ECOLO (AN)','0','10000000','ECOLO (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('R-UMP (AN)','0','10000000','R-UMP (AN)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Aucun (SENAT)','0','10000000','Aucun (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Soc. (SENAT)','0','10000000','Soc. (SENAT)');



insert into VOC_GROUPE_POLITIQUE ("id","obsolete","ordering","label") values ('Les Républicains (SENAT)','0','10000000','Les Républicains (SENAT)');



Insert into VOC_LEGISLATURE ("id","obsolete","dateFin","ordering","label","dateDebut") values ('13','0',to_timestamp('20/07/12 00:00:00','DD/MM/RR HH24:MI:SS,FF'),'10000000','13eme legislature',to_timestamp('20/07/07 00:00:00','DD/MM/RR HH24:MI:SS,FF'));



insert into VOC_LEGISLATURE ("id","obsolete","dateFin","ordering","label","dateDebut") values ('14','0',to_timestamp('20/06/17 00:00:00','DD/MM/RR HH24:MI:SS,FF'),'10000001','14eme legislature',to_timestamp('20/06/12 00:00:00','DD/MM/RR HH24:MI:SS,FF'));



Insert into VOC_ORIGINE_QUESTION ("id","obsolete","ordering","label") values ('AN','0','10000000','Assemblée nationale');



insert into VOC_ORIGINE_QUESTION ("id","obsolete","ordering","label") values ('SENAT','0','10000000','Sénat');



Insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('renvoi1','0','10000000','Renvoi pour ...');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('renvoi2','0','10000000','Un autre renvoi pour ....');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('renvoi3','0','10000000','Un autre renvoi pour ....');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('renvoi4','0','10000000','Un autre renvoi pour ....');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports sanitaires','0','10000000','Transports sanitaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Poste (La)','0','10000000','Poste (La)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cours et tribunaux','0','10000000','Cours et tribunaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hôpitaux','0','10000000','Hôpitaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éclairage public','0','10000000','Éclairage public');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Personnes âgées','0','10000000','Personnes âgées');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dépendance','0','10000000','Dépendance');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Jeunes','0','10000000','Jeunes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Languedoc-Roussillon','0','10000000','Languedoc-Roussillon');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Statistiques','0','10000000','Statistiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Opticiens-lunetiers','0','10000000','Opticiens-lunetiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Servitudes','0','10000000','Servitudes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Retraites agricoles','0','10000000','Retraites agricoles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Urgences médicales','0','10000000','Urgences médicales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mutuelles','0','10000000','Mutuelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Zones protégées','0','10000000','Zones protégées');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Comités d''entreprise','0','10000000','Comités d''entreprise');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Loyers','0','10000000','Loyers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Douanes','0','10000000','Douanes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Langues étrangères','0','10000000','Langues étrangères');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fraudes fiscales','0','10000000','Fraudes fiscales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vaccinations','0','10000000','Vaccinations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Successions','0','10000000','Successions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Alsace et Lorraine','0','10000000','Alsace et Lorraine');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travailleurs frontaliers','0','10000000','Travailleurs frontaliers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Suisse','0','10000000','Suisse');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agriculture','0','10000000','Agriculture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Arts et spectacles','0','10000000','Arts et spectacles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Incompatibilités','0','10000000','Incompatibilités');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prêts','0','10000000','Prêts');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Peines','0','10000000','Peines');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cotisations sociales','0','10000000','Cotisations sociales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Crimes, délits et contraventions','0','10000000','Crimes, délits et contraventions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Stockage','0','10000000','Stockage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Installations classées','0','10000000','Installations classées');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement supérieur','0','10000000','Enseignement supérieur');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Syndrome immunodéficitaire acquis (SIDA)','0','10000000','Syndrome immunodéficitaire acquis (SIDA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mali','0','10000000','Mali');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chasse et pêche','0','10000000','Chasse et pêche');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maladies','0','10000000','Maladies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Logement social','0','10000000','Logement social');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sports','0','10000000','Sports');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pauvreté','0','10000000','Pauvreté');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Jeunes agriculteurs','0','10000000','Jeunes agriculteurs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Accidents de la circulation','0','10000000','Accidents de la circulation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Camping caravaning','0','10000000','Camping caravaning');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Laboratoires','0','10000000','Laboratoires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bioéthique','0','10000000','Bioéthique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité sociale (organismes)','0','10000000','Sécurité sociale (organismes)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travail (durée du)','0','10000000','Travail (durée du)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Appellations d''origine contrôlée (AOC)','0','10000000','Appellations d''origine contrôlée (AOC)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Procédure administrative','0','10000000','Procédure administrative');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Carburants','0','10000000','Carburants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Faune et flore','0','10000000','Faune et flore');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pensions civiles et militaires','0','10000000','Pensions civiles et militaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Habitat','0','10000000','Habitat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Scieries','0','10000000','Scieries');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Villes','0','10000000','Villes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Immobilier','0','10000000','Immobilier');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travailleurs indépendants','0','10000000','Travailleurs indépendants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organismes divers','0','10000000','Organismes divers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hôtels et restaurants','0','10000000','Hôtels et restaurants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Médecine (enseignement de la)','0','10000000','Médecine (enseignement de la)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Énergie','0','10000000','Énergie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Autoroutes','0','10000000','Autoroutes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Affichage','0','10000000','Affichage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Propagande électorale','0','10000000','Propagande électorale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dotation globale de fonctionnement (DGF)','0','10000000','Dotation globale de fonctionnement (DGF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bretagne','0','10000000','Bretagne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Développement durable','0','10000000','Développement durable');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Service d''assistance médicale d''urgence (SAMU)','0','10000000','Service d''assistance médicale d''urgence (SAMU)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité civile','0','10000000','Sécurité civile');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ordinateurs','0','10000000','Ordinateurs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Corse','0','10000000','Corse');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Retraite (âge de la)','0','10000000','Retraite (âge de la)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Circonscriptions électorales','0','10000000','Circonscriptions électorales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pouvoir d''achat','0','10000000','Pouvoir d''achat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Carte scolaire','0','10000000','Carte scolaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Centres de vacances','0','10000000','Centres de vacances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Finances locales','0','10000000','Finances locales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Défense','0','10000000','Défense');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Spiritueux','0','10000000','Spiritueux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Handicapés (transports et accès aux locaux)','0','10000000','Handicapés (transports et accès aux locaux)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonction publique hospitalière','0','10000000','Fonction publique hospitalière');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droit des sociétés','0','10000000','Droit des sociétés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Région parisienne','0','10000000','Région parisienne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Trains à grande vitesse (TGV)','0','10000000','Trains à grande vitesse (TGV)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Discipline militaire','0','10000000','Discipline militaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Orientation scolaire et professionnelle','0','10000000','Orientation scolaire et professionnelle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Trésor','0','10000000','Trésor');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Belgique','0','10000000','Belgique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agriculture biologique','0','10000000','Agriculture biologique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Crémation','0','10000000','Crémation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Étiquetage','0','10000000','Étiquetage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mandataires','0','10000000','Mandataires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Professions judiciaires et juridiques','0','10000000','Professions judiciaires et juridiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gouvernement','0','10000000','Gouvernement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Climat','0','10000000','Climat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Publicité','0','10000000','Publicité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Raffinage','0','10000000','Raffinage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Stationnement','0','10000000','Stationnement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sociétés d''économie mixte (SEM)','0','10000000','Sociétés d''économie mixte (SEM)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vie politique','0','10000000','Vie politique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Impôts locaux','0','10000000','Impôts locaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections cantonales','0','10000000','Élections cantonales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conseils généraux','0','10000000','Conseils généraux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Rythmes scolaires','0','10000000','Rythmes scolaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Responsabilité administrative','0','10000000','Responsabilité administrative');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Voies navigables','0','10000000','Voies navigables');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Porcins','0','10000000','Porcins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Démographie','0','10000000','Démographie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Monaco','0','10000000','Monaco');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assemblée nationale','0','10000000','Assemblée nationale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Marchés','0','10000000','Marchés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Religions et cultes','0','10000000','Religions et cultes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chambres consulaires','0','10000000','Chambres consulaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assurances','0','10000000','Assurances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Permis de construire','0','10000000','Permis de construire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mines et carrières','0','10000000','Mines et carrières');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enquêtes publiques','0','10000000','Enquêtes publiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Emprunts','0','10000000','Emprunts');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assurance maladie','0','10000000','Assurance maladie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pollution (air)','0','10000000','Pollution (air)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Restauration','0','10000000','Restauration');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Auvergne','0','10000000','Auvergne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gens du voyage','0','10000000','Gens du voyage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bénéfices (imposition des)','0','10000000','Bénéfices (imposition des)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pédagogie','0','10000000','Pédagogie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Communes rurales','0','10000000','Communes rurales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vie privée (atteinte à la)','0','10000000','Vie privée (atteinte à la)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Politique sociale','0','10000000','Politique sociale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Copropriété','0','10000000','Copropriété');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement par correspondance','0','10000000','Enseignement par correspondance');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Administration pénitentiaire','0','10000000','Administration pénitentiaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Afrique','0','10000000','Afrique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Inspecteurs du travail','0','10000000','Inspecteurs du travail');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports en commun','0','10000000','Transports en commun');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Église (édifices)','0','10000000','Église (édifices)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taxe d''habitation','0','10000000','Taxe d''habitation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports ferroviaires','0','10000000','Transports ferroviaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ports','0','10000000','Ports');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vacances','0','10000000','Vacances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dotation de solidarité urbaine (DSU)','0','10000000','Dotation de solidarité urbaine (DSU)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Licenciements','0','10000000','Licenciements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maïs','0','10000000','Maïs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Iran','0','10000000','Iran');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Notariat','0','10000000','Notariat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Français de l''étranger','0','10000000','Français de l''étranger');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Alsace','0','10000000','Alsace');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections européennes','0','10000000','Élections européennes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Artisans','0','10000000','Artisans');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Métiers d''art','0','10000000','Métiers d''art');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Péages','0','10000000','Péages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Francophonie','0','10000000','Francophonie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hydraulique','0','10000000','Hydraulique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cimetières','0','10000000','Cimetières');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cyclisme','0','10000000','Cyclisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Reconversion industrielle','0','10000000','Reconversion industrielle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Exclusion','0','10000000','Exclusion');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Poids et mesures','0','10000000','Poids et mesures');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Laïcité','0','10000000','Laïcité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Archives','0','10000000','Archives');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Congés','0','10000000','Congés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Imprimerie','0','10000000','Imprimerie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Entreprises (très petites)','0','10000000','Entreprises (très petites)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Discrimination','0','10000000','Discrimination');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prix','0','10000000','Prix');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Distillation','0','10000000','Distillation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taxe foncière sur les propriétés non bâties','0','10000000','Taxe foncière sur les propriétés non bâties');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bourses d''études','0','10000000','Bourses d''études');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Force intérimaire des Nations Unies au Liban (FINUL)','0','10000000','Force intérimaire des Nations Unies au Liban (FINUL)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gendarmerie','0','10000000','Gendarmerie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Indemnités','0','10000000','Indemnités');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Décorations et médailles','0','10000000','Décorations et médailles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Urbanisme commercial','0','10000000','Urbanisme commercial');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Politiques communautaires','0','10000000','Politiques communautaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Finances publiques','0','10000000','Finances publiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mineurs (protection des)','0','10000000','Mineurs (protection des)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Logement','0','10000000','Logement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cours d''eau, étangs et lacs','0','10000000','Cours d''eau, étangs et lacs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Retraités','0','10000000','Retraités');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports routiers','0','10000000','Transports routiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Investissements','0','10000000','Investissements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Régions','0','10000000','Régions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Industrie automobile','0','10000000','Industrie automobile');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Apprentissage','0','10000000','Apprentissage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Consommation','0','10000000','Consommation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Recours','0','10000000','Recours');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dépenses de fonctionnement','0','10000000','Dépenses de fonctionnement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Personnel','0','10000000','Personnel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Routes','0','10000000','Routes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Économies d''énergie','0','10000000','Économies d''énergie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Caisse des dépôts et consignations','0','10000000','Caisse des dépôts et consignations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chefs d''entreprises','0','10000000','Chefs d''entreprises');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Culture','0','10000000','Culture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cumul des mandats','0','10000000','Cumul des mandats');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Handicapés (prestations et ressources)','0','10000000','Handicapés (prestations et ressources)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maîtres d''oeuvre','0','10000000','Maîtres d''oeuvre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Radar','0','10000000','Radar');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Avocats','0','10000000','Avocats');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mayotte','0','10000000','Mayotte');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Syndicats','0','10000000','Syndicats');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nouvelles technologies','0','10000000','Nouvelles technologies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aide au logement','0','10000000','Aide au logement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Armée','0','10000000','Armée');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Salaires et rémunérations','0','10000000','Salaires et rémunérations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conseil économique, social et environnemental (CESE)','0','10000000','Conseil économique, social et environnemental (CESE)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organisation commune des marchés (OCM)','0','10000000','Organisation commune des marchés (OCM)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sociétés','0','10000000','Sociétés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Usufruit','0','10000000','Usufruit');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Comptabilité','0','10000000','Comptabilité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Experts-comptables','0','10000000','Experts-comptables');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Délégués','0','10000000','Délégués');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Handicapés (établissements spécialisés et soins)','0','10000000','Handicapés (établissements spécialisés et soins)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Coopération','0','10000000','Coopération');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pharmaciens et pharmacies','0','10000000','Pharmaciens et pharmacies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Drogues et stupéfiants','0','10000000','Drogues et stupéfiants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ambassades et consulats','0','10000000','Ambassades et consulats');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Frais professionnels','0','10000000','Frais professionnels');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Oléagineux','0','10000000','Oléagineux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Automobiles','0','10000000','Automobiles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cour de justice de l''Union européenne ','0','10000000','Cour de justice de l''Union européenne ');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Indemnités de logement','0','10000000','Indemnités de logement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cliniques','0','10000000','Cliniques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droit d''asile','0','10000000','Droit d''asile');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chauffage','0','10000000','Chauffage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vétérinaires','0','10000000','Vétérinaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Greffiers','0','10000000','Greffiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chambres de métiers','0','10000000','Chambres de métiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Délinquance','0','10000000','Délinquance');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Recouvrement des impôts','0','10000000','Recouvrement des impôts');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Décentralisation','0','10000000','Décentralisation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Juridiction','0','10000000','Juridiction');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Police municipale','0','10000000','Police municipale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Établissements publics à caractère industriel et commercial (EPIC)','0','10000000','Établissements publics à caractère industriel et commercial (EPIC)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maisons de retraite et foyers logements','0','10000000','Maisons de retraite et foyers logements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Régies','0','10000000','Régies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Indivision','0','10000000','Indivision');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Collèges','0','10000000','Collèges');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éducation','0','10000000','Éducation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Régie autonome des transports parisiens (RATP)','0','10000000','Régie autonome des transports parisiens (RATP)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Action sanitaire et sociale','0','10000000','Action sanitaire et sociale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Animaux','0','10000000','Animaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aide sociale','0','10000000','Aide sociale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Électricité de France (EDF)','0','10000000','Électricité de France (EDF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Groupements agricoles d''exploitation en commun (GAEC)','0','10000000','Groupements agricoles d''exploitation en commun (GAEC)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éducation spécialisée','0','10000000','Éducation spécialisée');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contraventions de police','0','10000000','Contraventions de police');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pêche','0','10000000','Pêche');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('État','0','10000000','État');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Centrafrique','0','10000000','Centrafrique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Photographie','0','10000000','Photographie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Révision générale des politiques publiques','0','10000000','Révision générale des politiques publiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Terrorisme','0','10000000','Terrorisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maroc','0','10000000','Maroc');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nazisme','0','10000000','Nazisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Boissons','0','10000000','Boissons');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ambulances','0','10000000','Ambulances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Politique culturelle','0','10000000','Politique culturelle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Autriche','0','10000000','Autriche');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fromages','0','10000000','Fromages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chambres de commerce et d''industrie','0','10000000','Chambres de commerce et d''industrie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agence pour l''enseignement français à l''étranger (AEFE)','0','10000000','Agence pour l''enseignement français à l''étranger (AEFE)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Inspecteurs d''académie','0','10000000','Inspecteurs d''académie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Régie','0','10000000','Régie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aveugles','0','10000000','Aveugles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Devoir de réserve','0','10000000','Devoir de réserve');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonctionnaires','0','10000000','Fonctionnaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tunisie','0','10000000','Tunisie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conseils régionaux','0','10000000','Conseils régionaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Suicide','0','10000000','Suicide');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Inéligibilités et incompatibilités','0','10000000','Inéligibilités et incompatibilités');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hébergement','0','10000000','Hébergement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Plantes','0','10000000','Plantes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Salariés','0','10000000','Salariés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chirurgiens-dentistes','0','10000000','Chirurgiens-dentistes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Industrie pharmaceutique','0','10000000','Industrie pharmaceutique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Armes et armement','0','10000000','Armes et armement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chemins ruraux','0','10000000','Chemins ruraux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Édition','0','10000000','Édition');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Méthane','0','10000000','Méthane');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Crèches et garderies','0','10000000','Crèches et garderies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Forages','0','10000000','Forages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ordres professionnels','0','10000000','Ordres professionnels');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tunnel sous la Manche','0','10000000','Tunnel sous la Manche');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Radiodiffusion et télévision','0','10000000','Radiodiffusion et télévision');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mineurs (travailleurs de la mine)','0','10000000','Mineurs (travailleurs de la mine)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Croissance','0','10000000','Croissance');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organisation de coopération et de développement économique (OCDE)','0','10000000','Organisation de coopération et de développement économique (OCDE)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dette publique','0','10000000','Dette publique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Action humanitaire','0','10000000','Action humanitaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Union de recouvrement des cotisations de sécurité sociale et d''allocations familiales (URSSAF)','0','10000000','Union de recouvrement des cotisations de sécurité sociale et d''allocations familiales (URSSAF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Politique économique','0','10000000','Politique économique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Médecines parallèles','0','10000000','Médecines parallèles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Incendies','0','10000000','Incendies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Géothermie','0','10000000','Géothermie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonds marins','0','10000000','Fonds marins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports maritimes','0','10000000','Transports maritimes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections régionales','0','10000000','Élections régionales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éthanol','0','10000000','Éthanol');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sociétés d''aménagement foncier et d''établissement rural (SAFER)','0','10000000','Sociétés d''aménagement foncier et d''établissement rural (SAFER)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Engrais et amendements','0','10000000','Engrais et amendements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nigeria','0','10000000','Nigeria');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Plans d''urbanisme','0','10000000','Plans d''urbanisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bâtiment et travaux publics','0','10000000','Bâtiment et travaux publics');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Délais de paiement','0','10000000','Délais de paiement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections municipales','0','10000000','Élections municipales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conseil constitutionnel','0','10000000','Conseil constitutionnel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prévention des risques','0','10000000','Prévention des risques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Main-d''œuvre','0','10000000','Main-d''œuvre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bénévolat','0','10000000','Bénévolat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité sociale (cotisations)','0','10000000','Sécurité sociale (cotisations)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Charges sociales','0','10000000','Charges sociales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chevaux','0','10000000','Chevaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Centres équestres','0','10000000','Centres équestres');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Volontariat','0','10000000','Volontariat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Oiseaux','0','10000000','Oiseaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Internet','0','10000000','Internet');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contrats','0','10000000','Contrats');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Emploi','0','10000000','Emploi');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Impôt sur les sociétés','0','10000000','Impôt sur les sociétés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nationalité française','0','10000000','Nationalité française');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Justice (ministère de la)','0','10000000','Justice (ministère de la)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prescription','0','10000000','Prescription');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Liquidation judiciaire','0','10000000','Liquidation judiciaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Alsace-Moselle','0','10000000','Alsace-Moselle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Votes','0','10000000','Votes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports scolaires','0','10000000','Transports scolaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Paris','0','10000000','Paris');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sapeurs-pompiers','0','10000000','Sapeurs-pompiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Examens, concours et diplômes','0','10000000','Examens, concours et diplômes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement','0','10000000','Enseignement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enfance en danger','0','10000000','Enfance en danger');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Insertion','0','10000000','Insertion');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Revenus','0','10000000','Revenus');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ovins','0','10000000','Ovins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éducation populaire','0','10000000','Éducation populaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lait et produits laitiers','0','10000000','Lait et produits laitiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chèvres','0','10000000','Chèvres');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Télécommunications','0','10000000','Télécommunications');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lois','0','10000000','Lois');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Intercommunalité','0','10000000','Intercommunalité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aménagement du territoire','0','10000000','Aménagement du territoire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Indemnisation','0','10000000','Indemnisation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Réseau ferré de France (RFF)','0','10000000','Réseau ferré de France (RFF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Société nationale des chemins de fer français (SNCF)','0','10000000','Société nationale des chemins de fer français (SNCF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Union européenne','0','10000000','Union européenne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Créances et dettes','0','10000000','Créances et dettes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Circulaires','0','10000000','Circulaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Voirie','0','10000000','Voirie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nature (protection de la)','0','10000000','Nature (protection de la)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gaz','0','10000000','Gaz');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Exploitants agricoles','0','10000000','Exploitants agricoles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Départements','0','10000000','Départements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Seniors','0','10000000','Seniors');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chômage','0','10000000','Chômage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Français (langue)','0','10000000','Français (langue)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pollution et nuisances','0','10000000','Pollution et nuisances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Intempéries','0','10000000','Intempéries');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Impôts et taxes','0','10000000','Impôts et taxes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Économie et finances (Ministère de l'')','0','10000000','Économie et finances (Ministère de l'')');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contractuels','0','10000000','Contractuels');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bruit','0','10000000','Bruit');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cantons','0','10000000','Cantons');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Zones rurales','0','10000000','Zones rurales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Médecins','0','10000000','Médecins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Secret professionnel','0','10000000','Secret professionnel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Jurisprudence','0','10000000','Jurisprudence');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Plastiques','0','10000000','Plastiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Traitements et indemnités','0','10000000','Traitements et indemnités');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pôle emploi','0','10000000','Pôle emploi');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gares','0','10000000','Gares');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Institut national de la recherche agronomique (INRA)','0','10000000','Institut national de la recherche agronomique (INRA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Étudiants','0','10000000','Étudiants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonction publique','0','10000000','Fonction publique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Entreprises (petites et moyennes)','0','10000000','Entreprises (petites et moyennes)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Honoraires','0','10000000','Honoraires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Apiculture','0','10000000','Apiculture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Accidents du travail et maladies professionnelles','0','10000000','Accidents du travail et maladies professionnelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Militaires','0','10000000','Militaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Patrimoine (protection du)','0','10000000','Patrimoine (protection du)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fourrière','0','10000000','Fourrière');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maires','0','10000000','Maires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tourisme','0','10000000','Tourisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Baccalauréat','0','10000000','Baccalauréat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Distribution','0','10000000','Distribution');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taxes locales','0','10000000','Taxes locales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Restauration collective','0','10000000','Restauration collective');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Politique industrielle','0','10000000','Politique industrielle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Eau et assainissement','0','10000000','Eau et assainissement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assurance chômage','0','10000000','Assurance chômage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Famille','0','10000000','Famille');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Amiante','0','10000000','Amiante');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Retraite','0','10000000','Retraite');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Épargne','0','10000000','Épargne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Médecine du travail','0','10000000','Médecine du travail');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Amendes','0','10000000','Amendes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Loisirs','0','10000000','Loisirs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Union nationale pour l''emploi dans l''industrie et le commerce (UNEDIC)','0','10000000','Union nationale pour l''emploi dans l''industrie et le commerce (UNEDIC)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taxe foncière sur les propriétés bâties','0','10000000','Taxe foncière sur les propriétés bâties');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Foncier','0','10000000','Foncier');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Infirmiers et infirmières','0','10000000','Infirmiers et infirmières');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Québec','0','10000000','Québec');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nucléaire','0','10000000','Nucléaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Horticulture','0','10000000','Horticulture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droit du travail','0','10000000','Droit du travail');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Électricité','0','10000000','Électricité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Établissements publics','0','10000000','Établissements publics');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Financement','0','10000000','Financement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cambriolages','0','10000000','Cambriolages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conseils municipaux','0','10000000','Conseils municipaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Population','0','10000000','Population');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Uranium','0','10000000','Uranium');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Niger','0','10000000','Niger');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Défense nationale','0','10000000','Défense nationale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Libye','0','10000000','Libye');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contribution sociale généralisée (CSG)','0','10000000','Contribution sociale généralisée (CSG)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement primaire','0','10000000','Enseignement primaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Construction','0','10000000','Construction');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Animaux nuisibles','0','10000000','Animaux nuisibles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Boucherie-charcuterie','0','10000000','Boucherie-charcuterie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commerce électronique','0','10000000','Commerce électronique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nord','0','10000000','Nord');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Obsèques','0','10000000','Obsèques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Danse','0','10000000','Danse');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hélicoptères','0','10000000','Hélicoptères');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Police','0','10000000','Police');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Visas','0','10000000','Visas');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Justice','0','10000000','Justice');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Zones d''éducation prioritaires (ZEP)','0','10000000','Zones d''éducation prioritaires (ZEP)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Autorité administrative indépendante','0','10000000','Autorité administrative indépendante');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Déclarations publiques','0','10000000','Déclarations publiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Monuments historiques','0','10000000','Monuments historiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enfants intellectuellement précoces','0','10000000','Enfants intellectuellement précoces');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Barèmes','0','10000000','Barèmes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Question caduque redéposée','0','10000000','Question caduque redéposée');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Insignes et emblèmes','0','10000000','Insignes et emblèmes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Informatique','0','10000000','Informatique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Marchés publics','0','10000000','Marchés publics');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élus locaux','0','10000000','Élus locaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Domicile','0','10000000','Domicile');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taïwan','0','10000000','Taïwan');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Orthoptistes','0','10000000','Orthoptistes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hygiène','0','10000000','Hygiène');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Handicapés (travail et reclassement)','0','10000000','Handicapés (travail et reclassement)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enquêtes et sondages','0','10000000','Enquêtes et sondages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travaux','0','10000000','Travaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Concessions','0','10000000','Concessions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Comptabilité publique','0','10000000','Comptabilité publique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports','0','10000000','Transports');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sénat','0','10000000','Sénat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Produits financiers','0','10000000','Produits financiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Stages','0','10000000','Stages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Campagnes électorales','0','10000000','Campagnes électorales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Diffamation','0','10000000','Diffamation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tutelle et curatelle','0','10000000','Tutelle et curatelle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vote par procuration','0','10000000','Vote par procuration');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conseil supérieur de l''audiovisuel','0','10000000','Conseil supérieur de l''audiovisuel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Épizootie','0','10000000','Épizootie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prothèses','0','10000000','Prothèses');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ingénierie','0','10000000','Ingénierie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Campagne double','0','10000000','Campagne double');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections','0','10000000','Élections');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Catastrophes naturelles','0','10000000','Catastrophes naturelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Viticulture','0','10000000','Viticulture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chine','0','10000000','Chine');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Établissements scolaires','0','10000000','Établissements scolaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aides publiques','0','10000000','Aides publiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Anciens combattants et victimes de guerre','0','10000000','Anciens combattants et victimes de guerre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assurance maladie et maternité','0','10000000','Assurance maladie et maternité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité sociale','0','10000000','Sécurité sociale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travail','0','10000000','Travail');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travail (conditions de)','0','10000000','Travail (conditions de)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Infractions','0','10000000','Infractions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contentieux','0','10000000','Contentieux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Magistrats','0','10000000','Magistrats');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Services à la personne','0','10000000','Services à la personne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Jardins','0','10000000','Jardins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Coopératives d''utilisation de matériel agricole (CUMA)','0','10000000','Coopératives d''utilisation de matériel agricole (CUMA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fiscalité','0','10000000','Fiscalité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Grandes écoles','0','10000000','Grandes écoles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonction publique (traitements et indemnités)','0','10000000','Fonction publique (traitements et indemnités)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Israël','0','10000000','Israël');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Rapports et études','0','10000000','Rapports et études');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nord-Pas-de-Calais','0','10000000','Nord-Pas-de-Calais');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Produits agricoles et alimentaires','0','10000000','Produits agricoles et alimentaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Collectivités locales','0','10000000','Collectivités locales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Communication','0','10000000','Communication');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Papiers d''identité','0','10000000','Papiers d''identité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Permis de conduire','0','10000000','Permis de conduire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité routière','0','10000000','Sécurité routière');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Emploi (contrats aidés)','0','10000000','Emploi (contrats aidés)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement privé','0','10000000','Enseignement privé');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travailleurs sociaux','0','10000000','Travailleurs sociaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Entreprises','0','10000000','Entreprises');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Céréales','0','10000000','Céréales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Politique agricole commune (PAC)','0','10000000','Politique agricole commune (PAC)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Importations exportations','0','10000000','Importations exportations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Santé publique','0','10000000','Santé publique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Information des citoyens','0','10000000','Information des citoyens');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enfants','0','10000000','Enfants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fraudes et contrefaçons','0','10000000','Fraudes et contrefaçons');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Directives et réglementations européennes','0','10000000','Directives et réglementations européennes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Décrets et arrêtés','0','10000000','Décrets et arrêtés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonctionnaires et agents publics','0','10000000','Fonctionnaires et agents publics');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Communes','0','10000000','Communes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Services publics','0','10000000','Services publics');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Quotient familial','0','10000000','Quotient familial');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Étrangers','0','10000000','Étrangers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Surveillants','0','10000000','Surveillants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Produits toxiques','0','10000000','Produits toxiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Formation professionnelle','0','10000000','Formation professionnelle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonction publique territoriale','0','10000000','Fonction publique territoriale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Consommateur (protection du)','0','10000000','Consommateur (protection du)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Associations','0','10000000','Associations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Subventions','0','10000000','Subventions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Femmes','0','10000000','Femmes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Déchets','0','10000000','Déchets');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pêche maritime','0','10000000','Pêche maritime');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lycées','0','10000000','Lycées');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Retraites complémentaires','0','10000000','Retraites complémentaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Banques et établissements financiers','0','10000000','Banques et établissements financiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Technologie','0','10000000','Technologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Égalité des sexes et parité','0','10000000','Égalité des sexes et parité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Grenelle de l''environnement','0','10000000','Grenelle de l''environnement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Impôt sur le revenu','0','10000000','Impôt sur le revenu');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Loi (application de la)','0','10000000','Loi (application de la)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aluminium','0','10000000','Aluminium');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité sociale (prestations)','0','10000000','Sécurité sociale (prestations)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Carte Vitale','0','10000000','Carte Vitale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Question de rappel','0','10000000','Question de rappel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Police (personnel de)','0','10000000','Police (personnel de)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignants','0','10000000','Enseignants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Musique','0','10000000','Musique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ciment','0','10000000','Ciment');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Environnement','0','10000000','Environnement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Algérie','0','10000000','Algérie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lois de finances','0','10000000','Lois de finances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Presse','0','10000000','Presse');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tarif','0','10000000','Tarif');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Divorce','0','10000000','Divorce');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commémorations','0','10000000','Commémorations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Examens médicaux','0','10000000','Examens médicaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité','0','10000000','Sécurité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cycles et motocycles','0','10000000','Cycles et motocycles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Office national des forêts (ONF)','0','10000000','Office national des forêts (ONF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Veufs et veuves','0','10000000','Veufs et veuves');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Projets ou propositions de loi','0','10000000','Projets ou propositions de loi');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Expropriation','0','10000000','Expropriation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agences de bassin','0','10000000','Agences de bassin');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Montagne','0','10000000','Montagne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pisciculture','0','10000000','Pisciculture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Publications officielles','0','10000000','Publications officielles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Handicapés','0','10000000','Handicapés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('France Télécom','0','10000000','France Télécom');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Administration','0','10000000','Administration');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Parlement','0','10000000','Parlement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Architectes','0','10000000','Architectes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dotation de solidarité rurale (DSR)','0','10000000','Dotation de solidarité rurale (DSR)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Traités et conventions','0','10000000','Traités et conventions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Énergies nouvelles','0','10000000','Énergies nouvelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Partis politiques','0','10000000','Partis politiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Normes, marques et labels','0','10000000','Normes, marques et labels');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cour des comptes','0','10000000','Cour des comptes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Diabète','0','10000000','Diabète');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Recherche et innovation','0','10000000','Recherche et innovation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Guerres et conflits','0','10000000','Guerres et conflits');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Musées','0','10000000','Musées');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Codes et codification','0','10000000','Codes et codification');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hôpitaux (personnel des)','0','10000000','Hôpitaux (personnel des)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mer et littoral','0','10000000','Mer et littoral');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Viande','0','10000000','Viande');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Politique étrangère','0','10000000','Politique étrangère');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Filiation','0','10000000','Filiation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Procédure pénale','0','10000000','Procédure pénale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Euro','0','10000000','Euro');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Usagers','0','10000000','Usagers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dons et legs','0','10000000','Dons et legs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Naturalisation','0','10000000','Naturalisation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commerce et artisanat','0','10000000','Commerce et artisanat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élevage','0','10000000','Élevage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Urbanisme','0','10000000','Urbanisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Équipements collectifs','0','10000000','Équipements collectifs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Domaine public','0','10000000','Domaine public');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taxe sur la valeur ajoutée (TVA)','0','10000000','Taxe sur la valeur ajoutée (TVA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Médicaments','0','10000000','Médicaments');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ile-de-France','0','10000000','Ile-de-France');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gîtes ruraux','0','10000000','Gîtes ruraux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Syrie','0','10000000','Syrie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Budget','0','10000000','Budget');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Psychiatrie','0','10000000','Psychiatrie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Caisse nationale d''assurance vieillesse des travailleurs salariés (CNAVTS)','0','10000000','Caisse nationale d''assurance vieillesse des travailleurs salariés (CNAVTS)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ophtalmologie','0','10000000','Ophtalmologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Inondations','0','10000000','Inondations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cadastre','0','10000000','Cadastre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Harkis','0','10000000','Harkis');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aide à domicile','0','10000000','Aide à domicile');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Picardie','0','10000000','Picardie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prévention','0','10000000','Prévention');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Listes électorales','0','10000000','Listes électorales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fichiers','0','10000000','Fichiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports fluviaux','0','10000000','Transports fluviaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Guyane','0','10000000','Guyane');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Liban','0','10000000','Liban');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bois et forêts','0','10000000','Bois et forêts');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Soins à domicile','0','10000000','Soins à domicile');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Téléphone','0','10000000','Téléphone');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Entreprises publiques','0','10000000','Entreprises publiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cancer','0','10000000','Cancer');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Carte sanitaire','0','10000000','Carte sanitaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Crédits','0','10000000','Crédits');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Établissements sanitaires et sociaux','0','10000000','Établissements sanitaires et sociaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Oeuvres d''art','0','10000000','Oeuvres d''art');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Meublés et garnis','0','10000000','Meublés et garnis');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonds structurels','0','10000000','Fonds structurels');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Artisanat','0','10000000','Artisanat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bovins','0','10000000','Bovins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maladies du bétail','0','10000000','Maladies du bétail');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sous-traitance','0','10000000','Sous-traitance');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Accidents','0','10000000','Accidents');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Concurrence','0','10000000','Concurrence');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Plateau continental','0','10000000','Plateau continental');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Livres','0','10000000','Livres');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Régies municipales','0','10000000','Régies municipales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Redevances','0','10000000','Redevances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commissions','0','10000000','Commissions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pensions de retraite','0','10000000','Pensions de retraite');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sciences naturelles','0','10000000','Sciences naturelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement secondaire','0','10000000','Enseignement secondaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Calendrier scolaire','0','10000000','Calendrier scolaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aide aux pays en voie de développement','0','10000000','Aide aux pays en voie de développement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Universités','0','10000000','Universités');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Carte d''identité','0','10000000','Carte d''identité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éducation physique et sportive (EPS)','0','10000000','Éducation physique et sportive (EPS)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Zones défavorisées','0','10000000','Zones défavorisées');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonds social européen','0','10000000','Fonds social européen');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cessation d''activité','0','10000000','Cessation d''activité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Restaurants','0','10000000','Restaurants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Précarité','0','10000000','Précarité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Imagerie médicale','0','10000000','Imagerie médicale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Code de la route','0','10000000','Code de la route');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Brevets de technicien supérieur (BTS)','0','10000000','Brevets de technicien supérieur (BTS)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Factures','0','10000000','Factures');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Immigration','0','10000000','Immigration');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organisation mondiale du commerce (OMC)','0','10000000','Organisation mondiale du commerce (OMC)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Circulation routière','0','10000000','Circulation routière');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Brevet d''aptitude aux fonctions d''animateur (BAFA)','0','10000000','Brevet d''aptitude aux fonctions d''animateur (BAFA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Propriété littéraire, artistique et intellectuelle','0','10000000','Propriété littéraire, artistique et intellectuelle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sourds et sourds-muets','0','10000000','Sourds et sourds-muets');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Légitime défense','0','10000000','Légitime défense');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assemblée des Français de l''étranger','0','10000000','Assemblée des Français de l''étranger');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cérémonies publiques et fêtes légales','0','10000000','Cérémonies publiques et fêtes légales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections sénatoriales','0','10000000','Élections sénatoriales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Diététique','0','10000000','Diététique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mariage','0','10000000','Mariage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cadres','0','10000000','Cadres');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Terres agricoles','0','10000000','Terres agricoles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cartes bancaires et de crédit','0','10000000','Cartes bancaires et de crédit');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gaz de France (GDF)','0','10000000','Gaz de France (GDF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Épidémies','0','10000000','Épidémies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Compétitions','0','10000000','Compétitions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Débats et conférences','0','10000000','Débats et conférences');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aérodromes','0','10000000','Aérodromes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mort et décès','0','10000000','Mort et décès');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lorraine','0','10000000','Lorraine');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Parkings et garages','0','10000000','Parkings et garages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Livres et manuels scolaires','0','10000000','Livres et manuels scolaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Italie','0','10000000','Italie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Parcs naturels','0','10000000','Parcs naturels');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports internationaux','0','10000000','Transports internationaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organisations internationales','0','10000000','Organisations internationales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Seuils sociaux','0','10000000','Seuils sociaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Canada','0','10000000','Canada');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Économie (ministère de l'')','0','10000000','Économie (ministère de l'')');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tabagisme','0','10000000','Tabagisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prestations familiales','0','10000000','Prestations familiales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transports aériens','0','10000000','Transports aériens');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Coopératives','0','10000000','Coopératives');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Santé','0','10000000','Santé');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Obligation alimentaire','0','10000000','Obligation alimentaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prisons','0','10000000','Prisons');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Renseignements','0','10000000','Renseignements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Turquie','0','10000000','Turquie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Documents administratifs','0','10000000','Documents administratifs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignes et préenseignes','0','10000000','Enseignes et préenseignes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sans domicile fixe','0','10000000','Sans domicile fixe');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Industrie','0','10000000','Industrie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Andorre','0','10000000','Andorre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Retraites (financement des)','0','10000000','Retraites (financement des)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Risques technologiques','0','10000000','Risques technologiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pensions alimentaires','0','10000000','Pensions alimentaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Centres sociaux','0','10000000','Centres sociaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Zones franches','0','10000000','Zones franches');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Déneigement','0','10000000','Déneigement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Psychologie','0','10000000','Psychologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Yougoslavie','0','10000000','Yougoslavie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité sociale (contentieux)','0','10000000','Sécurité sociale (contentieux)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travail clandestin','0','10000000','Travail clandestin');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droit de séjour','0','10000000','Droit de séjour');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Baux de locaux d''habitation','0','10000000','Baux de locaux d''habitation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Riz','0','10000000','Riz');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Polynésie française','0','10000000','Polynésie française');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Manifestations et émeutes','0','10000000','Manifestations et émeutes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gardes','0','10000000','Gardes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Écoles primaires','0','10000000','Écoles primaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dermatologie','0','10000000','Dermatologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Indemnité de départ à la retraite','0','10000000','Indemnité de départ à la retraite');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Décrets','0','10000000','Décrets');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bourgogne','0','10000000','Bourgogne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Champagne','0','10000000','Champagne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement agricole','0','10000000','Enseignement agricole');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Naissances','0','10000000','Naissances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Abattoirs','0','10000000','Abattoirs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Plans d''occupation des sols (POS)','0','10000000','Plans d''occupation des sols (POS)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Méditerranée','0','10000000','Méditerranée');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hydrocarbures','0','10000000','Hydrocarbures');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chaussures','0','10000000','Chaussures');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prix agricoles','0','10000000','Prix agricoles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Disparitions','0','10000000','Disparitions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agences immobilières','0','10000000','Agences immobilières');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Allocations familiales','0','10000000','Allocations familiales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conférences internationales','0','10000000','Conférences internationales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Haras','0','10000000','Haras');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Échec scolaire','0','10000000','Échec scolaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Service civique','0','10000000','Service civique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Industries électriques','0','10000000','Industries électriques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sexualité','0','10000000','Sexualité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Caisses de retraite','0','10000000','Caisses de retraite');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ostréiculture','0','10000000','Ostréiculture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Russie','0','10000000','Russie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Masseurs et kinésithérapeutes','0','10000000','Masseurs et kinésithérapeutes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Abonnements','0','10000000','Abonnements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Formalités administratives','0','10000000','Formalités administratives');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Syndicats mixtes','0','10000000','Syndicats mixtes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Grèce','0','10000000','Grèce');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droit local','0','10000000','Droit local');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Limousin','0','10000000','Limousin');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maternité','0','10000000','Maternité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Location','0','10000000','Location');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Administrations financières','0','10000000','Administrations financières');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Antibiotiques','0','10000000','Antibiotiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conseils de prud''hommes','0','10000000','Conseils de prud''hommes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Plus-values (imposition des)','0','10000000','Plus-values (imposition des)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Établissements de soins','0','10000000','Établissements de soins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commerce extérieur','0','10000000','Commerce extérieur');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Corée du Sud','0','10000000','Corée du Sud');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Déconcentration administrative','0','10000000','Déconcentration administrative');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maladies de longue durée','0','10000000','Maladies de longue durée');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Frais de déplacement','0','10000000','Frais de déplacement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aide humanitaire','0','10000000','Aide humanitaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Outre-mer','0','10000000','Outre-mer');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Habitations à loyer modéré (HLM)','0','10000000','Habitations à loyer modéré (HLM)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commission nationale de l''informatique et des libertés (CNIL)','0','10000000','Commission nationale de l''informatique et des libertés (CNIL)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Revenu de solidarité active (RSA)','0','10000000','Revenu de solidarité active (RSA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organisations caritatives','0','10000000','Organisations caritatives');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Instituts universitaires de formation des maîtres (IUFM)','0','10000000','Instituts universitaires de formation des maîtres (IUFM)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Équipements sportifs et socio-éducatifs','0','10000000','Équipements sportifs et socio-éducatifs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cantines scolaires','0','10000000','Cantines scolaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Documentation','0','10000000','Documentation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Classes vertes','0','10000000','Classes vertes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Natation','0','10000000','Natation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cinéma et théâtre','0','10000000','Cinéma et théâtre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sites (protection des)','0','10000000','Sites (protection des)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bibliothèques et médiathèques','0','10000000','Bibliothèques et médiathèques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Logement (financement)','0','10000000','Logement (financement)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement artistique','0','10000000','Enseignement artistique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assistants familiaux, maternels et sociaux','0','10000000','Assistants familiaux, maternels et sociaux');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Économie','0','10000000','Économie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mobilier','0','10000000','Mobilier');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Stations d''épuration','0','10000000','Stations d''épuration');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aménagement rural','0','10000000','Aménagement rural');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Responsabilité civile','0','10000000','Responsabilité civile');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Rapatriés','0','10000000','Rapatriés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Météorologie','0','10000000','Météorologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Adoption','0','10000000','Adoption');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conchyliculture','0','10000000','Conchyliculture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Allemagne','0','10000000','Allemagne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sang et organes humains','0','10000000','Sang et organes humains');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fêtes','0','10000000','Fêtes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Crédit d''impôt-recherche','0','10000000','Crédit d''impôt-recherche');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Grêle','0','10000000','Grêle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Secourisme','0','10000000','Secourisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Moniteurs-éducateurs','0','10000000','Moniteurs-éducateurs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Caisses d''allocations familiales','0','10000000','Caisses d''allocations familiales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Prestations sociales','0','10000000','Prestations sociales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contentieux administratif','0','10000000','Contentieux administratif');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aide juridictionnelle','0','10000000','Aide juridictionnelle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Violence','0','10000000','Violence');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Référendum','0','10000000','Référendum');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Guadeloupe','0','10000000','Guadeloupe');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('État civil','0','10000000','État civil');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Alcootest','0','10000000','Alcootest');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aviation militaire','0','10000000','Aviation militaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Audiovisuel','0','10000000','Audiovisuel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Métiers','0','10000000','Métiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chèques-restaurant','0','10000000','Chèques-restaurant');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Quotas laitiers','0','10000000','Quotas laitiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Adjoints','0','10000000','Adjoints');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Résidences secondaires','0','10000000','Résidences secondaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Esclavage moderne','0','10000000','Esclavage moderne');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droit communautaire','0','10000000','Droit communautaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Venezuela','0','10000000','Venezuela');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Soudan','0','10000000','Soudan');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Archéologie','0','10000000','Archéologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Syndicats de communes','0','10000000','Syndicats de communes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Civisme','0','10000000','Civisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Huiles','0','10000000','Huiles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chiens','0','10000000','Chiens');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Optique','0','10000000','Optique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Marine marchande','0','10000000','Marine marchande');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Casernes','0','10000000','Casernes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Irak','0','10000000','Irak');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organisations professionnelles','0','10000000','Organisations professionnelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mutualité sociale agricole (MSA)','0','10000000','Mutualité sociale agricole (MSA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Professions libérales','0','10000000','Professions libérales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tribunaux administratifs','0','10000000','Tribunaux administratifs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Festivals','0','10000000','Festivals');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Architecture','0','10000000','Architecture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Garantie','0','10000000','Garantie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gérontologie et gériatrie','0','10000000','Gérontologie et gériatrie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Rentrée scolaire','0','10000000','Rentrée scolaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Homosexualité','0','10000000','Homosexualité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sous-préfectures','0','10000000','Sous-préfectures');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Mécénat','0','10000000','Mécénat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vols','0','10000000','Vols');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections législatives','0','10000000','Élections législatives');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Portugal','0','10000000','Portugal');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cautionnement','0','10000000','Cautionnement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Honorariat','0','10000000','Honorariat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Racisme et antisémitisme','0','10000000','Racisme et antisémitisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Palestine','0','10000000','Palestine');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Auto-écoles','0','10000000','Auto-écoles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éducation civique','0','10000000','Éducation civique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Radioactivité','0','10000000','Radioactivité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Épandage','0','10000000','Épandage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lecture','0','10000000','Lecture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Entreprises (création et transmission)','0','10000000','Entreprises (création et transmission)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Piétons','0','10000000','Piétons');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Décisions judiciaires','0','10000000','Décisions judiciaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Impôt de solidarité sur la fortune (ISF)','0','10000000','Impôt de solidarité sur la fortune (ISF)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Valeurs mobilières','0','10000000','Valeurs mobilières');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ordonnances','0','10000000','Ordonnances');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cidre','0','10000000','Cidre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assurance vie','0','10000000','Assurance vie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Arboriculture','0','10000000','Arboriculture');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Biologie médicale','0','10000000','Biologie médicale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éclairage','0','10000000','Éclairage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Zones économiques exclusives','0','10000000','Zones économiques exclusives');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('École nationale d''administration (ENA)','0','10000000','École nationale d''administration (ENA)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Recherche agronomique','0','10000000','Recherche agronomique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organismes génétiquement modifiés (OGM)','0','10000000','Organismes génétiquement modifiés (OGM)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Garages','0','10000000','Garages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droits d''enregistrement et de timbre','0','10000000','Droits d''enregistrement et de timbre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Scolarité','0','10000000','Scolarité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Saisies','0','10000000','Saisies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Temps partiel','0','10000000','Temps partiel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Électronique','0','10000000','Électronique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Emballages','0','10000000','Emballages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Génétique','0','10000000','Génétique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Protocole','0','10000000','Protocole');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Propriété','0','10000000','Propriété');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Langues régionales','0','10000000','Langues régionales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Classes de nature','0','10000000','Classes de nature');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aides au logement','0','10000000','Aides au logement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contrats de plan','0','10000000','Contrats de plan');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conventions collectives','0','10000000','Conventions collectives');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contribution de solidarité','0','10000000','Contribution de solidarité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Code pénal','0','10000000','Code pénal');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Maladies professionnelles','0','10000000','Maladies professionnelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Marchés financiers','0','10000000','Marchés financiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Frontaliers','0','10000000','Frontaliers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Questions parlementaires','0','10000000','Questions parlementaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gynécologie','0','10000000','Gynécologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Brocante','0','10000000','Brocante');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Marine','0','10000000','Marine');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Animateurs','0','10000000','Animateurs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Djibouti','0','10000000','Djibouti');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ostréiculteurs','0','10000000','Ostréiculteurs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement public','0','10000000','Enseignement public');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Produits chimiques','0','10000000','Produits chimiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('États-Unis','0','10000000','États-Unis');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taxe d''équipement','0','10000000','Taxe d''équipement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cour européenne des droits de l''homme','0','10000000','Cour européenne des droits de l''homme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Veuves','0','10000000','Veuves');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Services','0','10000000','Services');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éoliennes','0','10000000','Éoliennes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Médiation','0','10000000','Médiation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Enseignement technique et professionnel','0','10000000','Enseignement technique et professionnel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Amérique centrale','0','10000000','Amérique centrale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Institut national de la statistique et des études économiques (INSEE)','0','10000000','Institut national de la statistique et des études économiques (INSEE)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Parents d''élèves','0','10000000','Parents d''élèves');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bâtiments agricoles','0','10000000','Bâtiments agricoles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cardiologie','0','10000000','Cardiologie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Centrales nucléaires','0','10000000','Centrales nucléaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pensions de réversion','0','10000000','Pensions de réversion');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Exportations','0','10000000','Exportations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ski','0','10000000','Ski');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lotissements','0','10000000','Lotissements');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agence française pour la maîtrise de l''énergie (AFME)','0','10000000','Agence française pour la maîtrise de l''énergie (AFME)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Asie du Sud-Est','0','10000000','Asie du Sud-Est');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Défenseur des droits','0','10000000','Défenseur des droits');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aide alimentaire','0','10000000','Aide alimentaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Heures supplémentaires','0','10000000','Heures supplémentaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Transparence fiscale','0','10000000','Transparence fiscale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cartes électorales','0','10000000','Cartes électorales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commissaires-priseurs','0','10000000','Commissaires-priseurs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Énergie hydraulique','0','10000000','Énergie hydraulique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pneumatiques','0','10000000','Pneumatiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Lyon','0','10000000','Lyon');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contribution économique territoriale','0','10000000','Contribution économique territoriale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Organisation des Nations Unies (ONU)','0','10000000','Organisation des Nations Unies (ONU)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Arménie','0','10000000','Arménie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pompes funèbres','0','10000000','Pompes funèbres');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pilotes et pilotage','0','10000000','Pilotes et pilotage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contrôle médical','0','10000000','Contrôle médical');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Taxe d''apprentissage','0','10000000','Taxe d''apprentissage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cérémonies officielles','0','10000000','Cérémonies officielles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Non-voyants','0','10000000','Non-voyants');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Biotechnologies','0','10000000','Biotechnologies');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pensions de retraite militaire','0','10000000','Pensions de retraite militaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Invalides de guerre','0','10000000','Invalides de guerre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Gérance','0','10000000','Gérance');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Scrutin','0','10000000','Scrutin');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Carte d''invalidité','0','10000000','Carte d''invalidité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droits de la femme','0','10000000','Droits de la femme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sociétés anonymes','0','10000000','Sociétés anonymes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Faillite, règlement judiciaire et liquidation de biens','0','10000000','Faillite, règlement judiciaire et liquidation de biens');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aéronautique (militaire)','0','10000000','Aéronautique (militaire)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Espaces verts et paysages','0','10000000','Espaces verts et paysages');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pensions militaires d''invalidité','0','10000000','Pensions militaires d''invalidité');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Verreries','0','10000000','Verreries');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Escroqueries','0','10000000','Escroqueries');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Remembrement','0','10000000','Remembrement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chinois','0','10000000','Chinois');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Centre Georges Pompidou','0','10000000','Centre Georges Pompidou');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Libertés publiques','0','10000000','Libertés publiques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Avalanches','0','10000000','Avalanches');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Travailleurs saisonniers','0','10000000','Travailleurs saisonniers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droit de préemption','0','10000000','Droit de préemption');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sécurité maritime','0','10000000','Sécurité maritime');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Alcoolisme','0','10000000','Alcoolisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Engagés volontaires','0','10000000','Engagés volontaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Naissances (déclarations des)','0','10000000','Naissances (déclarations des)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Médecine','0','10000000','Médecine');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Administration (relations avec le public)','0','10000000','Administration (relations avec le public)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Afrique du Nord','0','10000000','Afrique du Nord');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Officiers','0','10000000','Officiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Exonérations','0','10000000','Exonérations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Changes','0','10000000','Changes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contrats de travail','0','10000000','Contrats de travail');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Couverture maladie universelle (CMU)','0','10000000','Couverture maladie universelle (CMU)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Logement temporaire','0','10000000','Logement temporaire');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Journalistes','0','10000000','Journalistes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Aéroports','0','10000000','Aéroports');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Crédit','0','10000000','Crédit');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Invalides','0','10000000','Invalides');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Australie','0','10000000','Australie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Paraguay','0','10000000','Paraguay');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Funérailles','0','10000000','Funérailles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Cour pénale internationale','0','10000000','Cour pénale internationale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Marchandises','0','10000000','Marchandises');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droits des victimes','0','10000000','Droits des victimes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Baux de locaux à usage professionnel','0','10000000','Baux de locaux à usage professionnel');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Comptes bancaires','0','10000000','Comptes bancaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Doctorat','0','10000000','Doctorat');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Népal','0','10000000','Népal');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Économie sociale','0','10000000','Économie sociale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Chantiers','0','10000000','Chantiers');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fruits et légumes','0','10000000','Fruits et légumes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Podologues','0','10000000','Podologues');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Débits de boisson et de tabac','0','10000000','Débits de boisson et de tabac');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Iles','0','10000000','Iles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droits de l''homme','0','10000000','Droits de l''homme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Terres abandonnées ou incultes','0','10000000','Terres abandonnées ou incultes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Huissiers de justice','0','10000000','Huissiers de justice');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pétitions','0','10000000','Pétitions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Passeports','0','10000000','Passeports');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Préfectures','0','10000000','Préfectures');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Stations-service','0','10000000','Stations-service');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Grandes surfaces','0','10000000','Grandes surfaces');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Tribunaux de commerce','0','10000000','Tribunaux de commerce');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Orchestres','0','10000000','Orchestres');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Véhicules','0','10000000','Véhicules');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Éthique','0','10000000','Éthique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conférences','0','10000000','Conférences');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Devises','0','10000000','Devises');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élections départementales','0','10000000','Élections départementales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Insectes','0','10000000','Insectes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Dimanches et jours fériés','0','10000000','Dimanches et jours fériés');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Vanuatu','0','10000000','Vanuatu');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Luxembourg','0','10000000','Luxembourg');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Assurances complémentaires','0','10000000','Assurances complémentaires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Centres régionaux des oeuvres universitaires et scolaires (CROUS)','0','10000000','Centres régionaux des oeuvres universitaires et scolaires (CROUS)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Procédure civile et commerciale','0','10000000','Procédure civile et commerciale');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Élèves','0','10000000','Élèves');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Absentéisme','0','10000000','Absentéisme');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Forains','0','10000000','Forains');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agronomie','0','10000000','Agronomie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Professions et activités paramédicales','0','10000000','Professions et activités paramédicales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Sucre','0','10000000','Sucre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Immatriculation','0','10000000','Immatriculation');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Espionnage','0','10000000','Espionnage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Associations culturelles','0','10000000','Associations culturelles');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Ascenseurs','0','10000000','Ascenseurs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Radios locales','0','10000000','Radios locales');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Conservatoires','0','10000000','Conservatoires');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Psychologues','0','10000000','Psychologues');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Radios libres','0','10000000','Radios libres');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Formation continue','0','10000000','Formation continue');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Hypothèques','0','10000000','Hypothèques');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Contraception','0','10000000','Contraception');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Locations','0','10000000','Locations');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Justice (organisation de la)','0','10000000','Justice (organisation de la)');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Rave-parties','0','10000000','Rave-parties');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Martinique','0','10000000','Martinique');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Fonds de péréquation de la taxe professionnelle','0','10000000','Fonds de péréquation de la taxe professionnelle');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Témoins','0','10000000','Témoins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Côte d''Azur','0','10000000','Côte d''Azur');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Agences de voyage','0','10000000','Agences de voyage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Versement transport','0','10000000','Versement transport');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Nouvelle-Calédonie','0','10000000','Nouvelle-Calédonie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Bourgs','0','10000000','Bourgs');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Papiers et papeteries','0','10000000','Papiers et papeteries');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Télétravail','0','10000000','Télétravail');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Caprins','0','10000000','Caprins');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Équarrissage','0','10000000','Équarrissage');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('La Réunion','0','10000000','La Réunion');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Pédiatrie','0','10000000','Pédiatrie');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Incarcération','0','10000000','Incarcération');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Commissions ad hoc','0','10000000','Commissions ad hoc');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Locaux professionnels','0','10000000','Locaux professionnels');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Occasions','0','10000000','Occasions');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Théâtre','0','10000000','Théâtre');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Droits d''auteur','0','10000000','Droits d''auteur');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Rénovation urbaine','0','10000000','Rénovation urbaine');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Experts','0','10000000','Experts');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Astreintes','0','10000000','Astreintes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Allocation logement','0','10000000','Allocation logement');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Langues anciennes','0','10000000','Langues anciennes');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Digues','0','10000000','Digues');



insert into VOC_SE_RENVOI ("id","obsolete","ordering","label") values ('Certificat d''aptitude professionnelle (CAP)','0','10000000','Certificat d''aptitude professionnelle (CAP)');



Insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('running_all','0','10000000','label.reponses.feuilleRoute.etape.running');



insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('ready_all','0','10000000','label.reponses.feuilleRoute.etape.ready');



insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_1','0','10000000','label.reponses.feuilleRoute.etape.valide.manuellement');



insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_2','0','10000000','label.reponses.feuilleRoute.etape.valide.refusValidation');



insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_3','0','10000000','label.reponses.feuilleRoute.etape.valide.automatiquement');



insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_4','0','10000000','label.reponses.feuilleRoute.etape.valide.nonConcerne');



Insert into VOC_TITLE ("id","obsolete","ordering","label") values ('Madame','0','10000000','Madame');



insert into VOC_TITLE ("id","obsolete","ordering","label") values ('Monsieur','0','10000000','Monsieur');



Insert into VOC_TYPE_QUESTION_AN ("id","obsolete","ordering","label") values ('QE','0','10000000','Question Ecrite');



insert into VOC_TYPE_QUESTION_AN ("id","obsolete","ordering","label") values ('QOSD','0','10000000','Question Orale Sans Débat');



insert into VOC_TYPE_QUESTION_AN ("id","obsolete","ordering","label") values ('QOAD','0','10000000','Question Orale Avec Débat');



insert into VOC_TYPE_QUESTION_AN ("id","obsolete","ordering","label") values ('QOAE','0','10000000','Question Orale Actualités Européenne');



insert into VOC_TYPE_QUESTION_AN ("id","obsolete","ordering","label") values ('QG','0','10000000','Question au Gouvernement');



insert into VOC_TYPE_QUESTION_AN ("id","obsolete","ordering","label") values ('QC','0','10000000','Question Crible');



Insert into VOC_TYPE_QUESTION_RECHERCHE ("id","obsolete","ordering","label") values ('QE','0','10000000','Questions Ecrites');



insert into VOC_TYPE_QUESTION_RECHERCHE ("id","obsolete","ordering","label") values ('QOSD','0','10000000','Questions Orales Sans Débat');



insert into VOC_TYPE_QUESTION_RECHERCHE ("id","obsolete","ordering","label") values ('QOAD','0','10000000','Questions Orales Avec Débat');



insert into VOC_TYPE_QUESTION_RECHERCHE ("id","obsolete","ordering","label") values ('QOAE','0','10000000','Questions Orales Actualités Européenne');



insert into VOC_TYPE_QUESTION_RECHERCHE ("id","obsolete","ordering","label") values ('QG','0','10000000','Questions au Gouvernement');



insert into VOC_TYPE_QUESTION_RECHERCHE ("id","obsolete","ordering","label") values ('QC','0','10000000','Questions Cribles');



Insert into VOC_TYPE_UNITE_STRUCTURELLE ("id","obsolete","ordering","label") values ('DIR','0','10000000','Direction');



insert into VOC_TYPE_UNITE_STRUCTURELLE ("id","obsolete","ordering","label") values ('UST','0','10000000','Autre');



Insert into VOC_VALIDATION_STATUT_ETAPE ("id","obsolete","ordering","label") values ('1','0','10000000','validée manuellement');



insert into VOC_VALIDATION_STATUT_ETAPE ("id","obsolete","ordering","label") values ('2','0','10000000','invalidée');



insert into VOC_VALIDATION_STATUT_ETAPE ("id","obsolete","ordering","label") values ('3','0','10000000','validée automatiquement');



insert into VOC_VALIDATION_STATUT_ETAPE ("id","obsolete","ordering","label") values ('4','0','10000000','non concernée');


--------------------------------------------------------
--  DDL for Index FULL_E17194F7_IDX
--------------------------------------------------------

  CREATE INDEX "FULL_IDQUEST_QA_IDX" ON "FULLTEXT" ("FULLTEXT_IDQUESTION") 
   INDEXTYPE IS "CTXSYS"."CONTEXT" PARAMETERS (' SYNC (ON COMMIT) TRANSACTIONAL');


--------------------------------------------------------
--  DDL for Index JENA_G1T0_REIFXO
--------------------------------------------------------

  CREATE INDEX "JENA_G1T0_REIFXO" ON "JENA_G1T0_REIF" ("OBJ") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_ACTIONNABLE_CASE_LINK_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_ACTIONNABLE_CASE_LINK_1" ON "ACTIONNABLE_CASE_LINK" ("STEPDOCUMENTID", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index FULL_406097D3_IDX
--------------------------------------------------------

  CREATE INDEX "FULL_SENATTITRE_QA_IDX" ON "FULLTEXT" ("FULLTEXT_SENATTITRE") 
   INDEXTYPE IS "CTXSYS"."CONTEXT" PARAMETERS ('LEXER REP_LEXER WORDLIST REP_WORDLIST SYNC (ON COMMIT) TRANSACTIONAL');


--------------------------------------------------------
--  DDL for Index IDX_CASE_LINK_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_CASE_LINK_2" ON "CASE_LINK" ("CASEDOCUMENTID", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index DR$FULL_80158323_IDX$X
--------------------------------------------------------

  CREATE INDEX "DR$FULL_80158323_IDX$X" ON "DR$FULL_80158323_IDX$I" ("TOKEN_TEXT", "TOKEN_TYPE", "TOKEN_FIRST", "TOKEN_LAST", "TOKEN_COUNT") 
  ;


--------------------------------------------------------
--  DDL for Index DOSSIER_REPONSE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DOSSIER_REPONSE_PK" ON "DOSSIER_REPONSE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index COMMON_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMMON_PK" ON "COMMON" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_13
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_13" ON "QUESTION" ("LEGISLATUREQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index CONTENT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "CONTENT_PK" ON "CONTENT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index VCARD_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VCARD_PK" ON "VCARD" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_J
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_J" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "JOB_NAME", "JOB_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index HIER_B0ED9092_IDX
--------------------------------------------------------

  CREATE INDEX "HIER_B0ED9092_IDX" ON "HIERARCHY" ("PARENTID", "NAME") 
  ;


--------------------------------------------------------
--  DDL for Index STEP_FOLDER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "STEP_FOLDER_PK" ON "STEP_FOLDER" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_NFT_ST
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_NFT_ST" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_STATE", "NEXT_FIRE_TIME") 
  ;


--------------------------------------------------------
--  DDL for Index TIMBRE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TIMBRE_PK" ON "TIMBRE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXA_AN_RUBRIQUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXA_AN_RUBRIQUE" ON "IXA_AN_RUBRIQUE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index ERRATUM#ANONYMOUSTYPE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ERRATUM#ANONYMOUSTYPE_PK" ON "ERRATUM#ANONYMOUSTYPE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index PRU_COLUMNS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "PRU_COLUMNS_ID_IDX" ON "PRU_COLUMNS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_19
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_19" ON "QUESTION" ("HASHCONNEXITETITRE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXA_SE_RENVOI
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXA_SE_RENVOI" ON "IXA_SE_RENVOI" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_22
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_22" ON "QUESTION" ("HASHCONNEXITESE") 
  ;


--------------------------------------------------------
--  DDL for Index CMDI_5E2F4527_IDX
--------------------------------------------------------

  CREATE INDEX "CMDI_5E2F4527_IDX" ON "CMDIST_INITIAL_COPY_I_D6588F7E" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index INFO_UTILISATEUR_CONNECTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "INFO_UTILISATEUR_CONNECTION_PK" ON "INFO_UTILISATEUR_CONNECTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index HIERARCHY_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "HIERARCHY_PK" ON "HIERARCHY" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index DR$FULLTEXT_FULLTEXT_IDX$X
--------------------------------------------------------

  CREATE INDEX "DR$FULLTEXT_FULLTEXT_IDX$X" ON "DR$FULLTEXT_FULLTEXT_IDX$I" ("TOKEN_TEXT", "TOKEN_TYPE", "TOKEN_FIRST", "TOKEN_LAST", "TOKEN_COUNT") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_PROXIES_1
--------------------------------------------------------

  CREATE INDEX "IDX_PROXIES_1" ON "PROXIES" ("ID", "TARGETID") 
  ;


--------------------------------------------------------
--  DDL for Index REQUETEMETADONNEES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REQUETEMETADONNEES_PK" ON "REQUETEMETADONNEES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_RTASK_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_RTASK_2" ON "ROUTING_TASK" ("ID", "DISTRIBUTIONMAILBOXID") 
  ;


--------------------------------------------------------
--  DDL for Index IXAC_F5D6E39A_IDX
--------------------------------------------------------

  CREATE INDEX "IXAC_F5D6E39A_IDX" ON "IXACOMP_SE_RUBRIQUE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_NXP_LOGS_1
--------------------------------------------------------

  CREATE INDEX "IDX_NXP_LOGS_1" ON "NXP_LOGS" ("LOG_DOC_UUID") 
  ;


--------------------------------------------------------
--  DDL for Index COMMENT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMMENT_PK" ON "COMMENT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G2T1_STMTXSP
--------------------------------------------------------

  CREATE INDEX "JENA_G2T1_STMTXSP" ON "JENA_G2T1_STMT" ("SUBJ", "PROP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_3
--------------------------------------------------------

  CREATE INDEX "IDX_DOSSIER_3" ON "DOSSIER_REPONSE" ("IDDOCUMENTQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_NXP_LOGS_2
--------------------------------------------------------

  CREATE INDEX "IDX_NXP_LOGS_2" ON "NXP_LOGS" ("LOG_EVENT_DATE") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_XO
--------------------------------------------------------

  CREATE INDEX "JENA_XO" ON "JENA_SYS_STMT" ("OBJ") 
  ;


--------------------------------------------------------
--  DDL for Index RQMET_ETATQUESTION_ID_IDX
--------------------------------------------------------

  CREATE INDEX "RQMET_ETATQUESTION_ID_IDX" ON "RQMET_ETATQUESTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ACLR_ACL_ID_IDX
--------------------------------------------------------

  CREATE INDEX "ACLR_ACL_ID_IDX" ON "ACLR" ("ACL_ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_24_DILA
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_24_DILA" ON "QUESTION" ("TYPEQUESTION", "DATEPUBLICATIONJO", "ID", "NUMEROQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_17
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_17" ON "QUESTION" ("ETATSIGNALE") 
  ;


--------------------------------------------------------
--  DDL for Index FSD_DC_NATURE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "FSD_DC_NATURE_ID_IDX" ON "FSD_DC_NATURE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ANCESTORS_HIERARCHY_ID_IDX
--------------------------------------------------------

  CREATE INDEX "ANCESTORS_HIERARCHY_ID_IDX" ON "ANCESTORS" ("HIERARCHY_ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_J_GRP
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_J_GRP" ON "QRTZ_JOB_DETAILS" ("SCHED_NAME", "JOB_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_JETON_MAITRE_3
--------------------------------------------------------

  CREATE INDEX "IDX_JETON_MAITRE_3" ON "JETON_MAITRE" ("TYPE_WEBSERVICE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXA_MOT_MINIST
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXA_MOT_MINIST" ON "IXA_MOTCLEF_MINISTERE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index JETON_DOC_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "JETON_DOC_PK" ON "JETON_DOC" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXACOMP_AN_RUBRIQUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXACOMP_AN_RUBRIQUE" ON "IXACOMP_AN_RUBRIQUE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index RQC__5653048E_IDX
--------------------------------------------------------

  CREATE INDEX "RQC__5653048E_IDX" ON "RQC_ORIGINEQUESTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FAVORISINDEXATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FAVORISINDEXATION_PK" ON "FAVORISINDEXATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index PUBLISH_SECTIONS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "PUBLISH_SECTIONS_ID_IDX" ON "PUBLISH_SECTIONS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index BIRTREPORT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "BIRTREPORT_PK" ON "BIRTREPORT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_5
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_QUESTION_5" ON "QUESTION" ("ORIGINEQUESTION", TO_CHAR("NUMEROQUESTION"), "ID") 
  ;


--------------------------------------------------------
--  DDL for Index DOSSIER_REPONSES_LINK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DOSSIER_REPONSES_LINK_PK" ON "DOSSIER_REPONSES_LINK" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXA_SE_RUBRIQUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXA_SE_RUBRIQUE" ON "IXA_SE_RUBRIQUE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index DEFAULTSETTINGS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DEFAULTSETTINGS_PK" ON "DEFAULTSETTINGS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_REPONSES_LINK_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOSSIER_REPONSES_LINK_1" ON "DOSSIER_REPONSES_LINK" ("IDMINISTEREATTRIBUTAIRE", "ID", "ROUTINGTASKTYPE") 
  ;


--------------------------------------------------------
--  DDL for Index REQUETECOMPLEXE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REQUETECOMPLEXE_PK" ON "REQUETECOMPLEXE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G3T0_REIFXO
--------------------------------------------------------

  CREATE INDEX "JENA_G3T0_REIFXO" ON "JENA_G3T0_REIF" ("OBJ") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G3T1_STMTXSP
--------------------------------------------------------

  CREATE INDEX "JENA_G3T1_STMTXSP" ON "JENA_G3T1_STMT" ("SUBJ", "PROP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_INFO_UTIL_CONNECT_USERNAME
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_INFO_UTIL_CONNECT_USERNAME" ON "INFO_UTILISATEUR_CONNECTION" ("USERNAME", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index ROUING_TASK_ROUTE_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "ROUING_TASK_ROUTE_ID" ON "ROUTING_TASK" ("DOCUMENTROUTEID", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index PARTICIPANTLIST_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PARTICIPANTLIST_PK" ON "PARTICIPANTLIST" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index CASE_LINK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "CASE_LINK_PK" ON "CASE_LINK" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JETON_MAITRE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "JETON_MAITRE_PK" ON "JETON_MAITRE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_THEME_1
--------------------------------------------------------

  CREATE INDEX "IDX_THEME_1" ON "LOCALTHEMECONFIG" ("DOCID") 
  ;


--------------------------------------------------------
--  DDL for Index MLBX_GROUPS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "MLBX_GROUPS_ID_IDX" ON "MLBX_GROUPS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ETAT_APPLICATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ETAT_APPLICATION_PK" ON "ETAT_APPLICATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index UITY_251B91EA_IDX
--------------------------------------------------------

  CREATE INDEX "UITY_251B91EA_IDX" ON "UITYPESCONF_ALLOWEDTYPES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index DR$FULL_E17194F7_IDX$X
--------------------------------------------------------

  CREATE INDEX "DR$FULL_E17194F7_IDX$X" ON "DR$FULL_E17194F7_IDX$I" ("TOKEN_TEXT", "TOKEN_TYPE", "TOKEN_FIRST", "TOKEN_LAST", "TOKEN_COUNT") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXACOMP_MOT_MINIST
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXACOMP_MOT_MINIST" ON "IXACOMP_MOTCLEF_MINISTERE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index REPONSES_LOGGING_LINE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REPONSES_LOGGING_LINE_PK" ON "REPONSES_LOGGING_LINE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index HISTORIQUEATTRIBUTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "HISTORIQUEATTRIBUTION_PK" ON "HISTORIQUEATTRIBUTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_HIER_4
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_HIER_4" ON "HIERARCHY" ("ID", "PRIMARYTYPE") 
  ;


--------------------------------------------------------
--  DDL for Index PROXIES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PROXIES_PK" ON "PROXIES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index REQUETETEXTEINTEGRAL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REQUETETEXTEINTEGRAL_PK" ON "REQUETETEXTEINTEGRAL" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_FT_J_G
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_FT_J_G" ON "QRTZ_FIRED_TRIGGERS" ("SCHED_NAME", "JOB_NAME", "JOB_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_FT_INST_JOB_REQ_RCVRY
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_FT_INST_JOB_REQ_RCVRY" ON "QRTZ_FIRED_TRIGGERS" ("SCHED_NAME", "INSTANCE_NAME", "REQUESTS_RECOVERY") 
  ;


--------------------------------------------------------
--  DDL for Index CLUSTER_INVALS_NODEID_IDX
--------------------------------------------------------

  CREATE INDEX "CLUSTER_INVALS_NODEID_IDX" ON "CLUSTER_INVALS" ("NODEID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_RTASK_3
--------------------------------------------------------

  CREATE INDEX "IDX_RTASK_3" ON "ROUTING_TASK" ("DATEFINETAPE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_SW_ACLR_USER_ACLID_UG
--------------------------------------------------------

  CREATE INDEX "IDX_SW_ACLR_USER_ACLID_UG" ON "SW_ACLR_USER_ACLID" ("USERGROUP", "ACL_ID") 
  ;


--------------------------------------------------------
--  DDL for Index RELATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "RELATION_PK" ON "RELATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index HIERARCHY_READ_ACL_ACL_ID_IDX
--------------------------------------------------------

  CREATE INDEX "HIERARCHY_READ_ACL_ACL_ID_IDX" ON "HIERARCHY_READ_ACL" ("ACL_ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXA_TA_RUBRIQUE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXA_TA_RUBRIQUE_ID_IDX" ON "IXA_TA_RUBRIQUE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index MISC_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MISC_PK" ON "MISC" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index REPLOG_LINES_ID_IDX
--------------------------------------------------------

  CREATE INDEX "REPLOG_LINES_ID_IDX" ON "REPLOG_LINES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index MAIL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MAIL_PK" ON "MAIL" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXA__04FA75C2_IDX
--------------------------------------------------------

  CREATE INDEX "IXA__04FA75C2_IDX" ON "IXA_MOTCLEF_MINISTERE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXACOMP_SE_RENVOI_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXACOMP_SE_RENVOI_ID_IDX" ON "IXACOMP_SE_RENVOI" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_JETON_DOC_2
--------------------------------------------------------

  CREATE INDEX "IDX_JETON_DOC_2" ON "JETON_DOC" ("ID_DOC") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G2T1_STMTXO
--------------------------------------------------------

  CREATE INDEX "JENA_G2T1_STMTXO" ON "JENA_G2T1_STMT" ("OBJ") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_21
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_21" ON "QUESTION" ("HASHCONNEXITEAN") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_10
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_10" ON "QUESTION" ("HASREPONSEINITIEE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOS_REP_LINK_MIN_TASK_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOS_REP_LINK_MIN_TASK_ID" ON "DOSSIER_REPONSES_LINK" ("IDMINISTEREATTRIBUTAIRE", "ROUTINGTASKTYPE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_XURI
--------------------------------------------------------

  CREATE UNIQUE INDEX "JENA_XURI" ON "JENA_LONG_URI" ("HEAD", "CHKSUM") 
  ;


--------------------------------------------------------
--  DDL for Index REPONSES_LOGGING_DETAIL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REPONSES_LOGGING_DETAIL_PK" ON "REPONSES_LOGGING_DETAIL" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index DEL_PROFILLIST_ID_IDX
--------------------------------------------------------

  CREATE INDEX "DEL_PROFILLIST_ID_IDX" ON "DEL_PROFILLIST" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXACOMP_AN_ANALYSE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXACOMP_AN_ANALYSE" ON "IXACOMP_AN_ANALYSE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index NAV_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "NAV_PK" ON "NAV" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXA_TA_RUBRIQUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXA_TA_RUBRIQUE" ON "IXA_TA_RUBRIQUE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_8_DILA
--------------------------------------------------------

  CREATE INDEX "IDX_DOSSIER_8_DILA" ON "DOSSIER_REPONSE" ("ETAPEREDACTIONATTEINTE", "IDDOCUMENTQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_HIER_6
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_HIER_6" ON "HIERARCHY" ("ID", "ISVERSION") 
  ;


--------------------------------------------------------
--  DDL for Index QUERYNAV_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "QUERYNAV_PK" ON "QUERYNAV" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index CMDI_FA613CD3_IDX
--------------------------------------------------------

  CREATE INDEX "CMDI_FA613CD3_IDX" ON "CMDIST_ALL_COPY_PARTI_21AB3C5B" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_INFO_UTIL_CONNECT_ISLOGOUT
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_INFO_UTIL_CONNECT_ISLOGOUT" ON "INFO_UTILISATEUR_CONNECTION" ("ISLOGOUT", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_HIER_2
--------------------------------------------------------

  CREATE INDEX "IDX_HIER_2" ON "HIERARCHY" ("PARENTID", "ISPROPERTY") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_RTASK_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_RTASK_1" ON "ROUTING_TASK" ("ID", "DATEFINETAPE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_RTASK_4
--------------------------------------------------------

  CREATE INDEX "IDX_RTASK_4" ON "ROUTING_TASK" ("DISTRIBUTIONMAILBOXID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_ACLS_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_ACLS_1" ON "ACLS" ("ID", "POS", "PERMISSION", "USER", "GRANT", "NAME", "GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index LISTEELIMINATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "LISTEELIMINATION_PK" ON "LISTEELIMINATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index INFO_COMMENTS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "INFO_COMMENTS_PK" ON "INFO_COMMENTS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index BIRTREPORTMODEL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "BIRTREPORTMODEL_PK" ON "BIRTREPORTMODEL" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FILE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FILE_PK" ON "FILE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_QUESTION_2" ON "QUESTION" ("GROUPEPOLITIQUE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index CMDI_F03AFF82_IDX
--------------------------------------------------------

  CREATE INDEX "CMDI_F03AFF82_IDX" ON "CMDIST_ALL_ACTION_PAR_6B4BBED8" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index LOCK_JETON_MAITRE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "LOCK_JETON_MAITRE_PK" ON "LOCK_JETON_MAITRE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXA_SE_RENVOI_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXA_SE_RENVOI_ID_IDX" ON "IXA_SE_RENVOI" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_RTASK_5
--------------------------------------------------------

  CREATE INDEX "IDX_RTASK_5" ON "ROUTING_TASK" ("DOCUMENTROUTEID") 
  ;


--------------------------------------------------------
--  DDL for Index CMDI_1093196C_IDX
--------------------------------------------------------

  CREATE INDEX "CMDI_1093196C_IDX" ON "CMDIST_INITIAL_ACTION_88A481B7" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index DUBLINCORE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DUBLINCORE_PK" ON "DUBLINCORE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index WSNOTIFICATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "WSNOTIFICATION_PK" ON "WSNOTIFICATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index NAVIGATIONSETTINGS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "NAVIGATIONSETTINGS_PK" ON "NAVIGATIONSETTINGS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G1T0_REIFXSP
--------------------------------------------------------

  CREATE INDEX "JENA_G1T0_REIFXSP" ON "JENA_G1T0_REIF" ("SUBJ", "PROP") 
  ;


--------------------------------------------------------
--  DDL for Index ELEMENTFONDDOSSIER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ELEMENTFONDDOSSIER_PK" ON "ELEMENTFONDDOSSIER" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_3
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_QUESTION_3" ON "QUESTION" (LOWER("NOMAUTEUR"), "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_18
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_18" ON "QUESTION" ("ETATRENOUVELE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXACOMP_SE_RENVOI
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXACOMP_SE_RENVOI" ON "IXACOMP_SE_RENVOI" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_NFT_ST_MISFIRE
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_NFT_ST_MISFIRE" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "MISFIRE_INSTR", "NEXT_FIRE_TIME", "TRIGGER_STATE") 
  ;


--------------------------------------------------------
--  DDL for Index FSD_DC_COVERAGE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "FSD_DC_COVERAGE_ID_IDX" ON "FSD_DC_COVERAGE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_SW_ACLR_USERID_USER_USE2
--------------------------------------------------------

  CREATE INDEX "IDX_SW_ACLR_USERID_USER_USE2" ON "SW_ACLR_USERID_USER" ("USERGROUP", "USER_ID") 
  ;


--------------------------------------------------------
--  DDL for Index VERSIONS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VERSIONS_PK" ON "VERSIONS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index USERSETTINGS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "USERSETTINGS_PK" ON "USERSETTINGS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index LOCKS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "LOCKS_PK" ON "LOCKS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ACLS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "ACLS_ID_IDX" ON "ACLS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_N_STATE
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_N_STATE" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP", "TRIGGER_STATE") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G2T0_REIFXO
--------------------------------------------------------

  CREATE INDEX "JENA_G2T0_REIFXO" ON "JENA_G2T0_REIF" ("OBJ") 
  ;


--------------------------------------------------------
--  DDL for Index SEAR_BEE8E2A1_IDX
--------------------------------------------------------

  CREATE INDEX "SEAR_BEE8E2A1_IDX" ON "SEARCH_CURRENTLIFECYCLESTATES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXACOMP_SE_THEME_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXACOMP_SE_THEME_ID_IDX" ON "IXACOMP_SE_THEME" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_COMMENT_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_COMMENT_1" ON "COMMENT" ("COMMENTEDDOCID", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_FULLTEXT_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_FULLTEXT_1" ON "FULLTEXT" ("JOBID", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G3T0_REIFXSP
--------------------------------------------------------

  CREATE INDEX "JENA_G3T0_REIFXSP" ON "JENA_G3T0_REIF" ("SUBJ", "PROP") 
  ;


--------------------------------------------------------
--  DDL for Index UITY_E32F2D57_IDX
--------------------------------------------------------

  CREATE INDEX "UITY_E32F2D57_IDX" ON "UITYPESCONF_DENIEDTYPES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_CMDITINITACTION_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_CMDITINITACTION_1" ON "CMDIST_INITIAL_ACTION_4CD43708" ("ID", "ITEM") 
  ;


--------------------------------------------------------
--  DDL for Index IXA_SE_THEME_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXA_SE_THEME_ID_IDX" ON "IXA_SE_THEME" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_JETON_MAITRE_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_JETON_MAITRE_2" ON "JETON_MAITRE" ("NUMERO_JETON", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index SMART_FOLDER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SMART_FOLDER_PK" ON "SMART_FOLDER" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_11
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_11" ON "QUESTION" ("DATEPUBLICATIONJO") 
  ;


--------------------------------------------------------
--  DDL for Index SEARCH_NATURE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "SEARCH_NATURE_ID_IDX" ON "SEARCH_NATURE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_NXP_LOGS_3
--------------------------------------------------------

  CREATE INDEX "IDX_NXP_LOGS_3" ON "NXP_LOGS" ("LOG_DOC_PATH", "LOG_ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_HIER_3
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_HIER_3" ON "HIERARCHY" ("PARENTID", "PRIMARYTYPE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G2T0_REIFXSP
--------------------------------------------------------

  CREATE INDEX "JENA_G2T0_REIFXSP" ON "JENA_G2T0_REIF" ("SUBJ", "PROP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_HIER_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_HIER_1" ON "HIERARCHY" ("PRIMARYTYPE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_CMDITINITACTION_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_CMDITINITACTION_2" ON "CMDIST_INITIAL_ACTION_4CD43708" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index FAVORISDOSSIER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FAVORISDOSSIER_PK" ON "FAVORISDOSSIER" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index RMLB_9A214F9D_IDX
--------------------------------------------------------

  CREATE INDEX "RMLB_9A214F9D_IDX" ON "RMLBX_PRECALCULLIST" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index RENOUVELLEMENTQUESTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "RENOUVELLEMENTQUESTION_PK" ON "RENOUVELLEMENTQUESTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXACOMP_AN_ANALYSE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXACOMP_AN_ANALYSE_ID_IDX" ON "IXACOMP_AN_ANALYSE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ANCESTORS_ANCESTORS_IDX
--------------------------------------------------------

  CREATE INDEX "ANCESTORS_ANCESTORS_IDX" ON "ANCESTORS_ANCESTORS" ("NESTED_TABLE_ID", "COLUMN_VALUE") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G2T0_REIF_IXSTMT
--------------------------------------------------------

  CREATE UNIQUE INDEX "JENA_G2T0_REIF_IXSTMT" ON "JENA_G2T0_REIF" ("STMT", "HASTYPE") 
  ;


--------------------------------------------------------
--  DDL for Index FSD_DC_SUBJECTS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "FSD_DC_SUBJECTS_ID_IDX" ON "FSD_DC_SUBJECTS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index REPLOGL_FULLLOG_ID_IDX
--------------------------------------------------------

  CREATE INDEX "REPLOGL_FULLLOG_ID_IDX" ON "REPLOGL_FULLLOG" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G1T1_STMTXSP
--------------------------------------------------------

  CREATE INDEX "JENA_G1T1_STMTXSP" ON "JENA_G1T1_STMT" ("SUBJ", "PROP") 
  ;


--------------------------------------------------------
--  DDL for Index PROTOCOL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PROTOCOL_PK" ON "PROTOCOL" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_G
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_G" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_6
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_QUESTION_6" ON "QUESTION" ("INTITULEMINISTERE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXAC_F6EA5AC3_IDX
--------------------------------------------------------

  CREATE INDEX "IXAC_F6EA5AC3_IDX" ON "IXACOMP_MOTCLEF_MINISTERE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FULLTEXT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FULLTEXT_PK" ON "FULLTEXT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_FEUILLE_ROUTE_1
--------------------------------------------------------

  CREATE INDEX "IDX_FEUILLE_ROUTE_1" ON "FEUILLE_ROUTE" ("MINISTERE") 
  ;


--------------------------------------------------------
--  DDL for Index UI_TYPES_CONFIGURATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "UI_TYPES_CONFIGURATION_PK" ON "UI_TYPES_CONFIGURATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index PROXIES_VERSIONABLEID_IDX
--------------------------------------------------------

  CREATE INDEX "PROXIES_VERSIONABLEID_IDX" ON "PROXIES" ("VERSIONABLEID") 
  ;


--------------------------------------------------------
--  DDL for Index INDEXATION_COMP_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "INDEXATION_COMP_PK" ON "INDEXATION_COMP" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index SEARCH_SEARCHPATH_ID_IDX
--------------------------------------------------------

  CREATE INDEX "SEARCH_SEARCHPATH_ID_IDX" ON "SEARCH_SEARCHPATH" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DELEGATION_3
--------------------------------------------------------

  CREATE INDEX "IDX_DELEGATION_3" ON "DELEGATION" ("DATEFIN") 
  ;


--------------------------------------------------------
--  DDL for Index ROUTING_TASK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ROUTING_TASK_PK" ON "ROUTING_TASK" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_MLBX_PRECALC_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_MLBX_PRECALC_1" ON "RMLBX_PRECALCULLIST" ("ID", "ITEM", "POS") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_4
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_QUESTION_4" ON "QUESTION" (LOWER("PRENOMAUTEUR"), "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_NFT_ST_MISFIRE_GRP
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_NFT_ST_MISFIRE_GRP" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "MISFIRE_INSTR", "NEXT_FIRE_TIME", "TRIGGER_GROUP", "TRIGGER_STATE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_ACLRMODIFIED_1
--------------------------------------------------------

  CREATE INDEX "IDX_ACLRMODIFIED_1" ON "ACLR_MODIFIED" ("HIERARCHY_ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_HIER_ACL_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_HIER_ACL_1" ON "HIERARCHY_READ_ACL" ("ID", "ACL_ID") 
  ;


--------------------------------------------------------
--  DDL for Index REQUETEFEUILLEROUTE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REQUETEFEUILLEROUTE_PK" ON "REQUETEFEUILLEROUTE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FONDDOSSIER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FONDDOSSIER_PK" ON "FONDDOSSIER" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index MAIL_RECIPIENTS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "MAIL_RECIPIENTS_ID_IDX" ON "MAIL_RECIPIENTS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_15
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_15" ON "QUESTION" ("NUMEROQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index TAG_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TAG_PK" ON "TAG" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOSSIER_1" ON "DOSSIER_REPONSE" ("MINISTEREATTRIBUTAIRECOURANT", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index FULL_80158323_IDX
--------------------------------------------------------

  CREATE INDEX "FULL_TXTREP_QA_IDX" ON "FULLTEXT" ("FULLTEXT_TXTREPONSE") 
   INDEXTYPE IS "CTXSYS"."CONTEXT" PARAMETERS ('LEXER REP_LEXER WORDLIST REP_WORDLIST SYNC (ON COMMIT) TRANSACTIONAL');


--------------------------------------------------------
--  DDL for Index FEUILLE_ROUTE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FEUILLE_ROUTE_PK" ON "FEUILLE_ROUTE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index HIERARCHY_PARENTID_IDX
--------------------------------------------------------

  CREATE INDEX "HIERARCHY_PARENTID_IDX" ON "HIERARCHY" ("PARENTID") 
  ;


--------------------------------------------------------
--  DDL for Index REPLOGD_FULLLOG_ID_IDX
--------------------------------------------------------

  CREATE INDEX "REPLOGD_FULLLOG_ID_IDX" ON "REPLOGD_FULLLOG" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index CVD__F112AB18_IDX
--------------------------------------------------------

  CREATE INDEX "CVD__F112AB18_IDX" ON "CVD_SELECTEDLAYOUTCOLUMNS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXACOMP_SE_THEME
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXACOMP_SE_THEME" ON "IXACOMP_SE_THEME" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index DR$FULL_989918B1_IDX$X
--------------------------------------------------------

  CREATE INDEX "DR$FULL_989918B1_IDX$X" ON "DR$FULL_989918B1_IDX$I" ("TOKEN_TEXT", "TOKEN_TYPE", "TOKEN_FIRST", "TOKEN_LAST", "TOKEN_COUNT") 
  ;


--------------------------------------------------------
--  DDL for Index CASE_DOCUMENTSID_ID_IDX
--------------------------------------------------------

  CREATE INDEX "CASE_DOCUMENTSID_ID_IDX" ON "CASE_DOCUMENTSID" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index CLAS_14028D7C_IDX
--------------------------------------------------------

  CREATE INDEX "CLAS_14028D7C_IDX" ON "CLASSIFICATION_TARGETS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FULL_989918B1_IDX
--------------------------------------------------------

  CREATE INDEX "FULL_TXTQUEST_QA_IDX" ON "FULLTEXT" ("FULLTEXT_TXTQUESTION") 
   INDEXTYPE IS "CTXSYS"."CONTEXT" PARAMETERS ('LEXER REP_LEXER WORDLIST REP_WORDLIST SYNC (ON COMMIT) TRANSACTIONAL');


--------------------------------------------------------
--  DDL for Index INDEXATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "INDEXATION_PK" ON "INDEXATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FULLTEXT_FULLTEXT_IDX
--------------------------------------------------------

  CREATE INDEX "FULL_FULLTEXT_QA_IDX" ON "FULLTEXT" ("FULLTEXT") 
   INDEXTYPE IS "CTXSYS"."CONTEXT" PARAMETERS (' SYNC (ON COMMIT) TRANSACTIONAL');


--------------------------------------------------------
--  DDL for Index ALLOTISSEMENT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ALLOTISSEMENT_PK" ON "ALLOTISSEMENT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index DOCR_C6FD7970_IDX
--------------------------------------------------------

  CREATE INDEX "DOCR_C6FD7970_IDX" ON "DOCRI_PARTICIPATINGDOCUMENTS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_23
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_23" ON "QUESTION" ("NUMEROQUESTION", "LEGISLATUREQUESTION", "ORIGINEQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index ADVANCED_SEARCH_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADVANCED_SEARCH_PK" ON "ADVANCED_SEARCH" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_STATE
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_STATE" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_STATE") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G1T1_STMTXO
--------------------------------------------------------

  CREATE INDEX "JENA_G1T1_STMTXO" ON "JENA_G1T1_STMT" ("OBJ") 
  ;


--------------------------------------------------------
--  DDL for Index RELATEDTEXTRESOURCE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "RELATEDTEXTRESOURCE_PK" ON "RELATEDTEXTRESOURCE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ACTIONNABLE_CASE_LINK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ACTIONNABLE_CASE_LINK_PK" ON "ACTIONNABLE_CASE_LINK" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_XBND
--------------------------------------------------------

  CREATE UNIQUE INDEX "JENA_XBND" ON "JENA_PREFIX" ("HEAD", "CHKSUM") 
  ;


--------------------------------------------------------
--  DDL for Index ALERT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ALERT_PK" ON "ALERT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_SW_ACLR_USERID_USER_USERID
--------------------------------------------------------

  CREATE INDEX "IDX_SW_ACLR_USERID_USER_USERID" ON "SW_ACLR_USERID_USER" ("USER_ID", "USERGROUP") 
  ;


--------------------------------------------------------
--  DDL for Index MAIL_CC_RECIPIENTS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "MAIL_CC_RECIPIENTS_ID_IDX" ON "MAIL_CC_RECIPIENTS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index REPONSE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REPONSE_PK" ON "REPONSE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index UID_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "UID_PK" ON "UID" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOSSIER_2" ON "DOSSIER_REPONSE" ("IDDOCUMENTQUESTION", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXA_SE_THEME
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXA_SE_THEME" ON "IXA_SE_THEME" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXA_AN_ANALYSE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXA_AN_ANALYSE_ID_IDX" ON "IXA_AN_ANALYSE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FSD_DC_CREATOR_ID_IDX
--------------------------------------------------------

  CREATE INDEX "FSD_DC_CREATOR_ID_IDX" ON "FSD_DC_CREATOR" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index LOT_NAME_ID
--------------------------------------------------------

  CREATE INDEX "LOT_NAME_ID" ON "ALLOTISSEMENT" ("NOM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_REPONSE_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_REPONSE_1" ON "REPONSE" ("ID", "DATEPUBLICATIONJOREPONSE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_J_REQ_RECOVERY
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_J_REQ_RECOVERY" ON "QRTZ_JOB_DETAILS" ("SCHED_NAME", "REQUESTS_RECOVERY") 
  ;


--------------------------------------------------------
--  DDL for Index QUESTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "QUESTION_PK" ON "QUESTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_MISC_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_MISC_1" ON "MISC" ("ID", "LIFECYCLESTATE") 
  ;


--------------------------------------------------------
--  DDL for Index IXA_AN_RUBRIQUE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXA_AN_RUBRIQUE_ID_IDX" ON "IXA_AN_RUBRIQUE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G1T0_REIF_IXSTMT
--------------------------------------------------------

  CREATE UNIQUE INDEX "JENA_G1T0_REIF_IXSTMT" ON "JENA_G1T0_REIF" ("STMT", "HASTYPE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_JETON_MAITRE_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_JETON_MAITRE_1" ON "JETON_MAITRE" ("ID_PROPRIETAIRE", "TYPE_WEBSERVICE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_14
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_14" ON "QUESTION" ("NOMCOMPLETAUTEUR") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_ACTIONNABLE_CASE_LINK_2
--------------------------------------------------------

  CREATE INDEX "IDX_ACTIONNABLE_CASE_LINK_2" ON "ACTIONNABLE_CASE_LINK" ("AUTOMATICVALIDATION") 
  ;


--------------------------------------------------------
--  DDL for Index RQC__09CA03E4_IDX
--------------------------------------------------------

  CREATE INDEX "RQC__09CA03E4_IDX" ON "RQC_CARACTERISTIQUEQUESTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index PARAMETER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PARAMETER_PK" ON "PARAMETER" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G3T1_STMTXO
--------------------------------------------------------

  CREATE INDEX "JENA_G3T1_STMTXO" ON "JENA_G3T1_STMT" ("OBJ") 
  ;


--------------------------------------------------------
--  DDL for Index CMDOC_SENDERS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "CMDOC_SENDERS_ID_IDX" ON "CMDOC_SENDERS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index REPONSES_LOGGING_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REPONSES_LOGGING_PK" ON "REPONSES_LOGGING" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_CASE_LINK_DATE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_CASE_LINK_DATE" ON "CASE_LINK" ("DATE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index PROXIES_TARGETID_IDX
--------------------------------------------------------

  CREATE INDEX "PROXIES_TARGETID_IDX" ON "PROXIES" ("TARGETID") 
  ;


--------------------------------------------------------
--  DDL for Index IXAC_CAB5E8CF_IDX
--------------------------------------------------------

  CREATE INDEX "IXAC_CAB5E8CF_IDX" ON "IXACOMP_TA_RUBRIQUE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index REPLOGL_DETAILS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "REPLOGL_DETAILS_ID_IDX" ON "REPLOGL_DETAILS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index SORTINFOTYPE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SORTINFOTYPE_PK" ON "SORTINFOTYPE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_XLIT
--------------------------------------------------------

  CREATE UNIQUE INDEX "JENA_XLIT" ON "JENA_LONG_LIT" ("HEAD", "CHKSUM") 
  ;


--------------------------------------------------------
--  DDL for Index MLBX_USERS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "MLBX_USERS_ID_IDX" ON "MLBX_USERS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_QUESTION_1" ON "QUESTION" ("NUMEROQUESTION", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index CMDI_A3C38F3F_IDX
--------------------------------------------------------

  CREATE INDEX "CMDI_A3C38F3F_IDX" ON "CMDIST_INITIAL_COPY_E_B3610C04" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOCRIPARTDOC_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOCRIPARTDOC_1" ON "DOCRI_PARTICIPATINGDOCUMENTS" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index ALLOT_IDDOSSIERS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "ALLOT_IDDOSSIERS_ID_IDX" ON "ALLOT_IDDOSSIERS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_REPONSES_LINK_3
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOSSIER_REPONSES_LINK_3" ON "DOSSIER_REPONSES_LINK" ("ID", "ROUTINGTASKID") 
  ;


--------------------------------------------------------
--  DDL for Index CONTENT_VIEW_DISPLAY_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "CONTENT_VIEW_DISPLAY_PK" ON "CONTENT_VIEW_DISPLAY" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ATTRIBUTION#ANONYMOUSTYPE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ATTRIBUTION#ANONYMOUSTYPE_PK" ON "ATTRIBUTION#ANONYMOUSTYPE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DELEGATION_1
--------------------------------------------------------

  CREATE INDEX "IDX_DELEGATION_1" ON "DELEGATION" ("DESTINATAIREID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_20
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_20" ON "QUESTION" ("HASHCONNEXITETEXTE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_NFT_MISFIRE
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_NFT_MISFIRE" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "MISFIRE_INSTR", "NEXT_FIRE_TIME") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_CASE_LINK_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_CASE_LINK_1" ON "CASE_LINK" ("ID", "DATE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXACOMP_TA_RUBRIQUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXACOMP_TA_RUBRIQUE" ON "IXACOMP_TA_RUBRIQUE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index STATUS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "STATUS_PK" ON "STATUS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index DELEGATION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DELEGATION_PK" ON "DELEGATION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_16
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_16" ON "QUESTION" ("GROUPEPOLITIQUE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_BATCH_RESULT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_BATCH_RESULT_PK" ON "BATCH_RESULT" ("ID_RESULT") 
  ;


--------------------------------------------------------
--  DDL for Index LOT_DOSSIER_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "LOT_DOSSIER_ID" ON "DOSSIER_REPONSE" ("IDDOCUMENTQUESTION", "IDDOSSIERLOT", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index FACETED_SEARCH_DEFAULT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FACETED_SEARCH_DEFAULT_PK" ON "FACETED_SEARCH_DEFAULT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXA_AN_ANALYSE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXA_AN_ANALYSE" ON "IXA_AN_ANALYSE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index CMDI_E06BC7E3_IDX
--------------------------------------------------------

  CREATE INDEX "CMDI_E06BC7E3_IDX" ON "CMDIST_INITIAL_ACTION_4CD43708" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_MAILBOX_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_MAILBOX_1" ON "MAILBOX" ("MAILBOX_ID", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index NOTIFICATIONS_SUIVI_BATCHS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOTIFICATIONS_SUIVI_BATCHS_PK" ON "NOTIFICATIONS_SUIVI_BATCHS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index REL_SRCH_ECM_PATH_ID_IDX
--------------------------------------------------------

  CREATE INDEX "REL_SRCH_ECM_PATH_ID_IDX" ON "REL_SRCH_ECM_PATH" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_IXACOMP_SE_RUBRIQUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_IXACOMP_SE_RUBRIQUE" ON "IXACOMP_SE_RUBRIQUE" ("ITEM", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_6
--------------------------------------------------------

  CREATE INDEX "IDX_DOSSIER_6" ON "DOSSIER_REPONSE" ("MINISTEREATTRIBUTAIRECOURANT") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_XSP
--------------------------------------------------------

  CREATE INDEX "JENA_XSP" ON "JENA_SYS_STMT" ("SUBJ", "PROP") 
  ;


--------------------------------------------------------
--  DDL for Index REQUETESIMPLE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REQUETESIMPLE_PK" ON "REQUETESIMPLE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index SEARCH_SUBJECTS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "SEARCH_SUBJECTS_ID_IDX" ON "SEARCH_SUBJECTS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index PROFIL_UTILISATEUR_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PROFIL_UTILISATEUR_PK" ON "PROFIL_UTILISATEUR" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index SEARCH_COVERAGE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "SEARCH_COVERAGE_ID_IDX" ON "SEARCH_COVERAGE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_FT_T_G
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_FT_T_G" ON "QRTZ_FIRED_TRIGGERS" ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index REPOSITORIES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "REPOSITORIES_PK" ON "REPOSITORIES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_JETON_DOC_COUNT
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_JETON_DOC_COUNT" ON "JETON_DOC" ("ID_JETON", "ID_OWNER", "ID_DOC", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_FT_TRIG_INST_NAME
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_FT_TRIG_INST_NAME" ON "QRTZ_FIRED_TRIGGERS" ("SCHED_NAME", "INSTANCE_NAME") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_12
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_12" ON "QUESTION" ("ETATRAPPELE") 
  ;


--------------------------------------------------------
--  DDL for Index RELATION_SEARCH_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "RELATION_SEARCH_PK" ON "RELATION_SEARCH" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_C
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_C" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "CALENDAR_NAME") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_BATCH_LOG_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_BATCH_LOG_PK" ON "BATCH_LOG" ("ID_LOG") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_N_G_STATE
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_N_G_STATE" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_GROUP", "TRIGGER_STATE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_JETON_DOC_1
--------------------------------------------------------

  CREATE INDEX "IDX_JETON_DOC_1" ON "JETON_DOC" ("ID_OWNER") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_4
--------------------------------------------------------

  CREATE INDEX "IDX_DOSSIER_4" ON "DOSSIER_REPONSE" ("IDDOCUMENTREPONSE") 
  ;


--------------------------------------------------------
--  DDL for Index WEBCONTAINER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "WEBCONTAINER_PK" ON "WEBCONTAINER" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IXAC_CCB8C609_IDX
--------------------------------------------------------

  CREATE INDEX "IXAC_CCB8C609_IDX" ON "IXACOMP_AN_RUBRIQUE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_MISC_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_MISC_2" ON "MISC" ("LIFECYCLESTATE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index NOTE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "NOTE_PK" ON "NOTE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index MAILBOX_ID_ID_IDX
--------------------------------------------------------

  CREATE INDEX "MAILBOX_ID_ID_IDX" ON "MAILBOX_ID" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FACETED_SEARCH_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FACETED_SEARCH_PK" ON "FACETED_SEARCH" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index MAILBOX_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MAILBOX_PK" ON "MAILBOX" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index PARAMETRE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PARAMETRE_PK" ON "PARAMETRE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index MLBX_PROFILES_ID_IDX
--------------------------------------------------------

  CREATE INDEX "MLBX_PROFILES_ID_IDX" ON "MLBX_PROFILES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_HIER_5
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_HIER_5" ON "HIERARCHY" ("ID", "MIXINTYPES") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_JG
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_JG" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "JOB_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DELEGATION_2
--------------------------------------------------------

  CREATE INDEX "IDX_DELEGATION_2" ON "DELEGATION" ("DATEDEBUT") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_FT_JG
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_FT_JG" ON "QRTZ_FIRED_TRIGGERS" ("SCHED_NAME", "JOB_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_REPONSES_LINK_2
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOSSIER_REPONSES_LINK_2" ON "DOSSIER_REPONSES_LINK" ("ID", "NUMEROQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_FT_TG
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_FT_TG" ON "QRTZ_FIRED_TRIGGERS" ("SCHED_NAME", "TRIGGER_GROUP") 
  ;


--------------------------------------------------------
--  DDL for Index ETATQUESTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ETATQUESTION_PK" ON "ETATQUESTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index SIGNALEMENTQUESTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SIGNALEMENTQUESTION_PK" ON "SIGNALEMENTQUESTION" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_8
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_8" ON "QUESTION" ("ORIGINEQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index MLBX_FAVORITES_ID_IDX
--------------------------------------------------------

  CREATE INDEX "MLBX_FAVORITES_ID_IDX" ON "MLBX_FAVORITES" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index FSD_ECM_PATH_ID_IDX
--------------------------------------------------------

  CREATE INDEX "FSD_ECM_PATH_ID_IDX" ON "FSD_ECM_PATH" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_5
--------------------------------------------------------

  CREATE UNIQUE INDEX "IDX_DOSSIER_5" ON "DOSSIER_REPONSE" ("ETAPEREDACTIONATTEINTE", "ID") 
  ;


--------------------------------------------------------
--  DDL for Index CASE_ITEM_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "CASE_ITEM_PK" ON "CASE_ITEM" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index JENA_G3T0_REIF_IXSTMT
--------------------------------------------------------

  CREATE UNIQUE INDEX "JENA_G3T0_REIF_IXSTMT" ON "JENA_G3T0_REIF" ("STMT", "HASTYPE") 
  ;


--------------------------------------------------------
--  DDL for Index VERS_E6513A3A_IDX
--------------------------------------------------------

  CREATE INDEX "VERS_E6513A3A_IDX" ON "VERSIONS" ("VERSIONABLEID") 
  ;


--------------------------------------------------------
--  DDL for Index IXA_SE_RUBRIQUE_ID_IDX
--------------------------------------------------------

  CREATE INDEX "IXA_SE_RUBRIQUE_ID_IDX" ON "IXA_SE_RUBRIQUE" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index ALTR_RECIPIENTS_ID_IDX
--------------------------------------------------------

  CREATE INDEX "ALTR_RECIPIENTS_ID_IDX" ON "ALTR_RECIPIENTS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_JETON_DOC_3
--------------------------------------------------------

  CREATE INDEX "IDX_JETON_DOC_3" ON "JETON_DOC" ("TYPE_WEBSERVICE") 
  ;


--------------------------------------------------------
--  DDL for Index MLBX_A22042A4_IDX
--------------------------------------------------------

  CREATE INDEX "MLBX_A22042A4_IDX" ON "MLBX_NOTIFIED_USERS" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index HIERARCHY_ISVERSION_IDX
--------------------------------------------------------

  CREATE INDEX "HIERARCHY_ISVERSION_IDX" ON "HIERARCHY" ("ISVERSION") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_DOSSIER_7
--------------------------------------------------------

  CREATE INDEX "IDX_DOSSIER_7" ON "DOSSIER_REPONSE" ("MINISTEREREATTRIBUTION") 
  ;


--------------------------------------------------------
--  DDL for Index TYPE_CONTACT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TYPE_CONTACT_PK" ON "TYPE_CONTACT" ("ID") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QUESTION_7
--------------------------------------------------------

  CREATE INDEX "IDX_QUESTION_7" ON "QUESTION" ("ETATQUESTION") 
  ;


--------------------------------------------------------
--  DDL for Index DR$FULL_406097D3_IDX$X
--------------------------------------------------------

  CREATE INDEX "DR$FULL_406097D3_IDX$X" ON "DR$FULL_406097D3_IDX$I" ("TOKEN_TEXT", "TOKEN_TYPE", "TOKEN_FIRST", "TOKEN_LAST", "TOKEN_COUNT") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_ACTIONNABLE_CASE_LINK_3
--------------------------------------------------------

  CREATE INDEX "IDX_ACTIONNABLE_CASE_LINK_3" ON "ACTIONNABLE_CASE_LINK" ("DUEDATE") 
  ;


--------------------------------------------------------
--  DDL for Index IDX_QRTZ_T_NEXT_FIRE_TIME
--------------------------------------------------------

  CREATE INDEX "IDX_QRTZ_T_NEXT_FIRE_TIME" ON "QRTZ_TRIGGERS" ("SCHED_NAME", "NEXT_FIRE_TIME") 
  ;


--------------------------------------------------------
--  Constraints for Table CLASSIFICATION_TARGETS
--------------------------------------------------------

  ALTER TABLE "CLASSIFICATION_TARGETS" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table IXA_AN_ANALYSE
--------------------------------------------------------

  ALTER TABLE "IXA_AN_ANALYSE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULLTEXT_FULLTEXT_IDX$I
--------------------------------------------------------

  ALTER TABLE "DR$FULLTEXT_FULLTEXT_IDX$I" MODIFY ("TOKEN_COUNT" NOT NULL ENABLE);



  ALTER TABLE "DR$FULLTEXT_FULLTEXT_IDX$I" MODIFY ("TOKEN_LAST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULLTEXT_FULLTEXT_IDX$I" MODIFY ("TOKEN_FIRST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULLTEXT_FULLTEXT_IDX$I" MODIFY ("TOKEN_TYPE" NOT NULL ENABLE);



  ALTER TABLE "DR$FULLTEXT_FULLTEXT_IDX$I" MODIFY ("TOKEN_TEXT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ETATQUESTION
--------------------------------------------------------

  ALTER TABLE "ETATQUESTION" ADD CONSTRAINT "ETATQUESTION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ETATQUESTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table STATISTIQUE_VALEUR
--------------------------------------------------------

  ALTER TABLE "STATISTIQUE_VALEUR" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STATISTIQUE_VALEUR" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table COMMENT
--------------------------------------------------------

  ALTER TABLE "COMMENT" ADD CONSTRAINT "COMMENT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "COMMENT" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ATTRIBUTION#ANONYMOUSTYPE
--------------------------------------------------------

  ALTER TABLE "ATTRIBUTION#ANONYMOUSTYPE" ADD CONSTRAINT "ATTRIBUTION#ANONYMOUSTYPE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ATTRIBUTION#ANONYMOUSTYPE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SW_ACLR_USERID_USER
--------------------------------------------------------

  ALTER TABLE "SW_ACLR_USERID_USER" MODIFY ("USER_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NOTE
--------------------------------------------------------

  ALTER TABLE "NOTE" ADD CONSTRAINT "NOTE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "NOTE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MLBX_PROFILES
--------------------------------------------------------

  ALTER TABLE "MLBX_PROFILES" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table JENA_LONG_URI
--------------------------------------------------------

  ALTER TABLE "JENA_LONG_URI" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "JENA_LONG_URI" MODIFY ("HEAD" NOT NULL ENABLE);



  ALTER TABLE "JENA_LONG_URI" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table CVD_SELECTEDLAYOUTCOLUMNS
--------------------------------------------------------

  ALTER TABLE "CVD_SELECTEDLAYOUTCOLUMNS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table LISTEELIMINATION
--------------------------------------------------------

  ALTER TABLE "LISTEELIMINATION" ADD CONSTRAINT "LISTEELIMINATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "LISTEELIMINATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table BIRTREPORTMODEL
--------------------------------------------------------

  ALTER TABLE "BIRTREPORTMODEL" ADD CONSTRAINT "BIRTREPORTMODEL_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "BIRTREPORTMODEL" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NAV
--------------------------------------------------------

  ALTER TABLE "NAV" ADD CONSTRAINT "NAV_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "NAV" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table HIERARCHY_READ_ACL
--------------------------------------------------------

  ALTER TABLE "HIERARCHY_READ_ACL" ADD PRIMARY KEY ("ID") ENABLE;


--------------------------------------------------------
--  Constraints for Table STATISTIQUE_MINISTERE
--------------------------------------------------------

  ALTER TABLE "STATISTIQUE_MINISTERE" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STATISTIQUE_MINISTERE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_G1T0_REIF
--------------------------------------------------------

  ALTER TABLE "JENA_G1T0_REIF" MODIFY ("HASTYPE" NOT NULL ENABLE);



  ALTER TABLE "JENA_G1T0_REIF" MODIFY ("STMT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXA_SE_RENVOI
--------------------------------------------------------

  ALTER TABLE "IXA_SE_RENVOI" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MLBX_USERS
--------------------------------------------------------

  ALTER TABLE "MLBX_USERS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table HISTORIQUE_MDP
--------------------------------------------------------

  ALTER TABLE "HISTORIQUE_MDP" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "HISTORIQUE_MDP" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table INFO_COMMENTS
--------------------------------------------------------

  ALTER TABLE "INFO_COMMENTS" ADD CONSTRAINT "INFO_COMMENTS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "INFO_COMMENTS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SORTINFOTYPE
--------------------------------------------------------

  ALTER TABLE "SORTINFOTYPE" ADD CONSTRAINT "SORTINFOTYPE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "SORTINFOTYPE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MLBX_GROUPS
--------------------------------------------------------

  ALTER TABLE "MLBX_GROUPS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table PARTICIPANTLIST
--------------------------------------------------------

  ALTER TABLE "PARTICIPANTLIST" ADD CONSTRAINT "PARTICIPANTLIST_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "PARTICIPANTLIST" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPLOGD_FULLLOG
--------------------------------------------------------

  ALTER TABLE "REPLOGD_FULLLOG" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table UI_TYPES_CONFIGURATION
--------------------------------------------------------

  ALTER TABLE "UI_TYPES_CONFIGURATION" ADD CONSTRAINT "UI_TYPES_CONFIGURATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "UI_TYPES_CONFIGURATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXACOMP_SE_THEME
--------------------------------------------------------

  ALTER TABLE "IXACOMP_SE_THEME" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CMDIST_INITIAL_COPY_E_B3610C04
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_COPY_E_B3610C04" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table USERSETTINGS
--------------------------------------------------------

  ALTER TABLE "USERSETTINGS" ADD CONSTRAINT "USERSETTINGS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "USERSETTINGS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULL_80158323_IDX$N
--------------------------------------------------------

  ALTER TABLE "DR$FULL_80158323_IDX$N" MODIFY ("NLT_MARK" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULL_E17194F7_IDX$I
--------------------------------------------------------

  ALTER TABLE "DR$FULL_E17194F7_IDX$I" MODIFY ("TOKEN_COUNT" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_E17194F7_IDX$I" MODIFY ("TOKEN_LAST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_E17194F7_IDX$I" MODIFY ("TOKEN_FIRST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_E17194F7_IDX$I" MODIFY ("TOKEN_TYPE" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_E17194F7_IDX$I" MODIFY ("TOKEN_TEXT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MAIL
--------------------------------------------------------

  ALTER TABLE "MAIL" ADD CONSTRAINT "MAIL_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "MAIL" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table MLBX_NOTIFIED_USERS
--------------------------------------------------------

  ALTER TABLE "MLBX_NOTIFIED_USERS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QUESTION
--------------------------------------------------------

  ALTER TABLE "QUESTION" ADD CONSTRAINT "QUESTION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "QUESTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_CRON_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_CRON_TRIGGERS" ADD PRIMARY KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;



  ALTER TABLE "QRTZ_CRON_TRIGGERS" MODIFY ("CRON_EXPRESSION" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_CRON_TRIGGERS" MODIFY ("TRIGGER_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_CRON_TRIGGERS" MODIFY ("TRIGGER_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_CRON_TRIGGERS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NSB_RECEIVERMAILLIST
--------------------------------------------------------

  ALTER TABLE "NSB_RECEIVERMAILLIST" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table TYPE_CONTACT
--------------------------------------------------------

  ALTER TABLE "TYPE_CONTACT" ADD CONSTRAINT "TYPE_CONTACT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "TYPE_CONTACT" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table LOCK_JETON_MAITRE
--------------------------------------------------------

  ALTER TABLE "LOCK_JETON_MAITRE" ADD CONSTRAINT "LOCK_JETON_MAITRE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "LOCK_JETON_MAITRE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REQUETETEXTEINTEGRAL
--------------------------------------------------------

  ALTER TABLE "REQUETETEXTEINTEGRAL" ADD CONSTRAINT "REQUETETEXTEINTEGRAL_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REQUETETEXTEINTEGRAL" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table USERSUBSCRIPTION
--------------------------------------------------------

  ALTER TABLE "USERSUBSCRIPTION" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "USERSUBSCRIPTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DELEGATION
--------------------------------------------------------

  ALTER TABLE "DELEGATION" ADD CONSTRAINT "DELEGATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "DELEGATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_SCHEDULER_STATE
--------------------------------------------------------

  ALTER TABLE "QRTZ_SCHEDULER_STATE" ADD PRIMARY KEY ("SCHED_NAME", "INSTANCE_NAME") ENABLE;



  ALTER TABLE "QRTZ_SCHEDULER_STATE" MODIFY ("CHECKIN_INTERVAL" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SCHEDULER_STATE" MODIFY ("LAST_CHECKIN_TIME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SCHEDULER_STATE" MODIFY ("INSTANCE_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SCHEDULER_STATE" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table RQC_ORIGINEQUESTION
--------------------------------------------------------

  ALTER TABLE "RQC_ORIGINEQUESTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table STATISTIQUE_MOIS
--------------------------------------------------------

  ALTER TABLE "STATISTIQUE_MOIS" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STATISTIQUE_MOIS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ANCESTORS
--------------------------------------------------------

  ALTER TABLE "ANCESTORS" MODIFY ("HIERARCHY_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table PRU_COLUMNS
--------------------------------------------------------

  ALTER TABLE "PRU_COLUMNS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPONSES_LOGGING_DETAIL
--------------------------------------------------------

  ALTER TABLE "REPONSES_LOGGING_DETAIL" ADD CONSTRAINT "REPONSES_LOGGING_DETAIL_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REPONSES_LOGGING_DETAIL" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SEARCH_SUBJECTS
--------------------------------------------------------

  ALTER TABLE "SEARCH_SUBJECTS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CMDIST_INITIAL_ACTION_4CD43708
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_ACTION_4CD43708" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_G2T1_STMT
--------------------------------------------------------

  ALTER TABLE "JENA_G2T1_STMT" MODIFY ("OBJ" NOT NULL ENABLE);



  ALTER TABLE "JENA_G2T1_STMT" MODIFY ("PROP" NOT NULL ENABLE);



  ALTER TABLE "JENA_G2T1_STMT" MODIFY ("SUBJ" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CMDIST_ALL_ACTION_PAR_6B4BBED8
--------------------------------------------------------

  ALTER TABLE "CMDIST_ALL_ACTION_PAR_6B4BBED8" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ALTR_RECIPIENTS
--------------------------------------------------------

  ALTER TABLE "ALTR_RECIPIENTS" MODIFY ("ID" NOT NULL ENABLE);




--------------------------------------------------------
--  Constraints for Table BIRTREPORT
--------------------------------------------------------

  ALTER TABLE "BIRTREPORT" ADD CONSTRAINT "BIRTREPORT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "BIRTREPORT" MODIFY ("ID" NOT NULL ENABLE);




--------------------------------------------------------
--  Constraints for Table BATCH_LOG
--------------------------------------------------------

  ALTER TABLE "BATCH_LOG" ADD CONSTRAINT "BATCH_LOG_PK" PRIMARY KEY ("ID_LOG") ENABLE;



  ALTER TABLE "BATCH_LOG" MODIFY ("TYPE" NOT NULL ENABLE);



  ALTER TABLE "BATCH_LOG" MODIFY ("TOMCAT" NOT NULL ENABLE);



  ALTER TABLE "BATCH_LOG" MODIFY ("START_TIME" NOT NULL ENABLE);



  ALTER TABLE "BATCH_LOG" MODIFY ("SERVER" NOT NULL ENABLE);



  ALTER TABLE "BATCH_LOG" MODIFY ("NAME" NOT NULL ENABLE);



  ALTER TABLE "BATCH_LOG" MODIFY ("ERROR_COUNT" NOT NULL ENABLE);



  ALTER TABLE "BATCH_LOG" MODIFY ("ID_LOG" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ALERT
--------------------------------------------------------

  ALTER TABLE "ALERT" ADD CONSTRAINT "ALERT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ALERT" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_BLOB_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_BLOB_TRIGGERS" ADD PRIMARY KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;



  ALTER TABLE "QRTZ_BLOB_TRIGGERS" MODIFY ("TRIGGER_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_BLOB_TRIGGERS" MODIFY ("TRIGGER_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_BLOB_TRIGGERS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table PUBLISH_SECTIONS
--------------------------------------------------------

  ALTER TABLE "PUBLISH_SECTIONS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table UID
--------------------------------------------------------

  ALTER TABLE "UID" ADD CONSTRAINT "UID_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "UID" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FSD_DC_COVERAGE
--------------------------------------------------------

  ALTER TABLE "FSD_DC_COVERAGE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPONSE
--------------------------------------------------------

  ALTER TABLE "REPONSE" ADD CONSTRAINT "REPONSE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REPONSE" MODIFY ("ID" NOT NULL ENABLE);




--------------------------------------------------------
--  Constraints for Table IXA_SE_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXA_SE_RUBRIQUE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DEFAULTSETTINGS
--------------------------------------------------------

  ALTER TABLE "DEFAULTSETTINGS" ADD CONSTRAINT "DEFAULTSETTINGS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "DEFAULTSETTINGS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_G3T1_STMT
--------------------------------------------------------

  ALTER TABLE "JENA_G3T1_STMT" MODIFY ("OBJ" NOT NULL ENABLE);



  ALTER TABLE "JENA_G3T1_STMT" MODIFY ("PROP" NOT NULL ENABLE);



  ALTER TABLE "JENA_G3T1_STMT" MODIFY ("SUBJ" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXA_SE_THEME
--------------------------------------------------------

  ALTER TABLE "IXA_SE_THEME" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REQUETECOMPLEXE
--------------------------------------------------------

  ALTER TABLE "REQUETECOMPLEXE" ADD CONSTRAINT "REQUETECOMPLEXE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REQUETECOMPLEXE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DOCRI_PARTICIPATINGDOCUMENTS
--------------------------------------------------------

  ALTER TABLE "DOCRI_PARTICIPATINGDOCUMENTS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXA_TA_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXA_TA_RUBRIQUE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FSD_DC_NATURE
--------------------------------------------------------

  ALTER TABLE "FSD_DC_NATURE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULL_406097D3_IDX$N
--------------------------------------------------------

  ALTER TABLE "DR$FULL_406097D3_IDX$N" MODIFY ("NLT_MARK" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table RQMET_ETATQUESTION
--------------------------------------------------------

  ALTER TABLE "RQMET_ETATQUESTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table INDEXATION_COMP
--------------------------------------------------------

  ALTER TABLE "INDEXATION_COMP" ADD CONSTRAINT "INDEXATION_COMP_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "INDEXATION_COMP" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULL_E17194F7_IDX$N
--------------------------------------------------------

  ALTER TABLE "DR$FULL_E17194F7_IDX$N" MODIFY ("NLT_MARK" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NXP_LOGS_MAPEXTINFOS
--------------------------------------------------------

  ALTER TABLE "NXP_LOGS_MAPEXTINFOS" ADD UNIQUE ("INFO_FK") ENABLE;



  ALTER TABLE "NXP_LOGS_MAPEXTINFOS" ADD PRIMARY KEY ("LOG_FK", "MAPKEY") ENABLE;



  ALTER TABLE "NXP_LOGS_MAPEXTINFOS" MODIFY ("MAPKEY" NOT NULL ENABLE);



  ALTER TABLE "NXP_LOGS_MAPEXTINFOS" MODIFY ("INFO_FK" NOT NULL ENABLE);



  ALTER TABLE "NXP_LOGS_MAPEXTINFOS" MODIFY ("LOG_FK" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table STATUS
--------------------------------------------------------

  ALTER TABLE "STATUS" ADD CONSTRAINT "STATUS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STATUS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QUERYNAV
--------------------------------------------------------

  ALTER TABLE "QUERYNAV" ADD CONSTRAINT "QUERYNAV_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "QUERYNAV" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXACOMP_MOTCLEF_MINISTERE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_MOTCLEF_MINISTERE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ADVANCED_SEARCH
--------------------------------------------------------

  ALTER TABLE "ADVANCED_SEARCH" ADD CONSTRAINT "ADVANCED_SEARCH_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ADVANCED_SEARCH" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table SMART_FOLDER
--------------------------------------------------------

  ALTER TABLE "SMART_FOLDER" ADD CONSTRAINT "SMART_FOLDER_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "SMART_FOLDER" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SEARCH_CURRENTLIFECYCLESTATES
--------------------------------------------------------

  ALTER TABLE "SEARCH_CURRENTLIFECYCLESTATES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table COMMON
--------------------------------------------------------

  ALTER TABLE "COMMON" ADD CONSTRAINT "COMMON_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "COMMON" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table LOCKS
--------------------------------------------------------

  ALTER TABLE "LOCKS" ADD CONSTRAINT "LOCKS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "LOCKS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NXP_UIDSEQ
--------------------------------------------------------

  ALTER TABLE "NXP_UIDSEQ" ADD UNIQUE ("SEQ_KEY") ENABLE;



  ALTER TABLE "NXP_UIDSEQ" ADD PRIMARY KEY ("SEQ_ID") ENABLE;



  ALTER TABLE "NXP_UIDSEQ" MODIFY ("SEQ_KEY" NOT NULL ENABLE);



  ALTER TABLE "NXP_UIDSEQ" MODIFY ("SEQ_INDEX" NOT NULL ENABLE);



  ALTER TABLE "NXP_UIDSEQ" MODIFY ("SEQ_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SEARCH_COVERAGE
--------------------------------------------------------

  ALTER TABLE "SEARCH_COVERAGE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXA_AN_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXA_AN_RUBRIQUE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table PROFIL_UTILISATEUR
--------------------------------------------------------

  ALTER TABLE "PROFIL_UTILISATEUR" ADD CONSTRAINT "PROFIL_UTILISATEUR_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "PROFIL_UTILISATEUR" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table RELATEDTEXTRESOURCE
--------------------------------------------------------

  ALTER TABLE "RELATEDTEXTRESOURCE" ADD CONSTRAINT "RELATEDTEXTRESOURCE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "RELATEDTEXTRESOURCE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table PARAMETRE
--------------------------------------------------------

  ALTER TABLE "PARAMETRE" ADD CONSTRAINT "PARAMETRE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "PARAMETRE" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table WEBCONTAINER
--------------------------------------------------------

  ALTER TABLE "WEBCONTAINER" ADD CONSTRAINT "WEBCONTAINER_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "WEBCONTAINER" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table JENA_LONG_LIT
--------------------------------------------------------

  ALTER TABLE "JENA_LONG_LIT" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "JENA_LONG_LIT" MODIFY ("HEAD" NOT NULL ENABLE);



  ALTER TABLE "JENA_LONG_LIT" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table RENOUVELLEMENTQUESTION
--------------------------------------------------------

  ALTER TABLE "RENOUVELLEMENTQUESTION" ADD CONSTRAINT "RENOUVELLEMENTQUESTION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "RENOUVELLEMENTQUESTION" MODIFY ("ID" NOT NULL ENABLE);




--------------------------------------------------------
--  Constraints for Table REQUETEMETADONNEES
--------------------------------------------------------

  ALTER TABLE "REQUETEMETADONNEES" ADD CONSTRAINT "REQUETEMETADONNEES_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REQUETEMETADONNEES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULLTEXT_FULLTEXT_IDX$N
--------------------------------------------------------

  ALTER TABLE "DR$FULLTEXT_FULLTEXT_IDX$N" MODIFY ("NLT_MARK" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MAIL_RECIPIENTS
--------------------------------------------------------

  ALTER TABLE "MAIL_RECIPIENTS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_SYS_STMT
--------------------------------------------------------

  ALTER TABLE "JENA_SYS_STMT" MODIFY ("OBJ" NOT NULL ENABLE);



  ALTER TABLE "JENA_SYS_STMT" MODIFY ("PROP" NOT NULL ENABLE);



  ALTER TABLE "JENA_SYS_STMT" MODIFY ("SUBJ" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NXP_LOGS
--------------------------------------------------------

  ALTER TABLE "NXP_LOGS" ADD PRIMARY KEY ("LOG_ID") ENABLE;



  ALTER TABLE "NXP_LOGS" MODIFY ("LOG_EVENT_ID" NOT NULL ENABLE);



  ALTER TABLE "NXP_LOGS" MODIFY ("LOG_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_SIMPROP_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_SIMPROP_TRIGGERS" ADD PRIMARY KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;



  ALTER TABLE "QRTZ_SIMPROP_TRIGGERS" MODIFY ("TRIGGER_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SIMPROP_TRIGGERS" MODIFY ("TRIGGER_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SIMPROP_TRIGGERS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CASE_ITEM
--------------------------------------------------------

  ALTER TABLE "CASE_ITEM" ADD CONSTRAINT "CASE_ITEM_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "CASE_ITEM" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table RELATION
--------------------------------------------------------

  ALTER TABLE "RELATION" ADD CONSTRAINT "RELATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "RELATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table INDEXATION
--------------------------------------------------------

  ALTER TABLE "INDEXATION" ADD CONSTRAINT "INDEXATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "INDEXATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ALLOTISSEMENT
--------------------------------------------------------

  ALTER TABLE "ALLOTISSEMENT" ADD CONSTRAINT "ALLOTISSEMENT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ALLOTISSEMENT" MODIFY ("ID" NOT NULL ENABLE);




--------------------------------------------------------
--  Constraints for Table FACETED_SEARCH_DEFAULT
--------------------------------------------------------

  ALTER TABLE "FACETED_SEARCH_DEFAULT" ADD CONSTRAINT "FACETED_SEARCH_DEFAULT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FACETED_SEARCH_DEFAULT" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table DR$FULL_406097D3_IDX$I
--------------------------------------------------------

  ALTER TABLE "DR$FULL_406097D3_IDX$I" MODIFY ("TOKEN_COUNT" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_406097D3_IDX$I" MODIFY ("TOKEN_LAST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_406097D3_IDX$I" MODIFY ("TOKEN_FIRST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_406097D3_IDX$I" MODIFY ("TOKEN_TYPE" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_406097D3_IDX$I" MODIFY ("TOKEN_TEXT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXACOMP_AN_ANALYSE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_AN_ANALYSE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CMDOC_SENDERS
--------------------------------------------------------

  ALTER TABLE "CMDOC_SENDERS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MAILBOX_ID
--------------------------------------------------------

  ALTER TABLE "MAILBOX_ID" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_G2T0_REIF
--------------------------------------------------------

  ALTER TABLE "JENA_G2T0_REIF" MODIFY ("HASTYPE" NOT NULL ENABLE);



  ALTER TABLE "JENA_G2T0_REIF" MODIFY ("STMT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table PROXIES
--------------------------------------------------------

  ALTER TABLE "PROXIES" ADD CONSTRAINT "PROXIES_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "PROXIES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REL_SRCH_ECM_PATH
--------------------------------------------------------

  ALTER TABLE "REL_SRCH_ECM_PATH" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JETON_MAITRE
--------------------------------------------------------

  ALTER TABLE "JETON_MAITRE" ADD CONSTRAINT "JETON_MAITRE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "JETON_MAITRE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ETAT_APPLICATION
--------------------------------------------------------

  ALTER TABLE "ETAT_APPLICATION" ADD CONSTRAINT "ETAT_APPLICATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ETAT_APPLICATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPONSES_LOGGING
--------------------------------------------------------

  ALTER TABLE "REPONSES_LOGGING" ADD CONSTRAINT "REPONSES_LOGGING_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REPONSES_LOGGING" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SEARCH_SEARCHPATH
--------------------------------------------------------

  ALTER TABLE "SEARCH_SEARCHPATH" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table CASE_DOCUMENTSID
--------------------------------------------------------

  ALTER TABLE "CASE_DOCUMENTSID" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ERRATUM#ANONYMOUSTYPE
--------------------------------------------------------

  ALTER TABLE "ERRATUM#ANONYMOUSTYPE" ADD CONSTRAINT "ERRATUM#ANONYMOUSTYPE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ERRATUM#ANONYMOUSTYPE" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table CONTENT
--------------------------------------------------------

  ALTER TABLE "CONTENT" ADD CONSTRAINT "CONTENT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "CONTENT" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_JOB_DETAILS
--------------------------------------------------------

  ALTER TABLE "QRTZ_JOB_DETAILS" ADD PRIMARY KEY ("SCHED_NAME", "JOB_NAME", "JOB_GROUP") ENABLE;



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("REQUESTS_RECOVERY" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("IS_UPDATE_DATA" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("IS_NONCONCURRENT" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("IS_DURABLE" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("JOB_CLASS_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("JOB_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("JOB_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_JOB_DETAILS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ACTIONNABLE_CASE_LINK
--------------------------------------------------------

  ALTER TABLE "ACTIONNABLE_CASE_LINK" ADD CONSTRAINT "ACTIONNABLE_CASE_LINK_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ACTIONNABLE_CASE_LINK" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JETON_DOC
--------------------------------------------------------

  ALTER TABLE "JETON_DOC" ADD CONSTRAINT "JETON_DOC_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "JETON_DOC" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DOSSIER_REPONSES_LINK
--------------------------------------------------------

  ALTER TABLE "DOSSIER_REPONSES_LINK" ADD CONSTRAINT "DOSSIER_REPONSES_LINK_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "DOSSIER_REPONSES_LINK" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FEUILLE_ROUTE
--------------------------------------------------------

  ALTER TABLE "FEUILLE_ROUTE" ADD CONSTRAINT "FEUILLE_ROUTE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FEUILLE_ROUTE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULL_989918B1_IDX$I
--------------------------------------------------------

  ALTER TABLE "DR$FULL_989918B1_IDX$I" MODIFY ("TOKEN_COUNT" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_989918B1_IDX$I" MODIFY ("TOKEN_LAST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_989918B1_IDX$I" MODIFY ("TOKEN_FIRST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_989918B1_IDX$I" MODIFY ("TOKEN_TYPE" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_989918B1_IDX$I" MODIFY ("TOKEN_TEXT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FSD_DC_SUBJECTS
--------------------------------------------------------

  ALTER TABLE "FSD_DC_SUBJECTS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CASE_LINK
--------------------------------------------------------

  ALTER TABLE "CASE_LINK" ADD CONSTRAINT "CASE_LINK_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "CASE_LINK" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table ACLS
--------------------------------------------------------

  ALTER TABLE "ACLS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table PROTOCOL
--------------------------------------------------------

  ALTER TABLE "PROTOCOL" ADD CONSTRAINT "PROTOCOL_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "PROTOCOL" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MLBX_FAVORITES
--------------------------------------------------------

  ALTER TABLE "MLBX_FAVORITES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table HIERARCHY
--------------------------------------------------------

  ALTER TABLE "HIERARCHY" ADD CONSTRAINT "HIERARCHY_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "HIERARCHY" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table IXACOMP_TA_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_TA_RUBRIQUE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DOSSIER_REPONSE
--------------------------------------------------------

  ALTER TABLE "DOSSIER_REPONSE" ADD CONSTRAINT "DOSSIER_REPONSE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "DOSSIER_REPONSE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REQUETEFEUILLEROUTE
--------------------------------------------------------

  ALTER TABLE "REQUETEFEUILLEROUTE" ADD CONSTRAINT "REQUETEFEUILLEROUTE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REQUETEFEUILLEROUTE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_TRIGGERS" ADD PRIMARY KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("START_TIME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("TRIGGER_TYPE" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("TRIGGER_STATE" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("JOB_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("JOB_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("TRIGGER_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("TRIGGER_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_TRIGGERS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MAILBOX
--------------------------------------------------------

  ALTER TABLE "MAILBOX" ADD CONSTRAINT "MAILBOX_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "MAILBOX" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FULLTEXT
--------------------------------------------------------

  ALTER TABLE "FULLTEXT" ADD CONSTRAINT "FULLTEXT_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FULLTEXT" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table RQC_CARACTERISTIQUEQUESTION
--------------------------------------------------------

  ALTER TABLE "RQC_CARACTERISTIQUEQUESTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_CALENDARS
--------------------------------------------------------

  ALTER TABLE "QRTZ_CALENDARS" ADD PRIMARY KEY ("SCHED_NAME", "CALENDAR_NAME") ENABLE;



  ALTER TABLE "QRTZ_CALENDARS" MODIFY ("CALENDAR" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_CALENDARS" MODIFY ("CALENDAR_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_CALENDARS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NXP_LOGS_EXTINFO
--------------------------------------------------------

  ALTER TABLE "NXP_LOGS_EXTINFO" ADD PRIMARY KEY ("LOG_EXTINFO_ID") ENABLE;



  ALTER TABLE "NXP_LOGS_EXTINFO" MODIFY ("LOG_EXTINFO_ID" NOT NULL ENABLE);



  ALTER TABLE "NXP_LOGS_EXTINFO" MODIFY ("DISCRIMINATOR" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_G3T0_REIF
--------------------------------------------------------

  ALTER TABLE "JENA_G3T0_REIF" MODIFY ("HASTYPE" NOT NULL ENABLE);



  ALTER TABLE "JENA_G3T0_REIF" MODIFY ("STMT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table WSNOTIFICATION
--------------------------------------------------------

  ALTER TABLE "WSNOTIFICATION" ADD CONSTRAINT "WSNOTIFICATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "WSNOTIFICATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXACOMP_SE_RENVOI
--------------------------------------------------------

  ALTER TABLE "IXACOMP_SE_RENVOI" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPLOG_LINES
--------------------------------------------------------

  ALTER TABLE "REPLOG_LINES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SW_ACLR_USER_ACLID
--------------------------------------------------------

  ALTER TABLE "SW_ACLR_USER_ACLID" MODIFY ("USERGROUP" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table STATISTIQUE_DIRECTION
--------------------------------------------------------

  ALTER TABLE "STATISTIQUE_DIRECTION" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STATISTIQUE_DIRECTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DEL_PROFILLIST
--------------------------------------------------------

  ALTER TABLE "DEL_PROFILLIST" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CMDIST_ALL_COPY_PARTI_21AB3C5B
--------------------------------------------------------

  ALTER TABLE "CMDIST_ALL_COPY_PARTI_21AB3C5B" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table RELATION_SEARCH
--------------------------------------------------------

  ALTER TABLE "RELATION_SEARCH" ADD CONSTRAINT "RELATION_SEARCH_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "RELATION_SEARCH" MODIFY ("ID" NOT NULL ENABLE);





--------------------------------------------------------
--  Constraints for Table LOCALTHEMECONFIG
--------------------------------------------------------

  ALTER TABLE "LOCALTHEMECONFIG" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "LOCALTHEMECONFIG" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table CMDIST_INITIAL_COPY_I_D6588F7E
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_COPY_I_D6588F7E" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table FILE
--------------------------------------------------------

  ALTER TABLE "FILE" ADD CONSTRAINT "FILE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FILE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table VCARD
--------------------------------------------------------

  ALTER TABLE "VCARD" ADD CONSTRAINT "VCARD_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "VCARD" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table STATISTIQUE_GROUPE
--------------------------------------------------------

  ALTER TABLE "STATISTIQUE_GROUPE" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STATISTIQUE_GROUPE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MISC
--------------------------------------------------------

  ALTER TABLE "MISC" ADD CONSTRAINT "MISC_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "MISC" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FSD_DC_CREATOR
--------------------------------------------------------

  ALTER TABLE "FSD_DC_CREATOR" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table HISTORIQUEATTRIBUTION
--------------------------------------------------------

  ALTER TABLE "HISTORIQUEATTRIBUTION" ADD CONSTRAINT "HISTORIQUEATTRIBUTION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "HISTORIQUEATTRIBUTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NAVIGATIONSETTINGS
--------------------------------------------------------

  ALTER TABLE "NAVIGATIONSETTINGS" ADD CONSTRAINT "NAVIGATIONSETTINGS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "NAVIGATIONSETTINGS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table UITYPESCONF_ALLOWEDTYPES
--------------------------------------------------------

  ALTER TABLE "UITYPESCONF_ALLOWEDTYPES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FAVORISINDEXATION
--------------------------------------------------------

  ALTER TABLE "FAVORISINDEXATION" ADD CONSTRAINT "FAVORISINDEXATION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FAVORISINDEXATION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_G1T1_STMT
--------------------------------------------------------

  ALTER TABLE "JENA_G1T1_STMT" MODIFY ("OBJ" NOT NULL ENABLE);



  ALTER TABLE "JENA_G1T1_STMT" MODIFY ("PROP" NOT NULL ENABLE);



  ALTER TABLE "JENA_G1T1_STMT" MODIFY ("SUBJ" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ROUTING_TASK
--------------------------------------------------------

  ALTER TABLE "ROUTING_TASK" ADD CONSTRAINT "ROUTING_TASK_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ROUTING_TASK" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPONSES_LOGGING_LINE
--------------------------------------------------------

  ALTER TABLE "REPONSES_LOGGING_LINE" ADD CONSTRAINT "REPONSES_LOGGING_LINE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REPONSES_LOGGING_LINE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table EXPORT_DOCUMENT
--------------------------------------------------------

  ALTER TABLE "EXPORT_DOCUMENT" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPLOGL_FULLLOG
--------------------------------------------------------

  ALTER TABLE "REPLOGL_FULLLOG" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table QRTZ_SIMPLE_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" ADD PRIMARY KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;



  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" MODIFY ("TIMES_TRIGGERED" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" MODIFY ("REPEAT_INTERVAL" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" MODIFY ("REPEAT_COUNT" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" MODIFY ("TRIGGER_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" MODIFY ("TRIGGER_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table VERSIONS
--------------------------------------------------------

  ALTER TABLE "VERSIONS" ADD CONSTRAINT "VERSIONS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "VERSIONS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DR$FULL_80158323_IDX$I
--------------------------------------------------------

  ALTER TABLE "DR$FULL_80158323_IDX$I" MODIFY ("TOKEN_COUNT" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_80158323_IDX$I" MODIFY ("TOKEN_LAST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_80158323_IDX$I" MODIFY ("TOKEN_FIRST" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_80158323_IDX$I" MODIFY ("TOKEN_TYPE" NOT NULL ENABLE);



  ALTER TABLE "DR$FULL_80158323_IDX$I" MODIFY ("TOKEN_TEXT" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPLOGL_DETAILS
--------------------------------------------------------

  ALTER TABLE "REPLOGL_DETAILS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table DUBLINCORE
--------------------------------------------------------

  ALTER TABLE "DUBLINCORE" ADD CONSTRAINT "DUBLINCORE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "DUBLINCORE" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table INFO_UTILISATEUR_CONNECTION
--------------------------------------------------------

  ALTER TABLE "INFO_UTILISATEUR_CONNECTION" ADD CONSTRAINT "INFO_UTILISATEUR_CONNECTION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "INFO_UTILISATEUR_CONNECTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_LOCKS
--------------------------------------------------------

  ALTER TABLE "QRTZ_LOCKS" ADD PRIMARY KEY ("SCHED_NAME", "LOCK_NAME") ENABLE;



  ALTER TABLE "QRTZ_LOCKS" MODIFY ("LOCK_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_LOCKS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table UITYPESCONF_DENIEDTYPES
--------------------------------------------------------

  ALTER TABLE "UITYPESCONF_DENIEDTYPES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SIGNALEMENTQUESTION
--------------------------------------------------------

  ALTER TABLE "SIGNALEMENTQUESTION" ADD CONSTRAINT "SIGNALEMENTQUESTION_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "SIGNALEMENTQUESTION" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_PREFIX
--------------------------------------------------------

  ALTER TABLE "JENA_PREFIX" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "JENA_PREFIX" MODIFY ("HEAD" NOT NULL ENABLE);



  ALTER TABLE "JENA_PREFIX" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REPOSITORIES
--------------------------------------------------------

  ALTER TABLE "REPOSITORIES" ADD CONSTRAINT "REPOSITORIES_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REPOSITORIES" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CONTENT_VIEW_DISPLAY
--------------------------------------------------------

  ALTER TABLE "CONTENT_VIEW_DISPLAY" ADD CONSTRAINT "CONTENT_VIEW_DISPLAY_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "CONTENT_VIEW_DISPLAY" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table QRTZ_FIRED_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_FIRED_TRIGGERS" ADD PRIMARY KEY ("SCHED_NAME", "ENTRY_ID") ENABLE;



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("STATE" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("PRIORITY" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("FIRED_TIME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("INSTANCE_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("TRIGGER_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("TRIGGER_NAME" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("ENTRY_ID" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_FIRED_TRIGGERS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table STATISTIQUE_QUESTION_REPONSE
--------------------------------------------------------

  ALTER TABLE "STATISTIQUE_QUESTION_REPONSE" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STATISTIQUE_QUESTION_REPONSE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table MAIL_CC_RECIPIENTS
--------------------------------------------------------

  ALTER TABLE "MAIL_CC_RECIPIENTS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FACETED_SEARCH
--------------------------------------------------------

  ALTER TABLE "FACETED_SEARCH" ADD CONSTRAINT "FACETED_SEARCH_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FACETED_SEARCH" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table JENA_GRAPH
--------------------------------------------------------

  ALTER TABLE "JENA_GRAPH" ADD PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "JENA_GRAPH" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table QRTZ_PAUSED_TRIGGER_GRPS
--------------------------------------------------------

  ALTER TABLE "QRTZ_PAUSED_TRIGGER_GRPS" ADD PRIMARY KEY ("SCHED_NAME", "TRIGGER_GROUP") ENABLE;



  ALTER TABLE "QRTZ_PAUSED_TRIGGER_GRPS" MODIFY ("TRIGGER_GROUP" NOT NULL ENABLE);



  ALTER TABLE "QRTZ_PAUSED_TRIGGER_GRPS" MODIFY ("SCHED_NAME" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table TAG
--------------------------------------------------------

  ALTER TABLE "TAG" ADD CONSTRAINT "TAG_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "TAG" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXA_MOTCLEF_MINISTERE
--------------------------------------------------------

  ALTER TABLE "IXA_MOTCLEF_MINISTERE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ELEMENTFONDDOSSIER
--------------------------------------------------------

  ALTER TABLE "ELEMENTFONDDOSSIER" ADD CONSTRAINT "ELEMENTFONDDOSSIER_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "ELEMENTFONDDOSSIER" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table BATCH_RESULT
--------------------------------------------------------

  ALTER TABLE "BATCH_RESULT" ADD CONSTRAINT "BATCH_RESULT_PK" PRIMARY KEY ("ID_RESULT") ENABLE;



  ALTER TABLE "BATCH_RESULT" MODIFY ("TEXT" NOT NULL ENABLE);



  ALTER TABLE "BATCH_RESULT" MODIFY ("EXECUTION_TIME" NOT NULL ENABLE);



  ALTER TABLE "BATCH_RESULT" MODIFY ("ID_LOG" NOT NULL ENABLE);



  ALTER TABLE "BATCH_RESULT" MODIFY ("ID_RESULT" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table FAVORISDOSSIER
--------------------------------------------------------

  ALTER TABLE "FAVORISDOSSIER" ADD CONSTRAINT "FAVORISDOSSIER_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FAVORISDOSSIER" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ACLR
--------------------------------------------------------

  ALTER TABLE "ACLR" MODIFY ("ACL_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FSD_ECM_PATH
--------------------------------------------------------

  ALTER TABLE "FSD_ECM_PATH" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table NOTIFICATIONS_SUIVI_BATCHS
--------------------------------------------------------

  ALTER TABLE "NOTIFICATIONS_SUIVI_BATCHS" ADD CONSTRAINT "NOTIFICATIONS_SUIVI_BATCHS_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "NOTIFICATIONS_SUIVI_BATCHS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table CMDIST_INITIAL_ACTION_88A481B7
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_ACTION_88A481B7" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table FONDDOSSIER
--------------------------------------------------------

  ALTER TABLE "FONDDOSSIER" ADD CONSTRAINT "FONDDOSSIER_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "FONDDOSSIER" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table IXACOMP_AN_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_AN_RUBRIQUE" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table PARAMETER
--------------------------------------------------------

  ALTER TABLE "PARAMETER" ADD CONSTRAINT "PARAMETER_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "PARAMETER" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table REQUETESIMPLE
--------------------------------------------------------

  ALTER TABLE "REQUETESIMPLE" ADD CONSTRAINT "REQUETESIMPLE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "REQUETESIMPLE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table RMLBX_PRECALCULLIST
--------------------------------------------------------

  ALTER TABLE "RMLBX_PRECALCULLIST" MODIFY ("ID" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table DR$FULL_989918B1_IDX$N
--------------------------------------------------------

  ALTER TABLE "DR$FULL_989918B1_IDX$N" MODIFY ("NLT_MARK" NOT NULL ENABLE);



--------------------------------------------------------
--  Constraints for Table STEP_FOLDER
--------------------------------------------------------

  ALTER TABLE "STEP_FOLDER" ADD CONSTRAINT "STEP_FOLDER_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "STEP_FOLDER" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table TIMBRE
--------------------------------------------------------

  ALTER TABLE "TIMBRE" ADD CONSTRAINT "TIMBRE_PK" PRIMARY KEY ("ID") ENABLE;



  ALTER TABLE "TIMBRE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table ALLOT_IDDOSSIERS
--------------------------------------------------------

  ALTER TABLE "ALLOT_IDDOSSIERS" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table SEARCH_NATURE
--------------------------------------------------------

  ALTER TABLE "SEARCH_NATURE" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Constraints for Table IXACOMP_SE_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_SE_RUBRIQUE" MODIFY ("ID" NOT NULL ENABLE);






--------------------------------------------------------
--  Ref Constraints for Table ACLS
--------------------------------------------------------

  ALTER TABLE "ACLS" ADD CONSTRAINT "ACLS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ACTIONNABLE_CASE_LINK
--------------------------------------------------------

  ALTER TABLE "ACTIONNABLE_CASE_LINK" ADD CONSTRAINT "ACTI_D69A363C_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ADVANCED_SEARCH
--------------------------------------------------------

  ALTER TABLE "ADVANCED_SEARCH" ADD CONSTRAINT "ADVA_102157D6_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ALERT
--------------------------------------------------------

  ALTER TABLE "ALERT" ADD CONSTRAINT "ALERT_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ALLOT_IDDOSSIERS
--------------------------------------------------------

  ALTER TABLE "ALLOT_IDDOSSIERS" ADD CONSTRAINT "ALLO_67CE8C90_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ALLOTISSEMENT
--------------------------------------------------------

  ALTER TABLE "ALLOTISSEMENT" ADD CONSTRAINT "ALLOTISSEMENT_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ALTR_RECIPIENTS
--------------------------------------------------------

  ALTER TABLE "ALTR_RECIPIENTS" ADD CONSTRAINT "ALTR_C630CF28_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ANCESTORS
--------------------------------------------------------

  ALTER TABLE "ANCESTORS" ADD CONSTRAINT "ANCESTORS_HIERARCHY_ID_FK" FOREIGN KEY ("HIERARCHY_ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table ATTRIBUTION#ANONYMOUSTYPE
--------------------------------------------------------

  ALTER TABLE "ATTRIBUTION#ANONYMOUSTYPE" ADD CONSTRAINT "ATTR_7B66046D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table BATCH_RESULT
--------------------------------------------------------

  ALTER TABLE "BATCH_RESULT" ADD CONSTRAINT "BR_ID_BATCH_LOG_FK" FOREIGN KEY ("ID_LOG")
	  REFERENCES "BATCH_LOG" ("ID_LOG") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table BIRTREPORT
--------------------------------------------------------

  ALTER TABLE "BIRTREPORT" ADD CONSTRAINT "BIRTREPORT_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table BIRTREPORTMODEL
--------------------------------------------------------

  ALTER TABLE "BIRTREPORTMODEL" ADD CONSTRAINT "BIRT_CB0F83D0_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CASE_DOCUMENTSID
--------------------------------------------------------

  ALTER TABLE "CASE_DOCUMENTSID" ADD CONSTRAINT "CASE_99AB76F4_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CASE_ITEM
--------------------------------------------------------

  ALTER TABLE "CASE_ITEM" ADD CONSTRAINT "CASE_ITEM_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CASE_LINK
--------------------------------------------------------

  ALTER TABLE "CASE_LINK" ADD CONSTRAINT "CASE_LINK_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CLASSIFICATION_TARGETS
--------------------------------------------------------

  ALTER TABLE "CLASSIFICATION_TARGETS" ADD CONSTRAINT "CLAS_4898D61D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;




--------------------------------------------------------
--  Ref Constraints for Table CMDIST_ALL_ACTION_PAR_6B4BBED8
--------------------------------------------------------

  ALTER TABLE "CMDIST_ALL_ACTION_PAR_6B4BBED8" ADD CONSTRAINT "CMDI_7B09D466_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CMDIST_ALL_COPY_PARTI_21AB3C5B
--------------------------------------------------------

  ALTER TABLE "CMDIST_ALL_COPY_PARTI_21AB3C5B" ADD CONSTRAINT "CMDI_516D49CB_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CMDIST_INITIAL_ACTION_4CD43708
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_ACTION_4CD43708" ADD CONSTRAINT "CMDI_DBF5EC6D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CMDIST_INITIAL_ACTION_88A481B7
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_ACTION_88A481B7" ADD CONSTRAINT "CMDI_248596C2_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CMDIST_INITIAL_COPY_E_B3610C04
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_COPY_E_B3610C04" ADD CONSTRAINT "CMDI_ED3FDFAC_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CMDIST_INITIAL_COPY_I_D6588F7E
--------------------------------------------------------

  ALTER TABLE "CMDIST_INITIAL_COPY_I_D6588F7E" ADD CONSTRAINT "CMDI_3EE0F322_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CMDOC_SENDERS
--------------------------------------------------------

  ALTER TABLE "CMDOC_SENDERS" ADD CONSTRAINT "CMDOC_SENDERS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table COMMENT
--------------------------------------------------------

  ALTER TABLE "COMMENT" ADD CONSTRAINT "COMMENT_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table COMMON
--------------------------------------------------------

  ALTER TABLE "COMMON" ADD CONSTRAINT "COMMON_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CONTENT
--------------------------------------------------------

  ALTER TABLE "CONTENT" ADD CONSTRAINT "CONTENT_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CONTENT_VIEW_DISPLAY
--------------------------------------------------------

  ALTER TABLE "CONTENT_VIEW_DISPLAY" ADD CONSTRAINT "CONT_0FEB3678_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;




--------------------------------------------------------
--  Ref Constraints for Table CVD_SELECTEDLAYOUTCOLUMNS
--------------------------------------------------------

  ALTER TABLE "CVD_SELECTEDLAYOUTCOLUMNS" ADD CONSTRAINT "CVD__98E4BA2C_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table DEFAULTSETTINGS
--------------------------------------------------------

  ALTER TABLE "DEFAULTSETTINGS" ADD CONSTRAINT "DEFA_795A5897_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table DELEGATION
--------------------------------------------------------

  ALTER TABLE "DELEGATION" ADD CONSTRAINT "DELEGATION_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table DEL_PROFILLIST
--------------------------------------------------------

  ALTER TABLE "DEL_PROFILLIST" ADD CONSTRAINT "DEL_PROFILLIST_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table DOCRI_PARTICIPATINGDOCUMENTS
--------------------------------------------------------

  ALTER TABLE "DOCRI_PARTICIPATINGDOCUMENTS" ADD CONSTRAINT "DOCR_336D9931_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table DOSSIER_REPONSE
--------------------------------------------------------

  ALTER TABLE "DOSSIER_REPONSE" ADD CONSTRAINT "DOSS_F4FA3C63_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table DOSSIER_REPONSES_LINK
--------------------------------------------------------

  ALTER TABLE "DOSSIER_REPONSES_LINK" ADD CONSTRAINT "DOSS_E3001DCC_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;






















--------------------------------------------------------
--  Ref Constraints for Table DUBLINCORE
--------------------------------------------------------

  ALTER TABLE "DUBLINCORE" ADD CONSTRAINT "DUBLINCORE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ELEMENTFONDDOSSIER
--------------------------------------------------------

  ALTER TABLE "ELEMENTFONDDOSSIER" ADD CONSTRAINT "ELEM_EDF685BB_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ERRATUM#ANONYMOUSTYPE
--------------------------------------------------------

  ALTER TABLE "ERRATUM#ANONYMOUSTYPE" ADD CONSTRAINT "ERRA_90BF0BDB_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ETAT_APPLICATION
--------------------------------------------------------

  ALTER TABLE "ETAT_APPLICATION" ADD CONSTRAINT "ETAT_E81FD654_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ETATQUESTION
--------------------------------------------------------

  ALTER TABLE "ETATQUESTION" ADD CONSTRAINT "ETAT_00C5451D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table FACETED_SEARCH
--------------------------------------------------------

  ALTER TABLE "FACETED_SEARCH" ADD CONSTRAINT "FACETED_SEARCH_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FACETED_SEARCH_DEFAULT
--------------------------------------------------------

  ALTER TABLE "FACETED_SEARCH_DEFAULT" ADD CONSTRAINT "FACE_D7EAAFD8_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FAVORISDOSSIER
--------------------------------------------------------

  ALTER TABLE "FAVORISDOSSIER" ADD CONSTRAINT "FAVORISDOSSIER_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FAVORISINDEXATION
--------------------------------------------------------

  ALTER TABLE "FAVORISINDEXATION" ADD CONSTRAINT "FAVO_36C497DF_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FEUILLE_ROUTE
--------------------------------------------------------

  ALTER TABLE "FEUILLE_ROUTE" ADD CONSTRAINT "FEUILLE_ROUTE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FILE
--------------------------------------------------------

  ALTER TABLE "FILE" ADD CONSTRAINT "FILE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FONDDOSSIER
--------------------------------------------------------

  ALTER TABLE "FONDDOSSIER" ADD CONSTRAINT "FONDDOSSIER_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FSD_DC_COVERAGE
--------------------------------------------------------

  ALTER TABLE "FSD_DC_COVERAGE" ADD CONSTRAINT "FSD__A5BA3E76_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FSD_DC_CREATOR
--------------------------------------------------------

  ALTER TABLE "FSD_DC_CREATOR" ADD CONSTRAINT "FSD_DC_CREATOR_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FSD_DC_NATURE
--------------------------------------------------------

  ALTER TABLE "FSD_DC_NATURE" ADD CONSTRAINT "FSD_DC_NATURE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FSD_DC_SUBJECTS
--------------------------------------------------------

  ALTER TABLE "FSD_DC_SUBJECTS" ADD CONSTRAINT "FSD__56431C6C_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FSD_ECM_PATH
--------------------------------------------------------

  ALTER TABLE "FSD_ECM_PATH" ADD CONSTRAINT "FSD_ECM_PATH_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table FULLTEXT
--------------------------------------------------------

  ALTER TABLE "FULLTEXT" ADD CONSTRAINT "FULLTEXT_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table HIERARCHY
--------------------------------------------------------

  ALTER TABLE "HIERARCHY" ADD CONSTRAINT "HIER_7A50B200_FK" FOREIGN KEY ("PARENTID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table HIERARCHY_READ_ACL
--------------------------------------------------------

  ALTER TABLE "HIERARCHY_READ_ACL" ADD CONSTRAINT "HIERARCHY_READ_ACL_ID_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table HISTORIQUEATTRIBUTION
--------------------------------------------------------

  ALTER TABLE "HISTORIQUEATTRIBUTION" ADD CONSTRAINT "HIST_A9D3CB76_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table INDEXATION
--------------------------------------------------------

  ALTER TABLE "INDEXATION" ADD CONSTRAINT "INDEXATION_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table INDEXATION_COMP
--------------------------------------------------------

  ALTER TABLE "INDEXATION_COMP" ADD CONSTRAINT "INDE_F21B7009_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table INFO_COMMENTS
--------------------------------------------------------

  ALTER TABLE "INFO_COMMENTS" ADD CONSTRAINT "INFO_COMMENTS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table INFO_UTILISATEUR_CONNECTION
--------------------------------------------------------

  ALTER TABLE "INFO_UTILISATEUR_CONNECTION" ADD CONSTRAINT "INFO_B4B11272_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXA_AN_ANALYSE
--------------------------------------------------------

  ALTER TABLE "IXA_AN_ANALYSE" ADD CONSTRAINT "IXA_AN_ANALYSE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXA_AN_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXA_AN_RUBRIQUE" ADD CONSTRAINT "IXA__AB4AA9D3_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXACOMP_AN_ANALYSE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_AN_ANALYSE" ADD CONSTRAINT "IXAC_16923046_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXACOMP_AN_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_AN_RUBRIQUE" ADD CONSTRAINT "IXAC_C62A5B20_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXACOMP_MOTCLEF_MINISTERE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_MOTCLEF_MINISTERE" ADD CONSTRAINT "IXAC_1F600CE3_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXACOMP_SE_RENVOI
--------------------------------------------------------

  ALTER TABLE "IXACOMP_SE_RENVOI" ADD CONSTRAINT "IXAC_1A24F39E_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXACOMP_SE_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_SE_RUBRIQUE" ADD CONSTRAINT "IXAC_FB83EC02_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXACOMP_SE_THEME
--------------------------------------------------------

  ALTER TABLE "IXACOMP_SE_THEME" ADD CONSTRAINT "IXAC_A9CD4796_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXACOMP_TA_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXACOMP_TA_RUBRIQUE" ADD CONSTRAINT "IXAC_F9BDB866_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXA_MOTCLEF_MINISTERE
--------------------------------------------------------

  ALTER TABLE "IXA_MOTCLEF_MINISTERE" ADD CONSTRAINT "IXA__97D03FBC_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXA_SE_RENVOI
--------------------------------------------------------

  ALTER TABLE "IXA_SE_RENVOI" ADD CONSTRAINT "IXA_SE_RENVOI_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXA_SE_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXA_SE_RUBRIQUE" ADD CONSTRAINT "IXA__F5EEFBB1_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXA_SE_THEME
--------------------------------------------------------

  ALTER TABLE "IXA_SE_THEME" ADD CONSTRAINT "IXA_SE_THEME_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table IXA_TA_RUBRIQUE
--------------------------------------------------------

  ALTER TABLE "IXA_TA_RUBRIQUE" ADD CONSTRAINT "IXA__AE90BCA6_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;













--------------------------------------------------------
--  Ref Constraints for Table JETON_DOC
--------------------------------------------------------

  ALTER TABLE "JETON_DOC" ADD CONSTRAINT "JETON_DOC_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table JETON_MAITRE
--------------------------------------------------------

  ALTER TABLE "JETON_MAITRE" ADD CONSTRAINT "JETON_MAITRE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table LISTEELIMINATION
--------------------------------------------------------

  ALTER TABLE "LISTEELIMINATION" ADD CONSTRAINT "LIST_3B07BA5B_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table LOCK_JETON_MAITRE
--------------------------------------------------------

  ALTER TABLE "LOCK_JETON_MAITRE" ADD CONSTRAINT "LJM_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



  ALTER TABLE "LOCK_JETON_MAITRE" ADD CONSTRAINT "LJM_ID_JETON_MAITRE_FK" FOREIGN KEY ("ID_JETON_MAITRE")
	  REFERENCES "JETON_MAITRE" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table MAIL
--------------------------------------------------------

  ALTER TABLE "MAIL" ADD CONSTRAINT "MAIL_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MAILBOX
--------------------------------------------------------

  ALTER TABLE "MAILBOX" ADD CONSTRAINT "MAILBOX_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MAILBOX_ID
--------------------------------------------------------

  ALTER TABLE "MAILBOX_ID" ADD CONSTRAINT "MAILBOX_ID_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MAIL_CC_RECIPIENTS
--------------------------------------------------------

  ALTER TABLE "MAIL_CC_RECIPIENTS" ADD CONSTRAINT "MAIL_0700D9E5_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MAIL_RECIPIENTS
--------------------------------------------------------

  ALTER TABLE "MAIL_RECIPIENTS" ADD CONSTRAINT "MAIL_926B433B_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MISC
--------------------------------------------------------

  ALTER TABLE "MISC" ADD CONSTRAINT "MISC_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MLBX_FAVORITES
--------------------------------------------------------

  ALTER TABLE "MLBX_FAVORITES" ADD CONSTRAINT "MLBX_FAVORITES_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MLBX_GROUPS
--------------------------------------------------------

  ALTER TABLE "MLBX_GROUPS" ADD CONSTRAINT "MLBX_GROUPS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MLBX_NOTIFIED_USERS
--------------------------------------------------------

  ALTER TABLE "MLBX_NOTIFIED_USERS" ADD CONSTRAINT "MLBX_9887A51E_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MLBX_PROFILES
--------------------------------------------------------

  ALTER TABLE "MLBX_PROFILES" ADD CONSTRAINT "MLBX_PROFILES_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table MLBX_USERS
--------------------------------------------------------

  ALTER TABLE "MLBX_USERS" ADD CONSTRAINT "MLBX_USERS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table NAV
--------------------------------------------------------

  ALTER TABLE "NAV" ADD CONSTRAINT "NAV_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table NAVIGATIONSETTINGS
--------------------------------------------------------

  ALTER TABLE "NAVIGATIONSETTINGS" ADD CONSTRAINT "NAVI_9BCB26B1_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table NOTE
--------------------------------------------------------

  ALTER TABLE "NOTE" ADD CONSTRAINT "NOTE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table NOTIFICATIONS_SUIVI_BATCHS
--------------------------------------------------------

  ALTER TABLE "NOTIFICATIONS_SUIVI_BATCHS" ADD CONSTRAINT "NOTI_B002CA82_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table NSB_RECEIVERMAILLIST
--------------------------------------------------------

  ALTER TABLE "NSB_RECEIVERMAILLIST" ADD CONSTRAINT "NSB__021DEF94_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;




--------------------------------------------------------
--  Ref Constraints for Table NXP_LOGS_MAPEXTINFOS
--------------------------------------------------------

  ALTER TABLE "NXP_LOGS_MAPEXTINFOS" ADD CONSTRAINT "FKF96F609C4EA9779" FOREIGN KEY ("INFO_FK")
	  REFERENCES "NXP_LOGS_EXTINFO" ("LOG_EXTINFO_ID") ENABLE;



  ALTER TABLE "NXP_LOGS_MAPEXTINFOS" ADD CONSTRAINT "FKF96F609E7AC49AA" FOREIGN KEY ("LOG_FK")
	  REFERENCES "NXP_LOGS" ("LOG_ID") ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table PARAMETER
--------------------------------------------------------

  ALTER TABLE "PARAMETER" ADD CONSTRAINT "PARAMETER_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PARAMETRE
--------------------------------------------------------

  ALTER TABLE "PARAMETRE" ADD CONSTRAINT "PARAMETRE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PARTICIPANTLIST
--------------------------------------------------------

  ALTER TABLE "PARTICIPANTLIST" ADD CONSTRAINT "PART_E4211619_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PROFIL_UTILISATEUR
--------------------------------------------------------

  ALTER TABLE "PROFIL_UTILISATEUR" ADD CONSTRAINT "PROF_24CEB1DF_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PROTOCOL
--------------------------------------------------------

  ALTER TABLE "PROTOCOL" ADD CONSTRAINT "PROTOCOL_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PROXIES
--------------------------------------------------------

  ALTER TABLE "PROXIES" ADD CONSTRAINT "PROXIES_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



  ALTER TABLE "PROXIES" ADD CONSTRAINT "PROXIES_TARGETID_HIERARCHY_FK" FOREIGN KEY ("TARGETID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PRU_COLUMNS
--------------------------------------------------------

  ALTER TABLE "PRU_COLUMNS" ADD CONSTRAINT "PRU_COLUMNS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PUBLISH_SECTIONS
--------------------------------------------------------

  ALTER TABLE "PUBLISH_SECTIONS" ADD CONSTRAINT "PUBL_D888C7AF_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table QRTZ_BLOB_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_BLOB_TRIGGERS" ADD FOREIGN KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP")
	  REFERENCES "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table QRTZ_CRON_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_CRON_TRIGGERS" ADD FOREIGN KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP")
	  REFERENCES "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table QRTZ_SIMPLE_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_SIMPLE_TRIGGERS" ADD FOREIGN KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP")
	  REFERENCES "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table QRTZ_SIMPROP_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_SIMPROP_TRIGGERS" ADD FOREIGN KEY ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP")
	  REFERENCES "QRTZ_TRIGGERS" ("SCHED_NAME", "TRIGGER_NAME", "TRIGGER_GROUP") ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table QRTZ_TRIGGERS
--------------------------------------------------------

  ALTER TABLE "QRTZ_TRIGGERS" ADD FOREIGN KEY ("SCHED_NAME", "JOB_NAME", "JOB_GROUP")
	  REFERENCES "QRTZ_JOB_DETAILS" ("SCHED_NAME", "JOB_NAME", "JOB_GROUP") ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table QUERYNAV
--------------------------------------------------------

  ALTER TABLE "QUERYNAV" ADD CONSTRAINT "QUERYNAV_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table QUESTION
--------------------------------------------------------

  ALTER TABLE "QUESTION" ADD CONSTRAINT "QUESTION_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table RELATEDTEXTRESOURCE
--------------------------------------------------------

  ALTER TABLE "RELATEDTEXTRESOURCE" ADD CONSTRAINT "RELA_835C3B64_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table RELATION
--------------------------------------------------------

  ALTER TABLE "RELATION" ADD CONSTRAINT "RELATION_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table RELATION_SEARCH
--------------------------------------------------------

  ALTER TABLE "RELATION_SEARCH" ADD CONSTRAINT "RELA_30BD7D76_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REL_SRCH_ECM_PATH
--------------------------------------------------------

  ALTER TABLE "REL_SRCH_ECM_PATH" ADD CONSTRAINT "REL__E3A6C2F4_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table RENOUVELLEMENTQUESTION
--------------------------------------------------------

  ALTER TABLE "RENOUVELLEMENTQUESTION" ADD CONSTRAINT "RENO_4AFFE04B_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPLOGD_FULLLOG
--------------------------------------------------------

  ALTER TABLE "REPLOGD_FULLLOG" ADD CONSTRAINT "REPL_68E6D88C_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPLOGL_DETAILS
--------------------------------------------------------

  ALTER TABLE "REPLOGL_DETAILS" ADD CONSTRAINT "REPL_294569B7_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPLOGL_FULLLOG
--------------------------------------------------------

  ALTER TABLE "REPLOGL_FULLLOG" ADD CONSTRAINT "REPL_DBB0E430_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPLOG_LINES
--------------------------------------------------------

  ALTER TABLE "REPLOG_LINES" ADD CONSTRAINT "REPLOG_LINES_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPONSE
--------------------------------------------------------

  ALTER TABLE "REPONSE" ADD CONSTRAINT "REPONSE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPONSES_LOGGING
--------------------------------------------------------

  ALTER TABLE "REPONSES_LOGGING" ADD CONSTRAINT "REPO_F95E4AAC_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPONSES_LOGGING_DETAIL
--------------------------------------------------------

  ALTER TABLE "REPONSES_LOGGING_DETAIL" ADD CONSTRAINT "REPO_11A28224_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPONSES_LOGGING_LINE
--------------------------------------------------------

  ALTER TABLE "REPONSES_LOGGING_LINE" ADD CONSTRAINT "REPO_9492F96B_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REPOSITORIES
--------------------------------------------------------

  ALTER TABLE "REPOSITORIES" ADD CONSTRAINT "REPOSITORIES_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REQUETECOMPLEXE
--------------------------------------------------------

  ALTER TABLE "REQUETECOMPLEXE" ADD CONSTRAINT "REQU_531E415A_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REQUETEFEUILLEROUTE
--------------------------------------------------------

  ALTER TABLE "REQUETEFEUILLEROUTE" ADD CONSTRAINT "REQU_126F669D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REQUETEMETADONNEES
--------------------------------------------------------

  ALTER TABLE "REQUETEMETADONNEES" ADD CONSTRAINT "REQU_C98AA63A_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REQUETESIMPLE
--------------------------------------------------------

  ALTER TABLE "REQUETESIMPLE" ADD CONSTRAINT "REQUETESIMPLE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table REQUETETEXTEINTEGRAL
--------------------------------------------------------

  ALTER TABLE "REQUETETEXTEINTEGRAL" ADD CONSTRAINT "REQU_FFB2E76B_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table RMLBX_PRECALCULLIST
--------------------------------------------------------

  ALTER TABLE "RMLBX_PRECALCULLIST" ADD CONSTRAINT "RMLB_D6ED5563_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table ROUTING_TASK
--------------------------------------------------------

  ALTER TABLE "ROUTING_TASK" ADD CONSTRAINT "ROUTING_TASK_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table RQC_CARACTERISTIQUEQUESTION
--------------------------------------------------------

  ALTER TABLE "RQC_CARACTERISTIQUEQUESTION" ADD CONSTRAINT "RQC__2F846F29_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table RQC_ORIGINEQUESTION
--------------------------------------------------------

  ALTER TABLE "RQC_ORIGINEQUESTION" ADD CONSTRAINT "RQC__9C51B20D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table RQMET_ETATQUESTION
--------------------------------------------------------

  ALTER TABLE "RQMET_ETATQUESTION" ADD CONSTRAINT "RQME_9F7ADB70_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SEARCH_COVERAGE
--------------------------------------------------------

  ALTER TABLE "SEARCH_COVERAGE" ADD CONSTRAINT "SEAR_34319F67_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SEARCH_CURRENTLIFECYCLESTATES
--------------------------------------------------------

  ALTER TABLE "SEARCH_CURRENTLIFECYCLESTATES" ADD CONSTRAINT "SEAR_1B5AF6D2_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SEARCH_NATURE
--------------------------------------------------------

  ALTER TABLE "SEARCH_NATURE" ADD CONSTRAINT "SEARCH_NATURE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SEARCH_SEARCHPATH
--------------------------------------------------------

  ALTER TABLE "SEARCH_SEARCHPATH" ADD CONSTRAINT "SEAR_8976FDB6_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SEARCH_SUBJECTS
--------------------------------------------------------

  ALTER TABLE "SEARCH_SUBJECTS" ADD CONSTRAINT "SEAR_110E4D75_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SIGNALEMENTQUESTION
--------------------------------------------------------

  ALTER TABLE "SIGNALEMENTQUESTION" ADD CONSTRAINT "SIGN_6745533D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SMART_FOLDER
--------------------------------------------------------

  ALTER TABLE "SMART_FOLDER" ADD CONSTRAINT "SMART_FOLDER_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table SORTINFOTYPE
--------------------------------------------------------

  ALTER TABLE "SORTINFOTYPE" ADD CONSTRAINT "SORTINFOTYPE_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table STATUS
--------------------------------------------------------

  ALTER TABLE "STATUS" ADD CONSTRAINT "STATUS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table STEP_FOLDER
--------------------------------------------------------

  ALTER TABLE "STEP_FOLDER" ADD CONSTRAINT "STEP_FOLDER_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table TAG
--------------------------------------------------------

  ALTER TABLE "TAG" ADD CONSTRAINT "TAG_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table TIMBRE
--------------------------------------------------------

  ALTER TABLE "TIMBRE" ADD CONSTRAINT "TIMBRE_00C5451D_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table TYPE_CONTACT
--------------------------------------------------------

  ALTER TABLE "TYPE_CONTACT" ADD CONSTRAINT "TYPE_CONTACT_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table UID
--------------------------------------------------------

  ALTER TABLE "UID" ADD CONSTRAINT "UID_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table UITYPESCONF_ALLOWEDTYPES
--------------------------------------------------------

  ALTER TABLE "UITYPESCONF_ALLOWEDTYPES" ADD CONSTRAINT "UITY_694B86C3_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table UITYPESCONF_DENIEDTYPES
--------------------------------------------------------

  ALTER TABLE "UITYPESCONF_DENIEDTYPES" ADD CONSTRAINT "UITY_6E2ADC3E_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table UI_TYPES_CONFIGURATION
--------------------------------------------------------

  ALTER TABLE "UI_TYPES_CONFIGURATION" ADD CONSTRAINT "UI_T_F6D8BC6A_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table USERSETTINGS
--------------------------------------------------------

  ALTER TABLE "USERSETTINGS" ADD CONSTRAINT "USERSETTINGS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table VCARD
--------------------------------------------------------

  ALTER TABLE "VCARD" ADD CONSTRAINT "VCARD_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table VERSIONS
--------------------------------------------------------

  ALTER TABLE "VERSIONS" ADD CONSTRAINT "VERSIONS_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;



--------------------------------------------------------
--  Ref Constraints for Table WEBCONTAINER
--------------------------------------------------------

  ALTER TABLE "WEBCONTAINER" ADD CONSTRAINT "WEBCONTAINER_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table WSNOTIFICATION
--------------------------------------------------------

  ALTER TABLE "WSNOTIFICATION" ADD CONSTRAINT "WSNOTIFICATION_ID_HIERARCHY_FK" FOREIGN KEY ("ID")
	  REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_ACLR_MODIFIED
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_ACLR_MODIFIED" 
  AFTER INSERT ON aclr
  FOR EACH ROW
      WHEN (NEW.acl_id IS NOT NULL) DECLARE
  acl NX_STRING_ARRAY;
  ace VARCHAR(4000);
  sep VARCHAR2(1) := '|';
BEGIN
	FOR r IN (SELECT DISTINCT USERGROUP FROM SW_ACLR_USER_ACLID) LOOP
		acl := split(:NEW.acl, sep);
		FOR i IN acl.FIRST .. acl.LAST LOOP
			ace := acl(i);
			IF ace = r.usergroup THEN
				-- GRANTED
				INSERT INTO SW_ACLR_USER_ACLID SELECT r.usergroup, :NEW.acl_id FROM DUAL
				WHERE NOT EXISTS (SELECT 1 FROM SW_ACLR_USER_ACLID WHERE usergroup = r.usergroup AND acl_id = :NEW.acl_id);
			END IF;
		END LOOP;
	END LOOP;
END;



ALTER TRIGGER "NX_TRIG_ACLR_MODIFIED" ENABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_ACLS_MODIFIED
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_ACLS_MODIFIED" 
  AFTER INSERT OR UPDATE OR DELETE ON acls
  FOR EACH ROW
-- Trigger to log change in the acls table
DECLARE
  doc_id acls.id%TYPE := CASE WHEN DELETING THEN :OLD.id ELSE :NEW.id END;
BEGIN
  INSERT INTO aclr_modified (hierarchy_id, is_new) VALUES (doc_id, 0);
END;



ALTER TRIGGER "NX_TRIG_ACLS_MODIFIED" ENABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_ANCESTORS_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_ANCESTORS_INSERT" 
  AFTER INSERT ON hierarchy
  FOR EACH ROW
         WHEN (NEW.isproperty = 0 AND NEW.parentid IS NOT NULL) BEGIN
  INSERT INTO ANCESTORS VALUES(:NEW.id, NULL);
END;



ALTER TRIGGER "NX_TRIG_ANCESTORS_INSERT" DISABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_ANCESTORS_PROCESS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_ANCESTORS_PROCESS" 
  AFTER INSERT OR UPDATE ON hierarchy
  -- statement level is required to be able to read hierarchy table with updated values
BEGIN
  UPDATE ancestors SET ancestors=nx_get_ancestors(hierarchy_id)
    WHERE ancestors IS NULL;
END;



ALTER TRIGGER "NX_TRIG_ANCESTORS_PROCESS" DISABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_ANCESTORS_UPDATE
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_ANCESTORS_UPDATE" 
  AFTER UPDATE ON hierarchy
  FOR EACH ROW         WHEN (NEW.isproperty = 0 AND NEW.parentid <> OLD.parentid) BEGIN
  UPDATE ancestors SET ancestors = NULL
    WHERE hierarchy_id IN (SELECT hierarchy_id FROM ancestors a
                           WHERE :NEW.id MEMBER OF a.ancestors OR  hierarchy_id = :NEW.id);
END;



ALTER TRIGGER "NX_TRIG_ANCESTORS_UPDATE" DISABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_FT_UPDATE
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_FT_UPDATE" 
  BEFORE INSERT OR UPDATE ON "FULLTEXT"
  FOR EACH ROW
BEGIN
    :NEW."FULLTEXT" := :NEW."SIMPLETEXT" || :NEW."BINARYTEXT";
  :NEW."FULLTEXT_IDQUESTION" := :NEW."SIMPLETEXT_IDQUESTION" || :NEW."BINARYTEXT_IDQUESTION";
  :NEW."FULLTEXT_TXTREPONSE" := :NEW."SIMPLETEXT_TXTREPONSE" || :NEW."BINARYTEXT_TXTREPONSE";
  :NEW."FULLTEXT_TXTQUESTION" := :NEW."SIMPLETEXT_TXTQUESTION" || :NEW."BINARYTEXT_TXTQUESTION";
  :NEW."FULLTEXT_SENATTITRE" := :NEW."SIMPLETEXT_SENATTITRE" || :NEW."BINARYTEXT_SENATTITRE";
END;



ALTER TRIGGER "NX_TRIG_FT_UPDATE" ENABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_HIERARCHY_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_HIERARCHY_INSERT" 
  AFTER INSERT ON hierarchy
  FOR EACH ROW
         WHEN (NEW.isproperty = 0) BEGIN
  INSERT INTO aclr_modified (hierarchy_id, is_new) VALUES (:NEW.id, 1);
END;



ALTER TRIGGER "NX_TRIG_HIERARCHY_INSERT" ENABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_HIERARCHY_UPDATE
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_HIERARCHY_UPDATE" 
  AFTER UPDATE ON hierarchy
  FOR EACH ROW
         WHEN (NEW.isproperty = 0 AND NEW.parentid <> OLD.parentid) BEGIN
  INSERT INTO aclr_modified (hierarchy_id, is_new) VALUES (:NEW.id, 0);
END;



ALTER TRIGGER "NX_TRIG_HIERARCHY_UPDATE" ENABLE;


--------------------------------------------------------
--  DDL for Trigger NX_TRIG_HIER_READ_ACL_MOD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NX_TRIG_HIER_READ_ACL_MOD" 
  AFTER INSERT OR UPDATE ON hierarchy_read_acl
  FOR EACH ROW
      WHEN (NEW.acl_id IS NOT NULL) BEGIN
  MERGE INTO aclr USING DUAL
    ON (aclr.acl_id = :NEW.acl_id)
    WHEN NOT MATCHED THEN
    INSERT (acl_id, acl) VALUES (:NEW.acl_id, nx_get_read_acl(:NEW.id));
END;



ALTER TRIGGER "NX_TRIG_HIER_READ_ACL_MOD" ENABLE;


--------------------------------------------------------
--  DDL for Function NEW_UUID
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NEW_UUID" (guid_in VARCHAR2) RETURN VARCHAR2 AS
 BEGIN
  RETURN SUBSTR (guid_in, 1, 8)|| '+'|| SUBSTR (guid_in, 9, 4)|| '+'|| SUBSTR (guid_in, 13, 4)|| '+'|| SUBSTR (guid_in, 17, 4)|| '+'|| SUBSTR (guid_in, 21);
END;


--------------------------------------------------------
--  DDL for Function NX_ACCESS_ALLOWED
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_ACCESS_ALLOWED" (id VARCHAR2, users NX_STRING_TABLE, permissions NX_STRING_TABLE)
RETURN NUMBER IS
  curid hierarchy.id%TYPE := id;
  newid hierarchy.id%TYPE;
  first BOOLEAN := TRUE;
BEGIN
  WHILE curid IS NOT NULL LOOP
    FOR r IN (SELECT * FROM acls WHERE acls.id = curid ORDER BY acls.pos) LOOP
      IF r.permission MEMBER OF permissions AND r.user MEMBER OF users THEN
        RETURN r."GRANT";
      END IF;
    END LOOP;
    SELECT parentid INTO newid FROM hierarchy WHERE hierarchy.id = curid;
    IF first AND newid IS NULL THEN
      SELECT versionableid INTO newid FROM versions WHERE versions.id = curid;
    END IF;
    first := FALSE;
    curid := newid;
  END LOOP;
  RETURN 0;
END;


--------------------------------------------------------
--  DDL for Function NX_ANCESTORS
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_ANCESTORS" (ids NX_STRING_TABLE)
RETURN NX_STRING_TABLE PIPELINED
IS
  id hierarchy.id%TYPE;
  curid hierarchy.id%TYPE;
BEGIN
  FOR i IN ids.FIRST .. ids.LAST LOOP
    curid := ids(i);
    LOOP
      SELECT parentid INTO curid FROM hierarchy WHERE hierarchy.id = curid;
      EXIT WHEN curid IS NULL;
      PIPE ROW(curid);
    END LOOP;
  END LOOP;
END;


--------------------------------------------------------
--  DDL for Function NX_GET_ANCESTORS
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_GET_ANCESTORS" (id VARCHAR2)
RETURN NX_STRING_TABLE
IS
  curid hierarchy.id%TYPE := id;
  newid hierarchy.id%TYPE;
  ret NX_STRING_TABLE := NX_STRING_TABLE();
  first BOOLEAN := TRUE;
BEGIN
  WHILE curid IS NOT NULL LOOP
    BEGIN
      SELECT parentid INTO newid FROM hierarchy WHERE hierarchy.id = curid;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      -- curid not in hierarchy at all
      newid := NULL;
    END;
    IF curid IS NOT NULL AND curid <> id THEN
      ret.EXTEND;
      ret(ret.COUNT) := curid;
    END IF;
    IF first AND newid IS NULL THEN
      BEGIN
        SELECT versionableid INTO newid FROM versions WHERE versions.id = curid;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN NULL;
      END;
    END IF;
    first := FALSE;
    curid := newid;
  END LOOP;
  RETURN ret;
END;


--------------------------------------------------------
--  DDL for Function NX_GET_READ_ACL
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_GET_READ_ACL" (id VARCHAR2)
RETURN VARCHAR2
-- Compute the merged read acl for a doc id
IS
  curid acls.id%TYPE := id;
  newid acls.id%TYPE;
  acl VARCHAR2(32767) := NULL;
  first BOOLEAN := TRUE;
  sep VARCHAR2(1) := '|';
  read_permissions NX_STRING_TABLE;
BEGIN
  SELECT permission BULK COLLECT INTO read_permissions FROM aclr_permission;
  WHILE curid IS NOT NULL LOOP
    FOR r in (SELECT * FROM acls
                WHERE permission MEMBER OF read_permissions
                AND acls.id = curid
                ORDER BY acls.pos) LOOP
      IF acl IS NOT NULL THEN
         acl := acl || sep;
      END IF;
      acl := acl || CASE WHEN r."GRANT" = 0 THEN '-' ELSE '' END || r."USER";
    END LOOP;
    -- recurse into parent
    BEGIN
      SELECT parentid INTO newid FROM hierarchy WHERE hierarchy.id = curid;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      -- curid not in hierarchy at all
      newid := NULL;
    END;
    IF first AND newid IS NULL THEN
      BEGIN
        SELECT versionableid INTO newid FROM versions WHERE versions.id = curid;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN NULL;
      END;
    END IF;
    first := FALSE;
    curid := newid;
  END LOOP;
  RETURN acl;
END;


--------------------------------------------------------
--  DDL for Function NX_GET_READ_ACL_ID
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_GET_READ_ACL_ID" (id VARCHAR2)
RETURN VARCHAR2
IS
BEGIN
  RETURN nx_hash(nx_get_read_acl(id));
END;


--------------------------------------------------------
--  DDL for Function NX_GET_READ_ACLS_FOR
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_GET_READ_ACLS_FOR" (users NX_STRING_TABLE)
RETURN NX_STRING_TABLE
-- List read acl ids for a list of user/groups, using the cache
IS
  user_md5 VARCHAR2(34) := sw_retrieve_user(users);
BEGIN
  RETURN SW_GET_READ_ACLS_FOR(user_md5);
END;


--------------------------------------------------------
--  DDL for Function NX_HASH
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_HASH" (string VARCHAR2)
RETURN VARCHAR2
IS
BEGIN
  -- hash function 1 is MD4 (faster than 2 = MD5)
  RETURN DBMS_CRYPTO.HASH(UTL_I18N.STRING_TO_RAW(string, 'AL32UTF8'), 1);
END;


--------------------------------------------------------
--  DDL for Function NX_HASH_USERS
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_HASH_USERS" (users NX_STRING_TABLE)
RETURN VARCHAR2
IS
  s VARCHAR2(32767) := NULL;
  sep VARCHAR2(1) := '|';
BEGIN
  -- TODO use canonical (sorted) order for users
  FOR i IN users.FIRST .. users.LAST LOOP
    IF s IS NOT NULL THEN
      s := s || sep;
    END IF;
    s := s || users(i);
  END LOOP;
  RETURN nx_hash(s);
END;


--------------------------------------------------------
--  DDL for Function NX_IN_TREE
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_IN_TREE" (id VARCHAR2, baseid VARCHAR2)
RETURN NUMBER IS
  curid hierarchy.id%TYPE := id;
BEGIN
  IF baseid IS NULL OR id IS NULL OR baseid = id THEN
    RETURN 0;
  END IF;
  LOOP
    SELECT parentid INTO curid FROM hierarchy WHERE hierarchy.id = curid;
    IF curid IS NULL THEN
      RETURN 0;
    ELSIF curid = baseid THEN
      RETURN 1;
    END IF;
  END LOOP;
END;


--------------------------------------------------------
--  DDL for Function NX_LIST_READ_ACLS_FOR
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_LIST_READ_ACLS_FOR" (users NX_STRING_TABLE)
RETURN NX_STRING_TABLE
-- List matching read acl ids for a list of user/groups
IS
  negusers NX_STRING_TABLE := NX_STRING_TABLE();
  aclusers NX_STRING_ARRAY;
  acluser VARCHAR2(32767);
  aclids NX_STRING_TABLE := NX_STRING_TABLE();
  sep VARCHAR2(1) := '|';
BEGIN
  -- Build a black list with negative users
  FOR n IN users.FIRST .. users.LAST LOOP
    negusers.EXTEND;
    negusers(n) := '-' || users(n);
  END LOOP;
  -- find match
  FOR r IN (SELECT acl_id, acl FROM aclr) LOOP
    aclusers := split(r.acl, sep);
    FOR i IN aclusers.FIRST .. aclusers.LAST LOOP
      acluser := aclusers(i);
      IF acluser MEMBER OF users THEN
        -- grant
        aclids.EXTEND;
        aclids(aclids.COUNT) := r.acl_id;
        GOTO next_acl;
      END IF;
      IF acluser MEMBER OF negusers THEN
        -- deny
        GOTO next_acl;
      END IF;
    END LOOP;
    <<next_acl>> NULL;
  END LOOP;
  RETURN aclids;
END;


--------------------------------------------------------
--  DDL for Function NX_NODEID
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "NX_NODEID" 
RETURN VARCHAR
IS
  cursid NUMBER := SYS_CONTEXT('USERENV', 'SID');
  curser NUMBER;
BEGIN
  SELECT SERIAL# INTO curser FROM GV$SESSION WHERE SID = cursid AND INST_ID = SYS_CONTEXT('USERENV', 'INSTANCE');
  RETURN cursid || ',' || curser;
END;


--------------------------------------------------------
--  DDL for Function SPLIT
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "SPLIT" (string VARCHAR2, sep VARCHAR2)
RETURN NX_STRING_ARRAY
-- splits a string, order matters
IS
  pos PLS_INTEGER := 1;
  len PLS_INTEGER := NVL(LENGTH(string), 0);
  i PLS_INTEGER;
  res NX_STRING_ARRAY := NX_STRING_ARRAY();
BEGIN
  WHILE pos <= len LOOP
    i := INSTR(string, sep, pos);
    IF i = 0 THEN i := len + 1; END IF;
    res.EXTEND;
    res(res.COUNT) := SUBSTR(string, pos, i - pos);
    pos := i + 1;
  END LOOP;
  RETURN res;
END;


--------------------------------------------------------
--  DDL for Function SW_GET_READ_ACLS_FOR
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "SW_GET_READ_ACLS_FOR" (userid VARCHAR)
RETURN NX_STRING_TABLE
-- List read acl ids for a list of user/groups, using the cache
IS
  in_cache NUMBER;
  aclids NX_STRING_TABLE;
BEGIN
        select distinct a.acl_id BULK COLLECT INTO aclids FROM SW_ACLR_USERID_USER u, SW_ACLR_USER_ACLID a
        WHERE u.USERGROUP = a.USERGROUP  AND REGEXP_LIKE(a.acl_id,'*[A-Z0-9]*','i') AND u.USER_ID= userid;
        RETURN aclids;
END;


--------------------------------------------------------
--  DDL for Function SW_RETRIEVE_USER
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "SW_RETRIEVE_USER" (usergroups NX_STRING_TABLE)
RETURN VARCHAR
IS
        PRAGMA AUTONOMOUS_TRANSACTION; -- needed for insert, ok since what we fill is a cache
        user_md5 VARCHAR2(34) := nx_hash_users(usergroups);
        is_decl number := 0;
BEGIN
        select count(*) INTO is_decl FROM SW_ACLR_USERID_USER WHERE user_id = user_md5;
        IF is_decl = 0 THEN
                -- DBMS_OUTPUT.PUT_LINE('sw_add_user : '||user_md5);
                INSERT INTO SW_ACLR_USERID_USER (select user_md5, column_value from TABLE(usergroups));
                FOR t in (select t.column_value AS c from TABLE(usergroups)  t WHERE NOT EXISTS(SELECT 1 FROM SW_ACLR_USER_ACLID  WHERE usergroup = t.column_value)) LOOP
                        SW_FILL_ACLID(t.c);
                END LOOP;
                COMMIT;
        -- ELSE
                -- DBMS_OUTPUT.PUT_LINE('skip_user : '||user_md5);
        END IF;
        RETURN user_md5;
END;


--------------------------------------------------------
--  DDL for Procedure COMPUTEPRECOMPTAGE
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE "UPDATE_CONNEXITE" AS
-- Met à jour le champ connexité contenu dans la table question
BEGIN
        update question set connexite=0;
  DBMS_OUTPUT.PUT_LINE('start updateConnexite');
  FOR connexan in (SELECT COUNT(1) AS nbre_doublon, hashconnexitean as texte FROM question where hashconnexitean is not null GROUP BY hashconnexitean) LOOP
    IF connexan.nbre_doublon > 1 THEN
     update question q set q.connexite = q.connexite + connexan.nbre_doublon where q.hashconnexitean = connexan.texte;
    END IF;
  END LOOP;
  FOR connextitre in (SELECT COUNT(1) AS nbre_doublon, hashconnexitetitre as texte FROM question where hashconnexitetitre is not null GROUP BY hashconnexitetitre) LOOP
    IF connextitre.nbre_doublon > 1 THEN
      update question q set q.connexite = q.connexite + connextitre.nbre_doublon where q.hashconnexitean = connextitre.texte;
    END IF;
   END LOOP;
  FOR connextexte in (SELECT COUNT(1) AS nbre_doublon, hashconnexitetexte as texte FROM question where hashconnexitetexte is not null GROUP BY hashconnexitetexte) LOOP
    IF connextexte.nbre_doublon > 1 THEN
     update question q set q.connexite = q.connexite + connextexte.nbre_doublon where q.hashconnexitean = connextexte.texte;
    END IF;
   END LOOP;
   DBMS_OUTPUT.PUT_LINE('end updateConnexite');
END;




  CREATE OR REPLACE PROCEDURE "COMPUTEPRECOMPTAGE" AS
BEGIN
	delete from RMLBX_PRECALCULLIST;
	DBMS_OUTPUT.PUT_LINE('start genPrecomptage');
	FOR rep in (select rep.id from hierarchy rep where rep.primarytype = 'ReponsesMailbox') LOOP
		computePrecomptageMailbox(rep.id);
	END LOOP;
END;


--------------------------------------------------------
--  DDL for Procedure COMPUTEPRECOMPTAGEMAILBOX
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "COMPUTEPRECOMPTAGEMAILBOX" (mailboxDocId VARCHAR2) AS
BEGIN
	DBMS_OUTPUT.PUT_LINE('genPrecomptage for '||mailboxDocId);
	DELETE FROM RMLBX_PRECALCULLIST WHERE ID = mailboxDocId;
	INSERT INTO RMLBX_PRECALCULLIST(ID,ITEM) SELECT H.PARENTID, idMinistereAttributaire  || ':' || routingTaskType || ':' || count(*)
		FROM HIERARCHY H, DOSSIER_REPONSES_LINK L, MISC M WHERE H.ID = L.ID AND H.PARENTID = mailboxDocId
		AND H.ID = M.ID AND M.LIFECYCLESTATE = 'todo'
		GROUP BY H.PARENTID, L.idMinistereAttributaire, L.routingTaskType;
	INSERT INTO CLUSTER_INVALS (SELECT NODEID, mailboxDocId, 'rmlbx:preCalculList', 1 FROM CLUSTER_NODES);
END;


--------------------------------------------------------
--  DDL for Procedure NX_CLUSTER_INVAL
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "NX_CLUSTER_INVAL" (i VARCHAR2, f VARCHAR2, k INTEGER, nid VARCHAR)
IS
BEGIN
  FOR c IN (SELECT nodeid FROM cluster_nodes WHERE nodeid <> nid) LOOP
    INSERT INTO cluster_invals (nodeid, id, fragments, kind) VALUES (c.nodeid, i, f, k);
  END LOOP;
END;


--------------------------------------------------------
--  DDL for Procedure NX_INIT_ANCESTORS
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "NX_INIT_ANCESTORS" 
IS
BEGIN
  EXECUTE IMMEDIATE 'TRUNCATE TABLE ancestors';
  INSERT INTO ancestors
    SELECT id, nx_get_ancestors(id)
    FROM (SELECT id FROM hierarchy WHERE isproperty=0);
END;


--------------------------------------------------------
--  DDL for Procedure NX_REBUILD_READ_ACLS
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "NX_REBUILD_READ_ACLS" 
-- Rebuild the read acls tables
IS
BEGIN
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE hierarchy_read_acl';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_modified';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE SW_ACLR_USERID_USER';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE SW_ACLR_USER_ACLID';
  INSERT INTO hierarchy_read_acl
    SELECT id, nx_get_read_acl_id(id)
      FROM (SELECT id FROM hierarchy WHERE isproperty = 0);
END;


--------------------------------------------------------
--  DDL for Procedure NX_UPDATE_READ_ACLS
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "NX_UPDATE_READ_ACLS" 
-- Rebuild only necessary read acls
IS
  update_count PLS_INTEGER;
BEGIN
  --
  -- 1/ New documents, no new ACL
  MERGE INTO hierarchy_read_acl t
    USING (SELECT DISTINCT(m.hierarchy_id) id
            FROM aclr_modified m
            JOIN hierarchy h ON m.hierarchy_id = h.id
            WHERE m.is_new = 1) s
    ON (t.id = s.id)
    WHEN NOT MATCHED THEN
      INSERT (id, acl_id) VALUES (s.id, nx_get_read_acl_id(s.id));
  DELETE FROM aclr_modified WHERE is_new = 1;
  --
  -- 2/ Compute the new read ACLS for updated documents
  UPDATE hierarchy_read_acl SET acl_id = nx_get_read_acl_id(id) WHERE id IN (
    SELECT h.id
      FROM hierarchy h
      start with h.id IN (select hierarchy_id from aclr_modified)
      connect by prior h.id = h.parentid);
  DELETE FROM aclr_modified;
  --
END;


--------------------------------------------------------
--  DDL for Procedure NX_VACUUM_READ_ACLS
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "NX_VACUUM_READ_ACLS" 
-- Remove unused read acls entries
IS
BEGIN
  -- nx_vacuum_read_acls vacuuming
  DELETE FROM aclr WHERE acl_id IN (SELECT r.acl_id FROM aclr r
    JOIN hierarchy_read_acl h ON r.acl_id=h.acl_id
    WHERE h.acl_id IS NULL);
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_modified';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE SW_ACLR_USERID_USER';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE SW_ACLR_USER_ACLID';
END;


--------------------------------------------------------
--  DDL for Procedure SW_FILL_ACLID
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "SW_FILL_ACLID" (usergroup VARCHAR) IS
  negusers NX_STRING_TABLE := NX_STRING_TABLE();
  aclusers NX_STRING_ARRAY;
  acluser VARCHAR2(32767);
  aclids NX_STRING_TABLE := NX_STRING_TABLE();
  sep VARCHAR2(1) := '|';
BEGIN
  -- Build a black list with negative users
  negusers.EXTEND;
  negusers(1) := '-' || usergroup;
  --
  -- find match
  FOR r IN (SELECT acl_id, acl FROM aclr) LOOP
    aclusers := split(r.acl, sep);
    FOR i IN aclusers.FIRST .. aclusers.LAST LOOP
      acluser := aclusers(i);
      IF acluser = usergroup THEN
        -- grant
        aclids.EXTEND;
        aclids(aclids.COUNT) := r.acl_id;
        GOTO next_acl;
      END IF;
      IF acluser MEMBER OF negusers THEN
        -- deny
        GOTO next_acl;
      END IF;
    END LOOP;
    <<next_acl>> NULL;
  END LOOP;
  --
  IF aclids.COUNT = 0 THEN
    INSERT INTO SW_ACLR_USER_ACLID VALUES(usergroup, NULL);
  ELSE
    INSERT INTO SW_ACLR_USER_ACLID (SELECT usergroup, COLUMN_VALUE FROM TABLE (aclids));
  END IF;
END;


--------------------------------------------------------
--  DDL for Procedure SW_REBUILD_MISSING_READ_ACLS
--------------------------------------------------------


  CREATE OR REPLACE PROCEDURE "SW_REBUILD_MISSING_READ_ACLS" 
-- Rebuild the missing read acls in hierarchy_read_acl
IS
BEGIN
  INSERT INTO hierarchy_read_acl
    SELECT id, nx_get_read_acl_id(id)
      FROM (select h.id from HIERARCHY h left join hierarchy_read_acl hr on h.id = hr.id where h.ISPROPERTY = 0 and hr.id is null);
/
END;






-----------------------------------------------------
-- Insertion des données de l'organigramme
-----------------------------------------------------

--------------------------------------------------------
--  Fichier créé - jeudi-juin-23-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table ENTITE
--------------------------------------------------------

  CREATE TABLE "REPONSES_QA"."ENTITE" 
   (	"ID_ORGANIGRAMME" VARCHAR2(255 CHAR), 
	"DATE_DEBUT" TIMESTAMP (6), 
	"DATE_FIN" TIMESTAMP (6), 
	"DATE_VERROU" TIMESTAMP (6), 
	"DELETED" NUMBER(1,0), 
	"FUNCTION_READ" VARCHAR2(255 CHAR), 
	"LABEL" VARCHAR2(255 CHAR), 
	"UTILISATEUR_VERROU" VARCHAR2(255 CHAR), 
	"CIVILITE_MINISTRE" VARCHAR2(255 CHAR), 
	"EDITION" VARCHAR2(255 CHAR), 
	"FORMULE" VARCHAR2(255 CHAR), 
	"NOM_MINISTRE" VARCHAR2(255 CHAR), 
	"NOR_MINISTERE" VARCHAR2(255 CHAR), 
	"ORDRE" NUMBER(19,0), 
	"ID_PARENT_GOUV" VARCHAR2(255 CHAR), 
	"PRENOM_MINISTRE" VARCHAR2(255 CHAR), 
	"SUIVI_ACTIVITE_NORMATIVE" NUMBER(1,0)
);
--------------------------------------------------------
--  DDL for Table POSTE
--------------------------------------------------------

  CREATE TABLE "REPONSES_QA"."POSTE" 
   (	"ID_ORGANIGRAMME" VARCHAR2(255 CHAR), 
	"DATE_DEBUT" TIMESTAMP (6), 
	"DATE_FIN" TIMESTAMP (6), 
	"DATE_VERROU" TIMESTAMP (6), 
	"DELETED" NUMBER(1,0), 
	"FUNCTION_READ" VARCHAR2(255 CHAR), 
	"LABEL" VARCHAR2(255 CHAR), 
	"UTILISATEUR_VERROU" VARCHAR2(255 CHAR), 
	"CHARGE_MISSION_SGG" NUMBER(1,0), 
	"CONSEILLER_PM" NUMBER(1,0), 
	"MEMBRES" VARCHAR2(255 CHAR), 
	"ID_PARENT_ENTITE" VARCHAR2(255 CHAR), 
	"ID_PARENT_UNITE" VARCHAR2(255 CHAR), 
	"POSTE_BDC" NUMBER(1,0), 
	"POSTE_WS" NUMBER(1,0), 
	"SUPERVISEUR_SGG" NUMBER(1,0), 
	"WS_CLE" VARCHAR2(255 CHAR), 
	"WS_LOGIN" VARCHAR2(255 CHAR), 
	"WS_MDP" VARCHAR2(255 CHAR), 
	"WS_URL" VARCHAR2(255 CHAR)
   );

--------------------------------------------------------
--  DDL for Table GOUVERNEMENT
--------------------------------------------------------

  CREATE TABLE "REPONSES_QA"."GOUVERNEMENT" 
   (	"ID_ORGANIGRAMME" VARCHAR2(255 CHAR), 
	"DATE_DEBUT" TIMESTAMP (6), 
	"DATE_FIN" TIMESTAMP (6), 
	"DATE_VERROU" TIMESTAMP (6), 
	"DELETED" NUMBER(1,0), 
	"FUNCTION_READ" VARCHAR2(255 CHAR), 
	"LABEL" VARCHAR2(255 CHAR), 
	"UTILISATEUR_VERROU" VARCHAR2(255 CHAR)
   );

--------------------------------------------------------
--  DDL for Table UNITE_STRUCTURELLE
--------------------------------------------------------

  CREATE TABLE "REPONSES_QA"."UNITE_STRUCTURELLE" 
   (	"ID_ORGANIGRAMME" VARCHAR2(255 CHAR), 
	"DATE_DEBUT" TIMESTAMP (6), 
	"DATE_FIN" TIMESTAMP (6), 
	"DATE_VERROU" TIMESTAMP (6), 
	"DELETED" NUMBER(1,0), 
	"FUNCTION_READ" VARCHAR2(255 CHAR), 
	"LABEL" VARCHAR2(255 CHAR), 
	"UTILISATEUR_VERROU" VARCHAR2(255 CHAR), 
	"NOR_DIRECTION" VARCHAR2(255 CHAR), 
	"ID_PARENT_ENTITE" VARCHAR2(255 CHAR), 
	"ID_PARENT_INSTITUTION" VARCHAR2(255 CHAR), 
	"ID_PARENT_UNITE" VARCHAR2(255 CHAR), 
	"TYPE" VARCHAR2(255 CHAR)
   );

Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('13370000',to_timestamp('15/01/09 01:00:00','DD/MM/RR HH24:MI:SS'),to_timestamp(null,'DD/MM/RR HH24:MI:SS'),null,'0','AssembleesParlementairesReader','Assemblées parlementaires',null,'à renseigner','Assemblées parlementaires','à renseigner','à renseigner',null,'18','gouv_1','à renseigner',null);

Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000507',to_timestamp('19/03/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Economie, industrie et emploi',null,'à renseigner','Min. Economie, industrie et emploi','à renseigner','à renseigner',null,'7','gouv_1','à renseigner',null);

Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000607',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Premier Ministre',null,'à renseigner','Premier Ministre','à renseigner','à renseigner',null,'17','gouv_1','à renseigner',null);


Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000632',to_timestamp('18/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Intérieur, outre-mer et collectiv.',null,'à renseigner','Min. Intérieur, outre-mer et collectiv.','à renseigner','à renseigner',null,'11','gouv_1','à renseigner',null);


Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000937',to_timestamp('18/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Affaires étrangères et européennes',null,'à renseigner','Min. Affaires étrangères et européennes','à renseigner','à renseigner',null,'1','gouv_1','à renseigner',null);

Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000938',to_timestamp('18/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Agriculture et pêche',null,'à renseigner','Min. Agriculture et pêche','à renseigner','à renseigner',null,'2','gouv_1','à renseigner',null);


Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000940',to_timestamp('18/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Enseignement sup. et recherche',null,'à renseigner','Min. Enseignement sup. et recherche','à renseigner','à renseigner',null,'9','gouv_1','à renseigner',null);

Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000966',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Culture et communication',null,'à renseigner','Min. Culture et communication','à renseigner','à renseigner',null,'4','gouv_1','à renseigner',null);


Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000967',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Défense',null,'à renseigner','Min. Défense','à renseigner','à renseigner',null,'5','gouv_1','à renseigner',null);


Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000968',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Education nationale',null,'à renseigner','Min. Education nationale','à renseigner','à renseigner',null,'8','gouv_1','à renseigner',null);


Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000969',to_timestamp('15/01/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Travail, rel soc, fam, solida, vil.',null,'à renseigner','Min. Travail, rel soc, fam, solida, vil.','à renseigner','à renseigner',null,'16','gouv_1','à renseigner',null);
/



Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000972',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Justice',null,'à renseigner','Min. Justice','à renseigner','à renseigner',null,'12','gouv_1','à renseigner',null);
/



Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000975',to_timestamp('19/01/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Santé et sports',null,'à renseigner','Min. Santé et sports','à renseigner','à renseigner',null,'15','gouv_1','à renseigner',null);
/



Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50000976',to_timestamp('19/03/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Ecologie, énerg, dév d, amgt  terr',null,'à renseigner','Min. Ecologie, énerg, dév d, amgt  terr','à renseigner','à renseigner',null,'6','gouv_1','à renseigner',null);
/



Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50001885',to_timestamp('18/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Immigration, intégr., id. nat.',null,'à renseigner','Min. Immigration, intégr., id. nat.','à renseigner','à renseigner',null,'10','gouv_1','à renseigner',null);
/



Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50001886',to_timestamp('15/01/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Logement',null,'à renseigner','Min. Logement','à renseigner','à renseigner',null,'13','gouv_1','à renseigner',null);
/



Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50003832',to_timestamp('09/11/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. outre-mer',null,'à renseigner','Min. outre-mer','à renseigner','à renseigner',null,'14','gouv_1','à renseigner',null);
/



Insert into REPONSES_QA.ENTITE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CIVILITE_MINISTRE,EDITION,FORMULE,NOM_MINISTRE,NOR_MINISTERE,ORDRE,ID_PARENT_GOUV,PRENOM_MINISTRE,SUIVI_ACTIVITE_NORMATIVE) values ('50013998',to_timestamp('18/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. Budget, comptes publics et FP',null,'à renseigner','Min. Budget, comptes publics et FP','à renseigner','à renseigner',null,'3','gouv_1','à renseigner',null);

commit;

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('13370001',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Utilisateur Webservices (Parlementaire)',null,'0','0','adminsgg;ws_an;ws_senat','13370000',null,'0','0','0',null,null,null,null);


Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('13370002',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Utilisateur Webservices (Agriculture)',null,'0','0','ws_agriculture','50000938',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('13370003',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Utilisateur Webservices (Ecologie)',null,'0','0','ws_ecologie','50000976',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('13380003',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Utilisateur Webservices (Economie)',null,'0','0','ws_eco','50000507',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000609',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents direction générale des entrep.',null,'0','0','CBISSERIER;DCITRON;ELEDEVEDEC;JBARDOLLET;MDGADAT;MJCYRILLE',null,'50000608','0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000611',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller Budget PM',null,'0','0','JDUBERTRET;SBUDGETAIRE',null,'50000610','0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000612',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère Aff. éco et finances PM',null,'0','0','DDAMARZIT;SECFINANCES;agriculture_ecofinance;ecofinance;ecologie_ecofinance;finance_ecofinance;outremer_ecofinance;travail_ecofinance',null,'50000610','0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000613',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller dev durable, recher, indus PM',null,'0','0','SECINDUSTRIE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000620',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller pour les affaires économiques',null,'0','0','LFOURQUET;TCOURRET',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000621',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargée de mission intérieur DOM-TOM',null,'0','0','NDECADENET;SRIMEU',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000622',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission équipement logement',null,'0','0','JGREFFE;LLAGARDE',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000627',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller Affaires intérieures PM',null,'0','0','MFUZEAU;SECINTERIEUR',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000628',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(Ancien) Conseiller dev durable PM',null,'0','0','adminsgg',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000642',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétaire de la section des finances',null,'0','0','JLLIPSKI;VGALY',null,'50000634','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000643',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétaire de la section de l''intérieur',null,'0','0','FKERBANE;KVERDU;LLEANDRI;MFRANCESCHI;NBELVEZET',null,'50000637','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000644',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétaire de la section des travx pubs',null,'0','0','AMICALOWA;NPELAT;SNEVERS',null,'50000638','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000645',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétaire de la section sociale',null,'0','0','EPLUCHE;JBEYGUESIER;MAMORERE;MFM''FOUILOU;TBRETON',null,'50000639','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000647',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents direction du budget',null,'0','0','AGIRARDIN;FHUYNH;MBAKER;RCHUA',null,'50000646','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000652',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC Ecologie MEDAD',null,'0','0','CPOINSARD;FIGLESIAS;JMGEUS;MROULEAU;RGUIGUES;ecologie_bdc',null,'50000984','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000656',to_timestamp('19/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (Economie)',null,'0','0','CPAROLINI;FISAIA;JCHIFFRE;MDALLARD;MQPA;PETCHEVERRY;SEC-BDC;T_ADM_ENT;bdc;finance_bdc;minagriculture;mince;minecologie;minfinance;minoutremer;mintravail;outremer_bdc',null,'50000655','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000658',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DPAEP (Economie)',null,'0','0','CCOURIO;MVILLAIN;SPEIDRO',null,'50000657','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000660',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGTPE (Economie)',null,'0','0','DGTPE-AFT;DGTPE-AMD;DGTPE-APE;DGTPE-ASSUR;DGTPE-BF;DGTPE-CABDG;DGTPE-DV;DGTPE-FINENT;DGTPE-SGE',null,'50000659','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000682',to_timestamp('24/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de missions DJO',null,'0','0','JMHELBECQUE;PVIRUEGA',null,'50000629','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000685',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable du dépt activité normative',null,'0','0','DARMIN',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000739',to_timestamp('10/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents du service de documentation',null,'0','0','DOCUMS;JURISTES;PPETITCOLLOT;SBONNISSENT',null,'50000737','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000761',to_timestamp('12/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur du Cab. PM',null,'0','0','JPFAUGERE;SECDIRCABPM',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000788',to_timestamp('18/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'testeurs',null,'0','0','N_CONTRIB_1;N_CONTRIB_2;N_CONTRIB_3;N_CONTRIB_5;XCHEN',null,'50000648','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000810',to_timestamp('19/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents affaires juridiques DEV',null,'0','0','ADESBROSSES;ADESOUCHES;CPOINSARD;FAGOSTINI;FPEROUZE;PCAPS;PHRICOLEDIAG;SSAINTGERMAI',null,'50000653','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000873',to_timestamp('10/10/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGCL Cabinet',null,'0','0','EJOSSA',null,'50000866','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000882',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission industrie agriculture',null,'0','0','BAVENTINO;ETISON',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000883',to_timestamp('24/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef rédaction DJO',null,'0','0','XPAYS',null,'50000629','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000907',to_timestamp('26/06/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agent rédaction DJO',null,'0','0','CBRUN;CLEMEVEL;FBIERNE;FGUILLOT;JROA;LCADET;LKARAR;PALBA;PBELLEFOND;RVAULTIER;SVIGNAL;XPAYS',null,'50000631','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000910',to_timestamp('01/12/06 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs',null,'0','0','MTAILLEFER;TMICHAUX;adminsgg',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000958',to_timestamp('01/12/06 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs',null,'0','0','CDUMONT;MTAILLEFER;TMICHAUX;adminsgg',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000964',to_timestamp('02/01/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef de la mission qualité de la norme',null,'0','0','JPBALCOU;PHMECHET',null,'50000963','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50000965',to_timestamp('02/01/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef de la mission DPDD',null,'0','0','T_ADMIN_ENT;adminsgg',null,'50000962','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001013',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'RH1',null,'0','0','JAUMOND;NATRAOUI',null,'50001010','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001014',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'RH2',null,'0','0','DBARBA',null,'50001010','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001015',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'RH4',null,'0','0','FGUERITZ',null,'50001010','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001016',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAF1',null,'0','0','DAF1BUD;DAF1REM;NCROWET',null,'50001011','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001017',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAF2',null,'0','0','DAF2C;DAF2E;DAF2QAF;JYRAIMBAULT;MAMASSON',null,'50001011','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001018',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Bureau cabinet MAE',null,'0','0','PBARAGHINI',null,'50000946','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001019',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGA MAE',null,'0','0','DGA;GOLLAGNIER',null,'50000947','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001020',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Serv. aff. juridiques internes',null,'0','0','LGUILLOTEAU;SAJI',null,'50001012','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001029',to_timestamp('16/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau Cabinet outre-mer',null,'0','0','FSAINTECROIX;NGUIRIABOYE;TLANDRY',null,'50001026','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001030',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau du droit public',null,'0','0',null,null,'50001088','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001031',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Justice)',null,'0','0','ICROSO',null,'50001088','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001039',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agts Dir. Fr étranger et étr. en France',null,'0','0','DFAE',null,'50000948','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001040',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DAJ',null,'0','0','BBAIRI;BBENTAYEB',null,'50000949','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001058',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Santé (Min. SJS)',null,'0','0','CHIDEUX;JMHAUTION',null,'50001043','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001059',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGS (Min. SJS)',null,'0','0','ABELLEGUIC;CAGRICOLE;JBOUTELOUP;NTRUCHOT;PBUZELIN;SNARDIN',null,'50001046','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001061',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DAGPB (Min. SJS)',null,'0','0','ADELOFFRE;CAMBROISE;CBELGACEM;EMIGEVANT;GSICART;IPILLAZ;JLLADRIX;XREGORD',null,'50001044','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001062',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DHOS (Min. SJS)',null,'0','0','CELUAUD;CMERCIER;DFEBI;JKOCH;MADJIAGE;MCHANELIERE;NGRENET;RPULVAL;SBENIDJER;SSOUPRA',null,'50001047','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001063',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DSS (Min. SJS)',null,'0','0','AMBOUCHARD;BGUERINEAU;CMARTIN;COGETGENDRE;DCAMUS;FLEMORVAN;JLREY;JPVINQUANT;MDELILLE;MSCHNEPF;PDEVAUX',null,'50001048','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001064',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DREES (Min. SJS)',null,'0','0','PFLEUTIAUX',null,'50001049','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001065',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DIF (Min. MTS)',null,'0','0','MBMAIZY',null,'50001056','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001066',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DIPH (MTS)',null,'0','0','SKOMPANY',null,'50001082','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001067',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau du droit international (Défense)',null,'0','0','BLINARI',null,'50001154','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001069',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. études et organisation (Défense)',null,'0','0','DBAVART',null,'50001155','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001097',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Justice',null,'0','0','TDAVIAU',null,'50001086','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001098',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secteur txt réglementaires (Justice)',null,'0','0','PMERLET;PTALABERT;SGOUPIL;SHAUGUEL;TDAVIAU',null,'50001086','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001099',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. aff. criminelles et des grâces',null,'0','0','ASAFFAR;FLEGUNEHEC;JMHUET;MBIERRY;NBECACHE;NMALASSIS',null,'50001089','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001107',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. droit économique',null,'0','0','NRICHOUX',null,'50001088','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001108',to_timestamp('17/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. droit civil',null,'0','0','CORHANT',null,'50002486','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001109',to_timestamp('17/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. prof. jud. et juridiques',null,'0','0','RBOURJOUANE',null,'50002487','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001114',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGEMP',null,'0','0','AMSALA;CBAZILE;CKOTFILA;PMARNE;ecologie_dgemp',null,'50000817','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001115',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents CGIET',null,'0','0','IVALLET;JCHANDELIER;JLMORTIER;PVANNIER',null,'50001057','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001116',to_timestamp('23/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef sect. informat. et bur. (Défense)',null,'0','0','JDCLERGET',null,'50001147','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001121',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau FP1 (DGCL)',null,'0','0','CCHAGNET',null,'50001306','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001122',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau FP2 (DGCL)',null,'0','0','VLEGLEUT',null,'50001306','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001123',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau FP3 (DGCL)',null,'0','0','PMOUTAFIAN',null,'50001306','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001124',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL1 (DGCL)',null,'0','0','AKOUTCHOUK',null,'50001307','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001125',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL2 (DGCL)',null,'0','0','MVILLIERS',null,'50001307','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001126',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL3 (DGCL)',null,'0','0','FL3',null,'50001307','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001127',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL5 (DGCL)',null,'0','0','OCHENAIN',null,'50001307','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001128',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL1 (DGCL)',null,'0','0','PHELBERT',null,'50001308','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001129',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL2 (DGCL)',null,'0','0','CIL2',null,'50001308','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001130',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL3 (DGCL)',null,'0','0','CIL3',null,'50001308','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001131',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL4 (DGCL)',null,'0','0','CIL4;DBRUEL',null,'50001308','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001149',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef BDC Défense',null,'0','0','PGAILLARD;RLEBAS',null,'50001147','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001150',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef de la section BC. 2 (Défense)',null,'0','0','FCHATELET',null,'50001147','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001151',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section BC. 2 (Défense)',null,'0','0','CCHEVREUX;CPOULARD;MCARPENTIER',null,'50001147','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001158',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. org, modern, aménagt struct (Défense)',null,'0','0','BHERELLE;LPHILIPPON',null,'50001153','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001159',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. expertise gal et légistique (Défense)',null,'0','0','PALAIN',null,'50001153','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001160',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B.  droit santé et envir. (Défense)',null,'0','0','ACALVIGNAC',null,'50001153','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001161',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cellule CADA, CNIL, médiateur (Défense)',null,'0','0','PBANZET',null,'50001153','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001176',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC travail ("Grenelle")',null,'0','0','BYVENOU;DLACOTTE;JMBOUSQUET;travail_bdc',null,'50000990','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001177',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGT-RT2',null,'0','0','ABEAUMARD;BESPINASSOUS;CLASSALLE;DMUNOZ;PPINTO',null,'50001163','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001178',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGT-CPS',null,'0','0','BDJEBALI;PAHOPITAL',null,'50001163','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001179',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAGEMO-MRSS',null,'0','0','VSCHWAB',null,'50001164','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001180',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAGEMO-BGPSD',null,'0','0','CCOUCKE;LGROSSE',null,'50001164','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001181',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAGEMO-BGPEF',null,'0','0','SPLANCHE',null,'50001164','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001182',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAGEMO-BRHAMS',null,'0','0','JDRON',null,'50001164','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001183',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-SG décret',null,'0','0','FLOQUET',null,'50001165','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001184',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-N3 rapportant',null,'0','0','CMODAT',null,'50001165','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001185',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-Direction',null,'0','0','JSOLTANI',null,'50001165','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001186',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-DM12',null,'0','0','MCPIRIOU',null,'50001165','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001187',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) DGEFP titre professionnel',null,'0','0',null,null,'50001166','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001188',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGEFP',null,'0','0','CMOREL;GDAUTANE;MDUDOME;MVANNIER;dgefp;finance_dgefp;outremer_dgefp',null,'50001166','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001189',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents IGAS',null,'0','0','LJABOL',null,'50001167','0','0','0',null,null,null,null);
/

commit;

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001190',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/DH 2',null,'0','0','CTETELBOM',null,'50001168','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001191',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents SDFE',null,'0','0','CPARESCHI',null,'50001169','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001192',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable Conseils d''administration',null,'0','0','CCANO;GFUCHS;PPETITCOLLOT',null,'50000737','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001194',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDE MEDAD',null,'0','0','JPAGULLO;JSIRONNEAU;MFAIVRE;RDOSSENA',null,'50000832','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001195',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Assistante chef bureau agences de l''eau',null,'0','0','NDROUET',null,'50000832','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001196',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau carto. risques et aménagement',null,'0','0','IDINIZ',null,'50000833','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001197',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service environn. industriel contentieux',null,'0','0','CGUYOT;MMAUFFRET',null,'50000833','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001198',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. produits et déchets',null,'0','0','MCCOURAUD',null,'50000833','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001199',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission Natura 2000',null,'0','0','JGODART;SSCHIANO',null,'50000834','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001200',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. chasse, faune et flore',null,'0','0','CSERVOT',null,'50000834','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001201',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des sites',null,'0','0','BFOURE',null,'50000834','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001202',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau réglement. intégration environ.',null,'0','0','MFFACON;VTIBI',null,'50000835','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001204',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. conseiller affaires économiques',null,'0','0','DBLIND;MNORTHEE',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001205',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. chargé de mission intérieur DOM-TOM',null,'0','0','CSIMOES;JCCARRER',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001206',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. chargé de mission équipement logt.',null,'0','0','CJANDOT;XMADORE',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001212',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGDDI',null,'0','0','CSABIRON;ECARTOU;HMARJOLLET;NLABADIE',null,'50000954','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001214',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGCP',null,'0','0','AMEISSIMILLY;CSOULI;EROGACZEWSKI;MPOLLEDRI;OANXIONNAZ',null,'50000838','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001215',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGME',null,'0','0','JMSARROT;PSUCEVIC',null,'50000648','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001216',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents service des pensions',null,'0','0','HLECA',null,'50000955','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001217',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents sec. général (Economie)',null,'0','0','MTEXIER;SCRENDAL;VMAPELLI;finance_secgeneco;outremer_secgeneco;secgeneco',null,'50000889','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001218',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGCCRF (Economie)',null,'0','0','MFMEOT;MMARNAS',null,'50000836','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001219',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DAJ (Economie)',null,'0','0','CBERGEAL;FAMERIGO;JSARNEL',null,'50001021','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001220',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents IGF (Economie)',null,'0','0','AFLAMANT;CLCANET',null,'50001022','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001221',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents INSEE (Economie)',null,'0','0','FVILLENEUVE;MCHAMAK;MPOLLEDRI2;NMAURIN;PGRISELLE',null,'50001023','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001222',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agts contrôle gal éco. et fin (Economie)',null,'0','0','FGENVRIN',null,'50001024','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001223',to_timestamp('22/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Haut fonctionnaire défense (Economie)',null,'0','0','SGUITEL',null,'50001025','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001237',to_timestamp('23/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef IGA (Intérieur)',null,'0','0','TKLINGER',null,'50000871','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001238',to_timestamp('10/10/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGCL ML',null,'0','0','JRICHARD',null,'50000866','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001251',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau des cabinets (Intérieur)',null,'0','0','MBAMEUL',null,'50001248','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001260',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. cabinet collectivités terr.',null,'0','0',null,null,'50001118','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001279',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du cabinet Intérieur',null,'0','0','SBLANCHOIN',null,'50000865','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001281',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission TIC',null,'0','0','SBLANCHOIN',null,'50001248','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001291',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sect. aff. réservées et budg (Intérieur)',null,'0','0','CLIECHTI;SGCAB',null,'50001283','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001297',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau élections et études pol.',null,'0','0','SBOURRON',null,'50001285','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001298',to_timestamp('01/12/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau associations et fondations',null,'0','0','MLOTTIER',null,'50001365','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001299',to_timestamp('01/12/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau central des cultes',null,'0','0','BCCULTES',null,'50001365','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001300',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bur. déconcentr. et pol. terr. Etat',null,'0','0','BPA;NCOLIN',null,'50001285','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001301',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du cabinet',null,'0','0','PMAGNIER',null,'50001286','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001302',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. des personnels',null,'0','0','MKIRRY',null,'50001286','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001303',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. recrutement et formation',null,'0','0','ITOBIA',null,'50001286','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001304',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. évaluation, perf, aff. fin. et immo',null,'0','0','BMUNCH',null,'50001287','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001310',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC sports',null,'0','0','IJOSSE;JGOMIS',null,'50001309','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001312',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DRH Adm. et coord. gale',null,'0','0','AMDEBAUW;BOURNONVILLE;CCALCAGNI',null,'50001311','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001316',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agent Dir jeunesse et éduc. populaire',null,'0','0','JCILPA',null,'50001315','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001318',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Dir vie asso, emploi, formations',null,'0','0','MSECK',null,'50001317','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001323',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. des libertés publiques',null,'0','0','SDLPCAB',null,'50001365','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001327',to_timestamp('01/12/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau des cercles et jeux',null,'0','0','VSOPRANA',null,'50001364','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001328',to_timestamp('01/12/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau nat., titres d''identité et voyage',null,'0','0','MCCORNEC',null,'50001365','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001333',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPN Cabinet',null,'0','0','CABDGPN;DGPNDECO',null,'50001332','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001382',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. circ. et sécurité routière',null,'0','0','PSALLES',null,'50001367','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001384',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau surveillance réseau routier',null,'0','0','BSRR;DSARTHOU',null,'50001367','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001390',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur de cabinet DDSC',null,'0','0','CAUMONIER',null,'50001386','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001391',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjoint sous-dir. gestion des risques',null,'0','0','PLEFEBVRE',null,'50001387','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001392',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau services incendie et secours',null,'0','0','ALAUGA',null,'50001388','0','0','0',null,null,null,null);


Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001393',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau statuts et dialogue social',null,'0','0','EPLUMEJEAU;MPORTEOUS',null,'50001388','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001394',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau affaires fin. et juridiques',null,'0','0','DJANKOWIAK',null,'50001389','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001406',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des affaires générales',null,'0','0',null,null,'50001405','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001407',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Coordination SGDN',null,'0','0','ADELETANG;FBILLON',null,'50001405','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001408',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat central SGDN',null,'0','0','MBLET;PLIENARD',null,'50001405','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001410',to_timestamp('14/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller auprès du PM',null,'0','0','IMITROFANOFF;SMITROFANOFF',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001412',to_timestamp('14/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur adj Cab PM Gosset-Grainville',null,'0','0','AGOSSET;SECGOSSET',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001414',to_timestamp('14/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du Cabinet PM',null,'0','0','FROBINE;SECCHEFCAB',null,'50000610','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001415',to_timestamp('14/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef adjoint du Cabinet PM',null,'0','0','GDUFEIGNEUX',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001416',to_timestamp('14/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du Cabinet militaire PM',null,'0','0','HRENAUD;PVILLIERS;SECDENUEL;TPINEAUD',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001417',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec chargé mission industr. agriculture',null,'0','0','PBRUDEY;VCASTAN',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001418',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef secteur conseil ministres',null,'0','0','CHEFCM',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001419',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Contributeur DAN',null,'0','0','DANCONTRIB',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001421',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef secteur législation',null,'0','0','CHEFLEG',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001423',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable DAN - arrivée ministères',null,'0','0','DARMIN',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001424',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable pôle décrets - arrivée txt',null,'0','0','DARRT',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001425',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Pôle décrets - traitement des textes',null,'0','0','DTRAIT',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001426',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable pôle décrets - contrôle',null,'0','0','DCONTR',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001427',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable DAN - textes 1e lecture',null,'0','0','TXT1',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001428',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable DAN - textes 2e lecture',null,'0','0','TXT2',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001429',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Pôle arrêtés - traitement des textes',null,'0','0','ATRAIT',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001430',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable pôle arrêtés - contrôle',null,'0','0','ACONTR',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001454',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC Agriculture',null,'0','0','CKUNTZ;FMIKEL;GCOLMETDAAGE;agriculture_bdc',null,'50001443','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001455',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mission Aff gén (DGFAR-AGR)',null,'0','0','ALOUSSALA',null,'50001445','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001456',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir org économique (DGPEEI-AGR)',null,'0','0','NCOGNARD;VTOUREILLE;agriculture_sdoe',null,'50001446','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001457',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir Pol contractuelle  (DGER-AGR)',null,'0','0','SAZOULAY',null,'50001447','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001458',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau Aff gén (DGA-AGR)',null,'0','0','SSAINTCLAIR',null,'50001448','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001459',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Dir pêches marit. et aquaculture',null,'0','0','EDUMOND;FMADELINE;JGABEZ',null,'50001449','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001463',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des statuts (Défense)',null,'0','0','GBILLOT',null,'50001460','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001464',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. études gales et réserve (Défense)',null,'0','0','CDAGES',null,'50001460','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001465',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. régl pers. admin paraméd et soc (Déf)',null,'0','0','MCHARTIER',null,'50001461','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001466',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. pers. techn. et ouvriers Etat (Déf.)',null,'0','0','CANSELIN',null,'50001461','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001467',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. régl. primes et indemnités (Déf.)',null,'0','0','CMARQUET',null,'50001461','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001468',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau concours et exam. prof. (Défense)',null,'0','0','JHUGON;RMLEVILLO',null,'50001462','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001469',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. gest. coll. carr. pers. enc. agt nt',null,'0','0','DDALLOT;JKUPCZAK',null,'50001462','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001470',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'B. des règles minist. de gestion (Déf.)',null,'0','0','JFERRU',null,'50001462','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001504',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du cabinet DFPN',null,'0','0','HJARRY',null,'50001605','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001505',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef études jur. de div. études prospect',null,'0','0','VLEBEGUEC',null,'50001606','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001506',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef inspection technique',null,'0','0','DCCRS',null,'50001607','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001507',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargée de mission culture justice',null,'0','0','CBARROIS;FGASPARD',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001508',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission fonction publique',null,'0','0','CTOUBOUL;FTRUC;SMARAIS',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001509',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. chargé de mission fonction publique',null,'0','0','CNAIT;JADANI',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001510',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission affaires étrangères',null,'0','0','BAVENTINO;BROQUEFEUIL',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001511',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. chargé de mission aff. étrangères',null,'0','0','CASTIER;FGUERIN',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001512',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission solidarité santé',null,'0','0','AARNAULT;GCONTREPOIS;NPOLGE',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001518',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller Education, Ens. sup., Rech PM',null,'0','0','JBCARPENTIER;SECEDUCATION',null,'50000610','0','0','0',null,null,null,null);
/

commit;

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001519',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Culture PM',null,'0','0','HCASSAGNA;SECCULTURE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001520',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillers Diplomatiques PM',null,'0','0','JLAPOUGE;SECDIPLO',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001523',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Poste vide Conseiller Equipement PM',null,'0','0','SECEQUIPT',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001524',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère Justice PM',null,'0','0','MCAILLIBOTTE;SECJUSTICE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001525',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère techn. Libertés publiques PM',null,'0','0','LMARION;SECLIBPUB',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001526',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Santé PM',null,'0','0','CGROUCHKA;SECSANTE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001527',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller Fonction publique PM',null,'0','0','JFMONTEILS;SECFP',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001529',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller Social PM',null,'0','0','ERAUBRY;SECSOCIAL',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001530',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère Etudes, prospective PM',null,'0','0','SBOISSARD;SECETUDES',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001531',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Coll. locales et OM PM',null,'0','0','OMAGNAVAL;SECTERRIT',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001532',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller Sécurité PM',null,'0','0','FLAUZE;SECCURITE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001533',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Strat indus, PME PM',null,'0','0','SECPME;YLEROY',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001534',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Aff. stratégiques PM',null,'0','0','EMIGNOT',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001535',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère techn Espace, rech.  ind. PM',null,'0','0','EPORTIER;SECESPACE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001536',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère techn. Macro-économie PM',null,'0','0','CLANGEVIN;SECMACROECO',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001539',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Ass Nat, Sénat PM',null,'0','0','CFINON;DBURGAUD;ERADELET',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001546',to_timestamp('02/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef BDC Recherche',null,'0','0','BDCTR;MIANNASCOLI',null,'50001545','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001547',to_timestamp('27/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef BDC Education (Min. Educ.)',null,'0','0','JJLADVIE',null,'50001263','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001551',to_timestamp('27/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCASPL PME',null,'0','0','FDUFRESNOY;JLAUBINEAU;LMOQUIN',null,'50001085','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001552',to_timestamp('16/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. corps préfect. et admin. civil',null,'0','0','BGCPAC;DEDMOND;JSCHNEIDER',null,'50002160','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001553',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau de la liberté individuelle',null,'0','0','LIBINDIV;LIBPUB',null,'50001365','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001557',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller PM (à préciser)',null,'0','0','MTAILLEFER;TMICHAUX;adminsgg',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001558',to_timestamp('08/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission SGG (à préciser)',null,'0','0','MTAILLEFER;TMICHAUX;adminsgg',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001563',to_timestamp('10/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MAE)',null,'0','0','JAUMOND;NATRAOUI;PBARAGHINI','50000937',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001564',to_timestamp('11/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAFP',null,'0','0','VTAUZIAC',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001565',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDAD)',null,'0','0','ADESBROSSES;CPOINSARD;PCAPS','50000976',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001566',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (AGR)',null,'0','0','DGUEUDIN;GBIJU','50000938',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001567',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDD)',null,'0','0','ADESBROSSES;CPOINSARD;PCAPS',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001568',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Min. Educ.)',null,'0','0','EGASCHAT;JJLADVIE','50000968',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001569',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MTS)',null,'0','0','BYVENOU;PAHOPITAL','50000969',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001570',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (IOC)',null,'0','0','MBAMEUL;SBLANCHOIN','50000632',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001571',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Outre-mer)',null,'0','0','NGUIRIABOYE',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001572',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MCC)',null,'0','0','DVILLEMAUX;VDONZEAUD','50000966',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001573',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (DEF)',null,'0','0','JDCLERGET;RLEBAS','50000967',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001574',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (JUS)',null,'0','0','ICROSO;MBIERRY;PMERLET','50000972',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001575',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Intérieur)',null,'0','0','SBLANCHOIN',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001576',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Emploi)',null,'0','0','BYVENOU;PAHOPITAL',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001577',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (ESR)',null,'0','0','BDCTR;MIANNASCOLI','50000940',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001578',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Emploi)',null,'0','0','BYVENOU;PAHOPITAL',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001579',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Equipement)',null,'0','0','FPEROUZE;PCAPS',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001580',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Défense)',null,'0','0','JDCLERGET;RLEBAS',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001581',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (Intérieur)',null,'0','0','SBLANCHOIN',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001583',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef cabinet collectivités terr.',null,'0','0',null,null,'50001118','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001590',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau FP1 (DGCL)',null,'0','0','CCHAGNET',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001591',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau FP2 (DGCL)',null,'0','0','VLEGLEUT',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001592',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau FP3 (DGCL)',null,'0','0','PMOUTAFIAN',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001593',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL1 (DGCL)',null,'0','0','AKOUTCHOUK',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001594',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL2 (DGCL)',null,'0','0',null,null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001595',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL3 (DGCL)',null,'0','0','ODAUVE',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001596',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau FL5 (DGCL)',null,'0','0','OCHENAIN',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001597',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL1 (DGCL)',null,'0','0','PHELBERT',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001598',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL2 (DGCL)',null,'0','0','CIL2',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001599',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL3 (DGCL)',null,'0','0','EAUBRY',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001600',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau CIL4 (DGCL)',null,'0','0','DBRUEL',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001614',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau commissaire police',null,'0','0','CFAURE',null,'50001613','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001615',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau rémun. et régimes indemn.',null,'0','0','GAUJALEU',null,'50001613','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001616',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau officiers de police',null,'0','0','FCHOUAKI',null,'50001613','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001617',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau recrutement',null,'0','0','DAPN RECRUT',null,'50001613','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001618',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau aff. juridiques et statut.',null,'0','0','CLEFRANC',null,'50001612','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001619',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau affaires juridiques et statutaire',null,'0','0','GGAUTIER',null,'50001612','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001621',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Chef cabinet SG (Culture)',null,'0','0',null,null,'50001620','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001622',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC Aménagement MEDAD',null,'0','0','CPOINSARD;FIGLESIAS;JMGEUS;MROULEAU;RGUIGUES',null,'50000984','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001624',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC/DAST/SEA 1',null,'0','0','FBARRAUD',null,'50001004','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001625',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC/DAST/SRD 1',null,'0','0','CDARWISH',null,'50001004','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001626',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC/DNSA/G1',null,'0','0','AHEURTIER',null,'50001033','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001627',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC/DRE/AG',null,'0','0','DMICHON',null,'50001003','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001628',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC/SG/SDJ',null,'0','0','FPOMMIER',null,'50001032','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001629',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC/SG/SDP 2',null,'0','0','MLVAUCLIN',null,'50001032','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001630',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT/DAM/LM 4',null,'0','0','DGIRAUD',null,'50001036','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001631',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT/DTFC/SFC1',null,'0','0','AGOGNEAU',null,'50001034','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001632',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGPN',null,'0','0','IGPN',null,'50001608','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001633',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef division finances (DGPN)',null,'0','0',null,null,'50001609','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001634',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. central de la sécurité publique',null,'0','0','DCSP;ELEDOUARON',null,'50001610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001635',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau de la sûreté',null,'0','0','RBERHNARDT',null,'50001611','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001636',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Vigies DGAFP',null,'0','0','FALADJIDI;VSAUVAGE',null,'50002549','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001637',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Encadrement supérieur (DGAFP)',null,'0','0','CLENALBAUT;ISELLOM;ODAILLY',null,'50002548','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001638',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Primes (DGAFP)',null,'0','0','BLABONNELIE;MQUINTUS;NEDJIDER;VTAUZIAC',null,'50002547','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001639',to_timestamp('27/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Culture',null,'0','0','GRICHERATAUX;PANDREU',null,'50001395','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001640',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir presse écrite et info (DDM)',null,'0','0','CCREVOT',null,'50001404','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001641',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. comm. audiovisuelle (DDM)',null,'0','0','SCROIX',null,'50001404','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001642',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service aff. juridiques et régl. (CNC)',null,'0','0','SDAVY',null,'50001402','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001643',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé mission dept éco. du livre',null,'0','0','ASÉTIENNE',null,'50001398','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001644',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SG Direction des musées de France',null,'0','0','BGESTIN',null,'50001399','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001645',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dept collections (Dir musées France)',null,'0','0','JPMERCIER;LDAMADE',null,'50001399','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001646',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau aff. jur. (Archives France)',null,'0','0','MWASIKOWSKI',null,'50001397','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001647',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau affaires juridiques (DMDTS)',null,'0','0','IPHALIPPON',null,'50001400','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001648',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau affaires juridiques (DAPA)',null,'0','0','ACONDOU;DTOUZELIN;SDELFANTE',null,'50001403','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001649',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau affaires juridiques (Culture)',null,'0','0','JPTROUBÉ',null,'50001401','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001654',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau cons. jur. et lég. gale (Culture)',null,'0','0','DVILLEMAUX;MCOMBES;VDONZEAUD',null,'50001650','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001655',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau propriété littéraire, artistique',null,'0','0','DPOUCHARD',null,'50001650','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001656',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef bureau politique immo. (Culture)',null,'0','0','SMOUSSETTE',null,'50001651','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001657',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mission politique personnels (Culture)',null,'0','0','FPETIT',null,'50001652','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001658',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau personnels admin. (Culture)',null,'0','0','SPERDRIAL',null,'50001652','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001659',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau pers. de conservation (Culture)',null,'0','0','DHÉRONDELLE',null,'50001652','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001663',to_timestamp('27/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Education - Resp. txt réglementaires',null,'0','0','EGASCHAT',null,'50001263','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001664',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur général DGESCO',null,'0','0','JLNEMBRINI',null,'50001264','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001665',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjoints directeur général DGESCO',null,'0','0','PALLAL;PLSIMONI',null,'50001264','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001666',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur général DGES',null,'0','0','PHETZEL',null,'50001265','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001667',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjoints directeur général DGES',null,'0','0','BLANNAUD;EPIOZIN;JMDION',null,'50001265','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001668',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur général DGRI',null,'0','0','GBLOCH',null,'50001266','0','0','0',null,null,null,null);
/

commit;

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001669',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjoint au directeur général DGRI',null,'0','0','JRCYTERMANN',null,'50001266','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001670',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-directeur DGRI',null,'0','0','PIMBERT',null,'50001266','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001671',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétaire général (Min. Educ)',null,'0','0','PYDUWOYE',null,'50001267','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001672',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur général DGRH (Min. Educ.)',null,'0','0','TLEGOFF',null,'50001268','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001673',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjoint directeur gal DGRH (Min. Educ.)',null,'0','0','EBERNET;JPBONHOTAL',null,'50001268','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001674',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-directrice DGRH (Min. Educ.)',null,'0','0','PSANTANA',null,'50001268','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001675',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DE (Min. Educ.)',null,'0','0','RCHUDEAU',null,'50001269','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001676',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjointe au directeur DE (Min. Educ.)',null,'0','0','CDANEYROLE',null,'50001269','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001677',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DAF (Min Educ.)',null,'0','0','MDELLACASA',null,'50001270','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001678',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjointe au directeur DAF (Min. Educ.)',null,'0','0','CGAUDY',null,'50001270','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001679',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directrice DAJ (Min. Educ.)',null,'0','0','CLANDAIS',null,'50001271','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001680',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjointe à la directrice DAJ (Min. Educ)',null,'0','0','IROUSSEL',null,'50001271','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001685',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau IGEN/IGAENR (Min. Educ.)',null,'0','0','RKRAUSS',null,'50001684','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001686',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du service SAAM (Min. Educ.)',null,'0','0','XTURION',null,'50001683','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001687',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DREIC (Min. Educ.)',null,'0','0','MFOUCAULT',null,'50001682','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001688',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DEPP (Min. Educ.)',null,'0','0','DVITRY',null,'50001681','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001689',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGI',null,'0','0','FDUHOT;GSAVOYANT;MBRETON;MCONTACOLLI;OBURELLE;SZYKOVA;VGLACE',null,'50000837','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001690',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DLF',null,'0','0','BBOUCLET;IESPINASSE;JFMALNOU',null,'50000837','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001691',to_timestamp('12/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Poste vide Conseiller Médias PM',null,'0','0','SECMEDIAS',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001692',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef BDC Santé, jeunesse et sports',null,'0','0','IJOSSE',null,'50001043','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001693',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjointe au chef du BDC SJS',null,'0','0',null,null,'50001043','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001694',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Poste MQPA',null,'0','0','MQPA',null,'50001742','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001695',to_timestamp('16/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (SJS)',null,'0','0','GSICART','50000975',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001696',to_timestamp('17/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DSAF',null,'0','0','ABEDIKIAN;AGODEFRAINT;CDUBOIS;CLMOREAU;MLANDAIS',null,'50001559','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001697',to_timestamp('17/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MJS)',null,'0','0','IJOSSE;JGOMIS',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001707',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT/DTMRF/PVL2',null,'0','0','JPBIARD',null,'50001035','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001708',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT/DTMRF/TR1',null,'0','0','HSIFFERLEN',null,'50001035','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001709',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT/DTFC/SOE2',null,'0','0','PLAMY',null,'50001034','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001711',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT/SG/GPB 3',null,'0','0','JCPARAVY',null,'50001710','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001712',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAJIL/ASP 4 (DGPA)',null,'0','0','FPEROUZE',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001713',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAJIL/CTIJ (DGPA)',null,'0','0','BO_DGPA;PCAPS',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001714',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA/SP/TEC1 (DGPA)',null,'0','0','SDIBENEDETTO',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001715',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA/SP/ER (DGPA)',null,'0','0','JLASTERIE',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001716',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA/DPC1 (DGPA)',null,'0','0','ADESOUCHES',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001717',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAJIL/ED 4 (DGPA)',null,'0','0','PHRICOLEDIAG',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001718',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA/SP/AMT 1',null,'0','0','CGARCIA',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001719',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA/SP/RCF2',null,'0','0','VSOUMARA',null,'50000988','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001720',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGR',null,'0','0','FDUVAL;ecologie_dgr',null,'50000992','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001721',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/DH 2',null,'0','0','CTETELBOM',null,'50001369','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001722',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/DU 1',null,'0','0','AVANDERVORST',null,'50001369','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001723',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/QC 2',null,'0','0','FLASSOT',null,'50001369','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001724',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DSCR/IC 3',null,'0','0','FRICHARD',null,'50000993','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001725',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SG/DAEI/SDBTPSP/BR',null,'0','0','PULLERN',null,'50000996','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001726',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SG/DRAST/AFI',null,'0','0','VDENEUVILLE;ecologie_secgen',null,'50000995','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001727',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SG/SPA/M',null,'0','0','JCOLOMBU',null,'50000997','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001728',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'ENIM/SDSSM/BEJCI',null,'0','0','JBOUVIER',null,'50000999','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001729',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DT/SDAGEI/BAJ',null,'0','0','JBOURQUE',null,'50001002','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001730',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/DU 1',null,'0','0','AVANDERVORST',null,'50001168','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001731',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/QC 2',null,'0','0','FLASSOT',null,'50001168','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001743',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents CEA (MINEFI)',null,'0','0','DGTPE-ASSUR',null,'50001742','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001744',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Cabinet Agriculture',null,'0','0','ALIEURE;CDALBERA;CMALVEZIN;DGUEUDIN;DLABORDE;DMETAYER;EAUBRET;EBANEL;FRIEGERT;FROCHEBRUYN;JJIGUET;MDANTIN;MLAPLACE;NBELOT;PDEDINGER;PDUCLAUD;PMENNECIER;SGENG;YBRICE',null,'50001732','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001749',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Secrétariat général central (AGR)',null,'0','0','BGASSE;agriculture_secgen',null,'50001745','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001750',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Secr gén - Aff juridiques (AGR)',null,'0','0','CGANDON;GBIJU',null,'50001746','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001751',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Sec gén Mobilité/Carrières (AGR)',null,'0','0','CMOULINS;JMILLIAND',null,'50001747','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001752',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SsDir DPRS - Bur ASTR (AGR)',null,'0','0','ASSOUBIE;EBLAZQUEZ;OSCHELTIENNE',null,'50001748','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001753',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ss-dir Gestion des personnels (AGR)',null,'0','0','ALIVIERI;GLEDAIN;MRICHARD;SMATHELOT',null,'50001748','0','0','0',null,null,null,null);
/


Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001754',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ss-dir Santé animale (DGA-AGR)',null,'0','0','NDECOGNE',null,'50001448','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001755',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir protection végétaux (DGA-AGR)',null,'0','0','BBELARDCHELF',null,'50001448','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001756',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir sécur aliments (DGA-AGR)',null,'0','0','NFREDRIKSEN',null,'50001448','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001757',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir régl / contrôles (DGA-AGR)',null,'0','0','ABEAUCLAIR',null,'50001448','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001758',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir Produits végétaux (DGPEEI-AGR)',null,'0','0','MBZANINI;MCBRILLAUD;agriculture_sdpv',null,'50001446','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001759',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir Echges intrnx (DGPEEI-AGR)',null,'0','0','LBRASIER',null,'50001446','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001760',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir Travail et emploi  (DGFAR-AGR)',null,'0','0','CDEBARD;JLABOUE',null,'50001445','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001761',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir Pol formation  (DGER-AGR)',null,'0','0','JFGONDARD',null,'50001447','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001763',to_timestamp('17/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (IND)',null,'0','0','MQPA',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001764',to_timestamp('17/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des cabinets (BCF)',null,'0','0','CPAROLINI;FISAIA;JCHIFFRE;MDALLARD;PETCHEVERRY;SEC-BDC','50013998',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001782',to_timestamp('19/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (PME)',null,'0','0',null,null,null,'0','0','0',null,null,null,null);
/


Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001815',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (Economie)',null,'0','0','CPAROLINI;JCHIFFRE;MDALLARD;PETCHEVERRY',null,'50001814','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001819',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC travail ("Grenelle")',null,'0','0','BYVENOU;DLACOTTE;JMBOUSQUET',null,'50001172','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001820',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC travail ("Grenelle")',null,'0','0','BYVENOU;DLACOTTE;JMBOUSQUET',null,'50001173','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001821',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC travail ("Grenelle")',null,'0','0','BYVENOU;DLACOTTE;JMBOUSQUET',null,'50001174','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001822',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DAEI - Vigie',null,'0','0','FPRAYNAUD',null,'50001175','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001823',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGAPB (MTS)',null,'0','0','CAMBROISE;GSICART;JLLADRIX;XREGORD;travail_dgapb',null,'50001170','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001824',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGAS (MTS)',null,'0','0','CMAILLARD;MLAMOTHE;SGUEZO;YCHAYNES',null,'50001211','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001825',to_timestamp('27/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur SOLON',null,'0','0','NATRAOUI',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001826',to_timestamp('27/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur SOLON',null,'0','0','NATRAOUI',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001829',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Poste MQPA',null,'0','0','MQPA',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001830',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents CEA (MINEFI)',null,'0','0','DGTPE-ASSUR',null,'50001828','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001833',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agent rédaction DJO',null,'0','0','CBRUN;CLEMEVEL;FBIERNE;FGUILLOT;JROA;LCADET;LKARAR;PALBA;PBELLEFOND;RVAULTIER;SVIGNAL;XPAYS',null,'50000629','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001835',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargé de mission DJO',null,'0','0','JMHELBECQUE;PVIRUEGA',null,'50000631','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001836',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef rédaction DJO',null,'0','0','XPAYS',null,'50000631','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001891',to_timestamp('19/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'MINEFI (ancien)',null,'0','0','MQPA',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001892',to_timestamp('21/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ministère de l''Intérieur (ancien)',null,'0','0','MBAMEUL;SBLANCHOIN',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001893',to_timestamp('21/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ministère de la Santé (ancien)',null,'0','0','CHIDEUX;GSICART;JMHAUTION',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001897',to_timestamp('21/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min. dél. au budget (ancien)',null,'0','0','MQPA;T_ADM_ENT',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001901',to_timestamp('22/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ministère de l''emploi (ancien)',null,'0','0','BYVENOU;CPARESCHI;JMBOUSQUET;MVANNIER;PAHOPITAL',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001903',to_timestamp('24/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère Communication auprès du PM',null,'0','0','MLEVY;SECCOM',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001904',to_timestamp('24/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef secrétariat particulier PM',null,'0','0','SECCHEFSPART;SFOURMONT',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001905',to_timestamp('24/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs S.O.L.O.N. (CE)',null,'0','0','MCAPPERON;PYLEMOULT',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001908',to_timestamp('29/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Cab. Min. Immigration',null,'0','0','FDARCY;PBELLEC;RROCHE',null,'50001907','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001911',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Agriculture PM',null,'0','0','GREGNARD;SECAGRI',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001914',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Vigies DGAFP',null,'0','0','FALADJIDI;VSAUVAGE',null,'50002536','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001915',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Encadrement supérieur (DGAFP)',null,'0','0','CLENALBAUT;ISELLOM;ODAILLY',null,'50002535','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001916',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Primes (DGAFP)',null,'0','0','BLABONNELIE;MQUINTUS;NEDJIDER;VTAUZIAC',null,'50002534','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001918',to_timestamp('19/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (BCF)',null,'0','0','CPAROLINI;FISAIA;JCHIFFRE;MDALLARD;MQPA;PETCHEVERRY;SEC-BDC;T_ADM_ENT',null,'50001917','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001919',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller Analyse PM',null,'0','0','SECANALYSE;VCHRIQUI',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001920',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Fiscalité PM',null,'0','0','SECFISCALITE;YBENARD',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001921',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Education nationale PM',null,'0','0','SECEDUNAT',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001922',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Jeunesse, faits soc PM',null,'0','0','SECJEUNESSE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001924',to_timestamp('31/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCASPL PME (Economie)',null,'0','0','FDUFRESNOY;JLAUBINEAU;LMOQUIN',null,'50001923','0','0','0',null,null,null,null);
/

commit;

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001925',to_timestamp('31/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MLV)',null,'0','0','PCAPS','50001886',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001928',to_timestamp('31/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DéGéOM (Outre-Mer)',null,'0','0','FSAINTECROIX;NGUIRIABOYE;TLANDRY',null,'50001926','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001930',to_timestamp('01/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGTPE',null,'0','0','DGTPE-CABDG;DGTPE-SGE',null,'50001929','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001931',to_timestamp('01/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'NOR EXT (ancien)',null,'0','0','MQPA',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001933',to_timestamp('01/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DLF',null,'0','0','BBOUCLET;IESPINASSE;JFMALNOU;dlf;finance_dlf;outremer_dlf',null,'50001932','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001944',to_timestamp('05/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents direction des sports',null,'0','0','LHANOTEAUX',null,'50001313','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001945',to_timestamp('06/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'NOR EQU',null,'0','0','FPEROUZE;FPOMMIER;PCAPS',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001949',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DREES (Min. MTS)',null,'0','0','PFLEUTIAUX',null,'50001946','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001950',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Ville et exclusion PM',null,'0','0','EETIENNE;SECVILLE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001951',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Ecologie PM',null,'0','0','SECECOLOGIE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001952',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Sport PM',null,'0','0','SECSPORT;XMALENFER',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001953',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Finances sociales PM',null,'0','0','PMAYEUR;SECFINSOC',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001954',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Affaires locales PM',null,'0','0','PMOLAGER;SECLOCAL',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001955',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Energie PM',null,'0','0','DMOLHO;SECENERGIE',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001956',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Travail PM',null,'0','0','HMONANGE;SECTRAVAIL',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001957',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Eco internationale PM',null,'0','0','EDAINVILLE;SECECOINT',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001958',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère techn. Transports, Climat PM',null,'0','0','CPREVIEU;SECTRANSPORT',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001960',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Financt entreprises PM',null,'0','0','FDEMAILLE;SFINENTREP',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001961',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DREES (Min. BCF)',null,'0','0','PFLEUTIAUX',null,'50001962','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001978',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents sec. général (BCF)',null,'0','0','MTEXIER;SCRENDAL;VMAPELLI',null,'50001963','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001979',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents IGF (BCF)',null,'0','0','AFLAMANT;CLCANET',null,'50001964','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001980',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DPAEP (BCF)',null,'0','0','DLEMAISTRE;MVILLAIN;SPEIDRO',null,'50001965','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001981',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DAJ (BCF)',null,'0','0','CBERGEAL;FAMERIGO',null,'50001966','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001982',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agts contrôle gal éco. et fin (BCF)',null,'0','0','FGENVRIN',null,'50001967','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001983',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Haut fonctionnaire défense (BCF)',null,'0','0','SGUITEL',null,'50001968','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50001988',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGAS (Logement)',null,'0','0','CMAILLARD;MLAMOTHE;YCHAYNES',null,'50001989','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002007',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du bureau IGEN/IGAENR',null,'0','0','RKRAUSS',null,'50001991','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002009',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DSS (MTS)',null,'0','0','AMBOUCHARD;BGUERINEAU;CMARTIN;COGETGENDRE;DCAMUS;FLEMORVAN;JLREY;JPVINQUANT;MDELILLE;MSCHNEPF;PDEVAUX;travail_dss',null,'50002010','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002015',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. étrangers et circ. transfront.',null,'0','0','JPGUARDIOLA',null,'50002014','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002016',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau nat., titres d''identité et voyage',null,'0','0','MCCORNEC',null,'50002014','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002017',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau circulation transfront. et visas',null,'0','0','LAUDINET',null,'50002014','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002019',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau droit et procédures éloignement',null,'0','0','CPOUGET',null,'50002014','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002021',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-SG décret',null,'0','0','FLOQUET',null,'50002020','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002022',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-N3 rapportant',null,'0','0','CMODAT',null,'50002020','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002023',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-Direction',null,'0','0','JSOLTANI',null,'50002020','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002024',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-DM12',null,'0','0','MCPIRIOU',null,'50002020','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002028',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseiller techn. Affaires diplo. PM',null,'0','0','ABARTHELEMY',null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002029',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseillère techn. Enseign. sup. PM',null,'0','0',null,null,'50000610','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002031',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétaire général (Min. ESR)',null,'0','0','PYDUWOYE',null,'50002032','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002034',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur général DGRH (Min. ESR)',null,'0','0','TLEGOFF',null,'50002033','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002035',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjoint directeur gal DGRH (Min. ESR)',null,'0','0','EBERNET;JPBONHOTAL',null,'50002033','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002036',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-directrice DGRH (Min. ESR)',null,'0','0','PSANTANA',null,'50002033','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002038',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DE (Min. ESR)',null,'0','0','RCHUDEAU',null,'50002037','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002039',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjointe au directeur DE (Min. ESR)',null,'0','0','CDANEYROLE',null,'50002037','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002041',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DAF (Min ESR)',null,'0','0','MDELLACASA',null,'50002040','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002042',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjointe au directeur DAF (Min. ESR)',null,'0','0','CGAUDY',null,'50002040','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002044',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directrice DAJ (Min. ESR)',null,'0','0','CLANDAIS',null,'50002043','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002045',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Adjointe à la directrice DAJ (Min. ESR)',null,'0','0','IROUSSEL',null,'50002043','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002049',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DEPP (Min ESR)',null,'0','0','DVITRY',null,'50002046','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002052',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur DREIC (Min ESR)',null,'0','0','MFOUCAULT',null,'50002050','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002055',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chef du service SAAM (Min ESR)',null,'0','0','XTURION',null,'50002053','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002057',to_timestamp('18/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des pensions (AGR)',null,'0','0','JBENITO;JITAN;NCARON',null,'50001748','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002059',to_timestamp('22/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.LO.N. (IOC)',null,'0','0','SBLANCHOIN',null,'50000868','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002083',to_timestamp('25/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DHOS (Min. BCF)',null,'0','0','ABASSET;CELUAUD;CMERCIER;DFEBI;JKOCH;MADJIAGE;MCHANELIERE;NGRENET;RPULVAL;SBENIDJER',null,'50002082','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002085',to_timestamp('25/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DSS (Min. BCF)',null,'0','0','AMBOUCHARD;BGUERINEAU;CMARTIN;COGETGENDRE;DCAMUS;FLEMORVAN;JLREY;JPVINQUANT;MDELILLE;MSCHNEPF;PDEVAUX',null,'50002084','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002107',to_timestamp('03/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (SJS)',null,'0','0','GSICART',null,'50001882','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002108',to_timestamp('05/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Logement',null,'0','0','FFRIQUET',null,'50001990','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002132',to_timestamp('23/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Textes en instance',null,'0','0','TINSTANCE',null,'50000624','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002157',to_timestamp('25/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DIV Direction (Logement)',null,'0','0','MCOURTOIS',null,'50001171','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002158',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC Direction',null,'0','0','PLEMAIRE',null,'50001168','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002159',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/OC',null,'0','0','VHASON',null,'50001168','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002161',to_timestamp('16/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BGCPAC-Chef section admin. civils',null,'0','0','MJOFFRE',null,'50002160','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002164',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC Direction',null,'0','0','PLEMAIRE',null,'50001369','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002165',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC/OC',null,'0','0','VHASON',null,'50001369','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002166',to_timestamp('16/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs S.O.L.O.N. (CE)',null,'0','0','MCAPPERON;PYLEMOULT',null,'50000640','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002182',to_timestamp('25/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs S.O.L.O.N. (IMI)',null,'0','0','FDARCY;PBELLEC;RROCHE','50001885',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002207',to_timestamp('17/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (BCF)',null,'0','0','MQPA','50013998',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002234',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SG/SPA/M',null,'0','0','CGODARD;JCOLOMBU',null,'50000991','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002235',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC/SG/SDJ',null,'0','0','DGAC1;DGAC10;DGAC2;DGAC3;DGAC4;DGAC5;DGAC6;DGAC7;DGAC8;DGAC9;FPOMMIER',null,'50000994','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002236',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPAC (MEDAD)',null,'0','0','FPOMMIER',null,'50001005','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002237',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCS (MEDAD)',null,'0','0','FPOMMIER',null,'50001006','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002238',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDAD)',null,'0','0','PCAPS',null,'50000986','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002239',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDAD)',null,'0','0','PCAPS',null,'50000987','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002240',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDAD)',null,'0','0','PCAPS',null,'50000989','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002241',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDAD)',null,'0','0','PCAPS',null,'50001000','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002242',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDAD)',null,'0','0','PCAPS',null,'50001001','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002243',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (MEDAD)',null,'0','0','DCITRON;PCAPS',null,'50001973','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002244',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DGE (MEDAD)',null,'0','0','CBISSERIER;DCITRON;ELEDEVEDEC;JBARDOLLET;MDGADAT;MJCYRILLE',null,'50001972','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002245',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT/SG/GPB 3',null,'0','0','JCPARAVY',null,'50000998','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002247',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA-DAJIL',null,'0','0','FPEROUZE;PCAPS',null,'50002246','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002248',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (PM)',null,'0','0','CDUMONT;MTADMINMIN;MTAILLEFER;MTCONTRIB;MTCONTRIBEXT;MTVIGIE;TMICHAUX;adminsgg','50000607',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002249',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (SGG)',null,'1','0','MTAILLEFER;TMICHAUX;adminsgg;superviseursgg',null,'50000615','0','0','1',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002333',to_timestamp('16/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sect. aff. réservée et budg (Intérieur)',null,'0','0','CLIECHTI;SGCAB',null,'50001249','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002337',to_timestamp('16/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (IOC)',null,'0','0','MBAMEUL;SBLANCHOIN',null,'50000867','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002338',to_timestamp('16/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAPN',null,'0','0','DAPN',null,'50001604','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002358',to_timestamp('23/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents CRE',null,'0','0','FHAUGUEL;RCOIN',null,'50002362','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002365',to_timestamp('25/09/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Vigie CRE',null,'0','0','VIGIECRE',null,'50002362','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002367',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Dir. Secr. Gl Min. Immigration',null,'0','0',null,null,'50002366','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002371',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'ARCEP (direction)',null,'0','0','ARCEPUSER1;ARCEPVIG1',null,'50002370','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002408',to_timestamp('06/09/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Coll. terr. Rép (Département)',null,'0','0',null,null,'50002383','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002409',to_timestamp('06/09/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Coll. terr. Rép (Région)',null,'0','0',null,null,'50002384','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002410',to_timestamp('06/09/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Coll. terr. Rép (Autres)',null,'0','0',null,null,'50002385','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002432',to_timestamp('12/09/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. DAG',null,'0','0','DVILLEMAUX',null,'50001396','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002457',to_timestamp('28/09/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DIACT',null,'0','0','PCAPS;PVIGNE',null,'50001948','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002483',to_timestamp('08/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Dir. circulation aérienne',null,'0','0','JMREBOT',null,'50002482','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002484',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. dir. affaires civiles et du sceau',null,'0','0',null,null,'50001088','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002488',to_timestamp('17/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section du Sceau',null,'0','0',null,null,'50002486','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002489',to_timestamp('17/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau off. minist. et déontologie',null,'0','0',null,null,'50002487','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002490',to_timestamp('18/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (AGR)',null,'0','0','DGUEUDIN;GBIJU',null,'50001444','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002537',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (DGAFP)',null,'0','0','BLABONNELIE;VTAUZIAC',null,'50001912','0','0','0',null,null,null,null);
/

commit;

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002538',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Modalités concours (DGAFP)',null,'0','0','GDOSSOUYOVO;SMAZATAUD',null,'50002533','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002539',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Jury concours (DGAFP)',null,'0','0','GDOSSOUYOVO;SMAZATAUD',null,'50002533','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002540',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Statuts particuliers (DGAFP)',null,'0','0','BLABONNELIE;CCHRISTIEN;CLENALBAUT;JLPASTOR;MQUINTUS;VTAUZIAC',null,'50002534','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002541',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Autres textes (DGAFP)',null,'0','0','BLABONNELIE;CCHRISTIEN;CLENALBAUT;CPINON;GDOSSOUYOVO;ISELLOM;JLPASTOR;JRIGABER;MQUINTUS;NBOUHAFS;NEDJIDER;ODAILLY;SMAZATAUD;VTAUZIAC',null,'50002534','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002542',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Contrôle textes (DGAFP)',null,'0','0','BLABONNELIE;VTAUZIAC',null,'50002536','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002543',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Autres corps (DGAFP)',null,'0','0','CPINON;JRIGABER',null,'50002535','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002544',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'CSFPE (DGAFP)',null,'0','0','LDOUMBE',null,'50002535','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002545',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Nominations commission (DGAFP)',null,'0','0','CLENALBAUT;CPINON;ISELLOM;JRIGABER;NBOUHAFS;ODAILLY',null,'50002535','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002550',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Contrôle textes (DGAFP)',null,'0','0','BLABONNELIE;VTAUZIAC',null,'50002549','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002551',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Autres corps (DGAFP)',null,'0','0','CPINON;JRIGABER',null,'50002548','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002552',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'CSFPE (DGAFP)',null,'0','0','LDOUMBE',null,'50002548','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002553',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Nominations commission (DGAFP)',null,'0','0','CLENALBAUT;CPINON;ISELLOM;JRIGABER;NBOUHAFS;ODAILLY',null,'50002548','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002554',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Statuts particuliers (DGAFP)',null,'0','0','BLABONNELIE;CCHRISTIEN;CLENALBAUT;JLPASTOR;MQUINTUS;VTAUZIAC',null,'50002547','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002555',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Autres textes (DGAFP)',null,'0','0','BLABONNELIE;CCHRISTIEN;CLENALBAUT;CPINON;GDOSSOUYOVO;ISELLOM;JLPASTOR;JRIGABER;MQUINTUS;NBOUHAFS;NEDJIDER;ODAILLY;SMAZATAUD;VTAUZIAC',null,'50002547','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002556',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Modalités concours (DGAFP)',null,'0','0','GDOSSOUYOVO;SMAZATAUD',null,'50002546','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002557',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Jury concours (DGAFP)',null,'0','0','GDOSSOUYOVO;SMAZATAUD',null,'50002546','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002558',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (DGAFP)',null,'0','0','BLABONNELIE;VTAUZIAC',null,'50001442','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002582',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGT-RT1',null,'0','0','JAFOY',null,'50001163','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002583',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGT-RT3',null,'0','0','LDURAND;MDEJEAN',null,'50001163','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002584',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM-N2 opposition',null,'0','0','PDIGUINY',null,'50001165','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002585',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGT-CT',null,'0','0','DDRU;travail_secgen',null,'50001163','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002609',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (BCF)',null,'0','0','CPAROLINI;FISAIA;JCHIFFRE;MDALLARD;PETCHEVERRY',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002610',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (ECO)',null,'0','0','CPAROLINI;JCHIFFRE;MDALLARD;PETCHEVERRY',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002611',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (BCF)',null,'0','0','CPAROLINI;FISAIA;JCHIFFRE;MDALLARD;PETCHEVERRY',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002612',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (ECO)',null,'0','0','CPAROLINI;JCHIFFRE;MDALLARD;PETCHEVERRY',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002613',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents BDC (ECO + BCF)',null,'0','0','CPAROLINI;FISAIA;JCHIFFRE;MDALLARD;PETCHEVERRY',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002614',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs SOLON (ECE)',null,'0','0','MQPA','50000507',null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002615',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs SOLON',null,'0','0','MQPA',null,'50001814','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002632',to_timestamp('03/12/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateur S.O.L.O.N. (IOC)',null,'0','0','MBAMEUL;SBLANCHOIN',null,'50000666','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002658',to_timestamp('13/12/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DMP-N2 opposition',null,'0','0','PDIGUINY',null,'50002020','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002707',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGT-Vigie',null,'0','0','JBLONDEL',null,'50001163','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002708',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SDFE Vigie',null,'0','0','AKURKDJIAN',null,'50001169','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002709',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGAS Vigie',null,'0','0','JGARCINI',null,'50001167','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002733',to_timestamp('19/02/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents DARQSI',null,'0','0','DCITRON',null,'50001976','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002783',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau parcs et réserves',null,'0','0','PGUERIN',null,'50000834','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002834',to_timestamp('14/03/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétaire de la section administration',null,'0','0','AFLACELIERE;LBURLET;SGUYARD;VKLEINHOLT',null,'50002833','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50002857',to_timestamp('13/03/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat BOG',null,'0','0','PLEMOS',null,'50001104','1','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003058',to_timestamp('26/05/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents CNG',null,'0','0','AMDUFOUR;CGOSTREL;CRICHARD;FSICCHIA;MLHENRY',null,'50003057','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003107',to_timestamp('02/06/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cellule documentation (DEF)',null,'0','0','MGALEA',null,'50001135','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003108',to_timestamp('02/06/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. centrale serv. de santé des armées',null,'0','0','ECHEREL;FMAYOUSSIER',null,'50001135','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003109',to_timestamp('02/06/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. centrale du service des essences',null,'0','0','JCPEZERON',null,'50001135','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003208',to_timestamp('01/09/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Responsable de la CPBO',null,'0','0','PGALLAND',null,'50003207','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003233',to_timestamp('04/09/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Directeur du cabinet SGG',null,'0','0','OFOMBARON',null,'50003232','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003234',to_timestamp('04/09/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Développement des systèmes d''information',null,'0','0','SCOTTIN',null,'50003232','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003258',to_timestamp('18/09/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents collège CSA',null,'0','0','CONTRIBCSA;VIGIECSA',null,'50002684','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003332',to_timestamp('17/10/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Cour des comptes',null,'0','0','AAMSON',null,'50002510','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003358',to_timestamp('24/10/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents B.O. DJO',null,'0','0','ANMOREAU;CPETRY;CQUINTON;PCANALE',null,'50000629','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003359',to_timestamp('24/10/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents B.O. DJO',null,'0','0','ANMOREAU;CPETRY;CQUINTON;PCANALE',null,'50003357','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003382',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGRH A1 (Min. ESR)',null,'0','0','PCHRISTMANN',null,'50002033','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003383',to_timestamp('30/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGRH A1 (Min. Educ.)',null,'0','0','PCHRISTMANN',null,'50001268','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003407',to_timestamp('20/06/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec chargée de mission culture justice',null,'0','0','HBENRAMDANE;RMACKHAN',null,'50000619','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003557',to_timestamp('09/01/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents CRE',null,'0','0','FHAUGUEL;RCOIN',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003585',to_timestamp('15/01/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Min Logement et ville (ancien)',null,'0','0','PCAPS',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003607',to_timestamp('16/01/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs S.O.L.O.N.',null,'0','0','CHIDEUX;GSICART;JGOMIS;JMHAUTION',null,null,'0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003708',to_timestamp('19/02/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DIV Direction (MTS)',null,'0','0','MCOURTOIS',null,'50003707','0','0','0',null,null,null,null);
/


Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003757',to_timestamp('11/03/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agents Autorité de la concurrence',null,'0','0','JGAUMET',null,'50003733','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003807',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'CGPC BPMG (MEDAD)',null,'0','0','GSTEIMETZ',null,'50000987','0','0','0',null,null,null,null);
/

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003833',to_timestamp('09/11/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Administrateurs S.O.L.O.N. OME',null,'0','0','NGUIRIABOYE','50003832',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003835',to_timestamp('09/11/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau Cabinet outre-mer',null,'0','0','FSAINTECROIX;NGUIRIABOYE;TLANDRY',null,'50003834','1','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003837',to_timestamp('09/11/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DéGéOM (Outre-Mer)',null,'0','0','FSAINTECROIX;NGUIRIABOYE;TLANDRY',null,'50003836','0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003857',to_timestamp('02/12/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Poste test vigie direction (ECE)',null,'0','0','T_VIG_DIR',null,'50001021','0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50003858',to_timestamp('02/12/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Poste test vigie direction (BCF)',null,'0','0','T_VIG_DIR',null,'50000646','0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50004001',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet',null,'0','0','AT_PARL;CT1;DIR_CAB','50000507',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50004002',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGCIS',null,'0','0','DGCIS_contributeur;DGCIS_redacteur;DGCIS_vigie_dir','50000507',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50004003',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet',null,'0','0','AT_PARL;CT1;DIR_CAB','50013998',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50004004',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGFIP',null,'0','0','DGFIP_contributeur;DGFIP_redacteur;DGFIP_vigie_dir','50013998',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50004005',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général',null,'0','0','budget_secgen','50013998',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('50004006',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des Cabinets',null,'0','0','budget_bdc','50013998',null,'0','0','0',null,null,null,null);

Insert into REPONSES_QA.POSTE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,CHARGE_MISSION_SGG,CONSEILLER_PM,MEMBRES,ID_PARENT_ENTITE,ID_PARENT_UNITE,POSTE_BDC,POSTE_WS,SUPERVISEUR_SGG,WS_CLE,WS_LOGIN,WS_MDP,WS_URL) values ('60000000',to_timestamp('29/11/10 18:23:45','DD/MM/RR HH24:MI:FF'),null,null,'0',null,'Secrétariat Conseillère Aff. éco et finances PM',null,'0','0','agriculture_sececofinance;ecologie_sececofinance;finance_sececofinance;outremer_sececofinance;sececofinance;travail_sececofinance',null,'50000610','0','0','0',null,null,null,null);

commit;

Insert into REPONSES_QA.GOUVERNEMENT (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU) values ('gouv_1',to_timestamp('14/11/10 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Gouvernement Fillon VII',null);

commit;

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000608',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGE (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000610',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet du Premier ministre',null,null,'50000607',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000615',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général du Gouvernement',null,null,'50000607',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000619',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Chargés de mission au SGG',null,null,null,null,'50000615','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000983',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service législation et qualité du droit',null,null,null,null,'50000615','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000624',to_timestamp('02/01/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Département de l''activité normative',null,null,null,null,'50000983','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000629',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des journaux officiels',null,null,null,null,'50000615','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000631',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'service de la rédaction DJO',null,null,null,null,'50000629','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000634',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section des finances',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000637',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section de l''intérieur',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000638',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section des travaux publics',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000639',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section sociale',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000640',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section CE (à préciser)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000646',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction du budget',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000648',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGME',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000653',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGA  (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000655',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des Cabinets (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000657',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPAEP (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000659',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGTPE - lettre T (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000666',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction gale de la police nationale',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000737',to_timestamp('10/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service de documentation',null,null,null,null,'50000615','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000817',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGEMP (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000832',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction de l''eau (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000833',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. préventions pollutions et risques',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000834',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction nature et paysage (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000835',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. études éco et éval. env. (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000836',to_timestamp('07/06/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGCCRF (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000837',to_timestamp('07/06/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGI',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000838',to_timestamp('07/06/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGCP',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000865',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet Intérieur',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000866',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction générale des Coll. Locales',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000867',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des Lib. Pub. et des Aff. Jur.',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000868',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction de la Déf. et de la Séc. Civ.',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000870',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des sys. d''inf. et de com.',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000871',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Inspection Gale de l''Admin. (Intérieur)',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000872',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Délégation à l''inf. et la communication',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000889',to_timestamp('12/09/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000946',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau du cabinet MAE',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000947',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. générale de l''administration MAE',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000948',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. Fr étranger et étrangers en France',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000949',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des affaires juridiques MAE',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000950',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service du Protocole',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000951',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Haut Fonctionnaire Corr. de Déf et Séc.',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000952',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. Gale de la Coop. Internat. et Dév.',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000953',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des Archives et de la Doc.',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000954',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGDDI',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000955',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service des Pensions',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000962',to_timestamp('02/01/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mission DPDD',null,null,null,null,'50000983','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000963',to_timestamp('02/01/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mission qualité de la norme',null,null,null,null,'50000983','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000984',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau du Cab. MEDAD',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000986',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service de défense et de sécurité (SDS)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000987',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseil gal des ponts et chaussées',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000988',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000989',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Délégation à l''action foncière (DAF)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000990',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC "Grenelle"',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000991',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général MEDAD',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000992',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGR (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000993',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DSCR (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000994',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAC (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000995',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. rech., aff. scien. et tech. (DRAST)',null,null,null,null,'50000991','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000996',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des aff. éco. et internat. (DAEI)',null,null,null,null,'50000991','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000997',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SG/SPA/M',null,null,null,null,'50000991','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000998',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGMT (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50000999',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'ENIM (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001000',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Inspections générales (IG hors CGPC)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001001',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. gal au tunnel sous la Manche',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001002',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction du tourisme (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001003',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. de la régulation éco. (DRE)',null,null,null,null,'50000994','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001004',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des aff. strat. et tech. (DAST)',null,null,null,null,'50000994','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001005',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des prog. aéro. et de coopé. (DPAC)',null,null,null,null,'50000994','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001006',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. du contrôle et de la sécur. (DCS)',null,null,null,null,'50000994','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001009',to_timestamp('09/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec. Etat coopération et francophonie',null,null,'50000937',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001010',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. ressources humaines MAE',null,null,null,null,'50000947','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001011',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. affaires financières MAE',null,null,null,null,'50000947','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001012',to_timestamp('12/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Serv. affaires juridiques internes MAE',null,null,null,null,'50000947','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001021',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAJ (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001022',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGF (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001023',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'INSEE (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001024',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Contrôle gal éco et financier (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001025',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ht fonctionnaire défense (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001026',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC secrétariat Etat Outre-mer',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001032',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général DGAC',null,null,null,null,'50000994','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001033',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des serv. de la navig. aér. (DSNA)',null,null,null,null,'50000994','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001034',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des trans. ferrov. et coll. (DTFC)',null,null,null,null,'50000998','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001035',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. trans. mar., rout. et fluv. (DTMRF)',null,null,null,null,'50000998','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001036',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des aff. maritimes (DAM)',null,null,null,null,'50000998','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001043',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet du ministre (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001044',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir adm gén personnel, budg (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001046',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gén. de la santé (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001047',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. hospit. et org. soins (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001048',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. de la sécurité sociale (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001049',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. rech, études, stats (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001050',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agce sécu. san prod santé (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001051',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'UNCAM (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001052',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Autorité sûreté nucléaire (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001053',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Etablissement frçs du sang (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001054',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agence biomédecine (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001055',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Agence vétérinaire (Min. SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001056',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Délég. interm famille (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001057',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'CGIET (Economie)',null,null,'50000507',null,null,'UST');

commit;

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001082',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Délég. interm pers. handic. (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001085',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCASPL Obsolète',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001086',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet du garde des sceaux',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001087',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des services judiciaires',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001088',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des aff. civiles et du sceau',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001089',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des aff. criminelles et des grâces',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001090',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. protection jud. de la jeunesse',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001091',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. admin. gale et équipement',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001092',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ecole nationale de la magistrature',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001093',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Services aff. européennes et internat.',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001094',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Serv. accès droit et just. polit. ville',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001095',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction de l''admin. pénitentiaire',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001096',to_timestamp('20/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Décret changement de noms',null,null,'50000972',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001103',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Délégation gale pour l''armement',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001104',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des officiers généraux',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001105',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Contrôle général des armées',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001106',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des affaires juridiques (Défense)',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001117',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(p. mémoire) Cab aménagt du territoire',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001118',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(p. mémoire) Cab coll territoriales',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001132',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Dir. affaires financières (DEF)',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001133',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Dir. gale de la gendarmerie nat.',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001134',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. des ressources humaines (Défense)',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001135',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Etat-major des armées',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001136',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Etat-major de l''armée de l''air',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001137',to_timestamp('13/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. BDC (Défense)',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001138',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Etat-major de la marine',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001139',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Dir mémoire, patrimoine, archives',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001140',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(vide) Etat-major de l''armée de terre',null,null,'50000967',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001147',to_timestamp('13/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Défense',null,null,null,null,'50001137','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001153',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ss-dir. droit public et privé (Défense)',null,null,null,null,'50001106','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001154',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ss-dir droit internat et europ (Défense)',null,null,null,null,'50001106','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001155',to_timestamp('21/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Division des affaires pénales militaires',null,null,null,null,'50001106','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001163',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGT',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001164',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAGEMO',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001165',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPM (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001166',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGEFP (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001167',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGAS (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001168',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC (Logement)',null,null,'50001886',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001169',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SDFE (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001170',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAGPB (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001171',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DIV (Logement)',null,null,'50001886',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001172',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DIIESES (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001173',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DARES (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001174',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DILTI (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001175',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAEI (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001211',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAS (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001248',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des Cabinets (Intérieur)',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001249',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général (Intérieur)',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001263',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau Cab éducation (Min. Educ.)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001264',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gale de l''enseignement scolaire',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001265',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gale de l''enseignement supérieur',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001266',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gale recherche et  innovation',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001267',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général (Min. Educ.)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001268',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction générale RH (Min. Educ.)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001269',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction de l''encadrement (Min. Educ.)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001270',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. affaires financières (Min. Educ.)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001271',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. affaires juridiques (Min. Educ.)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001283',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet Sécrétariat général',null,null,null,null,'50001249','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001285',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général - DMAT',null,null,null,null,'50001249','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001286',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général - DRH (A)',null,null,null,null,'50001249','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001287',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général - DEPAFI (F)',null,null,null,null,'50001249','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001288',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général - DSIC (G)',null,null,null,null,'50001249','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001306',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. élus locaux et fpt',null,null,null,null,'50000866','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001307',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. finances locales et action éco',null,null,null,null,'50000866','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001308',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. compétences et instit. loc.',null,null,null,null,'50000866','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001309',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC sports',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001311',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DRH, Adm. et coordination gale (SJS)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001313',to_timestamp('05/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des sports',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001315',to_timestamp('19/01/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir jeunesse, éduc pop et vie asso',null,null,'50000607',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001317',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. vie asso, emploi, formations',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001332',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPN Cabinet',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001364',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DLPAJ Cabinet',null,null,null,null,'50000867','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001365',to_timestamp('28/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. des libertés publiques',null,null,null,null,'50000867','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001367',to_timestamp('01/12/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. circ. et sécurité routière',null,null,null,null,'50001285','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001369',to_timestamp('01/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGUHC (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001386',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DDSC cabinet',null,null,null,null,'50000868','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001387',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. gestion des risques',null,null,null,null,'50000868','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001388',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. sapeurs pompiers et act. sec.',null,null,null,null,'50000868','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001389',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. administration et logistique',null,null,null,null,'50000868','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001395',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Culture',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001396',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. administration gale (Culture)',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001397',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des archives de France',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001398',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction du livre et de la lecture',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001399',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction des musées de France',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001400',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. musique, danse, théâtre et spect.',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001401',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Délégation aux arts plastiques',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001402',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Centre national de la cinématographie',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001403',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. de l''architecture et du patrimoine',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001404',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction du développement des médias',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001405',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat gal de la défense nationale',null,null,'50000607',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001442',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir gale de l''admin. et de la FP (DGAFP)',null,null,'50000607',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001443',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Agriculture',null,null,'50000938',null,null,'UST');


Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001444',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général (AGR)',null,null,'50000938',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001445',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gale forêt et affaires rurales',null,null,'50000938',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001446',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gale pol. éco., eur. et internat.',null,null,'50000938',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001447',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gale enseign. et recherche Agricult',null,null,'50000938',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001448',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir gale de l''alimentation (AGR)',null,null,'50000938',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001449',to_timestamp('09/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. pêches marit. et aquaculture',null,null,'50000938',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001460',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. fonction militaire',null,null,null,null,'50001134','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001461',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ss-dir rel. soc, statuts, filières (Déf)',null,null,null,null,'50001134','UST');

commit;

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001462',to_timestamp('12/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Ss-dir gestion collect. pers civil (Déf)',null,null,null,null,'50001134','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001545',to_timestamp('04/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau Cab recherche',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001559',to_timestamp('27/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. services admin et financiers (DSAF)',null,null,'50000607',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001604',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAPN',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001605',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DFPN',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001606',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCPJ',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001607',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCCRS',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001608',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGPN',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001609',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'SCTIP',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001610',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCSP',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001611',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCPAF',null,null,null,null,'50000666','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001612',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir admin. gale finances DAPN',null,null,null,null,'50001604','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001613',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir ressources humaines DAPN',null,null,null,null,'50001604','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001620',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général (Culture)',null,null,null,null,'50001396','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001650',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir affaires juridiques (Culture)',null,null,null,null,'50001396','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001651',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir affaires financières (Culture)',null,null,null,null,'50001396','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001652',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service personnel, aff. soc. (Culture)',null,null,null,null,'50001396','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001653',to_timestamp('29/03/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dél. au dév. et aff. internat. (Culture)',null,null,null,null,'50001396','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001681',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. éval, prospect et perf. (Min. Educ)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001682',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'D. relat. eur, internat, coop (Min Educ)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001683',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Serv action admin., modernis. (Min Educ)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001684',to_timestamp('27/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGEN/IGAENR (Min. Educ.)',null,null,'50000968',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001710',to_timestamp('08/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général DGMT',null,null,null,null,'50000998','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001732',to_timestamp('02/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet Agriculture',null,null,'50000938',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001734',to_timestamp('02/03/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dél. gale à la langue française (DGLF)',null,null,'50000966',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001741',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Comité entreprises assurances (CEA)',null,null,null,null,'50000659','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001742',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(CEA) Comité entreprises assurances',null,null,null,null,'50001741','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001745',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général central (AGR)',null,null,null,null,'50001444','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001746',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat gén - Aff juridiques (AGR)',null,null,null,null,'50001444','UST');


Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001747',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec gén - Mobilité / carrières (AGR)',null,null,null,null,'50001444','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001748',to_timestamp('04/04/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sec gén - RH (AGR)',null,null,null,null,'50001444','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001813',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseil de la concurrence (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001814',to_timestamp('20/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Conseil de la concurrence (Economie)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001828',to_timestamp('02/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'(CEA) Comité entreprises assurances',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001882',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction Santé (à préciser)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001907',to_timestamp('29/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet du ministère de l''immigration',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001912',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAFP',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001917',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Bureau des cabinets (BCF)',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001923',to_timestamp('31/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DCASPL PME (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001926',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DéGéOM (Outre-Mer)',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001929',to_timestamp('01/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGTPE-EXT (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001932',to_timestamp('01/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGI - DLF (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001934',to_timestamp('13/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'AFNOR (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001946',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DREES (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001948',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DIACT (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001962',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DREES',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001963',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général (BCF)',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001964',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGF',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001965',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DPAEP',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001966',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DAJ (BCF)',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001967',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Contrôle gal éco. et fin (BCF)',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001968',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Haut fonctionnaire de défense (BCF)',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001972',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGE (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001973',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DARQSI (MEDAD)',null,null,'50000976',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001974',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DARES (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001975',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DIIESES (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001976',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DARQSI (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001977',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. gale de la gendarmerie nationale',null,null,'50000632',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001989',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGAS (Logement)',null,null,'50001886',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001990',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet Logement',null,null,'50001886',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50001991',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'IGAENR (ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002010',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DSS (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002011',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Comité interminist. controle imi (IMI)',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002013',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir accueil, intégr, citoyenneté (IMI)',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002014',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. étrangers et circ. transfront.',null,null,null,null,'50002013','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002020',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction immigration (IMI)',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002027',to_timestamp('08/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service de l''asile (IMI)',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002032',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secrétariat général (Min ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002033',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction générale RH (Min. ESR.)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002037',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction de l''encadrement (Min. ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002040',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. affaires financières (Min. ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002043',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. affaires juridiques (Min. ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002046',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. éval, prospect et perf. (Min. ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002050',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'D. relat. eur, internat, coop (Min ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002053',to_timestamp('14/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Serv action admin., modernis. (Min ESR)',null,null,'50000940',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002058',to_timestamp('19/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Organismes de recherche (Min. ESR)',null,null,null,null,'50001266','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002082',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. hospit. et org. soins (Min. BCF)',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002084',to_timestamp('30/05/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. de la sécurité sociale (Min. BCF)',null,null,'50013998',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002160',to_timestamp('16/07/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. corps préfectoral-SDCPAC',null,null,null,null,'50001285','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002246',to_timestamp('08/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DGPA-DAJIL',null,null,null,null,'50000988','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002362',to_timestamp('23/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Régulation de l''énergie',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002366',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Secr. général Min. Immigration',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002370',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service opérateurs et ressources (ARCEP)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002383',to_timestamp('28/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Coll. terr. de la Rép. (Département)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002384',to_timestamp('28/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Coll. terr. de la Rép. (Région)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002385',to_timestamp('28/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Coll. terr. de la Rép. (Autre)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002482',to_timestamp('08/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Dir. circulation aérienne',null,null,null,null,'50001136','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002486',to_timestamp('17/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-direction du droit civil (Justice)',null,null,null,null,'50001088','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002487',to_timestamp('17/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sous-dir. prof. jud. et jurid. (Justice)',null,null,null,null,'50001088','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002508',to_timestamp('25/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction CNIL',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002510',to_timestamp('25/10/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Direction Cour des comptes',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002533',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Concours (DGAFP)',null,null,null,null,'50001912','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002534',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mesures normatives (DGAFP)',null,null,null,null,'50001912','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002535',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mesures individuelles (DGAFP)',null,null,null,null,'50001912','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002536',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Contrôle final textes (DGAFP)',null,null,null,null,'50001912','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002546',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Concours (DGAFP)',null,null,null,null,'50001442','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002547',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mesures normatives (DGAFP)',null,null,null,null,'50001442','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002548',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Mesures individuelles (DGAFP)',null,null,null,null,'50001442','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002549',to_timestamp('06/11/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Contrôle final textes (DGAFP)',null,null,null,null,'50001442','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002683',to_timestamp('10/01/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Président CSA',null,null,null,null,null,'UST');

commit;

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002684',to_timestamp('10/01/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Collège CSA',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002757',to_timestamp('20/02/08 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Décorations - Sanctions',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50002833',to_timestamp('04/05/06 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Section de l''administration',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003057',to_timestamp('14/02/07 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Centre national de gestion (CNG)',null,null,'50000975',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003082',to_timestamp('27/05/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Lois (Conseil constitutionnel)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003132',to_timestamp('01/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service commun laboratoires (Economie)',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003157',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service économie et prospective (ARCEP)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003158',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Serv. régulation marchés fixe et mobiles',null,null,null,null,null,'UST');
--invalid char

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003159',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Serv. collect. rég. marchés haut débit',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003160',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service juridique (ARCEP)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003161',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service international (ARCEP)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003162',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service du directeur gal ou du président',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003163',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Service régulation postale (ARCEP)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003164',to_timestamp('24/08/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Serv admin ressources humaines (ARCEP)',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003207',to_timestamp('01/09/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Commission perm. publication du BOA',null,null,null,null,'50001106','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003232',to_timestamp('04/09/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Cabinet du SGG',null,null,null,null,'50000615','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003282',to_timestamp('06/10/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'OFPRA',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003307',to_timestamp('08/10/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'ANAEM',null,null,'50001885',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003357',to_timestamp('24/10/08 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Sce Bulletins Officiels DJO',null,null,null,null,'50000629','UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003632',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Caisse des dépôts et consignations',null,null,'50000507',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003707',to_timestamp('07/06/07 02:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DIV (MTS)',null,null,'50000969',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003733',to_timestamp('02/03/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Rapporteur Autorité de la concurrence',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003758',to_timestamp('02/03/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'Président Autorité de la concurrence',null,null,null,null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003834',to_timestamp('09/11/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'BDC Outre-mer',null,null,'50003832',null,null,'UST');

Insert into REPONSES_QA.UNITE_STRUCTURELLE (ID_ORGANIGRAMME,DATE_DEBUT,DATE_FIN,DATE_VERROU,DELETED,FUNCTION_READ,LABEL,UTILISATEUR_VERROU,NOR_DIRECTION,ID_PARENT_ENTITE,ID_PARENT_INSTITUTION,ID_PARENT_UNITE,TYPE) values ('50003836',to_timestamp('09/11/09 01:00:00','DD/MM/RR HH24:MI:FF'),to_timestamp(null,'DD/MM/RR HH24:MI:FF'),null,'0',null,'DéGéOM (Outre-Mer)',null,null,'50003832',null,null,'UST'); 

commit; 



