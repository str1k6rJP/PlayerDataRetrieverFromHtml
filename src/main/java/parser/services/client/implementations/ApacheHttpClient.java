package parser.services.client.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import parser.beans.Player;
import parser.beans.Team;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class ApacheHttpClient extends AbstractHttpClient {

    private CloseableHttpClient client = HttpClients.createDefault();
    private UsernamePasswordCredentials credentials;


    @Override
    public boolean savePlayers(List<Player> playerList) {
        HttpPost postPlayers = new HttpPost(getConnectionPathTo(REQUEST_SAVE_PLAYERS));
        StringEntity entity;
        try {
            entity = new StringEntity(objectMapper.writeValueAsString(playerList), StandardCharsets.UTF_8);
            postPlayers.setEntity(entity);
        } catch (JsonProcessingException e) {
            log.error("Failed while parsing to JSON \\%n", e);
            return false;
        }

        postPlayers.setHeader("Accept", "application/json");
        postPlayers.setHeader("content-type", "application/json");


        try {
            postPlayers.addHeader(new BasicScheme().authenticate(getCredentials(), postPlayers, null));
        } catch (AuthenticationException e) {
            log.error(String.format("Failed to access savePlayers()(so false will be returned) in with username %s and password %s", getUsername(), getPassword()), e);

        }


        try {
            return executePost(postPlayers);
        } catch (ClientProtocolException e) {
            log.error("Error in HTTP protocol has occurred!", e.getMessage(), e);
        } catch (IOException e) {
            log.error("Connection has been aborted!\\%nThe service will try once more", e.getMessage(), e);
            try {
                return executePost(postPlayers);
            } catch (IOException e1) {
                log.error("Failed while second attempt", e.getCause(), e1);
            }
        }
        return false;

    }

    @Override
    public Team saveTeam(Team team) {

        HttpPost postTeam = new HttpPost(getConnectionPathTo(REQUEST_SAVE_TEAM) + "/" + team.getTeamName().replaceAll("\\s", "_"));

        try {
            postTeam.addHeader(new BasicScheme().authenticate(getCredentials(), postTeam, null));
        } catch (AuthenticationException e) {
            log.error(String.format("Failed to access saveTeam()(so null will be returned) in with username %s and password %s in case of: %n%s", getUsername(), getPassword(), e.getMessage()), e);
            return null;
        }

        try (CloseableHttpResponse response = client.execute(postTeam)) {
            return objectMapper.readValue(response.getEntity().getContent(), Team.class);
        } catch (IOException e) {
            log.error("Failed to get response!", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Overridden because this implementation needs specifically {@link UsernamePasswordCredentials}
     * Also includes initialization of super {@link #setCredentials(String, String)} with sole purpose
     * to provide backward compatibility
     *
     * @param username
     * @param password
     */
    @Override
    public void setCredentials(String username, String password) {
        super.setCredentials(username, password);
        credentials = new UsernamePasswordCredentials(username, password);
    }

    /**
     * Returns credentials in form defined by {@link UsernamePasswordCredentials} from the spring boot security lib
     *
     * @return <code>UsernamePasswordCredentials</code>  entity stored within the implementation class in the current moment
     */
    public UsernamePasswordCredentials getCredentials() {
        return credentials;

    }

    private boolean executePost(HttpPost httpPost) throws IOException {
        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            response.close();
            return true;
        }
        return false;
    }
}
