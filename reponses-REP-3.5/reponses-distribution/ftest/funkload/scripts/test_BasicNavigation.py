# -*- coding: utf-8 -*-
"""basic_navigation FunkLoad test

$Id: $
"""
import unittest
from funkload.FunkLoadTestCase import FunkLoadTestCase
from webunit.utility import Upload
from funkload.utils import Data
#from funkload.utils import xmlrpc_get_credential

class BasicNavigation(FunkLoadTestCase):
    """XXX

    This test use a configuration file BasicNavigation.conf.
    """

    def setUp(self):
        """Setting up test."""
        self.logd("setUp")
        self.server_url = self.conf_get('main', 'url')
        # XXX here you can setup the credential access like this
        # credential_host = self.conf_get('credential', 'host')
        # credential_port = self.conf_getInt('credential', 'port')
        # self.login, self.password = xmlrpc_get_credential(credential_host,
        #                                                   credential_port,
        # XXX replace with a valid group
        #                                                   'members')

    def test_basic_navigation(self):
        # The description should be set in the configuration file
        server_url = self.server_url
        # begin of test ---------------------------------------------

        # /tmp/tmpUpIzZ2_funkload/watch0001.request
        self.get(server_url + "/reponses/logout",
            description="Get /reponses/logout")
        # /tmp/tmpUpIzZ2_funkload/watch0004.request
        self.get(server_url + "/reponses/stats.jsp",
            description="Get /reponses/stats.jsp")
        # /tmp/tmpUpIzZ2_funkload/watch0005.request
        self.post(server_url + "/reponses/nxstartup.faces", params=[
            ['user_name', 'adminsgg'],
            ['user_password', 'adminsgg'],
            ['language', 'fr'],
            ['requestedUrl', ''],
            ['form_submitted_marker', ''],
            ['Submit', 'Connexion']],
            description="Post /reponses/nxstartup.faces")
        # /tmp/tmpUpIzZ2_funkload/watch0009.request
        self.post(server_url + "/reponses/casemanagement/mailbox/mailbox_view.faces", params=[
            ['userServicesForm_SUBMIT', '1'],
            ['javax.faces.ViewState', 'j_id1'],
            ['userServicesForm:userServicesActionsTable:2:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:2:userServicesActionCommandLink'],
            ['id', 'espace_recherche']],
            description="Post /reponses/casemanag.../mailbox_view.faces")
        # /tmp/tmpUpIzZ2_funkload/watch0012.request
        self.post(server_url + "/reponses/recherche/edit_recherche.faces", params=[
            ['userServicesForm_SUBMIT', '1'],
            ['javax.faces.ViewState', 'j_id2'],
            ['userServicesForm:userServicesActionsTable:0:userServicesActionCommandLink', 'userServicesForm:userServicesActionsTable:0:userServicesActionCommandLink'],
            ['id', 'espace_travail']],
            description="Post /reponses/recherche/edit_recherche.faces")
        # /tmp/tmpUpIzZ2_funkload/watch0015.request
        self.get(server_url + "/reponses/logout",
            description="Get /reponses/logout")
        # /tmp/tmpUpIzZ2_funkload/watch0018.request
        self.get(server_url + "/reponses/stats.jsp",
            description="Get /reponses/stats.jsp")

        # end of test -----------------------------------------------

    def tearDown(self):
        """Setting up test."""
        self.logd("tearDown.\n")



if __name__ in ('main', '__main__'):
    unittest.main()
