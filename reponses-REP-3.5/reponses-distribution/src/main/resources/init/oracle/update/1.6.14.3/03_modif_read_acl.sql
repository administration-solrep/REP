  
-- #########################################################################################


   CREATE TABLE SW_ACLR_USERID_USER (
	"USER_ID" VARCHAR2(34) NOT NULL ENABLE,
	"USERGROUP" VARCHAR(200)
);
 

CREATE TABLE SW_ACLR_USER_ACLID (
	"USERGROUP" VARCHAR(200) NOT NULL ENABLE,
	"ACL_ID" VARCHAR2(34)
);
   
-- #########################################################################################

CREATE INDEX IDX_SW_ACLR_USER_ACLID_UG ON SW_ACLR_USER_ACLID(USERGROUP, ACL_ID);

CREATE INDEX IDX_SW_ACLR_USERID_USER_USERID ON SW_ACLR_USERID_USER(USER_ID, USERGROUP);

CREATE INDEX IDX_SW_ACLR_USERID_USER_USE2 ON SW_ACLR_USERID_USER(USERGROUP, USER_ID);

-- #########################################################################################



CREATE OR REPLACE PROCEDURE SW_FILL_ACLID(usergroup VARCHAR) IS
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
/

CREATE OR REPLACE FUNCTION SW_RETRIEVE_USER(usergroups NX_STRING_TABLE)
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
/



CREATE OR REPLACE FUNCTION "SW_GET_READ_ACLS_FOR" (userid VARCHAR)
RETURN NX_STRING_TABLE
-- List read acl ids for a list of user/groups, using the cache
IS
  in_cache NUMBER;
  aclids NX_STRING_TABLE;
BEGIN
 	select distinct a.acl_id BULK COLLECT INTO aclids FROM SW_ACLR_USERID_USER u, SW_ACLR_USER_ACLID a 
	WHERE u.USERGROUP = a.USERGROUP  AND a.acl_id IS NOT NULL AND u.USER_ID= userid;
	RETURN aclids;
END;
/


CREATE OR REPLACE FUNCTION "NX_GET_READ_ACLS_FOR" (users NX_STRING_TABLE)
RETURN NX_STRING_TABLE
-- List read acl ids for a list of user/groups, using the cache
IS
  user_md5 VARCHAR2(34) := sw_retrieve_user(users);
BEGIN
  RETURN SW_GET_READ_ACLS_FOR(user_md5);
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
  EXECUTE IMMEDIATE 'TRUNCATE TABLE aclr_modified';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE SW_ACLR_USERID_USER';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE SW_ACLR_USER_ACLID';
END;
/


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
/



-- #########################################################################################

DROP TABLE ACLR_USER;

DROP TABLE ACLR_USER_MAP;

