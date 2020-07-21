-- schema dossier_reponses_link : ajout de la colonne pour la date de signalement
ALTER TABLE DOSSIER_REPONSES_LINK ADD ( "DATESIGNALEMENTQUESTION" TIMESTAMP(6) );


-- Mise à jour des données
UPDATE dossier_reponses_link drl SET drl.dateSignalementQuestion = 
	(select q.datesignalementquestion 
	from dossier_reponses_link drl2 LEFT JOIN question q ON drl2.motscles = q.motscles 
								AND drl2.sourcenumeroquestion = q.sourcenumeroquestion 
	                            AND drl2.nomcompletauteur = q.nomcompletauteur 
	                            AND drl2.etatsquestion = q.etatsquestion 
	WHERE drl.id=drl2.id) 
WHERE SUBSTR(drl.etatsquestion, 2, 1)='t';
commit;