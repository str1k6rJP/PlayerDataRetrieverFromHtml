package parser.services.client.implementations;

import lombok.Data;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parser.database.tables.Player;
import parser.database.tables.Team;
import parser.services.HTMLParserService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


@Service
public class ApacheHttpClient implements parser.services.client.implementations.HttpClient {
    private String host, port, request;
    private String username, password;

    private CloseableHttpClient client = HttpClients.createDefault();

    @Autowired
    private HTMLParserService htmlParserService;

    @Override
    public List<Player> savePlayers(List<Player> playerPrefabsToSet) throws IOException, AuthenticationException {
       /* HttpUriRequest request = RequestBuilder.get()
                .setUri(serviceUrl+"player/add")
                .setHeader(HttpHeaders.CONTENT_TYPE, )
                .build();*/
        HttpPost postPlayers = new HttpPost(host + ":" + port + "//" + request);
        String jsonString = htmlParserService.getPlayersInJsonFormat(htmlParserService
                .getPlayersStringBySiteWithTeamList());

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username,password);
postPlayers.setEntity(new StringEntity(jsonString));
postPlayers.setHeader("Accept","application/json");
postPlayers.setHeader("content-type","application/json");

        postPlayers.addHeader(new BasicScheme().authenticate(credentials,postPlayers,null));

        CloseableHttpResponse response = client.execute(postPlayers);

        return null;
    }

    public void setConnectionParams(String host, String port, String request) {
        setHost(host);
        setPort(port);
        setRequest(request);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String setLink(String url) {
        return htmlParserService.setLinkToSiteWithTeams("link");
    }

    public boolean setCredentials(String username,String password){
        setUsername(username);
        setPassword(password);
        return this.username.equals(username)&&this.password.equals(password);
    }

    @Override
    public Team saveTeam(Team teamPrefabToSet) {
        return null;
    }
}
