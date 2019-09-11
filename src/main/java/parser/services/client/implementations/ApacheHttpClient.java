package parser.services.client.implementations;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import parser.services.client.HttpClient;

import java.io.IOException;


@Service
public class ApacheHttpClient implements HttpClient {
    private String host, port, request;
    private String username, password;

    private CloseableHttpClient client = HttpClients.createDefault();
    private UsernamePasswordCredentials credentials;


    @Override
    public boolean savePlayers(String jsonString) throws IOException, AuthenticationException {
       /* HttpUriRequest request = RequestBuilder.get()
                .setUri(serviceUrl+"player/add")
                .setHeader(HttpHeaders.CONTENT_TYPE, )
                .build();*/
        HttpPost postPlayers = new HttpPost(getConnectionParams());
        /*String jsonString = htmlParserService.getPlayersInJsonFormat(htmlParserService
                .getPlayersStringBySiteWithTeamList());*/

        
        postPlayers.setEntity(new StringEntity(jsonString));
        postPlayers.setHeader("Accept", "application/json");
        postPlayers.setHeader("content-type", "application/json");

        postPlayers.addHeader(new BasicScheme().authenticate(credentials, postPlayers, null));

        CloseableHttpResponse response = client.execute(postPlayers);

        if (response.getStatusLine().getStatusCode() == 200) {
            response.close();
            client.close();
            return true;
        }
        return false;
    }

    public void setConnectionParams(String host, String port, String request) {
        setHost(host);
        setPort(port);
        setRequest(request);
    }

    public String getConnectionParams(){
        return host+":"+port+"/"+request;
    }

    private void setHost(String host) { this.host = host; }
    private void setPort(String port) { this.port = port; }
    private void setPassword(String password) { this.password = password; }
    private void setUsername(String username) { this.username = username; }
    private void setRequest(String request) { this.request = request; }



    public boolean setCredentials(String username, String password) {
        setUsername(username);
        setPassword(password);
        credentials =  new UsernamePasswordCredentials(this.username, this.password);
        return credentials.getUserName().equals(username) && credentials.getPassword().equals(password);
    }

    public UsernamePasswordCredentials getCredentials() {
        return credentials;
    }

    @Override
    public int saveTeam(String jsonStringWithName) throws IOException,AuthenticationException {
        String requestParameter = jsonStringWithName.split(":")[1];

        HttpPost postTeam = new HttpPost(getConnectionParams()+"/"+requestParameter);

        postTeam.addHeader(new BasicScheme().authenticate(credentials, postTeam, null));

        CloseableHttpResponse response = client.execute(postTeam);

        if (response.getStatusLine().getStatusCode() == 200) {
            response.close();

            client.close();
            System.out.println(response.getEntity().getContent().toString());
        }
        return -1;
    }
}
