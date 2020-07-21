Ce répertoire permet d'appliquer des correctifs à Nuxeo qui ne sont pas encore inclus dans la
distribution fournie par Nuxeo (via assembly.xml).

Poser simplement les .jar dans ce répertoire, ils seront copiés à la place des jar system
de la distribution.

Liste des JARS patchés:

- nuxeo-core-persistence-5.4.2-I20110404_0115.jar
    Exception lors de la fermeture d'une connexion deja fermee rattrapée et loggé
    	Modification sous svn : http://lyon-cvs2/dila_solrep/nuxeo/trunk/nuxeo-core-persistence

- nuxeo-platform-layouts-client-5.4.2-I20110404_0115.jar
    [SUPNXP-3761] Traduction Oui / Non dans les combobox

- nuxeo-platform-directory-ldap-5.4.2-I20110404_0115.jar
    - Modification de la méthode query de LDAPSession afin d'accepter des recherches avec '*'
    - Ajout d'un type de recherche dans le descriptor "submany"
    - Bug Nuxeo [SUPNXP-3773]
    
- nuxeo-core-storage-sql-5.4.2-I20110404_0115.jar
    - Nom initial : nuxeo-core-storage-sql-5.4.2-I20110404_0115-NODDL.jar
      patch pour SUPNXP-3634 (imcompatibilite clustering et noDDL)
    - patch pour SUPNXP-3507 (config de l'indexation fulltext : exclusion de document)
    - Ajout de la modification de nx_update_read_acl dans le fichier oracle.sql.txt
    - ajout correctif [NXP-7210]
    - ajout correctif [SUPNXP-4898 // NXP-7943 ] perte d'invalidation de cache sous oracle
    - modif requete pour la suppression des entree dans CLUSTER_NODES : test sur GV$SESSION au lieu de V$SESSION
    - (correction ligne commenté : ajout espace apres #) rev. 9163
    - NXP-9541 et NXP-7139 + modification de NX_UPDATE_READ_ACLS dans oracle.sql.txt
    
- nuxeo-core-storage-sql-extensions-5.4.2-I20110404_0115.jar
    - SUPNXP-6451
    
- nuxeo-core-api-5.4.2-I20110404_0115.jar
- nuxeo-platform-login-default-5.4.2-I20110404_0115.jar
    - patch pour SUPnXP-4118 (pb de login/logout concurrent)
    
- nuxeo-automation-server-5.4.2-I20110404_0115.jar
  [SUPNXP-4219] Impossible de liste dans le Nuxeo Shell les documents qui n'ont pas le schéma Dublin Core

- nuxeo-case-management-web-1.6-2.jar
  [SUPNXP-4222] Suppression des références aux contribs "forum" dans CMF

- nuxeo-platform-document-routing-api-5.4.2-I20110404_0115.jar
- nuxeo-platform-document-routing-core-5.4.2-I20110404_0115.jar
- nuxeo-platform-document-routing-web-5.4.2-I20110404_0115.jar
  [SUPNXP-4057] Utilisation de relation entre document : Nombre de requetes SQL
  [SUPNXP-4222] Suppression des références aux contribs "blogs/sites/pictures" dans CMF
- nuxeo-runtime-jtaca-5.4.2-I20110404_0115.jar
      - path pour générer des tmId differents sur les differentes instances
      (rev.7314 nuxeo/nuxeo-runtime-jtaca)
      le tmId est utilisé par XidImplFactory pour générer des id de transaction XA qui doivent etre unique
 
- nuxeo-platform-scheduler-core-5.4.2-I20110404_0115.jar
	Scheduler pour quartz 2.1.3