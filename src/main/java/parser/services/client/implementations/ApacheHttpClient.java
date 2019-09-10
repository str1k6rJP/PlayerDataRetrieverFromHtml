package parser.services.client.implementations;

import lombok.Data;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import parser.database.tables.Player;
import parser.database.tables.Team;

import java.util.List;


@Data
public class ApacheHttpClient implements parser.services.client.implementations.HttpClient {
    private String host, port, request;

    private CloseableHttpClient client = HttpClients.createDefault();

    @Override
    public List<Player> savePlayers(List<Player> playerPrefabsToSet) {
       /* HttpUriRequest request = RequestBuilder.get()
                .setUri(serviceUrl+"player/add")
                .setHeader(HttpHeaders.CONTENT_TYPE, )
                .build();*/
        HttpPost postPlayers = new HttpPost(host+":"+port+"//"+request);
        String json = playerPrefabsToSet.toString();
        System.out.println(json);

        return null;
    }

    public void setConnectionParams(String host,String port,String request){
        setHost(host);
        setPort(port);
        setRequest(request);
    }

    @Override
    public Team saveTeam(Team teamPrefabToSet) {
        return null;
    }
}
