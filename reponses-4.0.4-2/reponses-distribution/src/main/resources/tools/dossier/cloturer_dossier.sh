#!/bin/bash

# Usage : cloturer_dossier.sh [mode] [origine : AN ou SENAT] [numéro] [type de cloture]
#
# Script servant à cloturer une question.
#
# Arguments : 
# - mode : Mode d'exécution. LECTURE pour afficher la question à cloturer. EXECUTION pour exécuter la cloture de la question
# - origine : Origine de la question. AN pour Assemblée Nationale et SENAT pour Sénat
# - numéro : Numéro de la question qu'on souhaite clore.
# - Type de cloture : caduque, retiree ou cloture_autre

# Pour lancer ce script :
# 1 - copier ce fichier dans /tmp/
# 2 - Aller dans le répertoire contenant nxshell.sh
# 3 - Taper la commande sh /tmp/cloturer_dossier.sh [mode] [origine : AN ou SENAT] [numéro] [type de cloture]
# Exemple : sh /tmp/cloturer_dossier.sh EXECUTION AN 123456 CADUQUE

function showHelp {
	head -n 11 $0 | tail -n 9
	exit 0
}

# Affiche l'aide si ce n'est pas le bon nombre de paramètres
	if [ $# -gt 4 ] ; then showHelp ; fi
	if [ $# -lt 4 ] ; then showHelp ; fi

# Vérification des paramètres
if [[ $1  != "EXECUTION" && $1 != "LECTURE" ]]
then
	echo "Le mode ne peut prendre que deux valeurs : EXECUTION pour exécution et LECTURE pour lecture"
	exit 1
fi
if [[ $2 != "AN" && $2 != "SENAT" ]]
then
	echo "L'origine ne peut prendre que deux valeurs AN ou SENAT"
	exit 1
fi
if [[ $4 != "CADUQUE" && $4 != "RETIREE" && $4 != "CLOTURE_AUTRE" ]]
then
	echo "Le type de cloture est incorrect. Elle ne peut prendre que les valeurs caduque, retiree ou cloture_autre"
	exit 1
fi

# Fin de vérification des paramètres

# Affichage d'un message de confirmation seulement si on est en mode exécution. 
if [ $1 == "EXECUTION" ]
then
	echo "Début de cloture de la question $2 $3"
	read -p "Êtes-vous sûr? (o/n)" -n 1
	if [[  $REPLY != O && $REPLY != o ]]
	then
	    exit 1
	fi
fi

#Affichage d'un petit message disant qu'on affiche la question à clore si on est en mode lecture
if [ $1 == "LECTURE" ]
then
	echo "Question à clore"
fi

# Fichier temporaire qui va permettre d'appeler l'opération en lui passant les bons paramètres
tmpfile=/home/nxadmtech/tmp-script-$(date +%s).nxshell

# Ecriture du fichier temporaire
touch $tmpfile
chmod 777 $tmpfile
echo "# connect to http://localhost:8080/reponses (obligatoire sinon Unknown command...) 
connect -u Administrator -p 4ju7Ix662Eo1d77u http://localhost:8180/reponses/site/automation

use automation
Reponses.Cloturer.Dossier -mode $1 -origine $2 -numero $3 -typeCloture $4" >> $tmpfile


# Appel au nxshell en utilisant le fichier temporaire
sh nxshell.sh -f $tmpfile

# Suppression du fichier temporaire lorsqu'on n'en a plus besoin
rm -rf $tmpfile

exit 0