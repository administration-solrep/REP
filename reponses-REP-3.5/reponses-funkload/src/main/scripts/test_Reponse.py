# -*- coding: utf-8 -*-
"""Reponse FunkLoad test

$Id: $
"""
import unittest
from webunit.utility import Upload
import time
import random
from datetime import datetime

from funkload.FunkLoadTestCase import FunkLoadTestCase
from funkload.utils import Data
from funkload.utils import xmlrpc_get_credential
from funkload.Lipsum import Lipsum

from Utils import extractJsfState
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

        self.nbdistrib_host='localhost'
        self.nbdistrib_port=55503
        
        self.login = 'bdc'
        self.password = 'bdc'
        
        self.renvoiCommun = Lipsum().getWord() # renvoi commun a certaines questions injectees pour generer des questions connexes
        
        utilisateur_groupe = self.conf_get('scenario1', 'connexion_groupe')
        self.login,self.password = xmlrpc_get_credential(self.credential_host,
                    self.credential_port,
                    utilisateur_groupe)


    # ------------------------------------------------------------
    
    ''' 
    Scenario stats : 
       - recupere la page stats.jsp
    '''
    def testScenarioStats(self):
        self.manip.login('bdc','bdc')
        self.manip.chercheQuestionMailBox('AN', '104003') # SENAT 82024295
        self.manip.logout('bdc')
        # recuperation de la page de statistiques qui est sur la page d'accueil
        # self.manip.getStats()
        
        
    ''' 
    Scenario 1 : 
      - atteinte de la page login.jsp
      - login en temps qu'utilisateur 'bdc'
      - deconnexion
    '''
    def testScenario1(self):
        '''
        # atteinte de la page login.jsp
        self.manip.accueil()
        
        # recuperation de la page de statistiques qui est sur la page d'accueil
        self.manip.getStats()
        '''
        #print("--> prelogin : " + self.login)
        # login en temps qu'utilisateur 'bdc'
        self.manip.login(self.login,self.password)
        #print("--> postlogin : " + self.login)
        
        # deconnexion
        #print("--¤ preOUT : " + self.login)
        self.manip.logout(self.login)      
        #print("--¤ postOUT : " + self.login)
      
    '''
    Scenario 2 : 
       - atteinte de la page login.jsp
       - login en temps qu'utilisateur 'bdc'
       - chargement de la mailbox 'pour attribution'
       - deconnexion
    ''' 
    def testScenario2(self):
      
        # atteinte de la page login.jsp
        self.manip.accueil()
        
        # recuperation de la page de statistiques qui est sur la page d'accueil
        self.manip.getStats()
        
        # login en temps qu'utilisateur 'bdc'
        urlCourante = self.manip.login(self.login,self.password)
        
        # chargement de la mailbox 'pour attribution'
        self.manip.selectionneEtapeCorbeilleNum('0','0')
        
        # deconnexion
        self.manip.logout(self.login)
        
      
    ''' 
    Scenario 3 : 
      - atteinte de la page login.jsp
      - login en temps qu'utilisateur 'bdc'
      - rechargement de l'espace de travail
      - chargement de la premiere question
      - chargement de la deuxieme question
      - chargement de la troisieme question
      - deconnexion
    '''
    def testScenario3(self):
      
        # atteinte de la page login.jsp
        self.manip.accueil()
        
        # recuperation de la page de statistiques qui est sur la page d'accueil
        self.manip.getStats()
        
        # login en temps qu'utilisateur 'bdc'
        urlCourante = self.manip.login(self.login,self.password)
        
        # chargement de la mailbox 'pour attribution'
        self.manip.selectionneEtapeCorbeilleNum('0','2')
        
        # recuperation de la liste de toutes les questions affichees a l ecran
        listeQuestions = self.manip.listeQuestionsMailBox()
        
        self.manip.ouvrirDossierParSourceNumero(listeQuestions[0])
        self.manip.ouvrirDossierParSourceNumero(listeQuestions[1])
        self.manip.ouvrirDossierParSourceNumero(listeQuestions[2])
        
        # deconnexion
        self.manip.logout(self.login)
      
    ''' 
    Scenario 4 : 
      - atteinte de la page login.jsp
      - login en temps qu'utilisateur 'bdc'
      - rechargement de l'espace de travail
      - chargement de la premiere question
      - chargement de l'onglet bordereau
      - chargement de l'onglet Parapheur
      - chargement de l'onglet Fond de dossier
      - chargement de l'onglet Feuille de Route
      - chargement de l'onglet Journal
      - chargement de l'onglet Dossier Connexes
      - chargement de l'onglet Allotissement
      - deconnexion
    '''
    def testScenario4(self):
      # atteinte de la page login.jsp
        self.manip.accueil()
        
        # recuperation de la page de statistiques qui est sur la page d'accueil
        # self.manip.getStats()
        
        # login en temps qu'utilisateur 'bdc'
        urlCourante = self.manip.login(self.login,self.password)
        
        # chargement de la mailbox 'pour attribution'
        self.manip.selectionneEtapeCorbeilleNum('0','0')
        
        # chargement de la premiere question
        self.manip.ouvrirPremierDossier()
        
        # chargement successif des onglets
        self.manip.goToBordereauTab(urlCourante)
        
        self.manip.goToParapheurTab(urlCourante)
        
        self.manip.goToFondDeDossierTab(urlCourante)
        
        self.manip.goToFeuilleDeRouteTab(urlCourante)
        
        self.manip.goToJournalTab(urlCourante)
        
        self.manip.goToDossierConnexeTab(urlCourante)
        
        self.manip.goToAllostissementTab(urlCourante)
        
        # deconnexion
        self.manip.logout(self.login)
        
    ''' 
    Scenario 5 : 
      - atteinte de la page login.jsp
      - login en temps qu'utilisateur 'bdc'
      - rechargement de l'espace de travail
      - chargement de N dossiers (N aleatoire entre 0 et 10)
      -   chargement de l'onglet bordereau
      -   chargement de l'onglet Parapheur
      -   chargement de l'onglet Fond de dossier
      -   chargement de l'onglet Feuille de Route
      -   chargement de l'onglet Journal
      -   chargement de l'onglet Dossier Connexes
      -   chargement de l'onglet Allotissement
      - deconnexion
    '''
    def testScenario5(self):
      # atteinte de la page login.jsp
        self.manip.accueil()
        
        # recuperation de la page de statistiques qui est sur la page d'accueil
        # self.manip.getStats()
        
        # login en temps qu'utilisateur 'bdc'
        urlCourante = self.manip.login(self.login,self.password)
        
        # chargement de la mailbox 'pour attribution'
        self.manip.selectionneEtapeCorbeilleNum('0','0')
        
        # recuperation de la liste de toutes les questions affichees a l ecran
        listeQuestions = self.manip.listeQuestionsMailBox()

        nbDossierToRead = random.randrange(1, 10)
        
        for i in range(1, nbDossierToRead + 1):
        
        	dossierToRead = random.choice(listeQuestions)
        
	        # chargement de la premiere question
	        self.manip.ouvrirDossierParSourceNumero(dossierToRead)
	        
	        # chargement successif des onglets
	        self.manip.goToBordereauTab(urlCourante)
	        
	        self.manip.goToParapheurTab(urlCourante)
	        
	        self.manip.goToFondDeDossierTab(urlCourante)
	        
	        self.manip.goToFeuilleDeRouteTab(urlCourante)
	        
	        self.manip.goToJournalTab(urlCourante)
	        
	        self.manip.goToDossierConnexeTab(urlCourante)
	        
	        self.manip.goToAllostissementTab(urlCourante)
        
        # deconnexion
        self.manip.logout(self.login)


    def genQuery(self):
        tpl = open(self.conf_get('scenario1', 'query_template')).read()
        self.lipsum = Lipsum()
        self.queryNumber = str(time.time()).replace('.','')
        
        
        self.queryNumber = '%s%03d' % (self.queryNumber[6:12], random.randint(0,999))
        self.queryNumber = str(int(self.queryNumber)) 
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
        
        return (tpl.replace('{publish_date}', "2011-08-09")
                         .replace("{query_number}", self.queryNumber)
                         .replace("{content}", self.lipsum.getMessage())
                         .replace("{idMinistere}", idLDAPPoste)
                         .replace("{idAuteur}",idAuteur)
                         .replace("{theme}", theme)
                         .replace("{rubrique}", rubrique)
                         .replace("{renvoi}", renvoi)
                         .replace("{titreSenat}", titreSenat)
                         )
        
    def testInject(self):
         # 1.Creation d'une nouvelle question via le service web
        self.queryXml = self.genQuery()
                        
        # utilisateur du WS
        admin = xmlrpc_get_credential(self.credential_host,
                    self.credential_port,
                    'wsinject')
    
        # injection
        self.manip.injectQuestion(admin[0], admin[1], self.queryXml)
    
        # fin injection
        
    '''
        Ecriture
        
        Injection
        login
        ouverture du dossier injecte
        bordereau
        parapheur
        edition de la reponse
        logout
    '''
    def testScenario6(self):
        '''
        self.sourceQuestion = 'SENAT'
        self.queryNumber = '79685543'
        '''
        
        self.testInject()    
    
        # 2. Utilisation de l'IHM
    
        # groupe d'utilisateur qui peuvent voir la question dans leur mailbox
        urlCourante = self.manip.login(self.login, self.password)
    
        
    
        # chargement de la mailbox        
        # self.manip.selectionneEtapeCorbeilleNum('0','1')
        
        self.manip.chercheQuestionMailBox(self.sourceQuestion, str(self.queryNumber)) # cherche une question dans la mailbox courante puis l'ouvre
        
        # navigation onglet optionnel
        if random.random() < 0.33:
            self.manip.goToBordereauTab(urlCourante)            
            self.manip.goToParapheurTab(urlCourante)
        
        # edition de la reponse
        self.manip.verrouilleDossierFromParapheur(urlCourante)        
        self.manip.deVerouilleDossierFromParapheur(urlCourante, Lipsum().getSubject(), 'text/html')

        # navigation onglet optionnel
        if random.random() < 0.33:
            self.manip.goToFondDeDossierTab(urlCourante)
            self.manip.goToJournalTab(urlCourante)
        
        # onglet feuille de route
        self.manip.goToFeuilleDeRouteTab2(urlCourante, self.getBody())

        # validation
        self.manip.valideDossierFromFdR(urlCourante)
        
        # logout
        self.manip.logout(self.login)
        
        

    '''
    balancing 2 scenario 3 (lecture), 1 scenario 6 (ecriture)
    alea sur l'ordre des 3 executions 
    '''
    def testScenario7(self):
        rnd = random.random()
        if rnd < 0.33:
            self.testScenario3()
            self.testScenario6()
            self.testScenario3()
        elif rnd < 0.66:
            self.testScenario3()
            self.testScenario3()
            self.testScenario6()
        else:
            self.testScenario6()
            self.testScenario3()
            self.testScenario3()
            

    '''
        Ecriture
        
        login
        ouverture du dossier injecte
        bordereau
        parapheur
        edition de la reponse
        logout
    '''
    def testScenario8(self):
        '''
        self.sourceQuestion = 'SENAT'
        self.queryNumber = '79685543'
        '''
        
        # retrieve a number of dossier to work on
        self.queryNumber,self.sourceQuestion = xmlrpc_get_credential(self.nbdistrib_host,
                    self.nbdistrib_port,
                    self.conf_get('main', 'SOURCEQUESTION'))
    
        # 2. Utilisation de l'IHM
    
        # groupe d'utilisateur qui peuvent voir la question dans leur mailbox
        urlCourante = self.manip.login(self.login, self.password)
    
        self.manip.rs_atteindreAndOpenQuestion(self.sourceQuestion, self.queryNumber)            
        self.manip.rs_verrouillerQuestion()
        self.manip.rs_deverrouilleAndSaveReponse( Lipsum().getSubject())
        #self.manip.rs_validateQuestion()
            
        # logout
        self.manip.logout(self.login)

    def testScenario9(self):
        '''
        self.sourceQuestion = 'SENAT'
        self.queryNumber = '79685543'
        '''

        # retrieve a number of dossier to work on
        self.queryNumber,self.sourceQuestion = xmlrpc_get_credential(self.nbdistrib_host,
                    self.nbdistrib_port,
                    self.conf_get('main', 'SOURCEQUESTION'))

        print(self.queryNumber + " " + self.sourceQuestion);

        # 2. Utilisation de l'IHM

        # groupe d'utilisateur qui peuvent voir la question dans leur mailbox
        urlCourante = self.manip.login(self.login, self.password)

        self.manip.rs_atteindreAndOpenQuestion(self.sourceQuestion, self.queryNumber)
        self.manip.rs_validateQuestion()

        # logout
        self.manip.logout(self.login)


    def tearDown(self):
        """Setting up test."""
        self.logd("tearDown.\n")



if __name__ in ('main', '__main__'):
    unittest.main()
