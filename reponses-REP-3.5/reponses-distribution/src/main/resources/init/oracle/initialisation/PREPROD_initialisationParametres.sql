-- Permet d'initialiser les valeurs par défaut de Réponses
-- Script pour la plateforme de pré-production (PREPROD)

-- URL de l'application transmise par mail
UPDATE parametre p SET p.value='https://reponses-preprod.ader.gouv.fr/reponses' WHERE p.id=
	(SELECT h.id FROM hierarchy h WHERE h.primarytype='Parametre' AND h.name='url-application-transmise-par-mel');

-- Adresse mail administrateur
UPDATE parametre p SET p.value='Administration-SOLREP@dila.gouv.fr' WHERE p.id=
	(SELECT h.id FROM hierarchy h WHERE h.primarytype='Parametre' AND h.name='adresse-mail-administrateur-application');

commit;
