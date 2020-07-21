#!/bin/sh


# param <idx>
add_user() {
	local idx=$1
	cat <<EOF
dn: uid=agriculture_bdc${idx},ou=people,ou=Reponses,dc=dila,dc=fr
objectClass: top
objectClass: person
objectClass: organizationalPerson
objectClass: inetOrgPerson
objectClass: personne
cn:: IA==
dateDebut: 20101215230000Z
deleted: FALSE
occasional: FALSE
sn:: TWluaXN0w6hyZSBhZ3JpY3VsdHVyZQ==
temporary: FALSE
dateLastConnection: 20110921073641Z
mail: agriculture_bdc@dila.fr
uid: agriculture_bdc${idx}
userPassword:: e1NTSEF9Rkd0eXIxaHNyMzdEUk5iZXB3QmpEa0NBWG55bURJaUZuTEozaFE9P
 Q==

EOF

}

# param <idx>
mod_user(){
	local idx=$1
	cat <<EOF

dn: cn=60002369,ou=poste,ou=Reponses,dc=dila,dc=fr
changetype: modify
add: uniquemember
uniqueMember: uid=agriculture_bdc${idx},ou=people,ou=Reponses,dc=dila,dc=fr

dn:: Y249QWRtaW5pc3RyYXRldXIgbWluaXN0w6lyaWVsLG91PWdyb3VwcyxvdT1SZXBvbnNlcyx
 kYz1kaWxhLGRjPWZy
changetype: modify
add: uniquemember
uniqueMember: uid=agriculture_bdc${idx},ou=people,ou=Reponses,dc=dila,dc=fr

EOF

}


OUTPUT_ADD=./agri-add.txt
OUTPUT_MOD=./agri-mod.txt
OUTPUT_PASSWD=./agri-passwd.txt
OUTPUT_SCRIPT=./add-agri.sh
OUTPUT_GROUP=./agri-groupe.txt

rm -f $OUTPUT_ADD $OUTPUT_PASSWD $OUTPUT_SCRIPT $OUTPUT_GROUP $OUTPUT_MOD

i=0
while [ $i -lt 200 ]; do
	idx=$(printf "%03d" $i)
	add_user $idx >> $OUTPUT_ADD
	mod_user $idx >> $OUTPUT_MOD
	echo "agriculture_bdc${idx}:agriculture_bdc" >> $OUTPUT_PASSWD
	echo -n "agriculture_bdc${idx}," >> $OUTPUT_GROUP	
	let i++ 
done

cat > $OUTPUT_SCRIPT <<EOF
#!/bin/sh

ADMIN_DN="cn=ldapadmin,dc=dila,dc=fr"
ADMIN_PW=changeme
#HOSTNAME=localhost
HOSTNAME=10.1.3.102

ldapadd -h \${HOSTNAME} -D "\${ADMIN_DN}" -w \${ADMIN_PW} -x -f $OUTPUT_ADD

ldapmodify -h \${HOSTNAME} -D "\${ADMIN_DN}" -w \${ADMIN_PW} -x -f $OUTPUT_MOD
 
EOF

