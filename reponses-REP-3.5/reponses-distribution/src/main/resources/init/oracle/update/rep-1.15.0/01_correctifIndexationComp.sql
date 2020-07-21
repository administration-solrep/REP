UPDATE ixacomp_motclef_ministere idx set idx.item = TRIM(item) WHERE idx.item LIKE ' %' OR idx.item like '% ';
commit;