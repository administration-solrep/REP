update question q set q.idministereattributaire=(select dr.ministereattributairecourant from dossier_reponse dr where q.id = dr.iddocumentquestion) 
where q.id IN
(
    SELECT dr2.iddocumentquestion
    FROM dossier_reponse dr2
    WHERE q.id=dr2.iddocumentquestion and q.idministereattributaire != dr2.ministereattributairecourant
and dr2.ministereAttributairePrecedent is not null
);
commit;
