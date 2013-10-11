
    create table haku (
        id int8 not null unique,
        version int8 not null,
        hakukausi varchar(255) not null,
        hakukausi_vuosi int4 not null,
        hakulomake_url varchar(255),
        hakutapa varchar(255) not null,
        hakutyyppi varchar(255) not null,
        haun_tunniste varchar(255),
        kohdejoukko varchar(255) not null,
        koulutuksen_alkamisvuosi int4 not null,
        koulutuksen_alkamiskausi varchar(255) not null,
        oid varchar(255) not null,
        sijoittelu bool,
        tila varchar(255) not null,
        nimi_teksti_id int8,
        primary key (id),
        unique (oid)
    );

    create table haku_hakukohde (
        haku_id int8 not null,
        hakukohdes_id int8 not null,
        primary key (haku_id, hakukohdes_id),
        unique (hakukohdes_id)
    );

    create table hakuaika (
        id int8 not null unique,
        version int8 not null,
        alkamispvm timestamp,
        paattymispvm timestamp,
        sisaisenhakuajannimi varchar(255),
        haku_id int8,
        primary key (id)
    );

    create table hakukohde (
        id int8 not null unique,
        version int8 not null,
        alin_valinta_pistamaara int4,
        aloituspaikat_lkm int4,
        edellisenvuodenhakijat int4,
        hakukelpoisuusvaatimus varchar(255),
        hakukohde_koodisto_nimi varchar(255),
        hakukohde_nimi varchar(255) not null,
        oid varchar(255),
        tila varchar(255),
        ylin_valinta_pistemaara int4,
        haku_id int8 not null,
        lisatiedot_teksti_id int8,
        valintaperustekuvaus_teksti_id int8,
        primary key (id)
    );

    create table hakukohdeliite (
        id int8 not null unique,
        version int8 not null,
        erapaiva timestamp,
        liitetyyppi varchar(255) not null,
        sahkoinenToimitusosoite varchar(255),
        osoiterivi1 varchar(255),
        osoiterivi2 varchar(255),
        postinumero varchar(255),
        postitoimipaikka varchar(255),
        hakukohde_id int8,
        kuvaus_teksti_id int8,
        primary key (id)
    );

    create table koodisto_koodi (
        id varchar(255) not null,
        koodi_arvo varchar(255),
        koodi_nimi_en varchar(255),
        koodi_nimi_fi varchar(255),
        koodi_nimi_sv varchar(255),
        koodi_uri varchar(255),
        koodi_versio int4,
        koodisto_uri varchar(255),
        koodisto_versio int4,
        primary key (id)
    );

    create table koulutus_hakukohde (
        koulutus_id int8 not null,
        hakukohde_id int8 not null,
        primary key (koulutus_id, hakukohde_id)
    );

    create table koulutus_sisaltyvyys (
        id int8 not null unique,
        version int8 not null,
        maxArvo int4,
        minArvo int4,
        tyyppi varchar(255) not null,
        parent_id int8,
        primary key (id)
    );

    create table koulutus_sisaltyvyys_koulutus (
        koulutus_sisaltyvyys_id int8 not null,
        koulutusmoduuli_id int8 not null,
        primary key (koulutus_sisaltyvyys_id, koulutusmoduuli_id)
    );

    create table koulutusmoduuli (
        id int8 not null unique,
        version int8 not null,
        oid varchar(255) not null,
        tila varchar(255),
        updated timestamp,
        eqfluokitus varchar(255),
        koulutusaste varchar(255),
        koulutusluokitus_koodi varchar(255),
        koulutusala varchar(255),
        koulutusohjelmakoodi varchar(255),
        laajuusarvo varchar(255),
        laajuusyksikko varchar(255),
        moduulityyppi varchar(255),
        nqfluokitus varchar(255),
        organisaatio varchar(255),
        opintoala varchar(255),
        tutkintoohjelmanimi varchar(255),
        tutkintonimike varchar(255),
        ulkoinentunniste varchar(255),
        jatkoopintomahdollisuudet int8,
        koulutuksenrakenne int8,
        nimi int8,
        tavoitteet int8,
        primary key (id)
    );

    create table koulutusmoduuli_toteutus (
        id int8 not null unique,
        version int8 not null,
        oid varchar(255) not null,
        tila varchar(255),
        updated timestamp,
        koulutuksen_alkamis_pvm date,
        koulutusaste varchar(255),
        maksullisuus varchar(255),
        pohjakoulutusvaatimus varchar(255),
        suunniteltu_kesto_arvo varchar(255),
        suunniteltu_kesto_yksikko varchar(255),
        tarjoaja varchar(255),
        ulkoinentunniste varchar(255),
        arviointikriteerit int8,
        kansainvalistyminen int8,
        koulutusmoduuli_id int8 not null,
        kuvailevattiedot int8,
        loppukoevaatimukset int8,
        maksullisuus_teksti_id int8,
        painotus int8,
        sijoittuminentyoelamaan int8,
        sisalto int8,
        yhteistyomuidentoimijoidenkanssa int8,
        primary key (id)
    );

    create table koulutusmoduuli_toteutus_ammattinimike (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );

    create table koulutusmoduuli_toteutus_avainsana (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );

    create table koulutusmoduuli_toteutus_koulutuslaji (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );

    create table koulutusmoduuli_toteutus_linkki (
        koulutusmoduuli_toteutus_id int8 not null,
        kieli varchar(255) not null,
        linkki_tyyppi varchar(255) not null,
        url varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, kieli, linkki_tyyppi, url)
    );

    create table koulutusmoduuli_toteutus_opetuskieli (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );

    create table koulutusmoduuli_toteutus_opetusmuoto (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );

    create table koulutusmoduuli_toteutus_teema (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );

    create table koulutusmoduuli_toteutus_yhteyshenkilo (
        koulutusmoduuli_toteutus_id int8 not null,
        yhteyshenkilos_id int8 not null,
        primary key (koulutusmoduuli_toteutus_id, yhteyshenkilos_id),
        unique (yhteyshenkilos_id)
    );

    create table monikielinen_teksti (
        id int8 not null unique,
        version int8 not null,
        primary key (id)
    );

    create table teksti_kaannos (
        id int8 not null unique,
        version int8 not null,
        arvo varchar(4096),
        kieli_koodi varchar(255),
        teksti_id int8 not null,
        primary key (id)
    );

    create table valintakoe (
        id int8 not null unique,
        version int8 not null,
        tyyppiUri varchar(255),
        kuvaus_monikielinenteksti_id int8,
        hakukohde_id int8,
        primary key (id)
    );

    create table valintakoe_ajankohta (
        id int8 not null unique,
        version int8 not null,
        alkamisaika timestamp,
        nimi varchar(255),
        paattymisaika timestamp,
        valintakoe_id int8,
        primary key (id)
    );

    create table valintakoe_osoite (
        id int8 not null unique,
        version int8 not null,
        osoiterivi1 varchar(255),
        osoiterivi2 varchar(255),
        postinumero varchar(255),
        postitoimipaikka varchar(255),
        valintakoe_ajankohta_id int8,
        primary key (id)
    );

    create table yhteyshenkilo (
        id int8 not null unique,
        version int8 not null,
        etunimis varchar(255) not null,
        henkilo_oid varchar(255),
        kielis varchar(255),
        puhelin varchar(255),
        sahkoposti varchar(255),
        sukunimi varchar(255) not null,
        titteli varchar(255),
        primary key (id)
    );

    alter table haku 
        add constraint FK30C023FDDFB391 
        foreign key (nimi_teksti_id) 
        references monikielinen_teksti;

    alter table haku_hakukohde 
        add constraint FK8EDEE3A64B73A8C9 
        foreign key (haku_id) 
        references haku;

    alter table haku_hakukohde 
        add constraint FK8EDEE3A67F1D085C 
        foreign key (hakukohdes_id) 
        references hakukohde;

    alter table hakuaika 
        add constraint FKFBEBA6214B73A8C9 
        foreign key (haku_id) 
        references haku;

    alter table hakukohde 
        add constraint FK8218B8C289CB98C6 
        foreign key (valintaperustekuvaus_teksti_id) 
        references monikielinen_teksti;

    alter table hakukohde 
        add constraint FK8218B8C24B73A8C9 
        foreign key (haku_id) 
        references haku;

    alter table hakukohde 
        add constraint FK8218B8C2DBE0CDBE 
        foreign key (lisatiedot_teksti_id) 
        references monikielinen_teksti;

    alter table hakukohdeliite 
        add constraint FK59C9AE7B713EED 
        foreign key (kuvaus_teksti_id) 
        references monikielinen_teksti;

    alter table hakukohdeliite 
        add constraint FK59C9AE7B1822B1EB 
        foreign key (hakukohde_id) 
        references hakukohde;

    alter table koulutus_hakukohde 
        add constraint FKDEAC3B1822B1EB 
        foreign key (hakukohde_id) 
        references hakukohde;

    alter table koulutus_hakukohde 
        add constraint FKDEAC3BBABD3C63 
        foreign key (koulutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutus_sisaltyvyys 
        add constraint FKEF64623B5BB5BA8 
        foreign key (parent_id) 
        references koulutusmoduuli;

    alter table koulutus_sisaltyvyys_koulutus 
        add constraint FKEE97194EDC3A30B 
        foreign key (koulutusmoduuli_id) 
        references koulutusmoduuli;

    alter table koulutus_sisaltyvyys_koulutus 
        add constraint FKEE971946920A5BA 
        foreign key (koulutus_sisaltyvyys_id) 
        references koulutus_sisaltyvyys;

    alter table koulutusmoduuli 
        add constraint FK3317B427EEA58D9 
        foreign key (jatkoopintomahdollisuudet) 
        references monikielinen_teksti;

    alter table koulutusmoduuli 
        add constraint FK3317B427B7262A4 
        foreign key (nimi) 
        references monikielinen_teksti;

    alter table koulutusmoduuli 
        add constraint FK3317B427547CEF5D 
        foreign key (koulutuksenrakenne) 
        references monikielinen_teksti;

    alter table koulutusmoduuli 
        add constraint FK3317B42775D83DFE 
        foreign key (tavoitteet) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1C7C7468A 
        foreign key (maksullisuus_teksti_id) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1BA627773 
        foreign key (arviointikriteerit) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC17576801D 
        foreign key (yhteistyomuidentoimijoidenkanssa) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC11779FCF2 
        foreign key (sijoittuminentyoelamaan) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1F3652403 
        foreign key (kansainvalistyminen) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1885A8C50 
        foreign key (sisalto) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1B1C999E2 
        foreign key (kuvailevattiedot) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1EDC3A30B 
        foreign key (koulutusmoduuli_id) 
        references koulutusmoduuli;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1426E0926 
        foreign key (painotus) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus 
        add constraint FK47C9EC1D5AA7808 
        foreign key (loppukoevaatimukset) 
        references monikielinen_teksti;

    alter table koulutusmoduuli_toteutus_ammattinimike 
        add constraint FKC76EAF9C2566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_avainsana 
        add constraint FK7BE0C0F42566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_koulutuslaji 
        add constraint FKB8C312A2566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_linkki 
        add constraint FKE22F1A162566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_opetuskieli 
        add constraint FKAC127DD82566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_opetusmuoto 
        add constraint FKAC3447D62566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_teema 
        add constraint FK5A4F45AA2566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_yhteyshenkilo 
        add constraint FK1A1E6ADA2566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

    alter table koulutusmoduuli_toteutus_yhteyshenkilo 
        add constraint FK1A1E6ADA9DB9BB08 
        foreign key (yhteyshenkilos_id) 
        references yhteyshenkilo;

    alter table teksti_kaannos 
        add constraint FK6161347E1D23B139 
        foreign key (teksti_id) 
        references monikielinen_teksti;

    alter table valintakoe 
        add constraint FKF7AE1AEEFD02C7D 
        foreign key (kuvaus_monikielinenteksti_id) 
        references monikielinen_teksti;

    alter table valintakoe 
        add constraint FKF7AE1AE1822B1EB 
        foreign key (hakukohde_id) 
        references hakukohde;

    alter table valintakoe_ajankohta 
        add constraint FK58FC234AC5B6C9A9 
        foreign key (valintakoe_id) 
        references valintakoe;

    alter table valintakoe_osoite 
        add constraint FK5C250560A8E392EE 
        foreign key (valintakoe_ajankohta_id) 
        references valintakoe_ajankohta;

    create sequence hibernate_sequence;
