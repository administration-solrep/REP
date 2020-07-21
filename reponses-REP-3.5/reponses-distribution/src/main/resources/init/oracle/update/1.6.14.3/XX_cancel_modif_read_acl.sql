-- #########################################################################################
-- Permet d'annuler les modification appliquée par 03_modif_read_acl.sql
-- #########################################################################################
-- Suppression des nouveaux index
DROP INDEX IDX_SW_ACLR_USER_ACLID_UG;

DROP INDEX IDX_SW_ACLR_USERID_USER_USERID;

DROP INDEX IDX_SW_ACLR_USERID_USER_USE2;


-- Suppression des nouvelles tables

DROP TABLE SW_ACLR_USERID_USER;
 

DROP TABLE SW_ACLR_USER_ACLID;
   


-- #########################################################################################

-- DROP PROCEDURE SW_FILL_ACLID;
-- DROP FUNCTION SW_RETRIEVE_USER;
-- DROP FUNCTION "SW_GET_READ_ACLS_FOR";

-- #########################################################################################
-- Creation des anciennes tables et index

  CREATE TABLE "ACLR_USER"
   (	"USER_ID" VARCHAR2(34) NOT NULL ENABLE,
	"USERS" "NX_STRING_TABLE"
   )
 NESTED TABLE "USERS" STORE AS "ACLR_USER_USERS";



  CREATE TABLE "ACLR_USER_MAP"
   (	"USER_ID" VARCHAR2(34) NOT NULL ENABLE,
	"ACL_ID" VARCHAR2(34) NOT NULL ENABLE
   );


  CREATE INDEX "ACLR_USER_MAP_ACL_ID_IDX" ON "ACLR_USER_MAP" ("ACL_ID", "USER_ID");

  CREATE INDEX "ACLR_USER_MAP_USER_ID_IDX" ON "ACLR_USER_MAP" ("USER_ID");

  CREATE INDEX "ACLR_USER_USER_ID_IDX" ON "ACLR_USER" ("USER_ID");

  CREATE INDEX "IDX_ACLRUSERMAP_1" ON "ACLR_USER_MAP" ("USER_ID", "ACL_ID");
  

-- #########################################################################################
-- Creation des anciennes procédures et fonctions


  CREATE OR REPLACE FUNCTION "NX_GET_READ_ACLS_FOR" (users NX_STRING_TABLE)
RETURN NX_STRING_TABLE
-- List read acl ids for a list of user/groups, using the cache
IS
  PRAGMA AUTONOMOUS_TRANSACTION; -- needed for insert, ok since what we fill is a cache
  user_md5 VARCHAR2(34) := nx_hash_users(users);
  in_cache NUMBER;
  aclids NX_STRING_TABLE;
BEGIN
  SELECT acl_id BULK COLLECT INTO aclids FROM aclr_user_map WHERE user_id = user_md5;
  SELECT COUNT(*) INTO in_cache FROM TABLE(aclids);
  IF in_cache = 0 THEN
    -- dbms_output.put_line('no cache');
    aclids := nx_list_read_acls_for(users);
    -- below INSERT needs the PRAGMA AUTONOMOUS_TRANSACTION
    INSERT INTO aclr_user VALUES (user_md5, users);
    COMMIT;
    INSERT INTO aclr_user_map SELECT user_md5, COLUMN_VALUE FROM TABLE(aclids);
    COMMIT;
  END IF;
  RETURN aclids;
END;
/





  CREATE OR REPLACE PROCEDURE "NX_VACUUM_READ_ACLS"
-- Remove unused read acls entries
IS
BEGIN
  -- nx_vacuum_read_acls vacuuming
  DELETE FROM aclr WHERE acl_id IN (SELECT r.acl_id FROM aclr r
    JOIN hierarchy_read_acl h ON r.acl_id=h.acl_id
    WHERE h.acl_id IS NULL);
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_user';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_user_map';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_modified';
END;
/


  CREATE OR REPLACE PROCEDURE "NX_REBUILD_READ_ACLS"
-- Rebuild the read acls tables
IS
BEGIN
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_user';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_user_map';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE hierarchy_read_acl';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_modified';
  INSERT INTO hierarchy_read_acl
    SELECT id, nx_get_read_acl_id(id)
      FROM (SELECT id FROM hierarchy WHERE isproperty = 0);
END;
/



  CREATE OR REPLACE TRIGGER "NX_TRIG_HIER_READ_ACL_MOD"
  AFTER INSERT OR UPDATE ON hierarchy_read_acl
  FOR EACH ROW
   WHEN (NEW.acl_id IS NOT NULL) BEGIN
  MERGE INTO aclr USING DUAL
    ON (aclr.acl_id = :NEW.acl_id)
    WHEN NOT MATCHED THEN
    INSERT (acl_id, acl) VALUES (:NEW.acl_id, nx_get_read_acl(:NEW.id));
END;
/



  CREATE OR REPLACE TRIGGER "NX_TRIG_ACLR_MODIFIED"
  AFTER INSERT ON aclr
  FOR EACH ROW
   WHEN (NEW.acl_id IS NOT NULL) DECLARE
  negusers NX_STRING_TABLE;
  acl NX_STRING_ARRAY;
  ace VARCHAR(4000);
  sep VARCHAR2(1) := '|';
BEGIN
  FOR r IN (SELECT * FROM ACLR_USER) LOOP
    -- Build a black list with negative users
    negusers := NX_STRING_TABLE();
    FOR i IN r.users.FIRST .. r.users.LAST LOOP
      negusers.EXTEND;
      negusers(i) := '-' || r.users(i);
    END LOOP;
    acl := split(:NEW.acl, sep);
    FOR i IN acl.FIRST .. acl.LAST LOOP
      ace := acl(i);
      IF ace MEMBER OF r.users THEN
         -- GRANTED
         INSERT INTO ACLR_USER_MAP SELECT r.user_id, :NEW.acl_id FROM DUAL
         WHERE NOT EXISTS (SELECT 1 FROM ACLR_USER_MAP WHERE user_id=r.user_id AND acl_id = :NEW.acl_id);
         GOTO next_user;
      END IF;
      IF ace MEMBER OF negusers THEN
         -- DENIED
         GOTO next_user;
      END IF;
    END LOOP;
    <<next_user>> NULL;
  END LOOP;
END;
/


-- #########################################################################################

CALL NX_REBUILD_READ_ACLS();

COMMIT;
