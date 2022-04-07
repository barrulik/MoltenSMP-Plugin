package me.barrulik.MoltenSMP;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Objects;

public final class MoltenSMP extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        System.out.println("MoltenSMP by Barrulik");
        ItemManager.init();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Entity killer = event.getEntity().getKiller();
        if (killer instanceof Player) {
            AttributeInstance pAtr = getGenericMaxHealth(dead);
            AttributeInstance eAtr = getGenericMaxHealth(((Player) killer).getPlayer());
            System.out.println("Player " + killer.getName() + " Killed " + dead.getName());
            pAtr.setBaseValue(pAtr.getBaseValue() - 2.0);
            if (((Player) killer).getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() < 40)
                eAtr.setBaseValue(eAtr.getBaseValue() + 2.0);
            else {
                killer.sendMessage(ChatColor.RED + "Sorry, you have reached max heart amount");
                ((Player) killer).getPlayer().playSound(dead, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1f);
            }
            if (getGenericMaxHealth(dead).getBaseValue()< 2) {
                // dead. moved to spectator
                for (Player target : getServer().getOnlinePlayers()) {
                    target.playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                }
                Bukkit.getServer().broadcastMessage(ChatColor.RED + dead.getName() + " has ran out of hearts...");
                dead.kickPlayer("Sorry, you cant join because you ran out of hearts");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() == 1)
            event.getPlayer().kickPlayer("Sorry, you cant join because you ran out of hearts");
        if (!event.getPlayer().hasPlayedBefore()) {
            getGenericMaxHealth(event.getPlayer()).setBaseValue(20);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetHearts")) {
            for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                getGenericMaxHealth(target).setBaseValue(20.0);
                target.setGameMode(GameMode.SURVIVAL);
                target.playSound(target, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
            }
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "hearts have been resetted.");
        }

        if (command.getName().equalsIgnoreCase("health")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage(ChatColor.AQUA + "Your exact health is " + getGenericMaxHealth(p).getBaseValue() + "");
                p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
        }
        if (command.getName().equalsIgnoreCase("withdrawHeart")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                AttributeInstance pGenericMaxHealth = getGenericMaxHealth(p);
                if (pGenericMaxHealth.getBaseValue()>2) {
                    pGenericMaxHealth.setBaseValue(pGenericMaxHealth.getBaseValue() - 2);
                    p.getInventory().addItem(ItemManager.heart);
                    p.sendMessage("There you go");
                    p.playSound(p, Sound.ENTITY_GHAST_HURT, 1f, 1f);
                } else {
                    p.sendMessage(ChatColor.RED + "Sorry, i cant let you kill yourself");
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1f);
                }
            }
        }

        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem() != null) {
                if (Objects.equals(event.getItem().getItemMeta(), ItemManager.heart.getItemMeta())) {
                    Player p = event.getPlayer();
                    AttributeInstance pAtr= getGenericMaxHealth(p);
                    if (pAtr.getBaseValue() < 40) {
                        pAtr.setBaseValue(pAtr.getBaseValue() + 2.0);
                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                        p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    } else {
                        p.sendMessage(ChatColor.RED + "Sorry, you have reached max heart amount");
                        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1f);
                    }
                }
            }
        }
    }
        @EventHandler
        public void InventoryClickEvent(InventoryClickEvent e)
        {
            Inventory inv = e.getInventory();
            if (inv instanceof AnvilInventory)
            {
                AnvilInventory anvil = (AnvilInventory)inv;
                InventoryView view = e.getView();
                int rawSlot = e.getRawSlot();

                if (rawSlot == view.convertSlot(rawSlot))//If inv is upper inv (Anvil inv)
                {
                    ItemStack[] items = anvil.getContents();

                    ItemStack firstSlot = items[0];
                    ItemStack secondSlot = items[1];
                    Material firstSlotType = firstSlot.getType();
                    if (firstSlotType.equals(Material.WOODEN_SWORD) || firstSlotType.equals(Material.STONE_SWORD) || firstSlotType.equals(Material.IRON_SWORD) || firstSlotType.equals(Material.GOLDEN_SWORD) || firstSlotType.equals(Material.DIAMOND_SWORD) || firstSlotType.equals(Material.NETHERITE_SWORD)){
                        if (Objects.equals(secondSlot.getItemMeta(), ItemManager.theBook.getItemMeta())){
                            ItemStack r = new ItemStack(firstSlot);
                            r.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 3);
                            anvil.setItem(2, r);
                        }
                    }
                }
            }
    }

    @EventHandler
    public void chunk(ChunkLoadEvent e){
        if (e.isNewChunk()){
            if ((int)(Math.random()*3000)!=0) return;
            int x=(int)(Math.random()*16);
            int z=(int)(Math.random()*16);
            for (int y = 120; y > 60; y--) {
                Block block = e.getChunk().getBlock(x,y,z);
                if (block.getType().name().equals("GRASS_BLOCK") || block.getType().name().equals("SAND")){
                    block.setType(Material.CHEST);
                    Chest chest = (Chest)block.getState();
                    Inventory inv = chest.getInventory();
                    inv.addItem(ItemManager.theBook);
                    return;
                }
            }
        }
    }


    public AttributeInstance getGenericMaxHealth(Player p){
        return p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    }
}

