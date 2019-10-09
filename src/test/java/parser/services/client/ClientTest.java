package parser.services.client;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import parser.beans.Player;
import parser.beans.Team;
import parser.services.client.implementations.AbstractHttpClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class ClientTest {

    public static final int STUB_PORT = 8083;
    public static final String postAllPlayers = "/player/add", postSingleTeam = "^\\/team\\/[^\\/]+", jsonBodyForPlayerPost = "[{\"surname\":\"reallySurname\",\"role\":\"midfielder\",\"teamId\":\"1\"}" +
            ",{\"surname\":\"secondSurname\",\"role\":\"hz\",\"teamId\":\"1\"}]", teamSaveResponse = "{\"id\":\"1\",\"teamName\":\"Success!\"}";

    public static final String jsonPlayers = "[ {\n" +
            "  \"nameAndSurname\" : \"reallySurname\",\n" +
            "  \"role\" : \"midfielder\",\n" +
            "  \"teamId\" : 1,\n" +
            "  \"id\" : 0\n" +
            "}, {\n" +
            "  \"nameAndSurname\" : \"secondSurname\",\n" +
            "  \"role\" : \"hz\",\n" +
            "  \"teamId\" : 1,\n" +
            "  \"id\" : 0\n" +
            "} ]";
    @ClassRule
    public static WireMockClassRule wireMockRuleStat = new WireMockClassRule(STUB_PORT);
    @Rule
    public WireMockClassRule wireMockRule = wireMockRuleStat;
    String host = "localhost", port = "8083", request = "team";
    String userName = "str1k6r", password = "that'sME";
    String jsonStringWithTeam = "[{teamName:reallyBadTeamName}]";
    Team testTeam = new Team("reallyBadTeamName");
    String jsonPlayersString =
            "[{\"surname\":\"reallySurname\",\"role\":\"midfielder\",\"teamId\":\"1\"},{\"surname\":\"secondSurname\",\"role\":\"hz\",\"teamId\":\"1\"}]";

    public abstract AbstractHttpClient getHttpClient();

    @Before
    public void setup() {
        stubFor(
                post(urlPathMatching(postSingleTeam))
                        .withBasicAuth("str1k6r", "that'sME")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(teamSaveResponse)));
        stubFor(post(urlEqualTo(postAllPlayers)).withHeader("Accept", equalTo("application/json"))
                .withHeader("content-type", equalTo("application/json"))
                .withRequestBody(equalToJson(jsonPlayers))
                .withBasicAuth("str1k6r", "that'sME")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

    }

    @Before
    public void setConnectionParams() {
        getHttpClient().setInitialConnPath(host, port);
    }

    @Test
    public void testConnectionParams() throws Exception {
        String connectionParams;
        System.out.println(connectionParams = getHttpClient().getConnectionPathTo(request));
        assert (connectionParams.equals("http://" + host + ":" + port + "/" + request));
    }

    @Before
    public void testCredentials() throws Exception {
        getHttpClient().setCredentials(userName, password);
        System.out.println(getHttpClient().getUsername() + "\n" + getHttpClient().getPassword());
        assert (getHttpClient().getUsername().equals(userName) && getHttpClient().getPassword().equals(password));
    }

    @Test
    public void testSaveTeam() throws Exception {
        int teamId = getHttpClient().saveTeam(testTeam);
        System.out.println(teamId);
        assert (teamId > 0);
    }

    @Test
    public void testSavePlayers() throws Exception {
        getHttpClient().setInitialConnPath(host, port);
        assert getHttpClient().savePlayers(new ObjectMapper().readValue(jsonPlayersString, new TypeReference<List<Player>>() {
        }));
    }


}
