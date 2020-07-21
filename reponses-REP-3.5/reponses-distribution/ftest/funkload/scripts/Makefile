CREDCTL := fl-credential-ctl conf/credential.conf
MONCTL := fl-monitor-ctl conf/monitor.conf
NBDISTRIBCTL := fl-credential-ctl conf/nbdistrib.conf
LOG_HOME := ./log

ifdef URL
	FLOPS = -u $(URL) $(EXT)
else
	FLOPS = $(EXT)
endif

ifdef REPORT_HOME
	REPORT = $(REPORT_HOME)
else
	REPORT = ./reports
endif

all: 
	test-scenario1


start:
	-mkdir -p $(REPORT) $(LOG_HOME)
	-$(MONCTL) restart
	$(CREDCTL) restart
	fl-run-test test_Reponse.py -v --simple-fetch -e testAvailable $(FLOPS)

stop:
	-$(MONCTL) stop
	-$(CREDCTL) stop
	
nbdistribstart:
	-$(NBDISTRIBCTL) start
nbdistribstop:
	-$(NBDISTRIBCTL) stop	

build-report:
	-fl-build-report $(LOG_HOME)/reponse-bench.xml --html -o $(REPORT)

clean:
	-find . "(" -name "*~" -or  -name ".#*" -or  -name "*.pyc" ")" -print0 | xargs -0 rm -f

#############################

run-scenario1:
	fl-run-test -V -dv --simple-fetch test_Reponse.py -e testScenario1 $(FLOPS)


test-scenario1: start run-scenario1 stop

bench-scenario1: start run-scenario1 b-scenario1 build-report stop

b-scenario1:
	-fl-run-bench --label=Login --simple-fetch -c 10 -D 300 -m 0.5 -M 1 -s 0.1 test_Reponse.py Reponse.testScenario1 $(FLOPS)


#############################

run-scenario2:
	fl-run-test -V -dv --simple-fetch test_Reponse.py -e testScenario2 $(FLOPS)


test-scenario2: start run-scenario2 stop

bench-scenario2: start run-scenario2 b-scenario2 build-report stop

b-scenario2:
	-fl-run-bench --label=ChargementMailbox --simple-fetch -c 1:1:1:1:1 -D 300 -m 0 -M 0 -s 1 test_Reponse.py Reponse.testScenario2 $(FLOPS)


#############################

run-scenario3:
	fl-run-test --simple-fetch -V -dv test_Reponse.py Reponse.testScenario3 $(FLOPS)


test-scenario3: start run-scenario3 stop

bench-scenario3: start run-scenario3 b-scenario3 build-report stop

b-scenario3:
	-fl-run-bench  --simple-fetch --label=ChargementCase -c 2:5:10:20 -D 300 -m 2 -M 5 -s 1 test_Reponse.py Reponse.testScenario3 $(FLOPS)

#############################

run-scenario4:
	fl-run-test -V -dv --simple-fetch test_Reponse.py Reponse.testScenario4 $(FLOPS)


test-scenario4: start run-scenario4 stop

bench-scenario4: start run-scenario4 b-scenario4 build-report stop

b-scenario4:
	-fl-run-bench  --simple-fetch --label=NavigationOnglets --simple-fetch -c 1:2:5:10 -D 300 -m 0 -M 0 -s 1 test_Reponse.py Reponse.testScenario4 $(FLOPS)


#############################

run-inject:
	fl-run-test -V -dv --simple-fetch test_Reponse.py Reponse.testInject $(FLOPS)


test-inject: start run-inject stop

bench-inject: start run-inject b-inject build-report stop

b-inject:
	-fl-run-bench  --simple-fetch --label=Validation  -c 1:4:10:20 -D 60 -m 0 -M 0 -s 1 test_Reponse.py Reponse.testInject $(FLOPS)


#############################

run-scenario6:
	fl-run-test -V -dv --simple-fetch test_Reponse.py Reponse.testScenario6 $(FLOPS)


test-scenario6: start run-scenario6 stop

bench-scenario6: start run-scenario6 b-scenario6 build-report stop

b-scenario6:
	-fl-run-bench  --simple-fetch --label=Validation  -c 1:10:20 -D 180 -m 0 -M 0 -s 1 test_Reponse.py Reponse.testScenario6 $(FLOPS)


#############################

run-scenario7:
	fl-run-test -V -dv --simple-fetch test_Reponse.py Reponse.testScenario7 $(FLOPS)


test-scenario7: start run-scenario7 stop

bench-scenario7: start run-scenario7 b-scenario7 build-report stop

b-scenario7:
	-fl-run-bench  --simple-fetch --label=Validation  -c 10 -D 60 -m 0 -M 0 -s 1 test_Reponse.py Reponse.testScenario7 $(FLOPS)

#############################

run-scenario8:
	fl-run-test -V -dv --simple-fetch test_Reponse.py Reponse.testScenario8 $(FLOPS)


test-scenario8: start nbdistribstart run-scenario8 nbdistribstop stop

bench-scenario8: start nbdistribstart run-scenario8 b-scenario8 build-report nbdistribstop stop

b-scenario8:
	-fl-run-bench  --simple-fetch --label=Validation  -c 1:15:30:60 -D 180 -f  test_Reponse.py Reponse.testScenario8 $(FLOPS)

#############################

run-scenario9:
	fl-run-test -V -dv --simple-fetch test_Reponse.py Reponse.testScenario9 $(FLOPS)


test-scenario9: start nbdistribstart run-scenario9 nbdistribstop stop

bench-scenario9: start nbdistribstart run-scenario9 b-scenario9 build-report nbdistribstop stop

b-scenario9:
	-fl-run-bench  --simple-fetch --label=Validation  -c 1:15/30/60 -D 180 -f test_Reponse.py Reponse.testScenario9 $(FLOPS)


#############################

run-scenario1FEV:
	fl-run-test -V -dv test_ReponseFEV.py -e testScenario1 $(FLOPS)


test-scenario1FEV: start run-scenario1FEV stop

bench-scenario1FEV: start run-scenario1FEV b-scenario1FEV build-report stop

b-scenario1FEV:
	-fl-run-bench  --simple-fetch  --label=EspaceDeTravail -c 1:10:20:40:60:80:100 -D 1200 -m 0 -M 0 -s 1 test_ReponseFEV.py Reponse.testScenario1 $(FLOPS)


#############################

run-scenario2FEV:
	fl-run-test -V -dv test_ReponseFEV.py -e testScenario2 $(FLOPS)


test-scenario2FEV: start run-scenario2FEV stop

bench-scenario2FEV: start run-scenario2FEV b-scenario2FEV build-report stop

b-scenario2FEV:
	-fl-run-bench  --simple-fetch --label=Navigation -c 1:10:20:40:60:80:100 -D 1200 -m 0 -M 0 -s 1 test_ReponseFEV.py Reponse.testScenario2 $(FLOPS)


#############################

run-scenarioStats:
	fl-run-test -V -dv test_Reponse.py -e testScenarioStats $(FLOPS)


test-scenarioStats: start run-scenarioStats stop

bench-scenarioStats: start run-scenarioStats b-scenarioStats build-report stop

b-scenarioStats:
	-fl-run-bench  --simple-fetch --label=Stats -c 2:5:10:20 -D 60 -m 0 -M 0 -s 1 test_Reponse.py Reponse.testScenarioStats $(FLOPS)
