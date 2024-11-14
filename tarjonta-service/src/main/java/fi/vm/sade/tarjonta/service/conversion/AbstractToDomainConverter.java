package fi.vm.sade.tarjonta.service.conversion;

import fi.vm.sade.tarjonta.model.BaseEntity;
import org.springframework.core.convert.converter.Converter;

public abstract class AbstractToDomainConverter<FROM, TO extends BaseEntity>
    implements Converter<FROM, TO> {}
