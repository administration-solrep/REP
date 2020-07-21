--ajout d'une colonne pour stocker le label de l'étape suivante dans le dossier


ALTER TABLE DOSSIER_REPONSE ADD LABELETAPESUIVANTE NVARCHAR2(2000);


commit;