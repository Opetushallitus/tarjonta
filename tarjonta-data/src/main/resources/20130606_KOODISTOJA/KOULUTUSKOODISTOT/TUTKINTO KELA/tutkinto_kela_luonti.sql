select 
        TUTN_NIMI,
		lpad(to_char(tut_id), 10, '0') c1,
        rpad(tut_koukoodi, 6, ' ') c2,
        decode(tut_alkupvm, 
                null,'01.01.0001',to_char(tut_alkupvm,'dd.mm.rrrr')) apvm,
        lpad(to_char(tut_ala_id), 10, '0') c3,
        rpad(tut_amm_tutkinto, 3, ' ') c4,
        lpad(to_char(tut_kas_id), 10, '0') c5,
        decode(tut_loppupvm, 
                null,'01.01.0001',to_char(tut_loppupvm,'dd.mm.rrrr')) lpvm, 
        rpad(tut_hyvaksytty, 1, ' ') c6,
        rpad(tut_kehsuumuk, 1, ' ') c7,
        rpad(tut_oppisopimus, 1, ' ') c8,
        decode(tut_opsupvm,
                null,'01.01.0001',to_char(tut_opsupvm,'dd.mm.rrrr')) opvm,
        rpad(tut_opsudiaari, 18, ' ') c9,
        decode(tut_tupepvm, 
                null,'01.01.0001',to_char(tut_tupepvm,'dd.mm.rrrr')) tpvm,
        rpad(tut_tupedia, 18, ' ') c10,
        lpad(to_char(tut_pituus1), 10, '0') c11,
        rpad(tut_yksikko1, 5, ' ') c12, 
        rpad(tut_pituus2, 10, ' ') c13, 
        rpad(tut_yksikko2, 5, ' ') c14, 
        rpad(tut_pituus3, 10, ' ') c15, 
        rpad(tut_yksikko3, 5, ' ') c16, 
        rpad(tut_pituus4, 10, ' ') c17, 
        rpad(tut_yksikko4, 5, ' ') c18, 
        decode(tut_paivityspvm,
                null,'01.01.0001',to_char(tut_paivityspvm,'dd.mm.rrrr')) ppvm,
        rpad(tut_paivittaja, 30, ' ') c19,
        lpad(to_char(tut_uala_id), 10, '0') c20,
        lpad(to_char(tut_ukas_id), 10, '0') c21
from
        tutkinto, tutkintonimi
where 
        tut_alkupvm <= (sysdate + 365) and
        (tut_loppupvm >= sysdate or tut_loppupvm is null) and
		tut_id = TUTN_TUT_ID and
		tut_tty_id in ('41485', '41484') ----kotialousopetus, ammatilliset perustutkinnot, yleissivistävä
		and TUTN_VIRALLINEN = 'X';