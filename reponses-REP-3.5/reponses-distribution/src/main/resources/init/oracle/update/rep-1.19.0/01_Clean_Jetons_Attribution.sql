delete from JETON_DOC where TYPE_WEBSERVICE='WsChercherAttributions' and ID_OWNER NOT IN ('SENAT', 'AN');
delete from JETON_DOC where TYPE_WEBSERVICE='WsChercherAttributionsDate' and ID_OWNER NOT IN ('SENAT', 'AN');
commit;
