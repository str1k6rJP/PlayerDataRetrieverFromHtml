package parser.services.client.implementations;


import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApacheHttpClientTest {

    public static final int STUB_PORT = 8083;
    public static final String postAllPlayers = "/player/add", postSingleTeam = "^\\/team\\/[^\\/]+", jsonBodyForPlayerPost = "[{\"surname\":\"reallySurname\",\"role\":\"midfielder\",\"teamId\":\"1\"}" +
            ",{\"surname\":\"secondSurname\",\"role\":\"hz\",\"teamId\":\"1\"}]", teamSaveResponse = "{\"id\":\"1\",\"teamName\":\"Success!\"}";
    @ClassRule
    public static WireMockClassRule wireMockRuleStat = new WireMockClassRule(STUB_PORT);
    @Autowired
    public ApacheHttpClient apacheHttpClient;
    @Rule
    public WireMockClassRule wireMockRule = wireMockRuleStat;
    String host = "localhost", port = "8083", request = "team";
    String userName = "str1k6r", password = "that'sME";
    String jsonStringWithTeam = "[{teamName:reallyBadTeamName}]";
    String jsonPlayersString =
            "[{\"surname\":\"reallySurname\",\"role\":\"midfielder\",\"teamId\":\"1\"},{\"surname\":\"secondSurname\",\"role\":\"hz\",\"teamId\":\"1\"}]";

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
                .withRequestBody(equalToJson(jsonBodyForPlayerPost))
                .withBasicAuth("str1k6r", "that'sME")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

    }

    @Before
    public void setConnectionParams() {
        apacheHttpClient.setConnectionParams(host, port);
    }

    @Test
    public void testConnectionParams() throws Exception {
        String connectionParams;
        System.out.println(connectionParams = apacheHttpClient.getConnectionParams(request));
        assert (connectionParams.equals("http://" + host + ":" + port + "/" + request));
    }

    @Before
    @Test
    public void testCredentials() throws Exception {
        assert (apacheHttpClient.setCredentials(userName, password));
        System.out.println(apacheHttpClient.getCredentials().getUserName() + "\n" + apacheHttpClient.getCredentials().getPassword());
        assert (apacheHttpClient.getCredentials().getUserName().equals(userName) && apacheHttpClient.getCredentials().getPassword().equals(password));
    }

    @Test
    public void testSaveTeam() throws Exception {
        int teamId = apacheHttpClient.saveTeam(jsonStringWithTeam);
        System.out.println(teamId);
        assert (teamId > 0);
    }

    @Test
    public void testSavePlayers() throws Exception {
        apacheHttpClient.setConnectionParams(host, port);
        assert apacheHttpClient.savePlayers(jsonPlayersString);
    }


}
