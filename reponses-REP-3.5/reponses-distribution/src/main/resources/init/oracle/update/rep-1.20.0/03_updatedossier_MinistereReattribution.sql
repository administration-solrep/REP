update dossier_reponse set ministerereattribution = NULL  where dossier_reponse.id in 
(select dossier_reponse.id as id from dossier_reponse inner join question on question.id = dossier_reponse.iddocumentquestion 
where ministerereattribution is not null and ministerereattribution=ministereattributairecourant);
commit;
