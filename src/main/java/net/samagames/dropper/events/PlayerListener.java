package net.samagames.dropper.events;

import net.samagames.api.SamaGamesAPI;
import net.samagames.dropper.Dropper;
import net.samagames.dropper.common.GameItems;
import net.samagames.dropper.common.GameLocations;
import net.samagames.dropper.level.AbstractLevel;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Vialonyx
 */

public class PlayerListener implements Listener {

    /*
     * This Listener take care of events called by players.
     */

    private Dropper instance;
    public PlayerListener(Dropper instance){
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){

            if(event.getItem() != null){

                event.setCancelled(true);
                Player player = event.getPlayer();
                ItemStack item = event.getItem();

                if(item.isSimilar(GameItems.BACK_LEVEL_HUB.getStackValue())) {

                    if(this.instance.getDropperGame().getRegisteredGamePlayers().get(player.getUniqueId()).getCurrentlyLevel() != null){
                    	
                    	AbstractLevel leavedLevel = this.instance.getDropperGame().getRegisteredGamePlayers().get(player.getUniqueId()).getCurrentlyLevel();
                    	
                        this.instance.getDropperGame().getRegisteredGamePlayers().get(player.getUniqueId()).getCurrentlyLevel().usualLeave(player);
                        this.instance.getDropperGame().getRegisteredGamePlayers().get(player.getUniqueId()).setCurrentlyLevel(null);
                        
                        if(leavedLevel.getNumber() == 1 && this.instance.getLevelManager().timerIsStarted && leavedLevel.getLevelPlayers().size() == 0 ){
                        	
                        	this.instance.getServer().getScheduler().cancelTask(this.instance.getLevelManager().task);
                        	this.instance.getLevelManager().timerIsStarted = false;
                        	this.instance.getLevelManager().value = 21;
                        	this.instance.getServer().broadcastMessage(SamaGamesAPI.get().getGameManager().getCoherenceMachine().getGameTag() +
 									" §3Démarrage du §cNiveau " + leavedLevel.getNumber() + " §cannulé");
                        	
                        	this.instance.getLogger().log(Level.INFO, "Cooldown of level 1 has been stopped");
                        }
                        
                    }

                    player.teleport(GameLocations.SPAWN.locationValue());
                }

            }
        }
    }

}
