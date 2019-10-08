package parser.services;


import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HtmlParserServiceTest {

    public static final int STUB_PORT = 8084;
    public static final String postSingleTeam = "^\\/team\\/[^\\/]+", teamSaveResponse = "{\"id\":\"1\",\"teamName\":\"Success!\"}";
    @ClassRule
    public static WireMockClassRule wireMockRuleStat = new WireMockClassRule(STUB_PORT);
    @Autowired
    @Qualifier("configuredHtmlParserService")
    public HTMLParserService parserService;
    @Rule
    public WireMockClassRule wireMockRule = wireMockRuleStat;

    String host = "localhost", port = "8084";
    String userName = "str1k6r", password = "that'sME";

    @Before
    public void setup() {
        stubFor(
                post(urlPathMatching(postSingleTeam))
                        .withBasicAuth("str1k6r", "that'sME")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(teamSaveResponse)));
    }

    @Before
    @Test
    public void testSetLink() throws Exception {
        String s = parserService.setLinkToSiteWithTeams("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");
        System.out.println(s);
        assert s.equals("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");

    }


    @Before
    @Test
    public void testSetConnectionParams() throws Exception {
        assert parserService.setConnectionParams(host, port).equals(String.format("http://%s:%s/", host, port));
    }

    @Before
    @Test
    public void testCredentials() throws Exception {
        UsernamePasswordCredentials credentials = parserService.setUsernamePasswordCredentials(userName, password);

        assert (credentials.getPassword().equals(password) && credentials.getUserName().equals(userName));
    }

    @Test
    public void testJsonResult() throws Exception {
        System.out.println(parserService.getPlayersInJsonFormat(parserService.getPlayersStringBySiteWithTeamList()));

    }

}
