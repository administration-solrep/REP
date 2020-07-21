#!/bin/bash

## Script à executer en root dans le répertoire init/ldap, enleve le ldif existant et le recree
## ATTENTION: Script en cours d'élaboration - vérification qu'il marche sur toutes les plateformes

if [ -d /etc/openldap ]; then
	LDAPDIR=/etc/openldap
	LDAPDATADIR=/var/lib/ldap
	USER=ldap
	SERVICENAME=ldap
	IGNORE_SLAP_CONF=false
elif [ -d /etc/ldap ]; then
# config for debian
	LDAPDIR=/etc/ldap
	LDAPDATADIR=/var/lib/ldap
	USER=openldap
	SERVICENAME=slapd
	IGNORE_SLAP_CONF=true
else
	
	echo "No matching config : no operation done"
	
	exit
fi


/etc/init.d/$SERVICENAME stop

for f in $(find ${LDAPDATADIR} -mindepth 1 ! -name "DB_CONFIG"); do
	rm -f $f
done

if [ "${IGNORE_SLAP_CONF}" == "true" ]; then
	echo "WARNING : FILE slapd.conf IGNORED"
else
	cp ${LDAPDIR}/slapd.conf ${LDAPDIR}/slapd.conf.old
	cp ./slapd.conf ${LDAPDIR}/slapd.conf
fi
cp *.schema ${LDAPDIR}/schema
cp *.ldif ${LDAPDIR}/

slapadd -v -l  ${LDAPDIR}/create-init.ldif -f ${LDAPDIR}/slapd.conf
if [ -f ${LDAPDIR}/create-reponses.ldif ]; then
	slapadd -v -l  ${LDAPDIR}/create-reponses.ldif -f ${LDAPDIR}/slapd.conf
fi
if [ -f ${LDAPDIR}/create-solonepg.ldif ]; then
	slapadd -v -l  ${LDAPDIR}/create-solonepg.ldif -f ${LDAPDIR}/slapd.conf
fi
chown -R $USER:$USER ${LDAPDATADIR}
/etc/init.d/$SERVICENAME start

