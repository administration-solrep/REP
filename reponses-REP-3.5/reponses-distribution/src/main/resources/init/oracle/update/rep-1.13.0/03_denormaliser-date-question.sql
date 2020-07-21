update question qu set qu.datesignalementquestion = (select max(etat.date_changement_etat) from hierarchy h, etatquestion#anonymoustype etat where h.parentid=qu.id and h.name='qu:etatQuestionList' and h.id=etat.id and etat.etatquestion='signalee')
where qu.etatsignale=1;
commit;

update question qu set qu.datecloturequestion = (select max(etat.date_changement_etat) from hierarchy h, etatquestion#anonymoustype etat where h.parentid=qu.id and h.name='qu:etatQuestionList' and h.id=etat.id and etat.etatquestion='cloture_autre')
where etatquestion='cloture_autre';
commit;

