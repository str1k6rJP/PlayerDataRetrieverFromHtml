package parser.services.client.implementations;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import parser.database.tables.Player;
import parser.database.tables.Team;

import java.util.List;

public class ApacheHttpClient implements parser.services.client.implementations.HttpClient {

    HttpClient client = new DefaultHttpClient();

    @Override
    public List<Player> savePlayers(List<Player> playerPrefabsToSet) {
        HttpUriRequest request = RequestBuilder.get()
                .setUri(serviceUrl+"player/add")
                .setHeader(HttpHeaders.CONTENT_TYPE, )
                .build();
        HttpPost postPlayers =
        return null;
    }

    @Override
    public Team saveTeam(Team teamPrefabToSet) {
        return null;
    }
}
