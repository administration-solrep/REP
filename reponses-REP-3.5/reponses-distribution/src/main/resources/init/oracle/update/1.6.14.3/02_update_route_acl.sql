
DELETE FROM ACLS WHERE ID IN (SELECT h1.ID FROM hierarchy h1, hierarchy h2 WHERE h1.primarytype = 'FeuilleRoute' AND h1.parentid != h2.id AND h2.name = 'modele-route');

DELETE FROM ACLS a WHERE a.ID IN (SELECT h.id FROM hierarchy h where h.primarytype = 'DocumentRouteInstancesRoot') AND a."USER" IN ('administrators', 'routeManagers');

UPDATE ACLS a SET a."GRANT" = 1, a.POS=0 WHERE a.ID IN (SELECT h.id FROM hierarchy h where h.primarytype = 'DocumentRouteInstancesRoot') AND a."USER" IN ('Everyone');

COMMIT;

