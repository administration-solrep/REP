-- Création de la table pour l'affichage de la banniere en page d'accueil
ALTER TABLE "ETAT_APPLICATION"
add
   (
   "AFFICHAGE" NUMBER(1,0),  
   "MESSAGE" CLOB
   );   
commit;

