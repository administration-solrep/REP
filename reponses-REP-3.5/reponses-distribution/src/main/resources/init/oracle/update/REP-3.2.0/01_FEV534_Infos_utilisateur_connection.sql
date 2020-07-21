ALTER TABLE INFO_UTILISATEUR_CONNECTION 
ADD (
COURRIEL NVARCHAR2(2000),
TELEPHONE NVARCHAR2(2000),
MINISTERERATTACHEMENT NVARCHAR2(2000),
DIRECTION NVARCHAR2(2000),
POSTE NVARCHAR2(2000),
DATECREATION TIMESTAMP (6),
DATEDERNIERECONNEXION TIMESTAMP (6)
);

UPDATE INFO_UTILISATEUR_CONNECTION iuc 
SET iuc.DATEDERNIERECONNEXION = (select mix.derniereconnexion from (select h.name, pu.derniereconnexion from profil_utilisateur pu inner join hierarchy h on pu.id = h.id) mix WHERE iuc.username = mix.name)
WHERE iuc.DATEDERNIERECONNEXION IS NULL;

COMMIT;

alter table profil_utilisateur drop column derniereconnexion;