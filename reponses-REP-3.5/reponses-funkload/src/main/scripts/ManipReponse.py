# -*- coding: utf-8 -*-

import os # pour interagir avec l'OS et verifier qu'un fichier existe
import random
import urllib
from Utils import extractJsfState
from Utils import extractToken
from Utils import extractLastStartToken
from time import gmtime, strftime
from webunit.utility import Upload

from funkload.utils import Data
# EXAMPLE
# name "Feuille de route standard"
# key "Feuille de route standar"

#html_escape_table = {
#    '&': '&amp;',
#    '"': '&quot',
#    "'": '&apos;',
#    '>': '&gt;',
#    '<': '&lt;',    
#    '\xc3\xa8' : '&egrave;'
##    '': '&eacute;',
##    '': '&ecirc;',
##    '': '&agrave;',
##    '': '&acirc;'
#}
#
#def html_escape(text):
#    """Produce entities within text"""
#    return "".join(html_escape_table.get(c, c) for c in text)


class ManipReponse :
  
    def __init__(self, ftest):
        self.ftest = ftest
        
        # --- constantes pour les url des requetes POST ---
        self.urlMailBox = "/casemanagement/mailbox/mailbox_view.faces"
        self.urlPlanDeClassement = "/casemanagement/mailbox/plan_classement_view.faces"
        self.urlRecherche = "/recherche/edit_recherche.faces"
        self.urlResultatRecherche = "/recherche/requete_resultats.faces"
        self.urlSuivi = "/suivi/view_suivi.faces"
        self.urlAdministration = "/view_empty.faces"
        self.urlStatistiques = "/statistiques/view_report.faces"
        
        
        
    # -- login / logout ---
    # accueil
    # login
    # logout
    # getStats
    # checkLogin
    
    # Permet d atteindre la page login.jsp
    # N atteint pas la page de statistiques
    def accueil(self):
        self.ftest.get(self.ftest.server_url + "/login.jsp",
                 description="Recuperation de la page login.jsp")
        self.ftest.assert_('user_password' in self.ftest.getBody())
	
    def login(self, login, password):
        self.ftest.post(self.ftest.server_url + "/nxstartup.faces",
                  params=[
                ['user_name', login],
                ['user_password', password],
                ['language', 'fr'],
                ['requestedUrl', ''],
                ['form_submitted_marker', ''],
                ['Submit', 'Connexion']],
                  description="Login " + login)
        self.ftest.assert_('Mon identifiant dans REPONSES : ' + login in self.ftest.getBody(), 'login echoue pour : ' + login)
	  
	return self.urlMailBox

    def logout(self, login):
        self.ftest.get(self.ftest.server_url + "/logout",
                 description="Logout " + login)
        self.ftest.assert_('user_password' in self.ftest.getBody())
    
    def getStats(self):
        self.ftest.get(self.ftest.server_url + "/stats.jsp",
            description="Get /stats.jsp")
        
    def checkLogin(self, login, password):
        self.ftest.logd("checkLogin")
        self.login(login, password)
        self.logout(login)

    # --- USE TO INJECT QUESTION ---
    # injectQuestion
    def injectQuestion(self, user, password, xmlcontent):
        """Send a query using REST"""
        self.ftest.setBasicAuth(user, password)
        data = Data('text/xml', xmlcontent)
        #print(xmlcontent)
        self.ftest.post(self.ftest.server_url + '/site/reponses/WSquestion/envoyerQuestions',
                 data, description="Send query")
        print(self.ftest.getBody()) 
        self.ftest.assert_('<ns2:statut>OK</ns2:statut>' in self.ftest.getBody())
        self.ftest.clearBasicAuth()
        
        
    # --- Navigation dans le menu ---
    # goToMailBox
    # goToPlanDeClassement
    # goToRecherche
    # goToSuivi
    # goToAdministration
    # goToStatistiques
    
    ''' 
    La navigation dans le menu est fait via des requetes de type POST.
    L'URL de la requete de type POST qui mene a une page depend de la page source.
    C'est a cela que sert le parametre de type chaine de caracteres 'urlSource'
    Toutes les requetes de navigation retournent une nouvelle valeur d'urlSource (une chaine de caracteres)
    qui doit être utilise dans la requete suivante.
    '''   
    def goToMailBox(self, urlSource):
        self.ftest.post(self.ftest.server_url + urlSource, params=[
                                                                   ['userServicesForm_SUBMIT', '1'],
                                                                   ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
                                                                   ['userServicesForm:userServicesActionsTable:0:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:0:userServicesActionCommandLink'],
                                                                   ['id', 'espace_travail']],
                        description="Post go to MailBox")
        self.ftest.assert_('<h4>Mes Corbeilles</h4>' in self.ftest.getBody())
        return self.urlMailBox
	
    def goToPlanDeClassement(self, urlSource):
      self.ftest.post(self.ftest.server_url + urlSource, params=[
	['userServicesForm_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['userServicesForm:userServicesActionsTable:1:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:1:userServicesActionCommandLink'],
	['id', 'espace_plan_classement']],
	description="Post go to Plan de Classement")
      self.ftest.assert_('<h4>Plan de classement</h4>' in self.ftest.getBody())
      return self.urlPlanDeClassement
            
    def goToRecherche(self, urlSource):
      self.ftest.post(self.ftest.server_url + urlSource, params=[
	['userServicesForm_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['userServicesForm:userServicesActionsTable:2:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:2:userServicesActionCommandLink'],
	['id', 'espace_recherche']],
	description="Post go to Recherche")
      self.ftest.assert_('<h4>Mes Favoris</h4>' in self.ftest.getBody())
      return self.urlRecherche
    
    def goToSuivi(self, urlSource):
      self.ftest.post(self.ftest.server_url + urlSource, params=[
	['userServicesForm_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['userServicesForm:userServicesActionsTable:3:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:3:userServicesActionCommandLink'],
	['id', 'espace_suivi']],
	description="Post go to Suivi")
      self.ftest.assert_('<h4>Alertes</h4>' in self.ftest.getBody())
      return self.urlSuivi
      
    def goToAdministration(self, urlSource):
      self.ftest.post(self.ftest.server_url + urlSource, params=[
	['userServicesForm_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['userServicesForm:userServicesActionsTable:4:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:4:userServicesActionCommandLink'],
	['id', 'espace_administration']],
	description="Post go to Administration")
      self.ftest.assert_('<span class="subMenu">Utilisateurs</span>' in self.ftest.getBody())
      return self.urlAdministration
    
    def goToStatistiques(self, urlSource):
      self.ftest.post(self.ftest.server_url + urlSource, params=[
	['userServicesForm_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['userServicesForm:userServicesActionsTable:5:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:5:userServicesActionCommandLink'],
	['id', 'espace_statistiques']],
	description="Post go to Statistiques")
      self.ftest.assert_('Derni&egrave;re modification' in self.ftest.getBody())
      return self.urlStatistiques
    
    
    # --- Navigation dans la mail box ---
    # Navigation dans les dossiers #
      
    '''
    goToFirstDossier
    Affiche le premier dossier visible de la liste.
    Cette methode ne doit etre invoquee que depuis la mailbox quand une liste de dossiers est affichee dans l'espace de travail
    Elle fonctionne pour toutes les pages de la liste
    '''
    def ouvrirPremierDossier(self):
      link = extractToken(self.ftest.getBody(), '<a href=\"#\" onclick=\"if(typeof jsfcljs == \'function\'){jsfcljs(document.getElementById(\'corbeille_content\'),{\'corbeille_content:', "':'corbeille")
      self.ftest.post(self.ftest.server_url + self.urlMailBox, params=[
	  ['corbeille_content_SUBMIT', '1'],
	  ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	  ['corbeille_content:' + link, 'corbeille_content:' + link]],
	  description="Ouvre le premier dossier de la page")
	  
    
    '''
    listeQuestionsMailBox
    Cette methode ne doit etre invoquee que depuis la Mailbox
    Retourne la liste des dossiers visibles dans une page de la MailBox sous forme de tableau de chaines de car
    '''
    def listeQuestionsMailBox(self):
      listeQuestions = []
      
      '''
      Chaque bloc html qui contient un numero de question commence par '<div class=\"popupTarget\"'
      Cette chaine n'est utilisee que pour ces blocs.
      L'idee est d avancer de bloc en bloc en cherchant la chaine et d'en retirer l'identifiant
        de la question qui est toujours le premier element entoure de "> et de </a>
      '''
      html = self.ftest.getBody()
      index = html.find('<a href=\"#\" onclick=\"if(typeof jsfcljs == \'function\'){jsfcljs(document.getElementById(\'corbeille_content\')', 0)
      
      while index >= 0:
        html = html[index:]
        idQuestion = extractLastStartToken(html, '\">', '</a>')
        listeQuestions.append(idQuestion)
        html = html[10:]
        index = html.find('<a href=\"#\" onclick=\"if(typeof jsfcljs == \'function\'){jsfcljs(document.getElementById(\'corbeille_content\')')

      return listeQuestions
      
    '''
    ouvrirDossierParSourceNumero/
    Affiche un dossier visible de la liste a partir de son numero
    Cette methode ne doit etre invoquee que depuis la mailbox quand une liste de dossiers est affichee dans l'espace de travail    
    Elle ne fonctionne que si le dossier dont on passe le numero est visible a l'ecran
    
    sourceNumber est une chaine de caracteres (ex : 'AN 555')
    '''
    def ouvrirDossierParSourceNumero(self, sourceNumber):
          parts = sourceNumber.partition(" ");
          self.ouvrirDossierParNumero(parts[0], parts[2])
      
    '''
    ouvrirDossierParNumero
    Affiche un dossier visible de la liste a partir de son numero
    Cette methode ne doit etre invoquee que depuis la mailbox quand une liste de dossiers est affichee dans l'espace de travail    
    Elle ne fonctionne que si le dossier dont on passe le numero est visible a l'ecran
    
    source 'AN'
    number '555'
    '''
    def ouvrirDossierParNumero(self, source, number):
        
      sourceNumber = source + ' ' + number;
        
      # a appeler apres le chargement de la mailbox
      html = self.ftest.getBody()
      
      # extraction de l'identifiant du dossier dans la page. (il est de la forme nxl_cm_inbox_caselink:nxw_reponses_listing_title_link:j_id177)
      token = extractLastStartToken(html, '<a href=\"#\"', '\">' + sourceNumber + '</a>')
      link = extractToken(token, 'corbeille_content:', "':'corbeille")
     
      self.ftest.post(self.ftest.server_url + self.urlMailBox, params=[
    	['corbeille_content_SUBMIT', '1'],
    	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
    	['corbeille_content:' + link, 'corbeille_content:' + link]],
    	description="Ouvrir dossier numero : " + sourceNumber)
      
      checkstr = "N&deg;" + number + " de"
      self.ftest.assert_(checkstr in self.ftest.getBody(), "cannot found string " + checkstr)
     
    
     
    '''
    envoieQuestionEtapeSuivante
    Envoie la question courante dans l etape suivante
    il faut qu une question soit selectionnee
    fonctionne pour tous les utilisateurs (dbc, dgefp, ...)
    fonctionne a partir de la MailBox et de l'onglet bordereau
    '' '
    def envoieQuestionEtapeSuivanteFromBordereau(self):
      self.ftest.post(self.ftest.server_url + self.urlMailBox, params=[
            ['formFdr:reject_task_panelOpenedState', ''],
            ['formFdr:unconcerned_task_panelOpenedState', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_selectNodeOrganigrammePanelOpenedState', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_tree:input', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_nodeId', ''],
            ['formFdr_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['formFdr:fdrActionsTable:0:fdrValidateButton', 'formFdr:fdrActionsTable:0:fdrValidateButton']],
            description="Validation d'une question")
    '''
    
    def valideDossierFromFdR(self, urlCourante):
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
            ['formFdr:reject_task_panelOpenedState', ''],
            ['formFdr:unconcerned_task_panelOpenedState', ''],
            ['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_selectNodeOrganigrammePanelOpenedState', ''],
            ['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_tree:input', ''],
            ['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_nodeId', ''],
            ['formFdr_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['formFdr:fdrActionsTable:0:fdrValidateButton', 'formFdr:fdrActionsTable:0:fdrValidateButton']],
            description="Valide la question")
           
           
    # fonctionne a partir de la mailbox
    def verrouilleDossierFromParapheur(self, urlCourante):
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
            ['actionToolbarForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['actionToolbarForm:IdEditReponseButton', 'actionToolbarForm:IdEditReponseButton']],
            description="Verrouille le dossier")
      self.ftest.assert_("Verrouill&eacute; le " in self.ftest.getBody(), "chaine verrouile le non trouve")
            
            
    # fonctionne a partir de la mailbox
    def verrouilleDossierFromFdR(self, urlCourante):
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
            ['formFdr:reject_task_panelOpenedState', ''],
            ['formFdr:unconcerned_task_panelOpenedState', ''],
            ['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_selectNodeOrganigrammePanelOpenedState', ''],
            ['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_tree:input', ''],
            ['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_nodeId', ''],
            ['formFdr_SUBMIT', '1'],
            ['javax.faces.ViewState', 'j_id9'],
            ['formFdr:dossierActionsLockTable:0:j_id940', 'formFdr:dossierActionsLockTable:0:j_id940']],
            description="Verrouille le dossier")
      self.ftest.assert_("Verrouill&eacute; le " in self.ftest.getBody(), "chaine verrouile le non trouve")
      
    '''
    # 
    # !!! ATTENTION : Cette methode ne doit etre utilisee que depuis le bordereau sous peine de 'casser' le dossier
    def verrouilleDossierFromBordereau(self, urlCourante):
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
            ['formFdr:reject_task_panelOpenedState', ''],
            ['formFdr:unconcerned_task_panelOpenedState', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_selectNodeOrganigrammePanelOpenedState', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_tree:input', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_nodeId', ''],
            ['formFdr_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['formFdr:dossierActionsLockTable:0:j_id406', 'formFdr:dossierActionsLockTable:0:j_id406']],
            description="Verouille Dossier")
    '''        
    '''
    self.post(server_url + "/reponses/recherche/requete_resultats.faces", params=[
            ['formFdr:reject_task_panelOpenedState', ''],
            ['formFdr:unconcerned_task_panelOpenedState', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_selectNodeOrganigrammePanelOpenedState', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_tree:input', ''],
            ['formFdr:nxl_poste_reattribution_1:nxw_postes_1_nodeId', ''],
            ['formFdr_SUBMIT', '1'],
            ['javax.faces.ViewState', 'j_id30'],
            ['formFdr:dossierActionsLockTable:0:j_id410', 'formFdr:dossierActionsLockTable:0:j_id410']],
            description="Post /reponses/recherche...ete_resultats.faces")
    '''
    
    # deVerouilleDossierFromParapheur
    # Lorsque l'on deverouille un dossier depuis l'onglet parapheur, cela enregistre la reponse fournie.
    #  la requete de deverouillage a besoin de la reponse pour l'enregistrer
    # urlCourante : url du contexte. Il y a un probleme qui fait que la methode ne foncitonne que si cette url est celle de la mailbox
    # texteReponse : texte qui va etre enregistree comme reponse a la question
    # formatReponse : format d'enregistrement de la reponse. Peut prendre les valeurs 'text/html', 'texte/plain' ou 'texte/xml'
    def deVerouilleDossierFromParapheur(self, urlCourante, texteReponse, formatReponse):
      '''
      Exemple de requete complete : 
      self.post(server_url + "/reponses/casemanagement/mailbox/mailbox_view.faces", params=[
            ['actionToolbarForm:nxl_saisie_reponse:j_id1097:nxw_reponse_note_editor', '<p>Lorem Ipsum Facto</p>'],
            ['actionToolbarForm:nxl_saisie_reponse:j_id1097:nxw_reponse_note_editorselector', 'text/html'],
            ['actionToolbarForm_SUBMIT', '1'],
            ['javax.faces.ViewState', 'j_id15'],
            ['actionToolbarForm:IdSaveReponseButton', 'actionToolbarForm:IdSaveReponseButton']],
            description="Post /reponses/casemanag.../mailbox_view.faces")
      '''
      # recuperation du nom des parametres de la reponse
      html = self.ftest.getBody();
      idParamReponse = extractToken(html, 'nxw_reponse_note_editor\" name=\"', '\" class="mceEditor')
      
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
	[idParamReponse, texteReponse], # texte de la reponse
	[idParamReponse + 'selector', formatReponse], # format de la reponse : 'text/html', 'texte/plain' ou 'texte/xml'
	['actionToolbarForm_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['actionToolbarForm:IdSaveReponseButton', 'actionToolbarForm:IdSaveReponseButton']],
	description="Deverouille/enregistre le dossier depuis l'onglet parapheur")
      
      self.ftest.assert_(texteReponse in self.ftest.getBody(), "texte de la reponse non trouve")
      self.ftest.assert_('alt="Verrouiller"' in self.ftest.getBody(), "bouton verrouiller non present")
      
    
    def deVerrouilleDossierFromFdR(self, urlCourante):
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
	['formFdr:reject_task_panelOpenedState', ''],
	['formFdr:unconcerned_task_panelOpenedState', ''],
	['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_selectNodeOrganigrammePanelOpenedState', ''],
	['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_tree:input', ''],
	['formFdr:nxl_ministere_reattribution_1:nxw_reattributionMinistere_1_nodeId', ''],
	['formFdr_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['formFdr:dossierActionsLockTable:0:j_id940', 'formFdr:dossierActionsLockTable:0:j_id940']],
	description="Deverouille le dossier")
    
    
    #--- Onglet Fond de dossier ---#
            
    # ajouterPieceParlement
    # Suit toute la procedure pour ajouter une piece pour le parlement.
    # Fait plusieurs requetes a cause des nombreux (7) appels AJAX.
    # Ne fonctionne que si aucune piece n'est presente pour aucun des ministere ou parlement.
    # renvoie une exeption si le fichier a uploader n'est pas trouve ou si il n'a pas ete ajoute.
    # A cause des appels AJAX, a la fin de cette methode la page recuperable via getBody n'est pas une page complete.
    # afin de pouvoir recuperer certaines informations dans la page, la page html d'origine est retournee.
    def ajouterPieceParlement(self, urlCourante, filepath):
      
      # verification que le fichier existe
      if not os.path.exists(filepath):
        print('File not found : ' + filepath)
	
      # Il faut conserver le html de la page d'origine pour recuperer des information a l'interieur.
      #  les futur appels a la methode getBody vont retourner le resultat des requetes AJAX.
      htmlPageOrigine = self.ftest.getBody()
      
      
      # selection du crayon Vert du parlement.
      '''
      exemple de requete : 
      self.post(server_url + "/reponses/casemanagement/mailbox/mailbox_view.faces", params=[
	  ['AJAXREQUEST', 'document_properties:documentViewRegion'],
	  ['document_properties:fondDeDossierTree:1::j_id1064NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:2::j_id1064NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:3::j_id1064NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:input', 'document_properties:fondDeDossierTree:1::j_id1064'],
	  ['document_properties_SUBMIT', '1'],
	  ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	  ['document_properties:fondDeDossierTree:selectedNode', 'document_properties:fondDeDossierTree:1::j_id1064']],
	  description="Post /reponses/casemanag.../mailbox_view.faces")
      '''
      idNode = extractToken(self.ftest.getBody(), 'document_properties:fondDeDossierTree:1::', '\"><tbody>')
      
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
	['AJAXREQUEST', 'document_properties:documentViewRegion'],
	['document_properties:fondDeDossierTree:1::' + idNode + 'NodeExpanded', 'true'],
	['document_properties:fondDeDossierTree:2::' + idNode + 'NodeExpanded', 'true'],
	['document_properties:fondDeDossierTree:3::' + idNode + 'NodeExpanded', 'true'],
	['document_properties:fondDeDossierTree:input', 'document_properties:fondDeDossierTree:1::' + idNode],
	['document_properties_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['document_properties:fondDeDossierTree:selectedNode', 'document_properties:fondDeDossierTree:1::' + idNode]],
	description="AJAX : Select bouton parlement")
	
      # Clic sur 'Ajouter document'
      '''
      exemple de requete : 
      self.post(server_url + "/reponses/casemanagement/mailbox/mailbox_view.faces", params=[
	  ['AJAXREQUEST', 'document_properties:documentViewRegion'],
	  ['document_properties:fondDeDossierTree:1::j_id1064NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:2::j_id1064NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:3::j_id1064NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:input', 'document_properties:fondDeDossierTree:1::j_id1064'],
	  ['document_properties_SUBMIT', '1'],
	  ['javax.faces.ViewState', 'j_id3'],
	  ['SelectedNodeId', '9881bbf1-7c04-42b8-90e9-9f5f37bdf7be'],
	  ['typeAction', 'create'],
	  ['document_properties:j_id1101', 'document_properties:j_id1101'],
	  ['ajaxSingle', 'document_properties:j_id1101']],
	  description="Post /reponses/casemanag.../mailbox_view.faces")
      '''
      # idNode a la même valeur que dans la requete precedente
      idDocumentProperties = extractToken(htmlPageOrigine, 'document_properties:j_id', '\',\'parameters')
      SelectedNodeId = extractToken(htmlPageOrigine, 'return {\'id\': \'', '\', \'name\'')
      # print(SelectedNodeId)
      SelectedNodeId = SelectedNodeId.replace('\\x2D', '-')
      # print(SelectedNodeId)
      
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
	  ['AJAXREQUEST', 'document_properties:documentViewRegion'],
	  ['document_properties:fondDeDossierTree:1::' + idNode + 'NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:2::' + idNode + 'NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:3::' + idNode + 'NodeExpanded', 'true'],
	  ['document_properties:fondDeDossierTree:input', 'document_properties:fondDeDossierTree:1::' + idNode],
	  ['document_properties_SUBMIT', '1'],
	  ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	  ['SelectedNodeId', SelectedNodeId],
	  ['typeAction', 'create'],
	  ['document_properties:j_id' + idDocumentProperties, 'document_properties:j_id' + idDocumentProperties],
	  ['ajaxSingle', 'document_properties:j_id' + idDocumentProperties]],
	  description="AJAX : Ajouter document'")
      
      # ajout du fichier
      
      idCreateFondDeDossier = extractToken(self.ftest.getBody(), 'new ProgressBar(\'', '\',\'createFondDeDossierRegion')
      
      self.ftest.post(self.ftest.server_url + urlCourante, params=[
            ['AJAXREQUEST', 'createFondDeDossierRegion'],
            ['createFondDeDossierForm:uploadFondDeDossierFiles:file', ''],
            ['createFondDeDossierForm:radioButtonNiveauVisibilite', 'A diffuser vers le parlement'],
            ['createFondDeDossierForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            [idCreateFondDeDossier, idCreateFondDeDossier],
            ['AJAX:EVENTS_COUNT', '1']],
            description="AJAX : Requete pre-upload : Affichage de la boite de telechargement")

      # print("### Upload du fichier : " + filepath)
      filename = os.path.basename(filepath) 
      
      # rich face random uid
      upload_uid = str(random.random()) 

      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces?_richfaces_upload_uid=" + upload_uid + "&createFondDeDossierForm:uploadFondDeDossierFiles=createFondDeDossierForm:uploadFondDeDossierFiles&_richfaces_upload_file_indicator=true&AJAXREQUEST=createFondDeDossierRegion", params=[
            ['createFondDeDossierForm:uploadFondDeDossierFiles:file', Upload(filepath)], # adresse du fichier
            ['createFondDeDossierForm:radioButtonNiveauVisibilite', 'A diffuser vers le parlement'], # valeurs possibles : 'A diffuser vers le parlement', 'Minist\xc3\xa8re et SGG' et 
            ['createFondDeDossierForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())]],
            description="AJAX : Upload du fichier")
            
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[
            ['AJAXREQUEST', 'createFondDeDossierRegion'],
            ['createFondDeDossierForm:uploadFondDeDossierFiles:file', ''],
            ['createFondDeDossierForm:radioButtonNiveauVisibilite', 'A diffuser vers le parlement'],
            ['createFondDeDossierForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['createFondDeDossierForm:j_id1127', 'createFondDeDossierForm:j_id1127'], # id fixe
            ['AJAX:EVENTS_COUNT', '1']],
            description="AJAX : Requete post-upload : Reaffichage de la boite de telechargement.")
            
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[
            ['AJAXREQUEST', 'createFondDeDossierRegion'],
            ['createFondDeDossierForm:uploadFondDeDossierFiles:file', ''],
            ['createFondDeDossierForm:radioButtonNiveauVisibilite', 'A diffuser vers le parlement'],
            ['createFondDeDossierForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['createFondDeDossierForm:j_id1136', 'createFondDeDossierForm:j_id1136'], # id fixe
            ['AJAX:EVENTS_COUNT', '1']],
            description="AJAX")
    
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[
            ['AJAXREQUEST', 'createFondDeDossierRegion'],
            ['createFondDeDossierForm:uploadFondDeDossierFiles:file', ''],
            ['createFondDeDossierForm:radioButtonNiveauVisibilite', 'A diffuser vers le parlement'],
            ['createFondDeDossierForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['ajaxSingle', 'createFondDeDossierForm:createFondDeDossierButtonText'],
            ['createFondDeDossierForm:createFondDeDossierButtonText', 'createFondDeDossierForm:createFondDeDossierButtonText'],
            ['AJAX:EVENTS_COUNT', '0']],
            description="AJAX : Validation definitive de l'upload")
            
      # les reponses AJAX ne permettent pas de savoir si le document a bien ete uploade
      
      # Verification que le dossier dispose bien d'un document ayant le nom de celui que l'on vient d'ajouter.
      # Attention, si on ajoute 2 documents avec le meme nom, un seul d'entre eux est bien uploade.
      self.ftest.assert_('>' + filename + '</a>' in self.ftest.getBody())
    
      return htmlPageOrigine
    
    
   # -- ajout etape feuille de route --- #
   
    # ajoutEtapeFDRApresEtapeCourante
    # Cette methode ajoute une etape a la feuille de route du dossier apres l'etape en cours.
    # Ele doit être appellee depuis l'onglet feuille de route.
    # parametres : 
    #   CommentaireNouvelleEtape : chaine de caracteres
    #   idPoste : chaine de caracteres de l'identifiant ldap du poste (ex : 'poste-50000656')
    #   deadLine : chaine de caracteres representant un nomre (ex : '3')
    #   validationAutomatique : chaine de caracteres : 'on' ou 'off'
    #   obligatoire : chaine de caracteres : chaine de caracteres : 'on' ou 'off'
    # si un de ces parametres est passe a None, il prend par deffaut la valeur '' ou 'off'
    def ajoutEtapeFDRApresEtapeCourante(self, CommentaireNouvelleEtape, idPoste, deadLine, validationAutomatique, obligatoire):
      
      if CommentaireNouvelleEtape == None: 
        CommentaireNouvelleEtape = ''

      if deadLine == None:
        deadLine = ''

      dossierPath = self.__extractDossierPath(self.ftest.getBody())
      
      # recuperation de l'etape courante en utilisant l'image
      htmlEtapeCourante = extractToken(self.ftest.getBody(), '<li class=\"ctxMenuItemStyle\">', 'img/icons/bullet_ball_glass_yellow_16.png')
      # recuperation des differents elements de la requete
      hiddenSourceDocId = extractToken(htmlEtapeCourante, 'hiddenSourceDocId\').value = \'', '\';')
      # ajouter apres
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[
        ['selectRouteElementsTypeForCreationForm:hiddenSourceDocId', hiddenSourceDocId], # 865380c5-337d-4aa9-827e-7d5f2ae1875b
        ['selectRouteElementsTypeForCreationForm:hiddenDocOrder', 'after'],
        ['selectRouteElementsTypeForCreationForm:selectRouteElementsTypePanelOpenedState', ''],
        ['selectRouteElementsTypeForCreationForm_SUBMIT', '1'],
        ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
        ['selectRouteElementsTypeForCreationForm:selectRouteElementsTypeForCreationTable:0:selectRouteElementTypeForCreationCategory:0:selectRouteElementsTypeForCreationCategoryTable:0:createRouteElementLink', 'selectRouteElementsTypeForCreationForm:selectRouteElementsTypeForCreationTable:0:selectRouteElementTypeForCreationCategory:0:selectRouteElementsTypeForCreationCategoryTable:0:createRouteElementLink']],
        description="AJAX : insertion d'une etape apres l'etape courante")
      
      
      # creation de l'etape
      self.ftest.post(self.ftest.server_url + "/feuilleroute/create_route_element.faces", params=[
	['document_create:nxl_routing_task_detail:nxw_routing_task_type', '2'], # Pour attribution : '2', Pour reattribution : '8', ...
	['document_create:nxl_routing_task_detail:nxw_routing_task_distribution_mailbox_selectNodeOrganigrammePanelOpenedState', ''],
	['document_create:nxl_routing_task_detail:nxw_routing_task_distribution_mailbox_tree:input', ''],
	['document_create:nxl_routing_task_detail:nxw_routing_task_distribution_mailbox_nodeId', idPoste], # 'poste-50000656' ou 'poste-bureau-des-cabinets'
	['document_create:nxl_routing_task_detail:nxw_routing_task_description', CommentaireNouvelleEtape], # 'Commentaire nouvelle \xc3\xa9tape.'
	['document_create:nxl_routing_task_detail:nxw_routing_task_deadline:nxw_routing_task_deadline_from', deadLine],
	['document_create:nxl_routing_task_detail:nxw_routing_task_automatic_validation', validationAutomatique],
	['document_create:nxl_routing_task_detail:nxw_routing_task_obligatoire_ministere', obligatoire],
	['parentDocumentPath', dossierPath],
	['document_create:button_create', 'Cr\xc3\xa9er'], # bouton de validation
	['document_create_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())]],
	description="Creation de l'etape de la feuille de route")
   
   ############################################################################# --- fin feuille de route ---  #####
   
   
   
   
   
    
    # navigation dans la liste des questions #
    # goToLastPageOfCorbeille
    # goToPreviousPageofCorbeille
    # goToNextPageofCorbeille
    # goToFirstPageofCorbeille
    
    def goToLastPageOfCorbeille(self):
      self.__navigateById(self.__extractLastNavigationId(), "last page")
            
    def goToPreviousPageofCorbeille(self):
        self.__navigateById(self.__extractPrevNavigationId(), "prev page")

    def goToNextPageofCorbeille(self):
        self.__navigateById(self.__extractNextNavigationId(), "next page")

    def goToFirstPageofCorbeille(self):
        self.__navigateById(self.__extractFirstNavigationId(), "first page")
        
        
        
    def __extractLastNavigationId(self):
      return self.__extractNavigation('action_page_fastforward.gif')
    
    def __extractPrevNavigationId(self):
        return self.__extractNavigation('action_page_previous.gif')

    def __extractNextNavigationId(self):
        return self.__extractNavigation('action_page_next.gif')

    def __extractFirstNavigationId(self):
        return self.__extractNavigation('action_page_rewind.gif')


    def hasNavigationNext(self):
        return self.__extractNavigation('action_page_next.gif') != None
    
    def hasNavigationPrevious(self):
        return self.__extractNavigation('action_page_previous.gif') != None
        
    def hasNavigationLast(self):
        return self.__extractNavigation('action_page_fastforward.gif') != None
    
    def hasNavigationFirst(self):
        return self.__extractNavigation('action_page_rewind.gif') != None    
    
    
    
    def __extractNavigation(self, imgname):
      html = self.ftest.getBody()
      id = extractToken(html, imgname + '" name="', '" alt')
      return id
        
    def __navigateById(self, id, title):
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[         
        [id + '.x', '13'], # la valeur de ce parametre varie, mais ne semble pas avoir d'effet
	[id + '.y', '7'], # la valeur de ce parametre varie, mais ne semble pas avoir d'effet
	['corbeille_content_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())]],
	description="Go to " + title + " for : " + self.ftest.login)      
            
            
            
    # navigation dans les onglets du dossier selectionne #
    # Ces methodes sont utilisables pour la mail box, le plan de classement et les elements de la recherche
    # il convient de passer en paramettre l url courante (mailbox, plan de classement ou recherche) via le parametre urlContext)
    #  (voir les parametres ManipReponse.urlMailbox, ManipReponse.urlPlanDeClassement et ManipReponse.urlRecherche)
    # Ces methodes recupere des informations dans le contenu de la page courante.
    # Pour le cas où la page courante n'est pas la page complete (a cause des requetes AJAX), des methodes existent où l'on passe le contenu de la page en parametre.
    # goToBordereauTab
    # goToParapheurDeRouteTab
    # goToFondDeDossierTab
    # goToFeuilleDeRouteTab
    # goToJournalTab
    # goToDossierConnexeTab
    # goToAllostissementTab
    
    def goToBordereauTab(self, urlContext):
        dossierPath = self.__extractDossierPath(self.ftest.getBody())
        tab = 'TAB_DOSSIER_BORDEREAU'
        self.__goToDossierTab(dossierPath, tab, urlContext, 'Onglet bordereau')        
        # self.ftest.assert_('<h3 class=\"summaryTitle\">Indexation</h3>' in self.ftest.getBody())
        
    def goToParapheurTab(self, urlContext):
        dossierPath = self.__extractDossierPath(self.ftest.getBody())
        tab = 'TAB_DOSSIER_PARAPHEUR'
        self.__goToDossierTab(dossierPath, tab, urlContext, 'Onglet parapheur')        
        # self.ftest.assert_('<h3 class=\"summaryTitle\">Question</h3>' in self.ftest.getBody())
    
    def goToFondDeDossierTab(self, urlContext):
        dossierPath = self.__extractDossierPath(self.ftest.getBody())
        tab = 'TAB_DOSSIER_FDD'
        self.__goToDossierTab(dossierPath, tab, urlContext, 'Onglet dossier')        
        # self.ftest.assert_("<h3>Fond de Dossier</h3>" in self.ftest.getBody())
    
    def goToFeuilleDeRouteTab(self, urlContext):
        self.goToFeuilleDeRouteTab2(urlContext, self.ftest.getBody())

    # permet d'aller vers la feuille de route mais il faut passer le html de la page en parametre
    def goToFeuilleDeRouteTab2(self, urlContext, htmlPageComplete):
        dossierPath = self.__extractDossierPath(htmlPageComplete)
        tab = 'TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE'
        self.__goToDossierTab(dossierPath, tab, urlContext, 'Onglet feuille de route')        
        # self.ftest.assert_("" in self.ftest.getBody())
        
    def goToJournalTab(self, urlContext):
        dossierPath = self.__extractDossierPath(self.ftest.getBody())
        tab = 'TAB_DOSSIER_JOURNAL'
        self.__goToDossierTab(dossierPath, tab, urlContext, 'Onglet journal')
        # self.ftest.assert_("" in self.ftest.getBody())
         
    def goToDossierConnexeTab(self, urlContext):
        dossierPath = self.__extractDossierPath(self.ftest.getBody())
        tab = 'TAB_DOSSIER_CONNEXE'
        self.__goToDossierTab(dossierPath, tab, urlContext, 'Onglet dossiers connexes')
        # self.ftest.assert_("" in self.ftest.getBody())
        
    def goToAllostissementTab(self, urlContext):
        dossierPath = self.__extractDossierPath(self.ftest.getBody())
        tab = 'TAB_ALLOTISSEMENT'
        self.__goToDossierTab(dossierPath, tab, urlContext, 'Onglet allotissement')
        # self.ftest.assert_("" in self.ftest.getBody())       
        
         
    # Pour se rendre dans l'onglet d'un dossier --- 
    # L'url de l'onglet depend du menu courant (Espace de travail, Plan de classement, Recherche, ...)
    # tab : correspond a l'onglet que l'on souahite ouvrir. Il prend pour valeur TAB_DOSSIER_BORDEREAU/TAB_DOSSIER_PARAPHEUR/TAB_DOSSIER_FDD/TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE
    # urlMenu : correspond a l'url du menu courant. Il prend les valeurs possibles des variables urlMailBox, urlPlanDeClassement, ...
    def __goToDossierTab(self, dossierPath, tab, urlMenu, descrip=None):
        #dossierPath = "2011/02/15/Dossier%2034"
        
        if urlMenu == self.urlMailBox:
	  urlPart = 'mailbox_view'
	elif urlMenu == self.urlPlanDeClassement:
	  urlPart = 'view_plan_classement'
	elif urlMenu == self.urlRecherche:
	  urlPart = 'view_requete_results'
        
        if descrip == None:
	  descrip = "Ouvrir onglet " + tab + " pour le dossierpath[" + str(dossierPath) + "] et l'utilisateur : " + self.ftest.login
	  
        self.ftest.get(self.ftest.server_url + "/nxpath/default/case-management/case-root/" + dossierPath + '@' + urlPart + '?tabId=' + tab + "&conversationId=0NXMAIN",
            description=descrip)
    
            
            
    ''' 
    extractDossierPath
    Cette fonction extrait le path du dossier affiche et le retourne
    Le path est utilise pour la construction des url de navigation dans les onglets d'un dossier
    ex de path : 2011/05/24/Dossier 669.1306223817352
    '''
    def __extractDossierPath(self, html):
      
        dossierpath = extractToken(html, '<li class=\"selected\"><a href=\"' + self.ftest.server_url + '/nxpath/default/case-management/case-root/', '@') # + urlPart+ '?tabId=')
        if not dossierpath:
            raise ValueError('No dossier path found in the page')
        if not dossierpath.startswith('201'):
            raise ValueError('Invalid dossier path %s' % str(dossierpath))
        return dossierpath
        
     # / --- Fin navigation dans un dossier de la mail box ---    
     
    # -------------- Navgation dans le plan de classement --------------------- #
    
    
    
    # dans l'onglet plan de classement, cette methode permet de deplier une des rubriques puis de selectionner une tete d'analyse
    # les identifiant de rubrique et de tête d'analyse sont leur numero (sous forme de chaine decaractere ex : '1')
    def pDCAccedeTeteAnalyse(self, numRubrique, numTeteAnalyse):
      # deplie la rubrique
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/plan_classement_view.faces", params=[
	['AJAXREQUEST', 'j_id68:corbeilleRegion'],
	['j_id68:pct:input', 'j_id68:pct:n1:' + numRubrique + '::j_id69'],
	['j_id68_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['ajaxSingle', 'j_id68:pct'],
	['j_id68:pct:selectedNode', 'j_id68:pct:n1:' + numRubrique + '::j_id69'],
	['AJAX:EVENTS_COUNT', '1']],
	description="Deplie rubrique du plan de classement")
       # selectionne une tete d'analyse.
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/plan_classement_view.faces", params=[
	['j_id68:pct:input', 'j_id68:pct:n1:' + numRubrique + ':n2:' + numTeteAnalyse + '::j_id70'],
	['j_id68_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['j_id68:pct:n1:' + numRubrique + ':n2:' + numTeteAnalyse + '::n2Cmd', 'j_id68:pct:n1:' + numRubrique + ':n2:' + numTeteAnalyse + '::n2Cmd']],
	description="Selectionne une tête d'analyse du plan de classement")
     
    # ouvreDossierPlanDeClassement
    # ouvre le premier dossier visible dans la listening
    # il faut qu'une liste de dossiers soit visible
    # ne fonctionne que depuis une liste de dossiers du plan de classement
    def ouvreDossierPlanDeClassement(self, identifiant):
      # extraction de l'identifiant du dossier dans la page
      token = extractLastStartToken(self.ftest.getBody(), '<a href="#"', '   ' + identifiant + '</a>')
      link = extractToken(token, 'plan_classement_content:', "':'plan_classement_content")
      self.ftest.post(self.ftest.server_url + self.urlPlanDeClassement, params=[
	    ['plan_classement_content_SUBMIT', '1'],
	    ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	    ['plan_classement_content:' + link, 'plan_classement_content:' + link]],
	    description="Ouverture de la question " + identifiant + " dans le plan de classement.")
    
    # selectPremierMinistereDossiersConnexes
    # Selectionne dans la liste des dossiers connexe le premier ministere visibles
    def selectPremierMinistereDossiersConnexes(self):
      paramMinistere = extractToken(self.ftest.getBody(), 'jsfcljs(document.getElementById(\'document_properties\'),{\'', '\':\'document_properties:')
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/plan_classement_view.faces", params=[
            ['document_properties_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            [paramMinistere, paramMinistere]],
            description="Ouverture du premier ministere des questions connexes")
     
    # selectionne une des questions connexes visible a l'ecran
    # on fourni le numero d'ordre de la question ( ex : '0' permet d'ouvrir la permiere question connexe visible a l'ecran)
    def ouvertureDossierConnexe(self, numQuestionDansListe):
      idParamQuestion = extractToken(self.ftest.getBody(), 'document_properties\'),{\'document_properties:table_dc:' + numQuestionDansListe + ':', '\':\'document_properties')
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/plan_classement_view.faces", params=[
            ['document_properties_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['document_properties:table_dc:' + numQuestionDansListe + ':' + idParamQuestion, 'document_properties:table_dc:' + numQuestionDansListe + ':' + idParamQuestion]],
            description="Ouverture dossier connexe")
            
      
    # -- Navigation dans le menu recherche --- #
    
    '''
    Ne fonctionne plus avec la version 1.5.5
    chercherUneQuestion
    Envoie une requete qui recherche une question en utilisant son identifiant et les parametres par defaut de la recherche
    numero est une chaine de caracteres
    auteur est une chaine de caracteres qui peut rester vide si l'auteur ne doit pas être recherche (ex : FunkLoad Test)
    retourne une nouvelle urlCourante
    
    def chercherUneQuestionParNumero(self, numero = '', auteur = ''):
      currentDate = strftime("%d/%Y", gmtime())
        
      self.ftest.post(self.ftest.server_url + self.urlRecherche, params=[
            ['requete_all_form:nxl_requeteSimple:nxw_legislature', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_critereRequete', numero],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_fromInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_toInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_fromInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_toInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_auteur', auteur],
            ['requete_all_form:nxl_requeteSimple:j_id219_selection', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_groupePolitique', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_ministereAttribute', ''],
            ['requete_all_form:nxl_requeteSimple:j_id250_selection', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_ministereInterroge', ''],
            ['requete_all_form:nxl_requeteSimple:j_id270_selection', ''],
            ['requete_all_form:nxl_requeteTexteIntegral:nxw_critere_requete', ''],
            ['requete_all_form:nxl_requeteTexteIntegral:nxw_dansTexteQuestion', 'on'],
            ['requete_all_form:currentIndexationMode', 'TOUS'],
            ['requete_all_form:nxl_requeteIndexSenat:nxw_simple_indexation_senat_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexSenat:nxw_simple_indexation_senat_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteIndexMinistere:nxw_simple_indexation_ministere_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexMinistere:nxw_simple_indexation_ministere_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_route_suggest', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_route_suggestionBox_selection', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_routeId', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_typeStep', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_fromInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_toInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_fromInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_toInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_statusStep', ''],
            ['requete_all_form:rechercheSubmitButton', 'Recherche'],
            ['requete_all_form_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())]],
            description="Rechercher la question numero " + numero)
            
      return self.urlResultatRecherche
    '''       
    '''
    Version 1.5.3
    # retourne une nouvelle urlCourante
    def chercherUneQuestion(self, source, dateDebutJOQuestion, dateFinJOQuestion, groupePolitique):
      currentDate = strftime("%d/%Y", gmtime())
      self.ftest.post(self.ftest.server_url + "/recherche/edit_recherche.faces", params=[
            ['requete_all_form:nxl_requeteSimple:nxw_legislature', ''],
            ['requete_all_form:nxl_requeteSimple:j_id97', source],
            ['requete_all_form:nxl_requeteSimple:nxw_critereRequete', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_fromInputDate', dateDebutJOQuestion],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_toInputDate', dateFinJOQuestion],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_fromInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_toInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_auteur', ''],
            ['requete_all_form:nxl_requeteSimple:j_id219_selection', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_groupePolitique', groupePolitique],
            ['requete_all_form:nxl_requeteSimple:nxw_ministereAttribute', ''],
            ['requete_all_form:nxl_requeteSimple:j_id250_selection', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_ministereInterroge', ''],
            ['requete_all_form:nxl_requeteSimple:j_id270_selection', ''],
            ['requete_all_form:nxl_requeteTexteIntegral:nxw_critere_requete', ''],
            ['requete_all_form:nxl_requeteTexteIntegral:nxw_dansTexteQuestion', 'on'],
            ['requete_all_form:currentIndexationMode', 'TOUS'],
            ['requete_all_form:nxl_requeteIndexSenat:nxw_simple_indexation_senat_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexSenat:nxw_simple_indexation_senat_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteIndexMinistere:nxw_simple_indexation_ministere_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexMinistere:nxw_simple_indexation_ministere_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_route_suggest', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_route_suggestionBox_selection', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_routeId', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_typeStep', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_fromInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_toInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_fromInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_toInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_statusStep', ''],
            ['requete_all_form:rechercheSubmitButton', 'Recherche'],
            ['requete_all_form_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())]],
            description="Recherche")
            
      return self.urlResultatRecherche
    '''
    
    
    def chercherUneQuestion(self, source, dateDebutJOQuestion, dateFinJOQuestion, groupePolitique):
      currentDate = strftime("%d/%Y", gmtime())
      self.ftest.post(self.ftest.server_url + "/recherche/edit_recherche.faces", params=[
            ['requete_all_form:nxl_requeteSimple:nxw_legislature', ''],
            ['requete_all_form:nxl_requeteSimple:j_id97', source],
            ['requete_all_form:nxl_requeteSimple:nxw_critereRequete', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_fromInputDate', dateDebutJOQuestion],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_toInputDate', dateFinJOQuestion],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOQuestion:nxw_dateJOQuestion_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_fromInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_fromInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_toInputDate', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_dateJOReponse:nxw_dateJOReponse_toInputCurrentDate', currentDate],
            ['requete_all_form:nxl_requeteSimple:nxw_auteur', ''],
            ['requete_all_form:nxl_requeteSimple:j_id219_selection', ''],
            ['requete_all_form:nxl_requeteSimple:nxw_groupePolitique', groupePolitique],
            ['requete_all_form:nxl_requeteSimple:j_id246:j_id247:j_id248', ''],
            ['requete_all_form:nxl_requeteSimple:j_id266:j_id271:j_id272', ''],
            ['requete_all_form:nxl_requeteTexteIntegral:nxw_critere_requete', ''],
            ['requete_all_form:nxl_requeteTexteIntegral:nxw_dansTexteQuestion', 'on'],
            ['requete_all_form:currentIndexationMode', 'INDEX_ORIG'],
            ['requete_all_form:nxl_requeteIndexSenat:nxw_simple_indexation_senat_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexSenat:nxw_simple_indexation_senat_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteIndexMinistere:nxw_simple_indexation_ministere_indexLabel_', ''],
            ['requete_all_form:nxl_requeteIndexMinistere:nxw_simple_indexation_ministere_index_suggestBox__selection', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_route_suggest', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_route_suggestionBox_selection', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_titreFeuilleRoute_routeId', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_typeStep', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_fromInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_fromInputCurrentDate', '08/2011'],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_toInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeBeginDateStep:nxw_rangeBeginDateStep_toInputCurrentDate', '08/2011'],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_fromInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_fromInputCurrentDate', '08/2011'],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_toInputDate', ''],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_rangeEndDateStep:nxw_rangeEndDateStep_toInputCurrentDate', '08/2011'],
            ['requete_all_form:nxl_requeteFeuilleRoute:nxw_statusStep', ''],
            ['requete_all_form:rechercheSubmitButton', 'Recherche'],
            ['requete_all_form_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())]],
            description="Recherche")
            
      return self.urlResultatRecherche
      
    
    
    '''
    ouvrirUneQuestionTrouvee
    Ouvre une question qui a ete recherche et trouvee via le menu de recherche
    La question doit être presente a l'ecran.
    Le nom passe en parametre doit être l'identitifiant exact de la question ( ex : 'SENAT 13500' et pas juste '13500')
    numero est une chaine de caracteres
    '''
    def ouvrirUneQuestionTrouvee(self, numero):
      # print(' Ouvrir la question : ' + numero)
      # extraction de l'identifiant du dossier dans la page. (il est de la forme nxl_cm_inbox_caselink:nxw_reponses_listing_title_link:j_id177)
      token = extractLastStartToken(self.ftest.getBody(), '<a href="#"', '   ' + numero + '</a>')
      link = extractToken(token, 'requeteComposite:', "':'requeteComposite")
      
      self.ftest.post(self.ftest.server_url + "/recherche/requete_resultats.faces", params=[
            ['requeteComposite_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['requeteComposite:' + link, 'requeteComposite:' + link]],
            description="Ouvrir la question trouvee : " + numero)
            
    '''
    ouvrirPremiereQuestionTrouvee
    Apres une recherche, cette methode ouvre la premiere question de la liste
    (il doit y avoir au moins une question a l ecran)
    '''
    def ouvrirPremiereQuestionTrouvee(self):
      # extraction de l'identifiant du dossier dans la page. (il est de la forme nxl_cm_inbox_caselink:nxw_reponses_listing_title_link:j_id177)
      token = extractToken(self.ftest.getBody(), '<a href=\"#\" onclick=\"if(typeof jsfcljs == \'function\'){jsfcljs(document.getElementById(\'requeteComposite\'),{\'', 'return false\">')
      link = extractToken(token, 'requeteComposite:', "':'requeteComposite")

      self.ftest.post(self.ftest.server_url + "/recherche/requete_resultats.faces", params=[
            ['requeteComposite_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['requeteComposite:' + link, 'requeteComposite:' + link]],
            description="Ouvrir la premiere question trouvee")
            
    # --- Fin navigation dans le menu recherche ---
    
    # --- Navigation dans le Plan de classement ---
    '''
    CES METHODES NE FONCTIONNENT PLUS SUITE A UNE MISE A JOUR DE L'APPLICATION
    # numBranche correspond au numero de la branche que l'on souhaite deplier. La valeur commence a 0.
    def ouvrirRubriqueArbrePlanDeClassement(self, numBranche):
      self.ftest.post(self.ftest.server_url + self.urlPlanDeClassement, params=[
	      #['AJAXREQUEST', 'j_id67:corbeilleRegion'],
	      ['j_id67:planClassementTree:input', 'j_id67:planClassementTree:niveau1TNA:'+numBranche+'::j_id68'],
	      ['j_id67_SUBMIT', '1'],
	      ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	      ['ajaxSingle', 'j_id67:planClassementTree'],
	      ['j_id67:planClassementTree:selectedNode', 'j_id67:planClassementTree:niveau1TNA:'+numBranche+'::j_id68'],
	      ['AJAX:EVENTS_COUNT', '1']],
	      description="Deplie la branche numero" + numBranche)
	      
	
    # numBranche correspond au numero de la branche que l'on souhaite replier. La valeur commence a 0.
    def FermerRubriqueArbrePlanDeClassement(self, numBranche):
      self.ftest.post(self.ftest.server_url + self.urlPlanDeClassement, params=[
            #['AJAXREQUEST', 'j_id67:corbeilleRegion'],
            ['j_id67:planClassementTree:input', ''],
            ['j_id67_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['j_id67:planClassementTree:niveau1TNA:'+numBranche+'::j_id68AjaxExpanded', 'true'],
            ['j_id67:planClassementTree:niveau1TNA:'+numBranche+'::j_id68NodeExpanded', 'false'],
            ['ajaxSingle', 'j_id67:planClassementTree:niveau1TNA:'+numBranche+'::j_id68'],
            ['AJAX:EVENTS_COUNT', '1']],
            description="Replie la branche numero" + numBranche)
            
    def selectionneThemePlanDeClassement(self, numBranche, numDossier):
      self.ftest.post(self.ftest.server_url + self.urlPlanDeClassement, params=[
            ['j_id67:planClassementTree:input', 'j_id67:planClassementTree:niveau1TNA:'+numBranche+':niveau2TNA:'+numDossier+'::j_id69'],
            ['j_id67_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['j_id67:planClassementTree:niveau1TNA:'+numBranche+':niveau2TNA:'+numDossier+'::niveau2NodeCmd', 'j_id67:planClassementTree:niveau1TNA:'+numBranche+':niveau2TNA:'+numDossier+'::niveau2NodeCmd']],
            description="Affiche la liste de dossiers")
       
       
    def ouvreDossierPlanDeClassement(self, identifiant):
      # extraction de l'identifiant du dossier dans la page
      token = extractLastStartToken(self.ftest.getBody(), '<a href="#"','   '+identifiant+'</a>')
      link = extractToken(token, 'plan_classement_content:', "':'plan_classement_content")
      
      self.ftest.post(self.ftest.server_url + self.urlPlanDeClassement, params=[
	    ['plan_classement_content_SUBMIT', '1'],
	    ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	    ['plan_classement_content:' + link, 'plan_classement_content:' + link]],
	    description="Ouverture de la question " + identifiant + " dans le plan de classement.")
	    
    '''	    
    # --- Fin navigation dans le Plan de classement ---    
	   
    
    # Selectionne une etape dans un ministere ouvert.
    # fonctionne avec les numero des ministeres et etapes (qui commencent a 0)
    # ne fonctionne que si l espace de travail est affiche
    def selectionneEtapeCorbeilleNum(self, numMinistere, numEtape):    
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[
	['mailboxTreeForm:corbeilleTree:input', 'mailboxTreeForm:corbeilleTree:ministereTNA:'+numMinistere+':etapeTNA:'+numEtape+'::j_id343'],
            ['mailboxTreeForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['mailboxTreeForm:corbeilleTree:ministereTNA:'+ numMinistere +':etapeTNA:'+ numEtape +'::etapeNodeCmd2', 'mailboxTreeForm:corbeilleTree:ministereTNA:'+ numMinistere +':etapeTNA:'+ numEtape +'::etapeNodeCmd2']],

            description="Selectionne l etape numero " + numMinistere + " du ministere numero " + numEtape + " de la corbeille")


    '''
    # Selectionne une etape dans un ministere ouvert.
    # fonctionne avec les identifiants des ministeres et etapes
    # ne fonctionne que si l espace de travail est affiche
    def selectionneEtapeCorbeilleId(self, idEtape):
      token = extractLastStartToken(self.ftest.getBody(), 'href=\"#\" onclick=\"if(typeof jsfcljs == \'function\')', 'false\">' + idEtape)
      
      # linkMinistere = extractToken(token, 'corbeilleTree:ministereTNA:','\':\'')
      linkEtape = extractLastStartToken(token, 'corbeilleTree:','\'},\'\');}return')
      
      print(token)
      # print(linkMinistere)
      print(linkEtape)
      
      token2 = extractLastStartToken(self.ftest.getBody(), 'href=\"#\" onclick=\"if(typeof jsfcljs == \'function\')', 'false\">' + idEtape)
      
      self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[
            ['conversationId', '0NXMAIN'],
            ['j_id57:corbeilleTree:input', 'j_id57:corbeilleTree:ministereTNA:'+0+':etapeTNA:'+numEtape+'::j_id66'],
            ['j_id57_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            # ['j_id57:corbeilleTree:ministereTNA:'+linkMinistere, 'j_id57:corbeilleTree:ministereTNA:'+'::etapeNodeCmd2']],
            ['j_id57:corbeilleTree:'+linkEtape, 'j_id57:corbeilleTree:'+linkEtape]],
            description="Selectionne l etape numero "+0+"")
    '''
    
    # -- onglet administration -- #
    '''
    Version 1.5.3
    def accedeJournalAdmin(self):
      self.ftest.post(self.ftest.server_url + "/view_empty.faces", params=[
	['adminMenuForm_SUBMIT', '1'],
	['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
	['adminMenuForm:adminMenuBottomActionsTable:6:adminMenuActionCommandLink', 'adminMenuForm:adminMenuBottomActionsTable:6:adminMenuActionCommandLink']],
	description="Accede journal Admin")
    '''
    
    def accedeJournalAdmin(self):
      self.ftest.post(self.ftest.server_url + "/view_empty.faces", params=[                                                                   
            ['adminMenuForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['adminMenuForm:adminMenuBottomActionsTable:7:adminMenuActionCommandLink2', 'adminMenuForm:adminMenuBottomActionsTable:7:adminMenuActionCommandLink2']],
            description="Accede journal Admin")    

    # cherche une question dans la mailbox en commançant par le debut et l'ouvre
    # fonctionne pour la version 1.5.5
    def chercheQuestionMailBox(self, source, number):
      id = source + ' ' + number
      # print(' debut')
      while True:
          listeQuestionVisibles = self.listeQuestionsMailBox() # liste toutes les question visibles dans la page
          # print(listeQuestionVisibles)
    	  if id in listeQuestionVisibles:
    	    self.ouvrirDossierParNumero(source, number)
    	    return
    	  else:
    	    if self.hasNavigationNext():
    	      self.goToNextPageofCorbeille()
    	    else:
    	      self.ftest.assert_(false, "chercheQuestionMailbox : question " + id + " not found")
          return

    # ----------------------------------------
    # MANIPULATION DEPUIS LA RECHERCHE SIMPLE
    # ----------------------------------------

    def rs_atteindreQuestion(self, source, number):
        id = source + ' ' + number
        self.ftest.post(self.ftest.server_url + "/casemanagement/mailbox/mailbox_view.faces", params=[
            ['userMetaServicesSearchForm:questionIdInput', id],
            ['userMetaServicesSearchForm:rechercheNumeroSubmitButton', 'Atteindre'],
            ['userMetaServicesSearchForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())]],
            description="Atteindre ("+id+")")
        self.ftest.assert_(id in self.ftest.getBody())
        
    def rs_openFirstResult(self, source, number):        
        self.ftest.post(self.ftest.server_url + "/recherche/requete_resultats.faces", params=[
            ['recherche_simple_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['recherche_simple:nxl_requete_listing_dto:nxw_dossierTitre_dto:j_id222', 'recherche_simple:nxl_requete_listing_dto:nxw_dossierTitre_dto:j_id222']],
            description="Ouvrir dossier recherche")
        self.ftest.assert_((" N&deg;" + number) in self.ftest.getBody())
 
    def rs_atteindreAndOpenQuestion(self, source, number):
        self.rs_atteindreQuestion(source, number)
        self.rs_openFirstResult(source, number)
            
    def rs_verrouillerQuestion(self):
        self.ftest.post(self.ftest.server_url + "/recherche/requete_resultats.faces", params=[
            ['formFdr:reject_task_panelOpenedState', ''],
            ['formFdr_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['formFdr:dossierActionsLockTable:0:j_id366', 'formFdr:dossierActionsLockTable:0:j_id366']],
            description="Verrou du dossier courant")
        self.ftest.assert_("Verrouill&eacute; le" in self.ftest.getBody())
        
    def rs_deverrouilleAndSaveReponse(self, reponseContent):
        self.ftest.post(self.ftest.server_url + "/recherche/requete_resultats.faces", params=[
            ['actionToolbarForm:nxl_saisie_reponse:j_id607:nxw_reponse_note_editor', reponseContent],
            ['actionToolbarForm:nxl_saisie_reponse:j_id607:nxw_reponse_note_editorselector', 'text/html'],
            ['actionToolbarForm_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['actionToolbarForm:j_id636', 'actionToolbarForm:j_id636']],
            description="Edite et sauve la reponse")
        self.ftest.assert_(not("Verrouill&eacute; le" in self.ftest.getBody()))
        
    def rs_validateQuestion(self):
         self.ftest.post(self.ftest.server_url + "/recherche/requete_resultats.faces", params=[
            ['formFdr:reject_task_panelOpenedState', ''],
            ['formFdr:unconcerned_task_panelOpenedState', ''],
            ['formFdr:nxl_ministere_reattribution:nxw_reattributionMinistere_nodeId', ''],
            ['formFdr_SUBMIT', '1'],
            ['javax.faces.ViewState', extractJsfState(self.ftest.getBody())],
            ['formFdr:fdrActionsTable:0:fdrValidateButton', 'formFdr:fdrActionsTable:0:fdrValidateButton']],
            description="Valide la question")
         
         self.ftest.assert_("Vous venez de valider" in self.ftest.getBody());
        
        


