-- 1a KOMO uudet taulut & constraintit

create table koulutusmoduuli_tekstit (
    koulutusmoduuli_id int8 not null,
    monikielinen_teksti_id int8 not null,
    teksti varchar(255) not null,
    primary key (koulutusmoduuli_id, teksti),
    unique (monikielinen_teksti_id)
);

alter table koulutusmoduuli_tekstit 
    add constraint FK17E66E4EEDC3A30B 
    foreign key (koulutusmoduuli_id) 
    references koulutusmoduuli;

alter table koulutusmoduuli_tekstit 
    add constraint FK17E66E4EAD76422A 
    foreign key (monikielinen_teksti_id) 
    references monikielinen_teksti;

-- 1b KOMO migraatio

insert into koulutusmoduuli_tekstit (koulutusmoduuli_id, monikielinen_teksti_id, teksti)
	select id, jatkoopintomahdollisuudet, 'JATKOOPINTO_MAHDOLLISUUDET' from koulutusmoduuli where jatkoopintomahdollisuudet is not null;
	
insert into koulutusmoduuli_tekstit (koulutusmoduuli_id, monikielinen_teksti_id, teksti)
	select id, koulutuksenrakenne, 'KOULUTUKSEN_RAKENNE' from koulutusmoduuli where koulutuksenrakenne is not null;
	
insert into koulutusmoduuli_tekstit (koulutusmoduuli_id, monikielinen_teksti_id, teksti)
	select id, tavoitteet, 'TAVOITTEET' from koulutusmoduuli where tavoitteet is not null;
	
-- 1c KOMO vanhat constraintit pois   
        
alter table koulutusmoduuli drop constraint FK3317B427EEA58D9;
alter table koulutusmoduuli drop constraint FK3317B427547CEF5D;
alter table koulutusmoduuli drop constraint FK3317B42775D83DFE;
    
-- 1d KOMO vanhat sarakkeet pois

alter table koulutusmoduuli drop column jatkoopintomahdollisuudet;
alter table koulutusmoduuli drop column koulutuksenrakenne;
alter table koulutusmoduuli drop column tavoitteet;
    
-- 2a KOMOTO uudet taulut & constraintit
    
create table koulutusmoduuli_toteutus_tekstit (
    koulutusmoduuli_toteutus_id int8 not null,
    monikielinen_teksti_id int8 not null,
    teksti varchar(255) not null,
    primary key (koulutusmoduuli_toteutus_id, teksti),
    unique (monikielinen_teksti_id)
);

alter table koulutusmoduuli_toteutus_tekstit 
    add constraint FK3EC1EE82566EBFA 
    foreign key (koulutusmoduuli_toteutus_id) 
    references koulutusmoduuli_toteutus;

alter table koulutusmoduuli_toteutus_tekstit 
    add constraint FK3EC1EE8AD76422A 
    foreign key (monikielinen_teksti_id) 
    references monikielinen_teksti;

-- 2b KOMOTO migraatio

    	
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, maksullisuus_teksti_id, 'MAKSULLISUUS' from koulutusmoduuli_toteutus where maksullisuus_teksti_id is not null;
		
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, arviointikriteerit, 'ARVIOINTIKRITEERIT' from koulutusmoduuli_toteutus where arviointikriteerit is not null;
		
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, loppukoevaatimukset, 'LOPPUKOEVAATIMUKSET' from koulutusmoduuli_toteutus where loppukoevaatimukset is not null;
	
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, painotus, 'PAINOTUS' from koulutusmoduuli_toteutus where painotus is not null;
	
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, koulutusohjelmanvalinta, 'KOULUTUSOHJELMAN_VALINTA' from koulutusmoduuli_toteutus where koulutusohjelmanvalinta is not null;
	
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, kuvailevattiedot, 'KUVAILEVAT_TIEDOT' from koulutusmoduuli_toteutus where kuvailevattiedot is not null;
	
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, sisalto, 'SISALTO' from koulutusmoduuli_toteutus where sisalto is not null;
	
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, sijoittuminentyoelamaan, 'SIJOITTUMINEN_TYOELAMAAN' from koulutusmoduuli_toteutus where sijoittuminentyoelamaan is not null;
	
insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, kansainvalistyminen, 'KANSAINVALISTYMINEN' from koulutusmoduuli_toteutus where kansainvalistyminen is not null;

insert into koulutusmoduuli_toteutus_tekstit (koulutusmoduuli_toteutus_id, monikielinen_teksti_id, teksti)
	select id, yhteistyomuidentoimijoidenkanssa, 'YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA' from koulutusmoduuli_toteutus where yhteistyomuidentoimijoidenkanssa is not null;

-- 2c KOMOTO vanhat constraintit pois   

alter table koulutusmoduuli_toteutus drop constraint FK47C9EC1C7C7468A;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC1BA627773;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC17576801D;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC11779FCF2;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC1F3652403;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC1885A8C50;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC1B1C999E2;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC155133686;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC1426E0926;
alter table koulutusmoduuli_toteutus drop constraint FK47C9EC1D5AA7808;

-- 2d KOMOTO vanhat sarakkeet pois

alter table koulutusmoduuli_toteutus drop column maksullisuus_teksti_id;
alter table koulutusmoduuli_toteutus drop column arviointikriteerit;
alter table koulutusmoduuli_toteutus drop column yhteistyomuidentoimijoidenkanssa;
alter table koulutusmoduuli_toteutus drop column sijoittuminentyoelamaan;
alter table koulutusmoduuli_toteutus drop column kansainvalistyminen;
alter table koulutusmoduuli_toteutus drop column sisalto;
alter table koulutusmoduuli_toteutus drop column kuvailevattiedot;
alter table koulutusmoduuli_toteutus drop column koulutusohjelmanvalinta;
alter table koulutusmoduuli_toteutus drop column painotus;
alter table koulutusmoduuli_toteutus drop column loppukoevaatimukset;
