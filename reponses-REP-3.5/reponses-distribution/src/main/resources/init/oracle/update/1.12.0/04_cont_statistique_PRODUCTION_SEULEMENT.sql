-- Ajout de colonne idministererattachement et intituleMinistereRattachement dans la table Question

update question set IDMINISTERERATTACHEMENT='60003413', INTITULEMINISTERERATTACHEMENT='Ministère de l''écologie, du développement durable et de l''énergie' where IDMINISTEREATTRIBUTAIRE='60002514';
update question set IDMINISTERERATTACHEMENT='60003416', INTITULEMINISTERERATTACHEMENT='Ministère chargé des transports, de la mer et de la pêche' where IDMINISTEREATTRIBUTAIRE='60002535';

commit;
