package net.villenium.skywars.utils;

import com.google.gson.*;
import net.villenium.skywars.enums.Rarity;
import net.villenium.skywars.game.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Type;
import java.util.*;

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