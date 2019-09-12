package parser.services.client.implementations;

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
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;

import java.io.IOException;
import java.io.InputStream;


@Service
public class ApacheHttpClient implements HttpClient {
    private String host, port;
    private String username, password;

    private String requestForSaveTeam = "team"
            , requsetForSavePlayers="player/add";
    private static final String invalidInputErrorCustomAdviceMessageForConnectionParams="\nThe input should be in format http[s]://<hostname>:<port>/";

    private CloseableHttpClient client = HttpClients.createDefault();
    private UsernamePasswordCredentials credentials;
    private String prebuiltConnectionParams;

    @Override
    public boolean savePlayers(String jsonString) throws IOException, AuthenticationException {

        HttpPost postPlayers = new HttpPost(getConnectionParams(requsetForSavePlayers));

        postPlayers.setEntity(new StringEntity(jsonString));
        postPlayers.setHeader("Accept", "application/json");
        postPlayers.setHeader("content-type", "application/json");

        postPlayers.addHeader(new BasicScheme().authenticate(credentials, postPlayers, null));

        CloseableHttpResponse response = client.execute(postPlayers);

        if (response.getStatusLine().getStatusCode() == 200) {
            response.close();
            return true;
        }
        return false;
    }

    @Override
    public int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException {
        ResponseHandler<String> handler = new BasicResponseHandler();
        String requestParameter = jsonStringWithName.split(":")[1].split("}")[0].replace(' ','_');

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
            return Integer.parseInt(sb.toString().split(",")[0].split(":")[1]);
        }
        return -1;
    }

    public void setConnectionParams(String host, String port) {
        setHost(host);
        setPort(port);
        prebuiltConnectionParams = prebuiltConnectionParams = "http://" + host + ":" + port + "/";
    }

    public void setConnectionParams(String singleLineConnectionParams) throws InvalidInputError {
        String[] s = singleLineConnectionParams.trim().split("://|:|");
        try {
            setConnectionParams(s[0],s[1]);
        } catch (IndexOutOfBoundsException e){
            throw new InvalidInputError(invalidInputErrorCustomAdviceMessageForConnectionParams+e.getMessage());
        }
    }


    public String getConnectionParams(String request) {
        return prebuiltConnectionParams + request;
    }

    public boolean setCredentials(String username, String password) {
        setUsername(username);
        setPassword(password);
        credentials = new UsernamePasswordCredentials(this.username, this.password);
        return credentials.getUserName().equals(username) && credentials.getPassword().equals(password);
    }

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
