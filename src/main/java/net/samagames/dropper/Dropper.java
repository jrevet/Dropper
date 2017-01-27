package net.samagames.dropper;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.tools.LocationUtils;

public class Dropper extends Game<DropperPlayer> {
	
	private DropperMain instance;
	
	public ItemStack ITEM_GAMETYPE_SELECT_FREE;
	public ItemStack ITEM_GAMETYPE_SELECT_COMPETITION;
	public ItemStack ITEM_GAMETYPE_LEAVE;
	public ItemStack ITEM_ACTUAL_LEAVE;
	
	 public Dropper(String gameCodeName, String gameName, String gameDescription, Class<DropperPlayer> gamePlayerClass, DropperMain instance) {
		 super(gameCodeName, gameName, gameDescription, gamePlayerClass);
		 
		 this.instance = instance;
		 this.ITEM_GAMETYPE_SELECT_FREE = this.stackBuilder("Entrainement", null, Material.DIRT, (byte) 0);
		 this.ITEM_GAMETYPE_SELECT_COMPETITION = this.stackBuilder("Compétition", null, Material.GRASS, (byte) 0);
		 this.ITEM_GAMETYPE_LEAVE = this.stackBuilder("Quitter le mode de jeu actuel", null, Material.BONE, (byte) 0);
		 this.ITEM_ACTUAL_LEAVE = this.stackBuilder("Quitter le niveau actuel", null, Material.BIRCH_DOOR_ITEM, (byte) 0);
		 
	 }
	 
	 @Override 
	 public void handleLogin(Player player){
		 super.handleLogin(player);
		 player.teleport(this.getMapHub());
		 player.getInventory().clear();
		 player.getInventory().setItem(0, this.ITEM_GAMETYPE_SELECT_FREE);
		 player.getInventory().setItem(1, this.ITEM_GAMETYPE_SELECT_COMPETITION);
	 }
	 
	 public DropperMain getMainInstance(){
		 return this.instance;
	 }
	 
	 public void usualGameTypeUpdate(Player player, GameType newGameType){
		 // TODO Respond in function of old gameType
		 this.getPlayer(player.getUniqueId()).updatePlayerGameType(newGameType);
	 }
	 
	 public void usualGameJoin(Player player){
		 DropperPlayer dpPlayer = this.getPlayer(player.getUniqueId());
		 
		 if(dpPlayer.getGameType().equals(GameType.FREE)){
			 player.teleport(this.getMapLevelHub());
			 player.getInventory().clear();
			 player.getInventory().setItem(0, this.ITEM_GAMETYPE_LEAVE);

		 } else if(dpPlayer.getGameType().equals(GameType.COMPETITION)){
			 /*
			  * TODO Function to get logically the next level
			  * TODO Broadcast message (function to syntax LevelName & GameType)
			  * TODO Update player inventory
			  */
		 }

		 SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager().writeCustomMessage(
				 player.getName() + ChatColor.AQUA + " a commencé une nouvelle partie en mode "
						 + this.getGameTypeFormatColor(dpPlayer.getGameType())  + ChatColor.AQUA + " !",true);

	 }

	 public String getGameTypeFormatColor(GameType type){
	 	if(type.equals(GameType.UNSELECTED)){
	 		return ChatColor.GRAY + "Non sélectionné";
		} else if(type.equals(GameType.FREE)){
	 		return ChatColor.GREEN + "Entrainement";
		} else if(type.equals(GameType.COMPETITION)){
			return ChatColor.RED + "Compétition";
		}
		return "";
	 }
	 
	 public void usualGameLeave(Player player, boolean byPlayer){
		 DropperPlayer dpPlayer = this.getPlayer(player.getUniqueId());

		 if(byPlayer){
			 player.sendMessage(ChatColor.AQUA + "Vous avez quitté votre partie en mode " + this.getGameTypeFormatColor(dpPlayer.getGameType()));
		 }
		 
		 player.teleport(this.getMapHub());
		 dpPlayer.updatePlayerGameType(GameType.UNSELECTED);
		 dpPlayer.updateCurrentLevel(null);
		 player.getInventory().clear();
		 player.getInventory().setItem(0, this.ITEM_GAMETYPE_SELECT_FREE);
		 player.getInventory().setItem(1, this.ITEM_GAMETYPE_SELECT_COMPETITION);
		 
	 }
	 
	 private ItemStack stackBuilder(String name, List<String> lore, Material material, byte data){ 
	        org.bukkit.inventory.ItemStack tmpStack = new ItemStack(material, 1, data); 
	        ItemMeta tmpStackMeta = tmpStack.getItemMeta(); 
	        tmpStackMeta.setDisplayName(name); 
	        tmpStackMeta.setLore(lore); 
	        tmpStack.setItemMeta(tmpStackMeta); 
	 
	        return tmpStack; 
	    } 
	 
	 public Location getMapHub(){
	    JsonObject object = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs();
	    return LocationUtils.str2loc(object.get("world-name").getAsString() + ", " + object.get("map-hub").getAsString());
	 }
	 
	 public Location getMapLevelHub(){
	    JsonObject object = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs();
	    return LocationUtils.str2loc(object.get("world-name").getAsString() + ", " + object.get("level-hub").getAsString());
	 }
	
}
