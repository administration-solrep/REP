
Executer la requete 


SELECT L.SOURCENUMEROQUESTION
   FROM HIERARCHY MB, HIERARCHY H, DOSSIER_REPONSES_LINK L, CASE_LINK C, MISC
M
   WHERE H.ID = L.ID AND C.ID=H.ID AND M.ID=H.ID
    AND MB.PRIMARYTYPE = 'ReponsesMailbox' AND MB.NAME =
'webservice-agriculture'
    AND H.PARENTID = MB.ID AND l.ROUTINGTASKTYPE=13
   AND M.LIFECYCLESTATE = 'todo'
   ORDER BY C."DATE" DESC;



et stocker le resultat dans un fichier

Executer le script suivant en lui donnant le fichier précédemment généré
   sh listDossierLinkAgriAttr.sh <fichier contenant le resultat de la  requete>

lancer le bench

make bench-scenario8 URL=...

make bench-scenario9 URL=...


