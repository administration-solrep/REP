#!/bin/sh
ADMIN_DN="cn=ldapadmin,dc=dila,dc=fr"
ADMIN_PW=changeme
HOSTNAME=localhost

# Supprime et recrée la branche Réponses
ldapdelete -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x "ou=Reponses,dc=dila,dc=fr" -r
ldapadd -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x -f create-reponses.ldif

