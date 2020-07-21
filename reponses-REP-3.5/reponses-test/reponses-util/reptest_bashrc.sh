# Auteur : JGO
# Fichier de raccourcis pour compiler et lancer les tests webdrivers de Réponses.
# Pre-Requis : Le fichier dila_bashrc doit être sourcé avant ce fichier
#
# Pour utiliser ce script, ajouter les lignes suivantes dans ~/.bashrc :
#
#REP_TEST_BASHRC=${DILA_WS}/reponses/reponses-test/reponses-util/reptest_bashrc.sh
#if [ -e "${REP_TEST_BASHRC}" ]; then
#  source "${REP_TEST_BASHRC}"
#fi


alias br-alltests="cd ${REPONSES}/reponses-test && mvn clean -Dmaven.test.skip=true install"

export REP_TEST_TARGET=${REPONSES}/reponses-test/reponses-distribution-test/target
br-runtest(){
	REP_SCRIPT_TESTS="${REP_TEST_TARGET}/naiad-setup-base/main/scripts"
	cd "${REP_SCRIPT_TESTS}" && sh webdriver/exec-junit-list.sh ${REP_SCRIPT_TESTS}/../actions/${1}/tests.lst
}

