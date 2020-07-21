update misc set lifecyclestate = 'running' where id in (select hierarchy.id from hierarchy inner join misc on hierarchy.id = misc.id where misc.lifecyclestate = 'project' and hierarchy.primarytype = 'SmartFolder');
commit;
