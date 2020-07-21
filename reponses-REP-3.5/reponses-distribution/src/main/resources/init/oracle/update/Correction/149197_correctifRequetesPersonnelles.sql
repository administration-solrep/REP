-- Ce script permet de corriger les utilisateurs présentant des problèmes pour visualiser leurs requêtes personnelles
-- Il est à lancer après le script verificationRequetesPersonnelles 
UPDATE sw_aclr_user_aclid swac 
SET swac.acl_id = 
(SELECT distinct(hie.acl_id) FROM acls acl
	INNER JOIN hierarchy_read_acl hie ON hie.id=acl.id 
WHERE acl."USER"=swac.usergroup 
	AND NOT EXISTS 
		(SELECT null FROM sw_aclr_user_aclid sw2 
			WHERE sw2.acl_id=hie.acl_id))
WHERE swac.acl_id IS NULL AND EXISTS 
(SELECT 1 FROM acls acl
	INNER JOIN hierarchy_read_acl hie ON hie.id=acl.id 
WHERE acl."USER"=swac.usergroup 
	AND NOT EXISTS 
		(SELECT null FROM sw_aclr_user_aclid sw2 
			WHERE sw2.acl_id=hie.acl_id)) AND swac.usergroup in (select SUBSTR(aclr.acl, 0, INSTR(aclr.acl, '|')-1) from hierarchy_read_acl hie inner join aclr on hie.acl_id=aclr.acl_id where 
not exists (select 1 from sw_aclr_user_aclid sw where sw.acl_id=hie.acl_id) 
and hie.id in (select id from hierarchy where primarytype='SmartFolder'));
commit;
