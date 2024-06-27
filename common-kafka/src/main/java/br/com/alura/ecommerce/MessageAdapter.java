package br.com.alura.ecommerce;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("type", message.getPayload().getClass().getName());
        json.add("correlationId", context.serialize(message.getId()));
        json.add("payload", context.serialize(message.getPayload()));
        return json;
    }

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        var json = jsonElement.getAsJsonObject();
        var payloadType = json.get("type").getAsString();
        var correlationId = (CorrelationId) context.deserialize(json.get("correlationId"), CorrelationId.class);
        try {
            var payload = context.deserialize(json.get("payload"), Class.forName(payloadType));
            return new Message(correlationId, payload);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
