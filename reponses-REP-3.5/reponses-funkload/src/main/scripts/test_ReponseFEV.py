# -*- coding: utf-8 -*-
"""Reponse FunkLoad test

$Id: $
"""
import unittest
from webunit.utility import Upload
import time
from time import gmtime, strftime
import random
from datetime import datetime

from funkload.FunkLoadTestCase import FunkLoadTestCase
from funkload.utils import Data
from funkload.utils import xmlrpc_get_credential
from funkload.Lipsum import Lipsum

from Utils import extractJsfState
from Utils import randomTime
from Utils import randomDateInterval
import ManipReponse

class Reponse(FunkLoadTestCase):
    """This test use a configuration file Reponse.conf.
    """

    def setUp(self):
        """Setting up test."""
        self.logd("setUp")

        self.manip = ManipReponse.ManipReponse(self)

        self.server_url = self.conf_get('main', 'url')
        self.credential_host = self.conf_get('credential', 'host')
        self.credential_port = self.conf_getInt('credential', 'port')
        
        self.renvoiCommun = Lipsum().getWord() # renvoi commun a certaines questions injectees pour generer des questions connexes
        
        self.login = 'bdc'
        self.password = 'bdc'
        


    # ------------------------------------------------------------
    
    ''' 
    Scenario 1 : 
      - Creation d'une nouvelle question via le service web
      - Saisie du login du ministere destinataire
      - Selection du dossier qui vient d'être envoyé
      - Selection de l'onglet bordereau
      - Selection de l'onglet parapheur
      - Verouillage du dossier
      - Saisie d'une reponse
      (- Sauvegarde de la reponse)
      - Ajout d'un document au fond de dossier
      - Ajout d'une etape dans la feuille de route
      - Validation de l'étape
      (- logout)
    '''
    def testScenario1(self):
        '''
        self.sourceQuestion = 'SENAT'
        self.queryNumber = '79685543'
        '''
        
        # 1.Creation d'une nouvelle question via le service web
        
        # preparation du xml de la requete de creation de question
        tpl = open(self.conf_get('scenario1', 'query_template')).read()
        self.lipsum = Lipsum()
        self.now = datetime.now()
        self.queryNumber = str(time.time()).replace('.','')
        self.queryNumber = self.queryNumber[4:12]
        self.sourceQuestion = self.conf_get('scenario1', 'source') # SENAT ou AN
        idLDAPPoste = self.conf_get('scenario1', 'idLDAPPoste')
        idAuteur = self.conf_get('scenario1', 'idAuteur')
        titreSenat = Lipsum().getWord()
        theme = Lipsum().getWord()
        rubrique = Lipsum().getWord()
        
        if random.random() < 0.15:
	  renvoi = self.renvoiCommun # certaines questions ont le meme renvoi
	else:
	  renvoi = Lipsum().getWord()
        
        
        
        self.queryXml = (tpl.replace('{publish_date}', "2011-08-09")
                         .replace("{query_number}", self.queryNumber)
                         .replace("{content}", self.lipsum.getMessage())
                         .replace("{idMinistere}", idLDAPPoste)
                         .replace("{idAuteur}",idAuteur)
                         .replace("{theme}", theme)
                         .replace("{rubrique}", rubrique)
                         .replace("{renvoi}", renvoi)
                         .replace("{titreSenat}", titreSenat)
                         )
        
        
        # utilisateur du SW
	admin = xmlrpc_get_credential(self.credential_host,
				    self.credential_port,
				    'admin')
	
        # injection
        self.manip.injectQuestion(admin[0], admin[1], self.queryXml)
	
	# fin injection
	
	
	
	# 2. Utilisation de l'IHM
	
	# document qui sera uploade
	documentLocation = self.conf_get('scenario1', 'documentLocation')
	
	
	
	# groupe d'utilisateur qui peuvent voir la question dans leur mailbox
	utilisateur_groupe = self.conf_get('scenario1', 'connexion_groupe')
	user = xmlrpc_get_credential(self.credential_host,
				    self.credential_port,
				    utilisateur_groupe)

	urlCourante = self.manip.login(user[0],user[1])
	
	# chargement de la mailbox
        self.manip.selectionneEtapeCorbeilleNum('0','0')        
        
        self.manip.chercheQuestionMailBox(self.sourceQuestion, str(self.queryNumber)) # cherche une question dans la mailbox courante puis l'ouvre
        
        self.manip.verrouilleDossierFromParapheur(urlCourante)
        
        self.manip.deVerouilleDossierFromParapheur(urlCourante, Lipsum().getSubject(), 'text/html')
        
        self.manip.verrouilleDossierFromParapheur(urlCourante)
        
        self.manip.goToFondDeDossierTab(urlCourante)
        
        htmlPageComplete = self.manip.ajouterPieceParlement(urlCourante,documentLocation)
        
        self.manip.goToFeuilleDeRouteTab2(urlCourante, htmlPageComplete)
        
        # self.manip.goToFeuilleDeRouteTab(urlCourante)
        
        self.manip.ajoutEtapeFDRApresEtapeCourante(None, 'poste-50000656', None, 'off', 'off')
        
        self.manip.deVerrouilleDossierFromFdR(urlCourante)
        
        self.manip.valideDossierFromFdR(urlCourante)
        
	self.manip.logout(user[0])
        
      
    '''
    Scenario 2 : 
    
    ''' 
    def testScenario2(self):
      
      pDCNumRubrique = self.conf_get('scenario2', 'pDCNumRubrique')
      pDCTeteAnalyse = self.conf_get('scenario2', 'pDCTeteAnalyse')
      pDCDossier = self.conf_get('scenario2', 'pDCDossier')
      
      # atteinte de la page login.jsp
      self.manip.accueil()
      
      # recuperation de la page de statistiques qui est sur la page d'accueil
      self.manip.getStats()

      login, password = xmlrpc_get_credential(self.credential_host,
				  self.credential_port,
				  "groupe-bdc")
      
      # login
      urlCourante = self.manip.login(login,password)
      
      # aller sur l'onglet plan de classement
      urlCourante = self.manip.goToPlanDeClassement(urlCourante)
      
      self.manip.pDCAccedeTeteAnalyse(pDCNumRubrique, pDCTeteAnalyse)
      
      self.manip.ouvreDossierPlanDeClassement(pDCDossier)
      
      self.manip.goToDossierConnexeTab(urlCourante)
      
      self.manip.selectPremierMinistereDossiersConnexes()
      
      self.manip.ouvertureDossierConnexe('0')
      
      urlCourante = self.manip.goToRecherche(urlCourante)
      
      
      # génération des criteres de recherche
      formatDateRecherche = self.conf_get('scenario2', 'formatDateJOQuestion')
      dateJOQuestionMin = self.conf_get('scenario2', 'dateJOQuestionMin')
    
      listeGroupesPolitiques = ["D\xc3\xa9put\xc3\xa9s n'appartenant \xc3\xa0 aucun groupe", 
	  "Gauche d\xc3\xa9mocrate et r\xc3\xa9publicaine",
	  "Groupe CRC-SPG",
	  "Groupe du RDSE",
	  "Groupe Socialiste",
	  "Groupe UC",
	  "Groupe UC - UDF",
	  "Groupe UMP",
	  "Non connu",
	  "Nouveau Centre",
	  "R\xc3\xa9union administrative des non-inscrits",
	  "Socialiste, radical, citoyen et divers gauche",
	  "Union pour un Mouvement Populaire"]
	
      groupePolitique = random.choice(listeGroupesPolitiques) # choisi un groupe au hasard dans la liste
      
      listeSources = ["AN", "SENAT"]
      
      source = random.choice(listeSources) # choisi une source au hasard
      
      dateCourante = strftime(formatDateRecherche, gmtime())
      
      dateDebutJOQuestion, dateFinJOQuestion = randomDateInterval(dateJOQuestionMin, dateCourante, formatDateRecherche) # calcule un intervalle de dates au hasard
      
      # requete
      urlCourante = self.manip.chercherUneQuestion(source, dateDebutJOQuestion, dateFinJOQuestion, groupePolitique)
      
      urlCourante = self.manip.goToAdministration(urlCourante)
      
      self.manip.accedeJournalAdmin()
      
      # deconnexion
      self.manip.logout(login)
        
      
    '''
    Scenario 3 : 
    '''
    def testScenario3(self):
      print('Hello World !')
      
      
      
    ''' 
    Scenario 4 : 
    '''
    def testScenario4(self):
      print('Hello World !')

      
    def tearDown(self):
        """Setting up test."""
        self.logd("tearDown.\n")



if __name__ in ('main', '__main__'):
    unittest.main()
