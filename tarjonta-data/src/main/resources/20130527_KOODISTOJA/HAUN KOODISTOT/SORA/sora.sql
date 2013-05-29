
select yh_linja, yh_snimi, AMKOU_SSELITE, amkou_kala, ala_sselite, ala_kala
from yh_linja, koulutusala, amkou_kouluala
where yh_sora = 1
and koulutusala.ala_id = yh_ala_id
and koulutusala.ala_amkou_id=amkou_kouluala.amkou_id