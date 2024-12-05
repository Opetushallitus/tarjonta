package fi.vm.sade.tarjonta.service.copy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.slf4j.LoggerFactory;

/**
 * @author jani
 */
public class EntityToJsonHelper {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EntityToJsonHelper.class);
  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper
        .setVisibility(
            PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY) // auto-detect all member fields
        .setVisibility(
            PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE) // but only public getters
        .setVisibility(
            PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE); // and none of "is-setters"
  }

  public static String convertToJson(Object entity) {
    Writer strWriter = new StringWriter();
    String json = null;
    try {
      mapper.writeValue(strWriter, entity);
      json = strWriter.toString();
    } catch (IOException ex) {
      throw new RuntimeException("Convert object to JSON failed", ex);
    } finally {
      try {
        strWriter.close(); // close the writer
      } catch (IOException e) {
        LOG.error("StringWriter close failed");
      }
    }
    return json;
  }

  public static Object convertToEntity(String jsonEntity, Class clazz) {
    try {
      return mapper.readValue(jsonEntity, clazz);
    } catch (IOException ex) {
      LOG.error("Convert JSON to object failed. JSON : '{}'", jsonEntity, ex);
    }
    return null;
  }
}
