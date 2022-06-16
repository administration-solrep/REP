#!/bin/bash

# Usage : cloturer_dossier_legislature.sh [mode] [origine : AN ou SENAT] [législature] [date et heure de fin au format JJ-MM-AAAA-HH-MM] [type de cloture]
#
# Script servant à cloturer les questions d'une législature. Ce script ne prend en compte que les questions en cours pour la législature concernée.
#
# Arguments : 
# - mode : Mode d'exécution. LECTURE pour afficher les questions à cloturer. EXECUTION pour exécuter la cloture des questions
# - origine : Origine des questions. AN pour Assemblée Nationale et SENAT pour Sénat
# - Législature : Sur deux chiffres. Numéro de la législature pour laquelle on souhaite clore les questions.
# - Date de heure de fin au format JJ-MM-AAAA-hh-mm : Date et heure à laquelle on arrête le script. Permet d'éviter de bloquer la prod trop longtemps. 
# - Type de cloture : caduque, retiree ou cloture_autre

# Pour lancer ce script :
# 1 - copier ce fichier dans /tmp/
# 2 - Aller dans le répertoire contenant nxshell.sh
# 3 - Taper la commande sh /tmp/cloturer_dossier_legislature.sh [mode] [origine : AN ou SENAT] [législature] [date et heure de fin au format JJ-MM-AAAA-HH-MM] [type de cloture]
# Exemple : sh /tmp/cloturer_dossier_legislature.sh EXECUTION AN 14 09-05-2017-17-00 CADUQUE

function showHelp {
	head -n 12 $0 | tail -n 10
	exit 0
}

# Affiche l'aide si ce n'est pas le bon nombre de paramètres
	if [ $# -gt 5 ] ; then showHelp ; fi
	if [ $# -lt 5 ] ; then showHelp ; fi

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
if [[ "${#3}" != 2 ]]
then
	echo "La législature doit obligatoirement contenir deux caractères. Exemple : 14"
	exit 1
fi
if [[ ! $4 =~ ([0-9]{2})-([0-9]{2})-([0-9]{4})-([01][0-9]|2[0-3])-[0-5][0-9]$ ]]
then
	echo "La date de fin doit être au format JJ-MM-AAAA-hh-mm"
	exit 1
fi
if [[ $5 != "CADUQUE" && $5 != "RETIREE" && $5 != "CLOTURE_AUTRE" ]]
then
	echo "Le type de cloture est incorrect. Elle ne peut prendre que les valeurs caduque, retiree ou cloture_autre"
	exit 1
fi

# Fin de vérification des paramètres

# Affichage d'un message de confirmation seulement si on est en mode exécution. 
if [ $1 == "EXECUTION" ]
then
	echo "Début de cloture des questions pour la législature $3 ayant pour origine $2 avec la date de fin $4"
	read -p "Êtes-vous sûr? (o/n)" -n 1
	if [[  $REPLY != O && $REPLY != o ]]
	then
	    exit 1
	fi
fi

#Affichage d'un petit message disant qu'on affiche la liste des questions à clore si on est en mode lecture
if [ $1 == "LECTURE" ]
then
	echo "Liste des questions à clore pour la législature $3 ayant pour origine $2 avec la date de fin $4"
fi

# Fichier temporaire qui va permettre d'appeler l'opération en lui passant les bons paramètres
tmpfile=/home/nxadmtech/tmp-script-$(date +%s).nxshell

# Ecriture du fichier temporaire
touch $tmpfile
chmod 777 $tmpfile
echo "# connect to http://localhost:8080/reponses (obligatoire sinon Unknown command...) 
connect -u Administrator -p 4ju7Ix662Eo1d77u http://localhost:8180/reponses/site/automation

use automation
Reponses.Cloturer.Dossiers.Legislature -mode $1 -origine $2 -legislature $3 -dateFin $4 -typeCloture $5" >> $tmpfile


# Appel au nxshell en utilisant le fichier temporaire
sh nxshell.sh -f $tmpfile

# Suppression du fichier temporaire lorsqu'on n'en a plus besoin
rm -rf $tmpfile

exit 0