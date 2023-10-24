package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astral.cosmiccapital.api.CosmicCapitalAPI;
import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.types.IAccount;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.antritus.astral.cosmiccapital.api.types.IAccount.CustomAction.REMOVE;

public class ShopCMD extends AstralCommand implements Listener {

    private static final Map<Player, Integer> shopUsers = new HashMap<>();

    public ShopCMD(AstralDupe astralDupe) {
        super(astralDupe, "shop");
        astralDupe.register(this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        openShop(player, 0);
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private static void openShop(Player player, Integer page) {
        ShopPage shopPage = shop.get(page);
        Inventory inv = Bukkit.createInventory(player, 54, Utils.format(Config.getConfig().getString("economy.shop.name") + " <gray>- " + shopPage.name()));

        ItemStack blank = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        blank.editMeta(meta -> meta.displayName(Utils.format(" ")));
        for (int i = 0; i < 45; i++) inv.setItem(i, blank);

        shopPage.items().forEach((slot, shopItem) -> {
            ItemStack item = shopItem.itemStack;
            item.editMeta(meta -> {
                List<Component> lore = new ArrayList<>();
                lore.add(Utils.format("<green>Cost: <yellow>" + shopItem.price + " <green>dabloons"));
                Arrays.stream(shopItem.unparsedLore.split("<br>"))
                        .toList().forEach(str -> lore.add(MiniMessage.miniMessage().deserialize(str).decoration(TextDecoration.ITALIC, false)));
                meta.lore(lore);
            });
            inv.setItem(slot, item);
        });

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(player);
        CosmicCapitalAPI api = AstralDupe.cosmicCapital;
        IAccountManager accountManager = api.playerManager();
        IAccount playerAccount = accountManager.get(player.getUniqueId());
        if (playerAccount == null){
            player.sendRichMessage("<red>Internal Error! Couldn't load your economy account!");
            return;
        }
        skullMeta.displayName(Utils.format("<color:#11DE22>Your Balance <color:#8A8A8A>- <color:#E7D715>" + playerAccount.balance()));
        head.setItemMeta(skullMeta);

        ItemStack moreBlank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        moreBlank.editMeta(meta -> meta.displayName(Utils.format(" ")));
        for (int i = 45; i < 54; i++) inv.setItem(i, moreBlank);

        inv.setItem(49, head);

        player.openInventory(inv);
        shopUsers.put(player, page);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || !shopUsers.containsKey(e.getWhoClicked()) || e.getClickedInventory().equals(e.getWhoClicked().getInventory()))
            return;
        e.setCancelled(true);
        ShopPage shopPage = shop.get(shopUsers.get(e.getWhoClicked()));
        if (shopPage.items.containsKey(e.getSlot())) {
            ShopItem shopItem = shopPage.items.get(e.getSlot());
            CosmicCapitalAPI cosmicApi = AstralDupe.cosmicCapital;
            IAccountManager accountManager = cosmicApi.playerManager();
            IAccount account = accountManager.get(e.getWhoClicked().getUniqueId());
            if (account == null){
                e.getWhoClicked().sendRichMessage("<red>Internal Error! Couldn't load your economy account!");
                return;
            }
            if (account.balance() < shopItem.price) {
                e.getWhoClicked().sendMessage(Utils.cmdMsg("<red>You do not have enough money to buy this item"));
                return;
            }
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", "SHOP_PURCHASE");
            json.put("cost", shopItem.price);
            json.put("item", shopItem.getMaterial());
            JSONObject jsonObject = new JSONObject(json);
            account.custom(AstralDupe.economy, REMOVE, shopItem.price, jsonObject);
            ItemStack item = new ItemStack(shopItem.material);
            if (shopItem.undupable) {
                item.editMeta(meta -> meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true));
            }
            e.getWhoClicked().getInventory().addItem(item);
            e.getWhoClicked().sendMessage(Utils.cmdMsg("<green>You have successfully purchased this item"));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!shopUsers.containsKey(e.getPlayer())) return;
        shopUsers.remove(e.getPlayer());
    }

    public static Map<Integer, ShopPage> shop = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void reloadShop() {
        shop.clear();
        List<Map<String, ?>> pages = (List<Map<String, ?>>) Config.getConfig().getList("economy.shop.pages");
        AtomicInteger i = new AtomicInteger(0);
        assert pages != null;
        pages.forEach(page -> {
            Map<Integer, ShopItem> items = new HashMap<>();
            List<Map<String, ?>> itemsList = (List<Map<String, ?>>) page.get("items");
            AtomicInteger i2 = new AtomicInteger(0);
            itemsList.forEach(item -> {
                ItemStack itemStack = new ItemStack(Material.valueOf(((String) item.get("material")).toUpperCase()));
                itemStack.editMeta(meta -> {
                    meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
                    meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                    String name = (String) item.get("material");
                    name = name.replace("_", " ");
                    name = firstLetterCapitalWithSingleSpace(name);
                    meta.displayName(Utils.format("<yellow>" + name));
                });
                ShopItem shopItem = new ShopItem(Material.valueOf(((String) item.get("material")).toUpperCase()), itemStack, (Integer) item.get("cost"), (String) item.get("lore"));
                if (item.get("undupable") != null && (boolean) item.get(("undupable"))) {
                    shopItem.undupable = true;
                }
                items.put(i2.getAndIncrement(), shopItem);
            });
            shop.put(i.getAndIncrement(), new ShopPage((String) page.get("name"), items));
        });
    }

    private static String firstLetterCapitalWithSingleSpace(final String words) {
        return Stream.of(words.trim().split("\\s"))
                .filter(word -> word.length() > 0)
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public record ShopPage(String name, Map<Integer, ShopItem> items) {

    }

    public static class ShopItem {

        private final ItemStack itemStack;
        private final int price;
        private final String unparsedLore;
        private final Material material;
        public boolean undupable;

        public ShopItem(Material mat, ItemStack itemStack, int price, String unparsedLore) {
            this.itemStack = itemStack;
            this.price = price;
            this.unparsedLore = unparsedLore;
            this.material = mat;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public int getPrice() {
            return price;
        }

        public String getUnparsedLore() {
            return unparsedLore;
        }

        public Material getMaterial() {
            return material;
        }
    }
}
