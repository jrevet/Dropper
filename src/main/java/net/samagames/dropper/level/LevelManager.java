package net.samagames.dropper.level;

import net.samagames.api.SamaGamesAPI;
import net.samagames.dropper.Dropper;
import net.samagames.dropper.common.GameLocations;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Vialonyx
 */

public class LevelManager {

    /*
     * This class manages levels globaly.
     */

    public final AbstractLevel LEVEL_1;
    public int task, value;
    public boolean timerIsStarted;

    private Dropper instance;
    public LevelManager(Dropper instance){
        this.instance = instance;
        this.LEVEL_1 = new AbstractLevel(1, "Rainbow", "Test", GameLocations.LEVEL1_AREA.locationValue(), new Location(this.instance.getWorld(), 535, 234, -37));
    }

    public void joinLevel(Player joiner, AbstractLevel level){
        if(level.getLevelPlayers().contains(joiner.getUniqueId())){
            this.instance.getLogger().log(Level.SEVERE, "Specified player is already playing in the level.");
            return;
        } else {
            level.usualJoin(joiner);
            this.instance.getDropperGame().getRegisteredGamePlayers().get(joiner.getUniqueId()).setCurrentlyLevel(level);
            this.instance.sendTitle(joiner, level.getLevelName(), level.getLevelDescription(), 60);
            joiner.teleport(level.getRelatedLocation());
            joiner.sendMessage(SamaGamesAPI.get().getGameManager().getCoherenceMachine().getGameTag() +
            " §bVous avez rejoint le §cNiveau " + level.getNumber());
            
            if(level.getNumber() == 1 && this.timerIsStarted == false){
            	
            	 this.timerIsStarted = true;
            	 this.value = 21;
                 this.task = this.instance.getServer().getScheduler().scheduleSyncRepeatingTask(this.instance, new Runnable() {
     				
     				@Override
     				public void run() {
     					
     					if(value == 0){
     						instance.getServer().getScheduler().cancelTask(task);
     						timerIsStarted = false;
     						value = 21;
     						
     						for(UUID uuid : level.getLevelPlayers()){
     							Player tmpPlayer = instance.getServer().getPlayer(uuid);
     							tmpPlayer.teleport(new Location(instance.getWorld(),
     									tmpPlayer.getLocation().getX() - 37, 
     									tmpPlayer.getLocation().getY() + 200,
     									tmpPlayer.getLocation().getZ() - 38));
     						}
     						
     						return;
     					} else {
     						value--;
     						
     						if(value == 20 || value == 10 || value <= 5 && value != 1 && value != 0){
     							instance.getServer().broadcastMessage(SamaGamesAPI.get().getGameManager().getCoherenceMachine().getGameTag() +
     									" §3Démarrage du §cNiveau " + level.getNumber() + " §3dans §b" + value + " §3secondes");
     						} else if (value == 1){
     							instance.getServer().broadcastMessage(SamaGamesAPI.get().getGameManager().getCoherenceMachine().getGameTag() +
     									" §3Démarrage du §cNiveau " + level.getNumber() + " §3dans §b" + value + " §3seconde");
     						}
     					}
     					
     				}
     			}, 0L, 20L);
            	
            }
            
        }
    }

}
