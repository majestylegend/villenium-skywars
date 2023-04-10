package net.villenium.skywars.utils;

import com.google.gson.*;
import net.villenium.skywars.game.GameClass;
import net.villenium.skywars.game.GameClassManager;

import java.lang.reflect.Type;

public class ClassSerializer implements JsonSerializer<GameClass>, JsonDeserializer<GameClass> {

    @Override
    public GameClass deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = "";
        if (jsonObject.has("name")) {
            name = jsonObject.get("name").getAsString();
        }
        return GameClassManager.getGameClass(name);
    }

    @Override
    public JsonElement serialize(GameClass gameClass, Type type, JsonSerializationContext context) {
        JsonObject values = new JsonObject();
        if (gameClass == null) {
            return null;
        }
        if (gameClass.getName() != null) {
            values.add("name", new JsonPrimitive(gameClass.getName()));
        }
        return values;
    }
}