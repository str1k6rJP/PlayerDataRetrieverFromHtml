package parser.services.client.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import parser.beans.Player;
import parser.beans.Team;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class ApacheHttpClient extends AbstractHttpClient {

    private CloseableHttpClient client = HttpClients.createDefault();
    private UsernamePasswordCredentials credentials;

    private <T> HttpPost setEntityToPost(@NotNull T entity, HttpPost postPlayers) {

        try {
            StringEntity stringEntity = new StringEntity(objectMapper.writeValueAsString(entity), StandardCharsets.UTF_8);
            if (stringEntity == null || stringEntity.getContentLength() <= 1) {
                return null;
            }
            postPlayers.setEntity(stringEntity);
        } catch (JsonProcessingException e) {
            log.error(String.format("Failed while parsing to JSON %n"), e);
            return null;
        }
        return postPlayers;
    }

    private HttpPost setHeadersToPost(HttpPost httpPost) {
        if (httpPost == null) {
            return null;
        }
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("content-type", "application/json");
        return httpPost;
    }

    private HttpPost setAuthentication(HttpPost httpPost) {
        if (httpPost == null) {
            return null;
        }
        try {
            httpPost.addHeader(new BasicScheme().authenticate(getCredentials(), httpPost, null));
        } catch (AuthenticationException e) {
            log.error(String.format("Failed to access savePlayers()(so false will be returned) in with username %s and password %s", getUsername(), getPassword()), e);
            return null;
        }
        return httpPost;
    }

    @Override
    public boolean savePlayers(List<Player> playerList) {
        HttpPost postPlayers = setAuthentication(
                setHeadersToPost(setEntityToPost(playerList
                        , new HttpPost(getConnectionPathTo(REQUEST_SAVE_PLAYERS)))));

        if (postPlayers == null) {
            log.error(String.format("Error!!!%nPOST Request org.apache.http.client.methods.HttpPost is null!!!"));
            return false;
        }
        try (CloseableHttpResponse response = client.execute(postPlayers)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                response.close();
                return true;
            }
        } catch (IOException e) {
            log.error("Unexpected I/OException has occurred!", e.getMessage(), e);

        }
        return false;
    }

    @Override
    public Team saveTeam(Team team) {

        HttpPost postTeam = setAuthentication(
                setHeadersToPost(new HttpPost(
                        getConnectionPathTo(REQUEST_SAVE_TEAM) + "/" + team.getTeamName().replaceAll("\\s", "_"))));


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

}
