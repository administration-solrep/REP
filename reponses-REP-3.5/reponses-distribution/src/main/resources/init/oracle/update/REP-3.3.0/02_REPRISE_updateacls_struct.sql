-- ACL LOCAL
CREATE TABLE "RLACL" (	
    "HIERARCHY_ID" VARCHAR2(36 BYTE)  NOT NULL ENABLE, 
    "ACL" VARCHAR2(4000),
    CONSTRAINT "RLACL_ID_HIERARCHY_FK" FOREIGN KEY ("HIERARCHY_ID") REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE
); 
   
ALTER TABLE RLACL ADD CONSTRAINT RLACL_HIERARCHY_ID_PKEY PRIMARY KEY (HIERARCHY_ID);


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


-- modif NX_UPDATE_READ_ACLS : mise à jour rlacl, utilisation de rlacl dans le recalcul des
-- acls sur les enfants 
create or replace PROCEDURE "NX_UPDATE_READ_ACLS"
-- Rebuild only necessary read acls
IS
  update_count PLS_INTEGER;
BEGIN
 MERGE INTO RLACL t
    USING (SELECT id, LISTAGG(CASE WHEN a."GRANT" = 0 THEN '-' ELSE '' END || a."USER", '|') WITHIN GROUP (ORDER BY pos) AS l
				FROM acls a, ACLR_PERMISSION p WHERE a.permission = p.permission 
        and a.id in (SELECT DISTINCT(m.hierarchy_id) id FROM aclr_modified m)
        group by id) s
    ON (t.hierarchy_id = s.id)
	WHEN MATCHED THEN UPDATE SET acl = s.l
    WHEN NOT MATCHED THEN INSERT (hierarchy_id, acl) VALUES (s.id, s.l);
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
  MERGE INTO HIERARCHY_READ_ACL t
    USING (
      WITH recursiveRA
     (hid, hacl) AS
       (SELECT parent.id, nx_get_read_acl(id) as hacl
        FROM hierarchy parent
        WHERE parent.id in (SELECT distinct(HIERARCHY_ID) FROM ACLR_MODIFIED)
        
        UNION ALL
        
        SELECT child.id, case when a.acl IS null THEN parent.hacl ELSE a.ACL || '|' || parent.HACL END
        FROM recursiveRA parent, hierarchy child
        LEFT OUTER JOIN RLACL a ON a.HIERARCHY_ID = child.id
        WHERE child.parentid = parent.hid
        )
      SELECT hid, hacl
      FROM recursiveRA
    ) s
    ON (t.id = s.hid)
    WHEN MATCHED THEN 
      UPDATE SET acl_id = nx_hash(s.hacl);
    
  DELETE FROM aclr_modified;
END;
/

COMMIT;
