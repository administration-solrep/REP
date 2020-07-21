update misc set lifecyclepolicy='minimaliste', lifecyclestate='running' where id in (select id from hierarchy where primarytype='Allotissement');
commit;
