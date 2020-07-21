#!/bin/sh


INPUT=$1


if [ ! -f "$INPUT" ]; then

echo "NO input furnished : use sqlplus"

tmpfile=/tmp/listagri.sql

# attribution
ROUTINGTASKTYPE=2
# mailbox agri bdc
MID=3fb8d791-49a9-4f58-be51-f768c5ba3a36

cat > $tmpfile <<EOF

SELECT L.SOURCENUMEROQUESTION
FROM HIERARCHY H, DOSSIER_REPONSES_LINK L, CASE_LINK C, MISC M
WHERE H.ID = L.ID AND C.ID=H.ID AND M.ID=H.ID
AND H.PARENTID = '$MID' AND l.ROUTINGTASKTYPE=$ROUTINGTASKTYPE
AND M.LIFECYCLESTATE = 'todo'
ORDER BY C."DATE" DESC;

exit

EOF

OUTPUT=/tmp/output

ORACLE_SID=SOLON sqlplus REPONSES_INTE/REPONSES_INTE @$tmpfile > $OUTPUT

INPUT=$OUTPUT

fi

OUTPUTAN=/tmp/outputAN
OUTPUTSENAT=/tmp/outputSENAT


grep '^AN ' $INPUT | sed 's/AN //' > $OUTPUTAN
grep '^SENAT ' $INPUT | sed 's/SENAT //' > $OUTPUTSENAT



mkdir -p conf

OUTPUTP=conf/nbdistribpwd.txt
OUTPUTLIST=conf/nbdistribgrp.txt


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


