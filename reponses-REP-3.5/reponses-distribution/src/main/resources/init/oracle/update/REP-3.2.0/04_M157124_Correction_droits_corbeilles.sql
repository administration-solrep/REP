--Correction éventuelles de corbeilles qui pourraient poser problème avec les ACLS
insert into aclr_modified (HIERARCHY_ID, IS_NEW)
select ACLS.ID,0 from ACLS INNER JOIN HIERARCHY ON ACLS.ID = HIERARCHY.ID where HIERARCHY.PRIMARYTYPE LIKE 'ReponsesMailbox' 
AND ACLS.PERMISSION LIKE 'Everything' and ACLS."USER" LIKE 'Everyone' and ACLS.name LIKE 'local';
--Suppression des ACLS incohérentes
delete from aclr where acl_id in (SELECT hierarchy_read_acl.ACL_ID FROM hierarchy_read_acl where id in 
(select ACLS.ID from ACLS INNER JOIN HIERARCHY ON ACLS.ID = HIERARCHY.ID where HIERARCHY.PRIMARYTYPE LIKE 'ReponsesMailbox' 
AND ACLS.PERMISSION LIKE 'Everything' and ACLS."USER" LIKE 'Everyone' and ACLS.name LIKE 'local'));
--Suppression des permissions Everyone/Everything
DELETE FROM ACLS WHERE ACLS.ID IN (select ACLS.ID from ACLS INNER JOIN HIERARCHY ON ACLS.ID = HIERARCHY.ID where HIERARCHY.PRIMARYTYPE LIKE 'ReponsesMailbox' AND ACLS.PERMISSION LIKE 'Everything' and ACLS."USER" LIKE 'Everyone' and ACLS.name LIKE 'local'
) AND ACLS.PERMISSION LIKE 'Everything' and ACLS."USER" LIKE 'Everyone' and ACLS.name LIKE 'local';
--MAJ et recalcul avec les procédures stockées
call nx_update_read_acls();
call NX_VACUUM_READ_ACLS();
commit;
--En intégration SWORD -> 443 Corbeilles concernées. Temps d'exécution inférieur à 5 minutes.