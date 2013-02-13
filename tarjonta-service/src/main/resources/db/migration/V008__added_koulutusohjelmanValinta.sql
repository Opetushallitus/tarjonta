ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN koulutusohjelmanvalinta int8;

alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC155133686 
        foreign key (koulutusohjelmanvalinta) 
        references monikielinen_teksti;