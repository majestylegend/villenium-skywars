package net.villenium.skywars.utils;

import com.google.gson.*;
import net.villenium.skywars.enums.Rarity;
import net.villenium.skywars.game.GameClass;
import net.villenium.skywars.game.GameClassManager;
import net.villenium.skywars.game.GamePerk;
import net.villenium.skywars.game.GamePerkManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Type;
import java.util.*;

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