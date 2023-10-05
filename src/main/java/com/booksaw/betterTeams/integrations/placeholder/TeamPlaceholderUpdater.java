import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import com.booksaw.betterTeams.BetterTeams;
import com.booksaw.betterTeams.TeamData;
import com.booksaw.betterTeams.events.EventListener;
import com.booksaw.betterTeams.message.MessageManager;

public class TeamPlaceholderUpdater extends JavaPlugin implements Listener {

    private BetterTeams betterTeams;

    @Override
    public void onEnable() {
        // Assicurati di aver ottenuto l'istanza di BetterTeams correttamente
        if (Bukkit.getPluginManager().isPluginEnabled("BetterTeams")) {
            betterTeams = (BetterTeams) Bukkit.getPluginManager().getPlugin("BetterTeams");
        }

        if (betterTeams != null) {
            // Registra gli eventi
            Bukkit.getPluginManager().registerEvents(this, this);
        } else {
            getLogger().severe("BetterTeams non trovato, il plugin verrà disabilitato.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @EventHandler
    public void onTeamChange(EventListener.TeamChangeEvent event) {
        updateTabPlaceholders();
    }

    private void updateTabPlaceholders() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTabPlaceholder(player);
        }
    }

    private void updateTabPlaceholder(Player player) {
        TeamData playerTeam = betterTeams.getTeam(player.getUniqueId());

        if (playerTeam != null) {
            String playerTeamName = ChatColor.GREEN + playerTeam.getName(); // Colore verde

            updatePlayerTab(player, playerTeamName);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(player)) {
                    TeamData otherPlayerTeam = betterTeams.getTeam(onlinePlayer.getUniqueId());

                    if (otherPlayerTeam != null) {
                        String otherPlayerTeamName = ChatColor.RED + otherPlayerTeam.getName(); // Colore rosso
                        updateOtherPlayerTab(player, onlinePlayer, otherPlayerTeamName);
                    }
                }
            }
        }
    }

    private void updatePlayerTab(Player player, String playerTeamName) {
	//in teoria dovrebbe servrimi a ricaricare la tab, ma non so cosa sto combinando
    }

    private void updateOtherPlayerTab(Player viewer, Player otherPlayer, String otherPlayerTeamName) {
	//in teoria server a ricaricare la tab degli altri giocatoriS
    }
}
