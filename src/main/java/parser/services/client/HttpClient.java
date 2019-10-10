package parser.services.client;

import org.apache.http.auth.AuthenticationException;
import parser.beans.Player;
import parser.beans.Team;

import java.io.IOException;
import java.util.List;

public interface HttpClient {

    /**
     * Receives templates of <code>Player</code> to be set, returns true if and only if all the entities were successfully set to the database
     *
     * @param playerList
     * @return <code>boolean</code>
     */
    boolean savePlayers(List<Player> playerList);

    /**
     * Receives <code>Team</code> prefab to save in, returns id of team saved
     *
     * @param team team entity prefab
     * @return <code>Team</code> returned by database
     */
    Team saveTeam(Team team);

    boolean setInitialConnectionPath(String connectionPath);
}
