create table koulutusmoduuli_toteutus_alkamispvm (
    koulutusmoduuli_toteutus_id int8 not null,
    alkamispvm timestamp not null,
    primary key (koulutusmoduuli_toteutus_id, alkamispvm )
);    

alter table koulutusmoduuli_toteutus_alkamispvm 
    add constraint FKD5647CDF2566EBFA 
    foreign key (koulutusmoduuli_toteutus_id) 
    references koulutusmoduuli_toteutus;


-- original date columns to a row
INSERT INTO koulutusmoduuli_toteutus_alkamispvm (koulutusmoduuli_toteutus_id, alkamispvm) SELECT kt.id, kt.koulutuksen_alkamis_pvm 
FROM koulutusmoduuli_toteutus kt WHERE kt.koulutuksen_alkamis_pvm IS NOT NULL;