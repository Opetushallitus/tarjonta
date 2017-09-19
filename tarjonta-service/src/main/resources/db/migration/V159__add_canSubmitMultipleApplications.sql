ALTER TABLE haku
ADD COLUMN can_submit_multiple_applications boolean;

UPDATE haku
SET can_submit_multiple_applications = (
    ataru_lomake_avain IS NOT NULL
    OR hakutyyppi NOT LIKE 'hakutyyppi_01#%' -- ei varsinainen haku
    OR (hakutapa LIKE 'hakutapa_02#%' -- erillishaku
        AND (kohdejoukko LIKE 'haunkohdejoukko_11#%' -- toinen aste
            OR kohdejoukko LIKE 'haunkohdejoukko_17#%'
            OR kohdejoukko LIKE 'haunkohdejoukko_20#%'))
);

ALTER TABLE haku
ALTER COLUMN can_submit_multiple_applications SET NOT NULL;
