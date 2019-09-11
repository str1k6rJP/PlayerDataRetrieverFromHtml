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
import parser.services.client.HttpClient;

import java.io.IOException;
import java.io.InputStream;


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

    private String prebuiltConnectionParams;
    public String getConnectionParams(){
        return prebuiltConnectionParams==null
                ?prebuiltConnectionParams="http://"+host+":"+port+"/"+request
                :"http://"+host+":"+port+"/"+request;
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
        ResponseHandler<String> handler = new BasicResponseHandler();
        String requestParameter = jsonStringWithName.split(":")[1].split("}")[0];

        HttpPost postTeam = new HttpPost(getConnectionParams()+"/"+requestParameter);

        postTeam.addHeader(new BasicScheme().authenticate(credentials, postTeam, null));

        CloseableHttpResponse response = client.execute(postTeam);

        if (response.getStatusLine().getStatusCode() == 200) {



//            String tmp = handler.handleResponse(response);

            StringBuilder sb= new StringBuilder();
            try (InputStream responseStream = response.getEntity().getContent();){
                int currentByte;
                do{
                currentByte=responseStream.read();
                sb.append((char)currentByte);
                } while (currentByte != -1);
            }
            response.close();
            client.close();
            return Integer.parseInt(sb.toString().split(",")[0].split(":")[1]);
        }
        return -1;
    }
}
