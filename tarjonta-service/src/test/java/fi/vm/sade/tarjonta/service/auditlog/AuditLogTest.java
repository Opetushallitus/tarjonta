package fi.vm.sade.tarjonta.service.auditlog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import fi.vm.sade.auditlog.Changes;
import org.junit.Test;

import java.io.IOException;

public class AuditLogTest {
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
        assert(json.toString().length() > AuditLog.MAX_FIELD_LENGTH);

        AuditLog.traverseAndTruncate(json);

        String truncatedString = json.get("longString").textValue();
        assertTrue(truncatedString.length() < longString.length());
        assertTrue(truncatedString.length() < AuditLog.MAX_FIELD_LENGTH);
        assertTrue(json.toString().length() < AuditLog.MAX_FIELD_LENGTH);
    }

    @Test
    public void truncatesLongArrayElement() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        assertTrue(json.toString().length() > AuditLog.MAX_FIELD_LENGTH);

        AuditLog.traverseAndTruncate(json);

        String truncatedString = json.get("array").get(0).textValue();
        assertTrue(truncatedString.length() < longString.length());
        assertTrue(truncatedString.length() < AuditLog.MAX_FIELD_LENGTH);
        assertTrue(json.toString().length() < AuditLog.MAX_FIELD_LENGTH);
    }

    @Test
    public void truncatedStringsMatchForIdenticalInputs() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditLog.traverseAndTruncate(json);

        String truncatedString1 = json.get("longString").textValue();
        String truncatedString2 = json.get("array").get(0).textValue();
        assertEquals(truncatedString1, truncatedString2);
    }

    @Test
    public void doesNotTruncateShortField() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditLog.traverseAndTruncate(json);

        String shortString = json.get("shortString").textValue();
        assertEquals("bee", shortString);
    }

    @Test
    public void doesNotTruncateNumber() throws IOException {
        JsonNode json = mapper.readTree(jsonString);
        AuditLog.traverseAndTruncate(json);

        int number = json.get("number").intValue();
        assertEquals(99, number);
    }

    @Test
    public void testJsonPatchDiff() throws IOException {
        String changedJsonString = "{" +
                " \"longString\": \"" + longString + "\"," +
                " \"shortString\": \"wasp\"," +
                " \"number\": 99," +
                " \"array\": [ \"" + longString + "\" ] " +
                "}";

        JsonNode json = mapper.readTree(jsonString);
        JsonNode jsonChanged = mapper.readTree(changedJsonString);
        Changes.Builder builder = new Changes.Builder();
        AuditLog.jsonDiffToChanges(builder, json, jsonChanged);
        Changes build = builder.build();

        JsonObject jsonObject = build.asJson();
        assertEquals(jsonObject.get("shortString").getAsJsonObject().get("oldValue").getAsString(), "bee");
        assertEquals(jsonObject.get("shortString").getAsJsonObject().get("newValue").getAsString(), "wasp");
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
