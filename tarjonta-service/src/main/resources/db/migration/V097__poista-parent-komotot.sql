/**
Vaadin toteutus loi parent komotoja jokaiselle erilliselle koulutustyypille (1 per organisaatio).
Näitä ei enää tarvita Angularin puolella, joten merkitään parent komotot poistetuksi.
 */
update koulutusmoduuli_toteutus
set tila = 'POISTETTU'
where alkamisvuosi is null
and toteutustyyppi not like '%_VALMISTAVA'
and tila != 'POISTETTU';
