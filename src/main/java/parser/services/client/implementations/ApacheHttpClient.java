package parser.services.client.implementations;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import parser.beans.Player;
import parser.beans.Team;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class ApacheHttpClient extends AbstractHttpClient {

    private CloseableHttpClient client = HttpClients.createDefault();
    private UsernamePasswordCredentials credentials;


    @Override
    public boolean savePlayers(List<Player> playerList) throws IOException, AuthenticationException {
        String jsonString = getInstanceInJsonFormat(playerList);
        HttpPost postPlayers = new HttpPost(getConnectionPathTo(REQUEST_SAVE_PLAYERS));
        StringEntity entity = new StringEntity(jsonString, StandardCharsets.UTF_8);

        System.out.println(entity);
        postPlayers.setEntity(entity);
        postPlayers.setHeader("Accept", "application/json");
        postPlayers.setHeader("content-type", "application/json");


        postPlayers.addHeader(new BasicScheme().authenticate(getCredentials(), postPlayers, null));

        CloseableHttpResponse response = client.execute(postPlayers);

        if (response.getStatusLine().getStatusCode() == 200) {
            response.close();
            System.err.println(true);
            return true;
        }
        System.err.println(false);
        return false;
    }

    @Override
    public int saveTeam(Team team) throws IOException, AuthenticationException {
        ResponseHandler<String> handler = new BasicResponseHandler();

        HttpPost postTeam = new HttpPost(getConnectionPathTo(REQUEST_SAVE_TEAM) + "/" + team.getTeamName());

        postTeam.addHeader(new BasicScheme().authenticate(getCredentials(), postTeam, null));

        CloseableHttpResponse response = client.execute(postTeam);

        if (response.getStatusLine().getStatusCode() == 200) {

            StringBuilder sb = new StringBuilder();
            try (InputStream responseStream = response.getEntity().getContent();) {
                int currentByte;
                do {
                    currentByte = responseStream.read();
                    sb.append((char) currentByte);
                } while (currentByte != -1);
            }
            response.close();
            return objectMapper.readValue(sb.toString(), Team.class).getId();
            }
        return -1;
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
        setCredentials(username, password);
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
