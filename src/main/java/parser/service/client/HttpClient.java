package parser.service.client;

import parser.database.tables.Player;
import parser.database.tables.Team;

import java.util.List;

public interface HttpClient {

    String serviceUrl = "localhost:8083/";

    /**
     * Receives templates of objects to be set, returns list of <code>Player</code> which were set
     * @param playerPrefabsToSet prefabs of player rows
     *
     * @return <code>List<Player></code>
     */
    List<Player> savePlayers(List<Player> playerPrefabsToSet);

    /**
     * Receives <code>Team</code> prefab to save in, returns entity of <code>Team</code> which was saved.
     * If save was failed, returns an empty <code>Team</code>
     * @param teamPrefabToSet prefab of team row
     *
     * @return <code>Team</code>
     */
    Team saveTeam(Team teamPrefabToSet);


}
