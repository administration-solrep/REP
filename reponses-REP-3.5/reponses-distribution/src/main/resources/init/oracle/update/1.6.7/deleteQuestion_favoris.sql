delete from Question where id not in (select dr.iddocumentquestion from dossier_reponse dr);