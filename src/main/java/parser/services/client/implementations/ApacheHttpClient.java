package parser.services.client.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ApacheHttpClient implements HttpClient {

    private String host, port;
    private String username, password;
    private String requestForSaveTeam = "team", requestForSavePlayers = "player/add";
    private CloseableHttpClient client = HttpClients.createDefault();
    private UsernamePasswordCredentials credentials;
    private String prebuiltConnectionParams;

    @Override
    public boolean savePlayers(List<Player> players) throws IOException, AuthenticationException {
String jsonString = getInstanceInJsonFormat(players);
        HttpPost postPlayers = new HttpPost(getConnectionParams(requestForSavePlayers));
StringEntity entity = new StringEntity(jsonString, StandardCharsets.UTF_8);

        System.out.println(entity);
        postPlayers.setEntity(entity);
        postPlayers.setHeader("Accept", "application/json");
        postPlayers.setHeader("content-type", "application/json");


        postPlayers.addHeader(new BasicScheme().authenticate(credentials, postPlayers, null));

        CloseableHttpResponse response = client.execute(postPlayers);

        if (response.getStatusLine().getStatusCode() == 200) {
            response.close();
            System.err.println(true);
            return true;
        }
        System.err.println(false);
        return false;
    }


    /*//TODO not yet implemented and doesn't need to be implemented
    @Override
    public boolean savePlayers(List<Player> jsonString) throws IOException, AuthenticationException {
        System.err.println(false);
        return false;
    }*/

    @Override
    public int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException {
        ResponseHandler<String> handler = new BasicResponseHandler();
        String requestParameter = jsonStringWithName.split("[\"{,:}]++")[2].replace(' ', '_');

        HttpPost postTeam = new HttpPost(getConnectionParams(requestForSaveTeam) + "/" + requestParameter);

        postTeam.addHeader(new BasicScheme().authenticate(credentials, postTeam, null));

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
            Team team = new ObjectMapper().readValue(sb.toString(),Team.class);
            //retrieves id of team set to database just now
            return team.getId();
        }
        return -1;
    }

    @Override
    public void setConnectionParams(String host, String port) throws InvalidInputError {
        try {
            Integer.parseInt(port);
            if (host.matches(forbiddenHostPartsRegexSet)) {
                throw new InvalidInputError("It's not possible for the hostname to contain any of " + forbiddenHostPartsRegexSet + " symbols");
            }
            ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new InvalidInputError("Wrong input for port value!! It MUSTN'T contain any symbols except digits : "
                    + e.getMessage());
        }
        setHost(host);
        setPort(port);
        prebuiltConnectionParams = prebuiltConnectionParams = "http://" + host + ":" + port + "/";
    }

    @Override
    public String getConnectionParams() {
        return prebuiltConnectionParams;
    }


    @Override
    public boolean setCredentials(String username, String password) {
        setUsername(username);
        setPassword(password);
        credentials = new UsernamePasswordCredentials(this.username, this.password);
        return credentials.getUserName().equals(username) && credentials.getPassword().equals(password);
    }

    @Override
    public UsernamePasswordCredentials getCredentials() {
        return credentials;
    }

    //Simple setters/getters
    private void setHost(String host) {
        this.host = host;
    }

    private void setPort(String port) {
        this.port = port;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setUsername(String username) {
        this.username = username;
    }



}
