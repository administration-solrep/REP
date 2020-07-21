update voc_an_analyse set "id" = "label";

DELETE FROM voc_an_analyse
WHERE rowid not in
(SELECT MIN(rowid)
FROM voc_an_analyse
GROUP BY "label");

update voc_se_rubrique set "id" = "label";

update voc_ta_rubrique set "id" = "label";

DELETE FROM voc_ta_rubrique
WHERE rowid not in
(SELECT MIN(rowid)
FROM voc_ta_rubrique
GROUP BY "label");

commit;