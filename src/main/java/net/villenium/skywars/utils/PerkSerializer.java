package net.villenium.skywars.utils;

import com.google.gson.*;
import net.villenium.skywars.game.GamePerk;
import net.villenium.skywars.game.GamePerkManager;

import java.lang.reflect.Type;

public class PerkSerializer implements JsonSerializer<GamePerk>, JsonDeserializer<GamePerk> {

    @Override
    public GamePerk deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = "";
        if (jsonObject.has("name")) {
            name = jsonObject.get("name").getAsString();
        }
        return GamePerkManager.getPerk(name);
    }

    @Override
    public JsonElement serialize(GamePerk gamePerk, Type type, JsonSerializationContext context) {
        JsonObject values = new JsonObject();
        if (gamePerk == null) {
            return null;
        }
        if (gamePerk.getName() != null) {
            values.add("name", new JsonPrimitive(gamePerk.getName()));
        }
        return values;
    }
}