
-- modif nx_get_read_acl : utilisation de rlacl
create or replace FUNCTION "NX_GET_READ_ACL"(id VARCHAR2)
RETURN VARCHAR2
-- Compute the merged read acl for a doc id
IS
  curid acls.id%TYPE := id;
  newid acls.id%TYPE;
  acl VARCHAR2(32767) := NULL;
  first BOOLEAN := TRUE;
  sep VARCHAR2(1) := '|';
BEGIN
  WHILE curid IS NOT NULL LOOP
    FOR r in (SELECT * FROM RLACL WHERE HIERARCHY_ID = curid) LOOP
      IF acl IS NOT NULL THEN
         acl := acl || sep;
      END IF;
      if r.ACL IS NOT NULL THEN
         acl := acl || r.acl;
      END IF;
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
/

