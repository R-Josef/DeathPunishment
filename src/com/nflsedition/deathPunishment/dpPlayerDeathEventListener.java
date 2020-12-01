package com.nflsedition.deathPunishment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class dpPlayerDeathEventListener implements Listener{

    private final Plugin dp;

    public dpPlayerDeathEventListener(Plugin plugin) {
        this.dp = plugin; // Store the plugin in situations where you need it.
    }
    
	@EventHandler(priority = EventPriority.LOW)
	
	public void onPlayerDeath(PlayerDeathEvent event){
		
		if (dp.getConfig().contains("Enabled")) {
			
			boolean gamemode = true;
			
			Player player = event.getEntity();
			Location location = player.getLocation();
			Inventory inventory = player.getInventory();
			String worldname = location.getWorld().getName();
			String worlddir = "";
			String locale = "zh_cn";
			if ((dp.getConfig().getBoolean("Locale"))&&(!dp.getConfig().getBoolean("LowVersionMode.Enabled"))) {
				locale = player.getLocale();
			}
			
			Map<String, String> messagehash = new HashMap<>();
			messagehash.put("death", dp.getConfig().getString("Messages.death"));
			messagehash.put("deathworld", dp.getConfig().getString("Messages.deathworld"));
			messagehash.put("drop", dp.getConfig().getString("Messages.drop"));
			messagehash.put("exp", dp.getConfig().getString("Messages.exp"));
			messagehash.put("title", dp.getConfig().getString("Messages.title"));
			messagehash.put("subtitle", dp.getConfig().getString("Messages.subtitle"));
			messagehash.put("money", dp.getConfig().getString("Messages.money"));
			messagehash.put("loot", dp.getConfig().getString("Messages.loot"));
			messagehash.put("lootname", dp.getConfig().getString("Messages.lootname"));
			
			for (Map.Entry<String, String> entry: messagehash.entrySet()) {
				messagehash.put(entry.getKey(), dpFile.getLocaleMessage(entry.getKey(), locale, entry.getValue()));
			}
			
			//记录死亡世界
			DeathPunishment.loghash.put(player.getName(), worldname);
			
			//掉落物品
			
			//boolean Slots[] = new boolean[41];
			String namestring = "";
			List<Integer> Slots= new ArrayList<>();
			int namestringlns = 0,droppedcount = 0,droppingslot = 0,ran1 = 1;
			
			for(int i=0;i<player.getInventory().getSize();i++) {
				Slots.add(i);
			}
			
			if (dp.getConfig().getBoolean("Enabled")) {  //检查是否启用
				if (dp.getConfig().getBoolean("SurvivalOnly")) {  //检查是否仅限生存
					if (player.getGameMode().equals(GameMode.CREATIVE)) {
						gamemode = false;
					}
				}
				
				if (gamemode) {
					player.sendMessage(messagehash.get("death"));
					player.sendMessage(String.format(messagehash.get("deathworld"),worldname));
					
					if (dp.getConfig().contains(worldname))
						worlddir = worldname+".";
					
					//掉落金钱
					
					if ((dp.getConfig().getBoolean(worlddir+"DropMoney.Enabled"))&&(DeathPunishment.economyEnabled)){
						String mode = dp.getConfig().getString(worlddir+"DropMoney.Mode");
						Random random = new Random();
						if (mode != null) {
							double amount = 0.0;
							switch (mode) {
								case "Fixed": {
									double money = dp.getConfig().getDouble(worlddir+"DropMoney.Fixed.Money");
									OfflinePlayer offlinePlayer = (OfflinePlayer)player;
									if (DeathPunishment.economy.hasAccount(offlinePlayer)){
										if (DeathPunishment.economy.getBalance(offlinePlayer)>=money) {
											DeathPunishment.economy.withdrawPlayer(offlinePlayer, money);
											amount = money;
										}else {
											DeathPunishment.economy.withdrawPlayer(offlinePlayer, DeathPunishment.economy.getBalance(offlinePlayer));
											amount = DeathPunishment.economy.getBalance(offlinePlayer);
										}
									}else {
										DeathPunishment.economy.createPlayerAccount(offlinePlayer);
									}
									break;
								}
								case "Random": {
									double moneymin = dp.getConfig().getDouble(worlddir+"DropMoney.Random.MoneyMin");
									double moneymax = dp.getConfig().getDouble(worlddir+"DropMoney.Random.MoneyMax");
									int digit = dp.getConfig().getInt(worlddir+"DropMoney.Random.DigitAfterDot");
									double moneyraw = random.nextDouble()*(moneymax-moneymin)+moneymin;
									BigDecimal bigDecimal = new BigDecimal(moneyraw);
									double money = bigDecimal.setScale(digit,BigDecimal.ROUND_HALF_UP).doubleValue();
									OfflinePlayer offlinePlayer = (OfflinePlayer)player;
									if (DeathPunishment.economy.hasAccount(offlinePlayer)){
										if (DeathPunishment.economy.getBalance(offlinePlayer)>=money) {
											DeathPunishment.economy.withdrawPlayer(offlinePlayer, money);
											amount = money;
										}else {
											DeathPunishment.economy.withdrawPlayer(offlinePlayer, DeathPunishment.economy.getBalance(offlinePlayer));
											amount = DeathPunishment.economy.getBalance(offlinePlayer);
										}
									}else {
										DeathPunishment.economy.createPlayerAccount(offlinePlayer);
									}
									break;
								}
								case "Rate": {
									double moneyrate = dp.getConfig().getDouble(worlddir+"DropMoney.Rate.MoneyRate");
									int digit = dp.getConfig().getInt(worlddir+"DropMoney.Random.DigitAfterDot");
									OfflinePlayer offlinePlayer = (OfflinePlayer)player;
									double moneyraw = DeathPunishment.economy.getBalance(offlinePlayer)*moneyrate;
									BigDecimal bigDecimal = new BigDecimal(moneyraw);
									double money = bigDecimal.setScale(digit,BigDecimal.ROUND_HALF_UP).doubleValue();
									if (DeathPunishment.economy.hasAccount(offlinePlayer)){
										if (DeathPunishment.economy.getBalance(offlinePlayer)>=money) {
											DeathPunishment.economy.withdrawPlayer(offlinePlayer, money);
											amount = money;
										}else {
											DeathPunishment.economy.withdrawPlayer(offlinePlayer, DeathPunishment.economy.getBalance(offlinePlayer));
											amount = DeathPunishment.economy.getBalance(offlinePlayer);
										}
									}else {
										DeathPunishment.economy.createPlayerAccount(offlinePlayer);
									}
									break;
								}
								default: dp.getLogger().info("金钱掉落模式配置错误！已取消掉落");break;
							}
							player.sendMessage(String.format(messagehash.get("money"), ""+amount));
							if (dp.getConfig().getDouble(worlddir+"DropMoney.Loot")>0.0){
								double loot = dp.getConfig().getDouble(worlddir+"DropMoney.Loot");
								int digit = dp.getConfig().getInt(worlddir+"DropMoney.DigitAfterDot");
								BigDecimal lootbd = new BigDecimal(amount*loot);
								double lootfinal = lootbd.setScale(digit,BigDecimal.ROUND_HALF_UP).doubleValue();
								if (lootfinal>0.0) {
									Material looticon = Material.getMaterial(dp.getConfig().getString(worlddir+"DropMoney.LootIcon"));
									ItemStack lootitem = new ItemStack(looticon, (int)Math.floor(amount));
									ItemMeta meta = lootitem.getItemMeta();
									meta.setDisplayName(""+amount);
									List<String> lore = new ArrayList<>();
									lore.add("§c金钱掉落");
									lore.add(""+lootfinal);
									meta.setLore(lore);
									meta.setDisplayName(""+lootfinal);
									lootitem.setItemMeta(meta);
									Item itemdrop = location.getWorld().dropItem(location, lootitem);
									itemdrop.setCustomNameVisible(true);
									itemdrop.setCustomName(String.format(messagehash.get("lootname"), ""+lootfinal));
									player.sendMessage(String.format(messagehash.get("loot"), ""+lootfinal));
								}
							}
						}else {
							dp.getLogger().info("读取金钱掉落配置时发生错误！请删除配置文件重新生成！");
						}
					}
					
					//掉落物品
					
					int count = dp.getConfig().getInt(worlddir+"DropSlots.Count");
					List<?> protectedslots = dp.getConfig().getList(worlddir+"DropSlots.ProtectedSlots");
					List<?> protecteditems = dp.getConfig().getList(worlddir+"DropSlots.ProtectedItems");
					List<?> protectedlores = dp.getConfig().getList(worlddir+"DropSlots.ProtectedLores");
					List<?> protectedenchantments = dp.getConfig().getList(worlddir+"DropSlots.ProtectedEnchantments");
					
					if ((dp.getConfig().contains(worlddir+"DropSlots.Enabled"))&&(dp.getConfig().getBoolean(worlddir+"DropSlots.Enabled"))) {
						if (count!=0) {
							Random r = new Random();
							for(int i=0;((i<count)&&(!(Slots.isEmpty())));) {
								if (Slots.size()>1){
									ran1 = r.nextInt(Slots.size()-1);
								}else {
									ran1 = 0;
								}
								if (!(protectedslots.size()+count>41)) {
									
									droppingslot = Slots.get(ran1);
									
									if (inventory.getItem(droppingslot)!=null) {
										boolean anyProtected = false;
										if (protecteditems.contains(inventory.getItem(droppingslot).getType().toString())) {
											anyProtected = true;
										}
											
										//Lore保护
											
											
										for (Object lore : protectedlores) {
											String string = lore.toString();
											if (lore.toString().contains("%s")) {
												string = String.format(string, player.getName());
											}
											if (inventory.getItem(droppingslot).getItemMeta().hasLore()) {
												if (inventory.getItem(droppingslot).getItemMeta().getLore().contains(string)) {
													anyProtected = true;
												}
											}
										}
										
										//附魔保护
										
										for (Object enchantment: protectedenchantments) {
											Enchantment ench = Enchantment.getByName((String) enchantment);
											if (inventory.getItem(droppingslot).getEnchantments().containsKey(ench)) {
												anyProtected = true;
											}
										}
										
										if (!(anyProtected)){
											location.getWorld().dropItem(location, inventory.getItem(droppingslot));
											droppedcount++;
											if (inventory.getItem(droppingslot).getItemMeta().hasDisplayName()){
												namestring += inventory.getItem(droppingslot).getItemMeta().getDisplayName()+"§c*"+inventory.getItem(droppingslot).getAmount()+", §f";
												if ((namestring.length()-namestringlns*10)>=10) {
													namestring += "\\n";
													namestringlns++;
												}
											}else {
												namestring += dpFile.getLocaleName(inventory.getItem(droppingslot).getType(),locale)+"§c*"+inventory.getItem(droppingslot).getAmount()+", §f";
												if ((namestring.length()-namestringlns*10)>=10) {
													namestring += "\\n";
													namestringlns++;
												}
											}
											inventory.clear(droppingslot);
											i++;
										}
									}
									Slots.remove(ran1);
								}else {
									Bukkit.getServer().getLogger().info("未写保护的物品格数量不够！已取消掉落！");
									Bukkit.getServer().getLogger().info("这个孩子不会算数呢……");
								}
							}
							if (!dp.getConfig().getBoolean("LowVersionMode.Enabled")) {
								player.sendTitle(messagehash.get("title"), messagehash.get("subtitle"), dp.getConfig().getInt("Messages.fadein"), dp.getConfig().getInt("Messages.stay"), dp.getConfig().getInt("Messages.fadeout"));
							}else {
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" title "+dp.getConfig().getString("LowVersionMode.Title"));
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" subtitle "+dp.getConfig().getString("LowVersionMode.Subtitle"));
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" times "+dp.getConfig().getInt("Messages.fadein")+" "+dp.getConfig().getInt("Messages.stay")+" "+dp.getConfig().getInt("Messages.fadeout"));
							}
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+player.getName()+" [{\"text\":\""+String.format(messagehash.get("drop"),droppedcount)+"\",\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""+namestring+"\"},\"color\":\"red\",\"underlined\":\"true\"}]");
							player.updateInventory();
						}
					}
					
					//掉落经验
					
					if (dp.getConfig().getBoolean(worlddir+"DropExp.Enabled")){
						
						String mode = dp.getConfig().getString(worlddir+"DropExp.Mode");
						int droppedlevel = 0;
						
						switch (mode) {
						case "Fixed":{
							int level = dp.getConfig().getInt(worlddir+"DropExp.Fixed.Level");
							boolean spawnorb = dp.getConfig().getBoolean(worlddir+"DropExp.Fixed.SpawnOrb");
							int orbperlevel = dp.getConfig().getInt(worlddir+"DropExp.Fixed.OrbPerLevel");
							int orbvalue = dp.getConfig().getInt(worlddir+"DropExp.Fixed.OrbValue");
							droppedlevel = dpExpDrop.dropExpFixed(player, level, spawnorb, orbperlevel, orbvalue);
							break;
						}
						case "Random":{
							int levelmin = dp.getConfig().getInt(worlddir+"DropExp.Random.LevelMin");
							int levelmax = dp.getConfig().getInt(worlddir+"DropExp.Random.LevelMax");
							boolean spawnorb = dp.getConfig().getBoolean(worlddir+"DropExp.Fixed.SpawnOrb");
							int orbperlevel = dp.getConfig().getInt(worlddir+"DropExp.Fixed.OrbPerLevel");
							int orbvalue = dp.getConfig().getInt(worlddir+"DropExp.Fixed.OrbValue");
							droppedlevel = dpExpDrop.dropExpRandom(player, levelmin, levelmax, spawnorb, orbperlevel, orbvalue);
							break;
						}
						case "Rate":{
							double levelrate = dp.getConfig().getDouble(worlddir+"DropExp.Rate.Level");
							boolean spawnorb = dp.getConfig().getBoolean(worlddir+"DropExp.Rate.SpawnOrb");
							int orbperlevel = dp.getConfig().getInt(worlddir+"DropExp.Rate.OrbPerLevel");
							int orbvalue = dp.getConfig().getInt(worlddir+"DropExp.Rate.OrbValue");
							droppedlevel = dpExpDrop.dropExpRate(player, levelrate, spawnorb, orbperlevel, orbvalue);
							break;
						}
						default: droppedlevel = dpExpDrop.dropExpDefault(player, dp.getConfig().getInt(worlddir+"DropExp.Default.Limit"));break;
						}
						
						player.sendMessage(String.format(messagehash.get("exp"), ""+droppedlevel));
					}
				}
			}
		}else {
			dp.saveDefaultConfig();
		}
	}
}
