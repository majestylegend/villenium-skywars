package net.villenium.skywars.utils;

import com.google.gson.*;
import net.villenium.skywars.game.Cage;
import net.villenium.skywars.game.CageManager;

import java.lang.reflect.Type;

public class CageSerializer implements JsonSerializer<Cage>, JsonDeserializer<Cage> {

    @Override
    public Cage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = "";
        if (jsonObject.has("name")) {
            name = jsonObject.get("name").getAsString();
        }
        return CageManager.getCages().get(name);
    }

    @Override
    public JsonElement serialize(Cage cage, Type type, JsonSerializationContext context) {
        JsonObject values = new JsonObject();
        if (cage == null) {
            return null;
        }
        if (cage.getName() != null) {
            values.add("name", new JsonPrimitive(cage.getName()));
        }
        return values;
    }
}