
-- Initialisation du contenu de la table rlacl

MERGE INTO RLACL t
    USING (SELECT id, LISTAGG(CASE WHEN a."GRANT" = 0 THEN '-' ELSE '' END || a."USER", '|') WITHIN GROUP (ORDER BY pos) AS l
				FROM acls a, ACLR_PERMISSION p WHERE a.permission = p.permission group by id) s
    ON (t.hierarchy_id = s.id)
    WHEN NOT MATCHED THEN 
      INSERT (hierarchy_id, acl) VALUES (s.id, s.l);

COMMIT;
