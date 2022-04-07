package me.barrulik.MoltenSMP;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public static ItemStack heart;
    public static ItemStack theBook;

    public static void init(){
        createRecipes();
    }

    private static void createRecipes(){
        ItemStack heartItem = new ItemStack(Material.RED_DYE, 1);
        ItemMeta heartMeta = heartItem.getItemMeta();
        heartMeta.setDisplayName("Heart");
        List<String> heartLore = new ArrayList<>();
        heartLore.add("Gives one heart when consumed");
        heartMeta.setLore(heartLore);
        heartItem.setItemMeta(heartMeta);

        heart = heartItem;


        ShapedRecipe heart = new ShapedRecipe(NamespacedKey.minecraft("heart"), heartItem);
        heart.shape("DGD"
                , "DND"
                , "DBD");

        heart.setIngredient('D', Material.DIAMOND_BLOCK);
        heart.setIngredient('G', Material.GOLDEN_APPLE);
        heart.setIngredient('B', Material.GOLD_BLOCK);
        heart.setIngredient('N', Material.NETHERITE_INGOT);



        ItemStack bookItem = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta bookMeta = bookItem.getItemMeta();
        List<String> bookLore = new ArrayList<>();
        bookLore.add("fire aspect 3");
        bookMeta.setLore(bookLore);
        bookItem.setItemMeta(bookMeta);


        EnchantmentStorageMeta esm = (EnchantmentStorageMeta)bookItem.getItemMeta();
        esm.addStoredEnchant(Enchantment.FIRE_ASPECT, 3, false);
        bookItem.setItemMeta(esm);
        theBook = bookItem;




        Bukkit.getServer().addRecipe(heart);
    }
}