package net.villenium.skywars.handler;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.game.ChestsManager;
import net.villenium.skywars.game.GameClass;
import net.villenium.skywars.game.GameTeam;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.AlgoUtil;
import net.villenium.skywars.utils.Task;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import net.villenium.skywars.utils.simple.SimplePotionEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.lang.ref.WeakReference;
import java.util.*;

public class GameHandler implements Listener {
    private static final ItemStack arrow;
    private static final ItemStack lapis;

    static {
        arrow = new ItemStack(Material.ARROW, 1);
        Dye dye = new Dye();
        dye.setColor(DyeColor.BLUE);
        lapis = dye.toItemStack(64);
    }

    private static void kick(Player p, String cause) {
        GamePlayer gp = GamePlayer.wrap(p);
        p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", cause));
        gp.moveToShard(Shard.getRandomLobby());
    }

    public static void onJoin(Player p) {
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
        if (game != null) {
            if (game.getPlayers().size() >= game.getPlayersMaximumAllowed()) {
                kick(p, "&cСервер полон!");
            } else if (game.getGamePhase() != GamePhase.WAITING && !GameApi.getUserManager().get(p).getPermission().isModerator()) {
                kick(p, "&cИгра уже началась!");
            }
        }

        if (game != null) {
            if (game.getGamePhase() == GamePhase.WAITING) {
                game.toWaiting(p);
            }

            if (game.getGamePhase() == GamePhase.RELOADING) {
                kick(p, "&cИгра уже закончилась!");
                return;
            }

            if (game.getGamePhase() != GamePhase.WAITING) {
                VScoreboard.setupGameScoreboard(GamePlayer.wrap(p));
                p.setGameMode(GameMode.SPECTATOR);
                return;
            }
            p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&eОжидание завершится через &a%s с", game.getTimer().getTime()));
        }

        p.setGameMode(GameMode.SURVIVAL);
    }

    public static void onQuit(Player p) {
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        GamePlayer gp = GamePlayer.wrap(p);

        try {
            GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
            if (game != null) {
                game.getTimer().getBar().removePlayer(p);

                if (gp.getTeam() != null) {
                    gp.getTeam().quit(p);
                    if (game.getTeams().getTeamsLeft() <= 1) {
                        game.endTheGame();
                    }
                }
                gp.resetGamePlayer();
            }
        } catch (Throwable throwable) {
            throw throwable;
        }
    }

