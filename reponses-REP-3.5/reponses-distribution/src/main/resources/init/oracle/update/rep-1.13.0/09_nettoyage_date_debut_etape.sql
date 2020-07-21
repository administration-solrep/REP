update routing_task r
set r.datedebutetape = null
where r.id IN (select routing_task.id from routing_task, misc
where misc.id = routing_task.id
and misc.lifecyclestate = 'ready'
and routing_task.datedebutetape is not null);

commit;