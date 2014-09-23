package fi.vm.sade.tarjonta.service.resources.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String stringValue = jsonParser.getValueAsString();

        if (stringValue.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(stringValue);
        } catch (NumberFormatException e) {
            return new BigDecimal(stringValue.replace(",", "."));
        }
    }
}
