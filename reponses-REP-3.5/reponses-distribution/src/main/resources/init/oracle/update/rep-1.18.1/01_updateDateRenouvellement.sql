merge into question q 
using (select q.id, rq.maxDate from Question q inner join (select parentid as idQuestion, max(datedeffet) as maxDate from hierarchy h inner join renouvellementquestion r on h.id = r.id group by h.parentid) rq on q.id = rq.idQuestion where q.daterenouvellementquestion != rq.maxDate) up
on (q.id = up.id)
when matched then update set q.daterenouvellementquestion = up.maxDate;
commit;
/