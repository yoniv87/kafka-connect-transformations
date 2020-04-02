package com.purbon.kafka.connect.converters.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.purbon.kafka.connect.converters.JsonConverterConfig;
import java.text.SimpleDateFormat;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;

public class TimestampLogicalTypeConverter implements LogicalTypeConverter {

  @Override
  public JsonNode toJson(final Schema schema, final Object value, final JsonConverterConfig config) {
    if (value instanceof String) {
      String valueAsText = String.valueOf(value);
      String pattern = "YYYYMMDDHHmmss";
      SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
      java.util.Date parsedDate = null;
      try {
        parsedDate = dateFormat.parse(valueAsText);
        return  JsonNodeFactory.instance.numberNode(Timestamp.fromLogical(schema, parsedDate));
      } catch (Exception ex){
        throw new DataException("Invalid type for Timestamp, underlying representation should be integral but was " + value+ " "+valueAsText+" "+parsedDate);
      }
    }

    if (!(value instanceof java.util.Date))
      throw new DataException("Invalid type for Timestamp, expected Date but was " + value.getClass());

    return JsonNodeFactory.instance.numberNode(Timestamp.fromLogical(schema, (java.util.Date) value));
  }

  @Override
  public Object toConnect(final Schema schema, final JsonNode value) {
    String valueAsText = value.asText();
    String pattern = "YYYYMMDDHHmmssSSSSSSS";
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    java.util.Date parsedDate = null;
    try {
      parsedDate = dateFormat.parse(valueAsText);
      java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
      return Timestamp.toLogical(schema, parsedDate.getTime());
    } catch (Exception ex){
      throw new DataException("Invalid type for Timestamp, underlying representation should be integral but was " + value.getNodeType()+ " "+valueAsText+" "+parsedDate);
    }
  }
}
