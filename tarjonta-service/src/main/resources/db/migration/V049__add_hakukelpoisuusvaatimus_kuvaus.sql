ALTER TABLE hakukohde DROP COLUMN IF EXISTS  hakukelpoisuusvaatimuskuvaus_teksti_id;


ALTER TABLE hakukohde ADD COLUMN hakukelpoisuusvaatimuskuvaus_teksti_id int8;

alter table hakukohde
        add constraint FK8218B8C2D0AC1968
        foreign key (hakukelpoisuusvaatimuskuvaus_teksti_id)
        references monikielinen_teksti;