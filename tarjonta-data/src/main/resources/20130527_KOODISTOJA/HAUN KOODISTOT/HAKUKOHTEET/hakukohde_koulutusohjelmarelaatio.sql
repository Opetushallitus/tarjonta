select distinct kok_koodi, kok_sselite, yh_linja, yh_snimi
from kohjelmak, opintolinja, yh_linja, kohjelma
where OPL_TUT_ID = koh_tut_id
and KOH_KOK_ID = kok_id
and kok_kaytto = 'TOKA'
and opl_yh_linja = yh_linja
and (yh_loppupvm is null or yh_loppupvm > sysdate)
and (kok_loppupvm is null or kok_loppupvm > sysdate)
and yh_snimi not like '%sekä lukio%';
