#!/bin/sh


INPUT=$1


if [ ! -f "$INPUT" ]; then

echo "NO input furnished : use sqlplus"

tmpfile=/tmp/listagri.sql

# attribution
ROUTINGTASKTYPE=13
# mailbox agri bdc
MNAME=webservice-agriculture

# --- DB ----
DBNAME=ORA112
DBUSER=REPONSES_TEST
DBPASSWD=$DBUSER

cat > $tmpfile <<EOF

SELECT L.SOURCENUMEROQUESTION
   FROM HIERARCHY MB, HIERARCHY H, DOSSIER_REPONSES_LINK L, CASE_LINK C, MISC M
   WHERE H.ID = L.ID AND C.ID=H.ID AND M.ID=H.ID
    AND MB.PRIMARYTYPE = 'ReponsesMailbox' AND MB.NAME = '$MNAME'
    AND H.PARENTID = MB.ID AND l.ROUTINGTASKTYPE=$ROUTINGTASKTYPE
   AND M.LIFECYCLESTATE = 'todo'
   ORDER BY C."DATE" DESC;

exit

EOF

OUTPUT=/tmp/output

ORACLE_SID=$DBNAME sqlplus $DBUSER/$DBPASSWD @$tmpfile > $OUTPUT

INPUT=$OUTPUT

fi

OUTPUTAN=/tmp/outputAN
OUTPUTSENAT=/tmp/outputSENAT


grep '^AN ' $INPUT | sed 's/AN //' > $OUTPUTAN
grep '^SENAT ' $INPUT | sed 's/SENAT //' > $OUTPUTSENAT



mkdir -p conf
OUTPUTP=/tmp/nbdistribpwd.txt
OUTPUTLIST=/tmp/nbdistribgrp.txt


sed 's/\(.*\)/\1:AN/' $OUTPUTAN > $OUTPUTP
sed 's/\(.*\)/\1:SENAT/' $OUTPUTSENAT >> $OUTPUTP


echo -n "AN:" > $OUTPUTLIST

cat $OUTPUTAN | while read aline; do 
	echo -n "$aline," >> $OUTPUTLIST
done

echo -e "\n\n" >> $OUTPUTLIST

echo -n "SENAT:" >> $OUTPUTLIST

cat $OUTPUTSENAT | while read aline; do
        echo -n "$aline," >> $OUTPUTLIST
done 


rm -f $tmpfile


