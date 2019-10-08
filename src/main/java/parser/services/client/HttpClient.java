package parser.services.client;

import org.apache.http.auth.AuthenticationException;
import parser.beans.Player;
import parser.beans.Team;

import java.io.IOException;
import java.util.List;

public interface HttpClient {

    /**
     * Receives templates of objects to be set, returns list of <code>Player</code> which were set
     *
     * @param playerList
     * @return <code>boolean</code>
     */
    boolean savePlayers(List<Player> playerList) throws IOException, AuthenticationException;

    /**
     * Receives <code>Team</code> prefab to save in, returns id of team saved
     * If save was failed, returns -1
     *
     * @param team team entity prefab
     * @return <code>Team.id</code> field value
     */
    int saveTeam(Team team) throws IOException, AuthenticationException;
}
