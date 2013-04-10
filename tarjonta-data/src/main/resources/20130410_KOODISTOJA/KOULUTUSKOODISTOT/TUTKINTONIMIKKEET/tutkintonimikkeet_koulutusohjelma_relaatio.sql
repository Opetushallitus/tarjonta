select distinct  tut_koukoodi, kok_koodi as KOULUTUSOHJELMA, kok_sselite, amk_xnimi, amk_koodi
from amktutkinto
inner join kohjelma on koh_amk_koodi = amk_koodi
inner join kohjelmak on koh_kok_id = kok_id
inner join tutkinto on tut_id = koh_tut_id
where tut_tty_id = '41485'
and (amk_loppupvm is null or amk_loppupvm > sysdate)
and (tut_loppupvm is null or tut_loppupvm > sysdate)
order by tut_koukoodi, kok_koodi;



SELECT AMK_KOODI,AMK_XNIMI,AMK_XLYH,AMK_SNIMI,AMK_SLYH,AMK_RNIMI,AMK_RLYH, amk_alkupvm as ALKUPVM, amk_loppupvm as LOPPUPVM
from amktutkinto
inner join kohjelma on koh_amk_koodi = amk_koodi
inner join kohjelmak on koh_kok_id = kok_id
inner join tutkinto on tut_id = koh_tut_id
where tut_tty_id = '41485'
and (amk_loppupvm is null or amk_loppupvm > sysdate) 
and (tut_loppupvm is null or tut_loppupvm > sysdate)
order by tut_koukoodi, kok_koodi

select distinct  tut_koukoodi, kok_koodi as KOULUTUSOHJELMA, kok_sselite, amk_xnimi, amk_koodi
from amktutkinto
inner join kohjelma on koh_amk_koodi = amk_koodi
inner join kohjelmak on koh_kok_id = kok_id
inner join tutkinto on tut_id = koh_tut_id
where tut_tty_id = '41485'
and (amk_loppupvm is null or amk_loppupvm > sysdate)
and (tut_loppupvm is null or tut_loppupvm > sysdate)
order by tut_koukoodi, kok_koodi