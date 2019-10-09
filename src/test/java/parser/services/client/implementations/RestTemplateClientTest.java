package parser.services.client.implementations;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import parser.services.client.ClientTest;
import parser.services.client.HttpClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RestTemplateClientTest extends ClientTest {

    @Autowired
    @Qualifier("httpClientRestTemplate")
    public AbstractHttpClient httpClient;

    @Override
    public AbstractHttpClient getHttpClient() {
        return httpClient;
    }

    @Override
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
                .withBasicAuth("str1k6r", "that'sME")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

    }

   /* public static final int STUB_PORT = 8083;
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

    }*/


}
