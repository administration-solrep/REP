-- Ce script permet de lister les utilisateurs présentant des problèmes pour visualiser leurs requêtes personnelles
-- Il est à lancer avant le script correctifRequetesPersonnelles afin d'identifier un certain nombre d'utilisateurs 
-- pour vérifier que les accès sont bien récupérés
select distinct(SUBSTR(aclr.acl, 0, INSTR(aclr.acl, '|')-1)) username 
from hierarchy_read_acl hie inner join aclr on hie.acl_id=aclr.acl_id 
where not exists (select 1 from sw_aclr_user_aclid sw where sw.acl_id=hie.acl_id) 
and hie.id in (select id from hierarchy where primarytype='SmartFolder');
