DELETE 
FROM hierarchy 
WHERE id IN (
	SELECT id 
	FROM feuille_route 
	WHERE id NOT IN (
		SELECT f.id 
		FROM feuille_route f INNER JOIN dossier_reponse r ON f.id = r.lastdocumentroute)
		AND id IN (
			SELECT m.id 
			FROM misc m 
			WHERE lifecyclestate IN ('running', 'ready')
		)
	);
COMMIT;
