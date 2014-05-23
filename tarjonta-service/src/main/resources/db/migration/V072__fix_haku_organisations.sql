--
-- Haku tallennuksessa täytyy olla organisaatio haussa määriteltynä, muuten tulee permission denied
--
UPDATE haku SET tarjoajaoid = '1.2.246.562.10.00000000001' WHERE tarjoajaoid IS NULL;
UPDATE haku SET organisationoids = '1.2.246.562.10.00000000001' WHERE organisationoids IS NULL;
