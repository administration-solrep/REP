alter table question rename column hasreponse to hasreponseinitiee;

commit;

update question q2
set q2.hasreponseinitiee = 1
where q2.id in (select q.id from dossier_reponse d, question q, note n
where d.iddocumentquestion = q.id
and d.iddocumentreponse = n.id
and n.note is not null);

update question q2
set q2.hasreponseinitiee = 0
where q2.id in (select q.id from dossier_reponse d, question q, note n
where d.iddocumentquestion = q.id
and d.iddocumentreponse = n.id
and n.note is null);

commit;