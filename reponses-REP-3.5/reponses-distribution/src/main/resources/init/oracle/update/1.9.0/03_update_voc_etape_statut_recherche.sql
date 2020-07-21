delete from VOC_STATUT_ETAPE_RECHERCHE;
insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('running_all','0',10000000,'label.reponses.feuilleRoute.etape.running');
insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('ready_all','0',10000000,'label.reponses.feuilleRoute.etape.ready');
insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_1','0',10000000,'label.reponses.feuilleRoute.etape.valide.manuellement');
insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_2','0',10000000,'label.reponses.feuilleRoute.etape.valide.refusValidation');
insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_3','0',10000000,'label.reponses.feuilleRoute.etape.valide.automatiquement');
insert into VOC_STATUT_ETAPE_RECHERCHE ("id","obsolete","ordering","label") values ('done_4','0',10000000,'label.reponses.feuilleRoute.etape.valide.nonConcerne');
commit;

