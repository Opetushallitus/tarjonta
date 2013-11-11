alter table koulutusmoduuli_toteutus add column nimi int8;

alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1B7262A4 
        foreign key (nimi) 
        references monikielinen_teksti;