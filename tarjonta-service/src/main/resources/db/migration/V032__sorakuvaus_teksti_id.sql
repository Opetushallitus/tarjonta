alter table hakukohde
		add column sorakuvaus_teksti_id int8;

alter table hakukohde 
        add constraint FK8218B8C25458BE78 
        foreign key (sorakuvaus_teksti_id) 
        references monikielinen_teksti;
