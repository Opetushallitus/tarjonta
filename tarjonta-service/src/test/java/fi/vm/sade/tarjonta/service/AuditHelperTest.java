package fi.vm.sade.tarjonta.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.auditlog.Audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        assertTrue(truncatedString.length() < longString.length());
        assertTrue(truncatedString.length() < Audit.MAX_FIELD_LENGTH);
        assertTrue(json.toString().length() < Audit.MAX_FIELD_LENGTH);
    }

    @Test
    public void truncatesLongArrayElement() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        assertTrue(json.toString().length() > Audit.MAX_FIELD_LENGTH);

        AuditHelper.traverseAndTruncate(json);

        String truncatedString = json.get("array").get(0).textValue();
        assertTrue(truncatedString.length() < longString.length());
        assertTrue(truncatedString.length() < Audit.MAX_FIELD_LENGTH);
        assertTrue(json.toString().length() < Audit.MAX_FIELD_LENGTH);
    }

    @Test
    public void truncatedStringsMatchForIdenticalInputs() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditHelper.traverseAndTruncate(json);

        String truncatedString1 = json.get("longString").textValue();
        String truncatedString2 = json.get("array").get(0).textValue();
        assertEquals(truncatedString1, truncatedString2);
    }

    @Test
    public void doesNotTruncateShortField() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditHelper.traverseAndTruncate(json);

        String shortString = json.get("shortString").textValue();
        assertEquals("bee", shortString);
    }

    @Test
    public void doesNotTruncateNumber() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditHelper.traverseAndTruncate(json);

        int number = json.get("number").intValue();
        assertEquals(99, number);
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
