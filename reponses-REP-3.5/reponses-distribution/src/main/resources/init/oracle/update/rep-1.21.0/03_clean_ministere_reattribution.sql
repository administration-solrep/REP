update dossier_reponse d set ministerereattribution = NULL where ministerereattribution is not null
  and not exists(select rt.id from routing_task rt inner join misc lcs on rt.id = lcs.id where rt.documentRouteId = d.lastdocumentroute and lcs.lifecyclestate = 'running' and rt.type='8'); 
  
commit;
