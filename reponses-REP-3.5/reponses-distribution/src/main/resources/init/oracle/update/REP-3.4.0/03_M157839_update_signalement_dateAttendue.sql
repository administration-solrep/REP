-- Le délai de réponse aux questions signalées paramétré dans l'application en production est de 10 jours.

UPDATE signalementquestion SET dateAttendue = dateDEffet + INTERVAL '10' DAY WHERE dateAttendue is null;
COMMIT;