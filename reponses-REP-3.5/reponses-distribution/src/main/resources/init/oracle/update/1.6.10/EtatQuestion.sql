update question set etatquestion='en cours' where etatquestion='renouvelee';
update question set etatquestion='en cours' where etatquestion='signalee';

delete voc_etat_question where voc_etat_question."id"='signalee ';
delete voc_etat_question where voc_etat_question."id"='renouvelee';
