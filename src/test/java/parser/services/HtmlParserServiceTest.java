package parser.services;


import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
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
import parser.beans.Team;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HtmlParserServiceTest {

    public static final int STUB_PORT = 8084;
    public static final String postSingleTeam = "^\\/team\\/[^\\/]+";
    @ClassRule
    public static final WireMockClassRule wireMockRuleStat = new WireMockClassRule(STUB_PORT);
    @Autowired
    @Qualifier("configuredHtmlParserService")
    public HTMLParserService parserService;
    @Rule
    public WireMockClassRule wireMockRule = wireMockRuleStat;

    String connection = "http://localhost:8084";
    String userName = "str1k6r", password = "that'sME";

    @Before
    public void setup() {
        stubFor(
                post(urlPathMatching(postSingleTeam))
                        .withBasicAuth("str1k6r", "that'sME")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())));
    }

    @Before
    public void testSetLink() throws Exception {
        String s = parserService.setLinkToSiteWithTeams("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");
        System.out.println(s);
        assert s.equals("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");

    }


    @Test
    public void testJsonResult() throws Exception {
        System.out.println(parserService.getPlayersListBySiteWithTeamList(getMap()));

    }

    private Map<URL, Team> getMap() throws MalformedURLException {
        Map<URL, Team> result = new HashMap<>();
        result.put(new URL("https://en.wikipedia.org/wiki/Deportivo_Alav%C3%A9s"), new Team("Deportivo Alavés", 1));
        result.put(new URL("https://en.wikipedia.org/wiki/Athletic_Bilbao"), new Team("Athletic Bilbao", 2));
        return result;
    }

}
