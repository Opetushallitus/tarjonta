package fi.vm.sade.tarjonta.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.auditlog.Audit;
import org.junit.Test;

import java.io.IOException;

public class AuditHelperTest {
    private final static ObjectMapper mapper = new ObjectMapper();

    private String longString = createLongString();

    private String jsonString =
                "{" +
                    " \"longString\": \"" + longString + "\"," +
                    " \"shortString\": \"bee\"," +
                    " \"number\": 99," +
                    " \"array\": [ \"" + longString + "\" ] " +
                "}";

    @Test
    public void truncatesLongField() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        assert(json.toString().length() > Audit.MAX_FIELD_LENGTH);

        AuditHelper.traverseAndTruncate(json);

        String truncatedString = json.get("longString").textValue();
        assert(truncatedString.length() < longString.length());
        assert(truncatedString.length() < Audit.MAX_FIELD_LENGTH);
        assert(json.toString().length() < Audit.MAX_FIELD_LENGTH);
    }

    @Test
    public void truncatesLongArrayElement() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        assert(json.toString().length() > Audit.MAX_FIELD_LENGTH);

        AuditHelper.traverseAndTruncate(json);

        String truncatedString = json.get("array").get(0).textValue();
        assert(truncatedString.length() < longString.length());
        assert(truncatedString.length() < Audit.MAX_FIELD_LENGTH);
        assert(json.toString().length() < Audit.MAX_FIELD_LENGTH);
    }

    @Test
    public void truncatedStringsMatchForIdenticalInputs() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditHelper.traverseAndTruncate(json);

        String truncatedString1 = json.get("longString").textValue();
        String truncatedString2 = json.get("array").get(0).textValue();
        assert(truncatedString1.equals(truncatedString2));
    }

    @Test
    public void doesNotTruncateShortField() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditHelper.traverseAndTruncate(json);

        String shortString = json.get("shortString").textValue();
        assert(shortString.equals("bee"));
    }

    @Test
    public void doesNotTruncateNumber() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditHelper.traverseAndTruncate(json);

        int number = json.get("number").intValue();
        assert(number == 99);
    }

    private String createLongString() {
        int length = 33000;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        return sb.toString();
    }
}
