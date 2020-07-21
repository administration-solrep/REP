-- #########################################################################################
-- Augmentation de la taille alloué pour le NX_STRING_ARRAY
-- #########################################################################################

CREATE OR REPLACE TYPE NX_STRING_ARRAY AS VARRAY(200) OF VARCHAR2(32767);
/