    @EventHandler
    public void AsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        if (e.getMessage().equalsIgnoreCase("gg") || e.getMessage().equalsIgnoreCase("гг")) {
            GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
            if (game != null) {
                Task.schedule(() -> {
                    game.gg(p);
                });
            }
        }

    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        onQuit(e.getPlayer());
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onQuit(PlayerQuitEvent e) {
        onQuit(e.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!(GamePlayer.wrap(e.getPlayer()).getShard() instanceof GameShard)) return;
        GameShard game = (GameShard) GamePlayer.wrap(e.getPlayer()).getShard();
        if (game != null && game.getGamePhase() != GamePhase.INGAME) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
        if (game != null) {
            if (game.getGamePhase() != GamePhase.INGAME && !p.isOp() && (!e.hasItem() || e.getItem().getType() != Material.IRON_SWORD && e.getItem().getType() != Material.MAGMA_CREAM)) {
                e.setCancelled(true);
            } else if (p.getGameMode() == GameMode.SPECTATOR) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!(GamePlayer.wrap((Player) e.getEntity()).getShard() instanceof GameShard)) return;
            GameShard game = (GameShard) GamePlayer.wrap((Player) e.getEntity()).getShard();
            if (game == null) {
                e.setCancelled(true);
            } else {
                if (game.getGamePhase() == GamePhase.INGAME && System.currentTimeMillis() - game.getGameStarted() >= 10000L) {
                    if (e.getEntity() instanceof Player && ((Player) e.getEntity()).getGameMode() == GameMode.SPECTATOR) {
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                }

            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!(GamePlayer.wrap(e.getPlayer()).getShard() instanceof GameShard)) return;
        GameShard game = (GameShard) GamePlayer.wrap(e.getPlayer()).getShard();
        if (game == null || game.getGamePhase() != GamePhase.INGAME) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (!(GamePlayer.wrap(e.getPlayer()).getShard() instanceof GameShard)) return;
        GameShard game = (GameShard) GamePlayer.wrap(e.getPlayer()).getShard();
        if (game == null || game.getGamePhase() != GamePhase.INGAME) {
            e.setCancelled(true);
            e.getItem().remove();
        }

    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != SpawnReason.SPAWNER_EGG && e.getSpawnReason() != SpawnReason.CUSTOM) {
            e.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onInteractHighest(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        if (GamePlayer.wrap(p).getDoomedUntil() > System.currentTimeMillis()) {
            e.setCancelled(true);
        } else {
            if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.hasItem()) {
                ItemStack is = e.getItem();
                Location spawnLocation;
                EntityEquipment eq;
                int amount;
                if (is.getType() == Material.MONSTER_EGG && is.getDurability() == 51) {
                    e.setCancelled(true);
                    spawnLocation = e.getAction() == Action.RIGHT_CLICK_AIR ? p.getLocation() : e.getClickedBlock().getRelative(BlockFace.UP).getLocation();
                    Skeleton skeleton = spawnLocation.getWorld().spawn(spawnLocation, Skeleton.class);
                    skeleton.setMetadata("owner", new FixedMetadataValue(SkyWars.getInstance(), new WeakReference(p)));
                    skeleton.setCanPickupItems(false);
                    skeleton.setCustomNameVisible(true);
                    skeleton.setCustomName("Прислужник " + p.getName());
                    eq = skeleton.getEquipment();
                    eq.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
                    eq.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
                    eq.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
                    eq.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
                    eq.setHelmetDropChance(0.0F);
                    eq.setChestplateDropChance(0.0F);
                    eq.setLeggingsDropChance(0.0F);
                    eq.setBootsDropChance(0.0F);
                    eq.setItemInMainHandDropChance(0.0F);
                    if (is.getItemMeta().getDisplayName().contains("палача")) {
                        skeleton.setSkeletonType(SkeletonType.WITHER);
                        eq.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD, 1));
                    } else {
                        skeleton.setSkeletonType(SkeletonType.NORMAL);
                        eq.setItemInMainHand(new SimpleItemStack(Material.BOW, "Лук скелета", Enchantment.ARROW_DAMAGE, 1));
                    }

                    amount = is.getAmount();
                    if (amount == 1) {
                        p.getInventory().setItemInMainHand(null);
                    } else {
                        --amount;
                        is.setAmount(amount);
                        p.getInventory().setItemInMainHand(is);
                    }
                } else if (is.getType() == Material.MONSTER_EGG && is.getDurability() == 54) {
                    e.setCancelled(true);
                    spawnLocation = e.getAction() == Action.RIGHT_CLICK_AIR ? p.getLocation() : e.getClickedBlock().getRelative(BlockFace.UP).getLocation();
                    Zombie zombie = spawnLocation.getWorld().spawn(spawnLocation, Zombie.class);
                    zombie.setMetadata("owner", new FixedMetadataValue(SkyWars.getInstance(), new WeakReference(p)));
                    zombie.setCanPickupItems(false);
                    zombie.setCustomNameVisible(true);
                    zombie.setCustomName("Прислужник " + p.getName());
                    zombie.setBaby(true);
                    zombie.setVillager(false);
                    zombie.addPotionEffect(new SimplePotionEffect(PotionEffectType.SPEED, 1));
                    eq = zombie.getEquipment();
                    eq.setHelmetDropChance(0.0F);
                    eq.setChestplateDropChance(0.0F);
                    eq.setLeggingsDropChance(0.0F);
                    eq.setBootsDropChance(0.0F);
                    eq.setItemInMainHandDropChance(0.0F);
                    if (is.getItemMeta().getDisplayName().contains("зловещего")) {
                        zombie.setHealth(20.0D);
                        zombie.setMaxHealth(20.0D);
                        eq.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
                        eq.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
                        eq.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
                        eq.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
                        eq.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD, 1));
                    } else {
                        zombie.setHealth(10.0D);
                        zombie.setMaxHealth(10.0D);
                        eq.setItemInMainHand(new ItemStack(Material.IRON_SWORD, 1));
                    }

                    amount = is.getAmount();
                    if (amount == 1) {
                        p.getInventory().setItemInMainHand(null);
                    } else {
                        --amount;
                        is.setAmount(amount);
                        p.getInventory().setItemInMainHand(is);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getEntity().hasMetadata("owner")) {
            WeakReference<Player> reference = (WeakReference) e.getEntity().getMetadata("owner").get(0).value();
            if (reference.get() != null && reference.get() == e.getTarget()) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        GamePlayer gp = GamePlayer.wrap(p);
        if (gp.hasPerk("Instant_Smelting")) {
            Inventory top = e.getView().getTopInventory();
            if (top.getType() == InventoryType.FURNACE) {
                Furnace f = (Furnace) top.getHolder();
                int slot = e.getRawSlot();
                if (slot == 0 || slot == 1 || slot == 2) {
                    ItemStack out = top.getItem(2);
                    Task.schedule(() -> {
                        f.setCookTime((short) 200);
                    }, 2L);
                    Task.schedule(() -> {
                        ItemStack newout = top.getItem(2);
                        ItemStack from = top.getItem(0);
                        if (newout != null && from != null) {
                            if (out == null) {
                                newout.setAmount(1 + from.getAmount());
                                top.setItem(0, null);
                            } else {
                                if (newout.getType() != out.getType()) {
                                    return;
                                }

                                int delta = from.getAmount();
                                int amount = newout.getAmount() + delta;
                                if (amount > 64) {
                                    delta -= amount - 64;
                                    amount = 64;
                                }

                                newout.setAmount(amount);
                                if (delta == from.getAmount()) {
                                    top.setItem(0, null);
                                } else {
                                    from.setAmount(from.getAmount() - delta);
                                }
                            }

                        }
                    }, 4L);
                }
            }

        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onInteractMonitor(PlayerInteractEvent e) {
        if (!(GamePlayer.wrap(e.getPlayer()).getShard() instanceof GameShard)) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                ChestsManager.checkAndFillChest((Chest) block.getState(), e.getPlayer());
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
            GamePlayer gp = GamePlayer.wrap(p);
            Player damager = null;
            boolean ranged = false;
            boolean useIncreasedDamage = false;
            if (e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();
                useIncreasedDamage = true;
            } else if (e.getDamager() instanceof Projectile) {
                Projectile pj = (Projectile) e.getDamager();

                if (pj.getShooter() instanceof Player) {
                    damager = (Player) pj.getShooter();
                }

                if (pj instanceof EnderPearl) {
                    double perkModifier = (double) gp.getPerkModifier("Ender_Mastery") * 0.01D;
                    e.setDamage(e.getDamage() * (1.0D - perkModifier));
                } else if (pj instanceof Arrow) {
                    ranged = true;
                    useIncreasedDamage = true;
                }
            }

            if (damager != null) {
                GamePlayer gd = GamePlayer.wrap(damager);
                if (gp.getTeam() == gd.getTeam() || gd.getDoomedUntil() > System.currentTimeMillis()) {
                    e.setCancelled(true);
                    return;
                }

                if (useIncreasedDamage) {
                    e.setDamage(e.getDamage() + gd.getIncreasedDamage());
                }

                float damageModifier = gd.getWraithInfometer() == null ? 1.0F : gd.getWraithInfometer().getIncreasedOutcomingDamage();
                e.setDamage(e.getDamage() * (double) damageModifier);
                damageModifier = gp.getWraithInfometer() == null ? 1.0F : gp.getWraithInfometer().getIncreasedIncomingDamage();
                e.setDamage(e.getDamage() * (double) damageModifier);
                gp.addLastDamager(damager);
                if (ranged) {
                    double left = p.getHealth() - e.getFinalDamage();
                    if (left > 0.5D) {
                        damager.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы попали в %s&a! У него осталось &c%.1f &aсердец здоровья.", GameApi.getUserManager().get(p).getFullDisplayName(), left / 2.0D));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            Entity tnt = event.getPlayer().getWorld().spawn(event.getBlock().getLocation().add(0.5D, 0.25D, 0.5D), TNTPrimed.class);
            ((TNTPrimed) tnt).setFuseTicks(30);
        }

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, lapis);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, null);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        p.getWorld().strikeLightningEffect(p.getLocation());
        p.setHealth(p.getMaxHealth());
        p.setGameMode(GameMode.SPECTATOR);
        e.setDeathMessage(null);
        Task.schedule(() -> {
            if (p.isOnline()) {
                GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
                if (game != null) {
                    GamePlayer gp = GamePlayer.wrap(p);
                    if (gp.getTeam() != null) {
                        p.teleport(gp.getTeam().getSpawns().get(new Random().nextInt(gp.getTeam().getSpawns().size())));
                        Player lastDamager = gp.getKiller();
                        if (lastDamager != null && lastDamager.isOnline()) {
                            GamePlayer killer = GamePlayer.wrap(lastDamager);
                            int amount = 20;
                            GameTeam killerTeam = killer.getTeam();
                            if (killerTeam != null) {
                                amount /= killerTeam.getPlayers().size();
                            }

                            killer.addSoloKill((int) ((double) amount * 1.5D));
                            Iterator var8 = killerTeam.getPlayers().iterator();

                            while (var8.hasNext()) {
                                Player pt = (Player) var8.next();
                                if (pt != lastDamager) {
                                    GamePlayer.wrap(pt).addJustCoins(amount);
                                }
                            }

                            gp.getAssistants().stream().map(GamePlayer::wrap).forEach(GamePlayer::addAssist);
                            VScoreboard.updateKills(GamePlayer.wrap(lastDamager));
                            int perkModifier = killer.getPerkModifier("Bulldozer");
                            lastDamager.addPotionEffect(new SimplePotionEffect(PotionEffectType.INCREASE_DAMAGE, 1, perkModifier));
                            perkModifier = killer.getPerkModifier("Juggernaut");
                            lastDamager.addPotionEffect(new SimplePotionEffect(PotionEffectType.REGENERATION, 1, perkModifier));
                            TextComponent prefix = ChatUtil.makeTextComponent(ChatUtil.prefixed("&6&lSkyWars", ""));
                            TextComponent first = this.makeClassComponent(lastDamager);
                            TextComponent between = ChatUtil.makeTextComponent(" &cубил ");
                            TextComponent second = this.makeClassComponent(p);
                            TextComponent after = ChatUtil.makeTextComponent("&c!");
                            prefix.addExtra(first);
                            prefix.addExtra(between);
                            prefix.addExtra(second);
                            prefix.addExtra(after);
                            game.getPlayers().forEach((pl) -> {
                                pl.spigot().sendMessage(prefix);
                            });
                            int level;
                            GameClass clazz = killer.getSelectedClass();
                            level = killer.getClassLevel(clazz);
                            String var16 = clazz.getName();
                            byte var17 = -1;
                            switch (var16.hashCode()) {
                                case -2080094781:
                                    if (var16.equals("DesperationCollector")) {
                                        var17 = 0;
                                    }
                                    break;
                                case 1124339766:
                                    if (var16.equals("WraithCollector")) {
                                        var17 = 1;
                                    }
                            }

                            switch (var17) {
                                case 0:
                                    lastDamager.setMaxHealth(lastDamager.getMaxHealth() + (double) level);
                                    if (level == 5) {
                                        lastDamager.setHealth(lastDamager.getMaxHealth());
                                    }
                                    break;
                                case 1:
                                    double increase = 0.0D;
                                    switch (level) {
                                        case 1:
                                            increase = 1.0D;
                                            break;
                                        case 2:
                                            increase = 1.5D;
                                            break;
                                        case 3:
                                            increase = 2.0D;
                                            break;
                                        case 4:
                                            increase = 3.0D;
                                            break;
                                        case 5:
                                            increase = 4.0D;
                                    }

                                    killer.setIncreasedDamage(killer.getIncreasedDamage() + increase);
                            }
                        } else {
                            TextComponent prefixx = ChatUtil.makeTextComponent(ChatUtil.prefixed("&6&lSkyWars", ""));
                            TextComponent only = this.makeClassComponent(p);
                            TextComponent suffix = ChatUtil.makeTextComponent(" &cпогиб!");
                            prefixx.addExtra(only);
                            prefixx.addExtra(suffix);
                            game.getPlayers().forEach((pl) -> {
                                pl.spigot().sendMessage(prefixx);
                            });
                        }

                        Collection<GameTeam> teams = new HashSet();
                        teams.addAll(game.getTeams().getTeams());
                        teams.forEach((team) -> {
                            team.remove(p);
                        });
                        game.getPlayers().forEach((player -> {
                            if(player != null)
                                VScoreboard.updateTeamsLeft(GamePlayer.wrap(player));
                        }));
                        if (game.getTeams().getTeamsLeft() <= 1) {
                            game.endTheGame();
                        }

                        p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&7Останьтесь до завершения игры, чтобы получить опыт и немного серебра (за написание gg)."));
                        ChatUtil.sendClickableMessage(p, "&a&lНачать новую игру?", Arrays.asList("&f*сюда можно нажать*"), "/replay " + game.getGameType().toString());
                    }
                }
            }
        }, 2L);
    }

    private TextComponent makeClassComponent(Player player) {
        TextComponent component = ChatUtil.makeTextComponent(GameApi.getUserManager().get(player).getDisplayName());
        GamePlayer gp = GamePlayer.wrap(player);
        GameClass clazz = gp.getSelectedClass();
        String className = clazz == null ? "&8скрыт" : String.format("%s (уровень %d)", clazz.getVisibleName(), gp.getClassLevel(clazz));
        component.setHoverEvent(ChatUtil.makeHoverEvent(Lists.newArrayList("&7Выбранный игроком класс:", "&a" + className)));
        return component;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(GamePlayer.wrap((Player) e.getEntity()).getShard() instanceof GameShard)) return;
        GameShard game = (GameShard) GamePlayer.wrap((Player) e.getEntity()).getShard();
        if (game == null || game.getGamePhase() != GamePhase.INGAME) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile pj = e.getEntity();
        if (pj instanceof Arrow && pj.getShooter() instanceof Player) {
            Player shooter = (Player) pj.getShooter();
            if (!(GamePlayer.wrap(shooter).getShard() instanceof GameShard)) return;
            GamePlayer gp = GamePlayer.wrap(shooter);
            int perkModifier = gp.getPerkModifier("Arrow_Recovery");
            if (AlgoUtil.r(100) < perkModifier) {
                shooter.getInventory().addItem(arrow);
            }

            perkModifier = gp.getPerkModifier("Blazing_Arrows");
            if (AlgoUtil.r(100) < perkModifier) {
                pj.setFireTicks(Integer.MAX_VALUE);
            }

        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!(GamePlayer.wrap(e.getPlayer()).getShard() instanceof GameShard)) return;
        if (e.getTo().getY() <= 1) {
            e.getPlayer().damage(100);
        }
        if (((GameShard) GamePlayer.wrap(e.getPlayer()).getShard()).isDeathmatchLock()) e.setCancelled(true);
    }
}