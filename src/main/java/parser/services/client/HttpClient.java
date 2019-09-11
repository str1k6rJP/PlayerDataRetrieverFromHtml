package parser.services.client;

import org.apache.http.auth.AuthenticationException;

import java.io.IOException;

public interface HttpClient {

    String serviceUrl = "localhost:8083/";

    /**
     * Receives templates of objects to be set, returns list of <code>Player</code> which were set
     *
     * @param jsonString
     * @return <code>boolean</code>
     */
    boolean savePlayers(String jsonString) throws IOException, AuthenticationException;

    /**
     * Receives <code>Team</code> prefab to save in, returns entity of <code>Team</code> which was saved.
     * If save was failed, returns an empty <code>Team</code>
     *
     * @param jsonStringWithName
     * @return <code>Team</code>
     */
    int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException;


}
