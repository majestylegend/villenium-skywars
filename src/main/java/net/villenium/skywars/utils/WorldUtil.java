package net.villenium.skywars.utils;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import net.villenium.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

public class WorldUtil {

    public static void unloadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }

        try {
            if (SkyWars.getInstance().getSlimeLoader().worldExists(worldName)) {
                Bukkit.unloadWorld(world, false);
                SkyWars.getInstance().getSlimeLoader().deleteWorld(worldName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UnknownWorldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadWorld(String worldName) {

        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            unloadWorld(world.getName());
        }

        SlimePropertyMap map = new SlimePropertyMap();
        map.setString(SlimeProperties.DIFFICULTY, "EASY");
        map.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        map.setBoolean(SlimeProperties.ALLOW_MONSTERS, true);
        map.setBoolean(SlimeProperties.PVP, true);
        map.setInt(SlimeProperties.SPAWN_X, 0);
        map.setInt(SlimeProperties.SPAWN_Y, 79);
        map.setInt(SlimeProperties.SPAWN_Z, -6);


        SlimeWorld slimeWorld = null;
        try {
            slimeWorld = SkyWars.getInstance().getSlimePlugin().loadWorld(SkyWars.getInstance().getSlimeLoader(), worldName, true, map);
        } catch (UnknownWorldException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CorruptedWorldException e) {
            throw new RuntimeException(e);
        } catch (NewerFormatException e) {
            throw new RuntimeException(e);
        } catch (WorldInUseException e) {
            throw new RuntimeException(e);
        }
        SkyWars.getInstance().getSlimePlugin().generateWorld(slimeWorld);

    }

    public static void cloneWorld(String worldName, String newWorld) {

        File worldFile = new File(new File("slime_worlds/"), worldName + ".slime");

        FileUtils.copyFiles(worldFile, new File(new File("slime_worlds/"), newWorld + ".slime"));

        loadWorld(newWorld);
    }
}
