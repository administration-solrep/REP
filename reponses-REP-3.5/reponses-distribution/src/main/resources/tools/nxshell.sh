#!/bin/sh

JAVA_OPT=
#JAVA_OPT="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

USER=Administrator
PASSWD=4ju7Ix662Eo1d77u
BASE_URL=http://localhost:8180/reponses
INIT_DIR=/case-management

java ${JAVA_OPT} -cp nuxeo-shell-5.4.2-I20110404_0115.jar org.nuxeo.shell.Main ${BASE_URL}/site/automation -u $USER -p $PASSWD -d $INIT_DIR $@


