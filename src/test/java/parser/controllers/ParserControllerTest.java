package parser.controllers;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import parser.services.client.ClientTest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ParserControllerTest {

    public static final int STUB_PORT = 8083;
    public static final String postAllPlayers = "/player/add", postSingleTeam = "http://localhost:8083/team/*", jsonBodyForPlayerPost = "[{\"surname\":\"reallySurname\",\"role\":\"midfielder\",\"teamId\":\"1\"}" +
            ",{\"surname\":\"secondSurname\",\"role\":\"hz\",\"teamId\":\"1\"}]", teamSaveResponse = "{\"id\":\"1\",\"teamName\":\"Success!\"}";
    @ClassRule
    public static WireMockClassRule wireMockRuleStat = new WireMockClassRule(STUB_PORT);
    @Autowired
    public MockMvc mockMvc;
    @Rule
    public WireMockClassRule wireMockRule = wireMockRuleStat;


    @Before
    public void setup() {
        stubFor(post(urlEqualTo(postAllPlayers)).withRequestBody(equalToJson(ClientTest.jsonPlayers))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));
        stubFor(
                post(urlMatching(postSingleTeam))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(teamSaveResponse)));
    }

    @Test
    public void testSetNewTeamListSite() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/autofill/setLink?link=https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").exists())
                .andExpect(jsonPath("$.link").value("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain"));
    }

    @Test
    public void testSetCurrentUserCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/autofill/log-as/myName/pAsSw0rd")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("myName"))
                .andExpect(jsonPath("$.password").value("pAsSw0rd"));
    }

    @Test
    public void testSetServiceLink() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/autofill/service/localhost/8080")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.s-link").value("http://localhost:8080/"));
    }

    @Test
    public void advancedTestSetServiceLink() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/autofill/service?p=localhost:8083")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.s-link").value("http://localhost:8083/"));
    }

    @Test
    public void testPutPlayersToAppViaAPI() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .post("/autofill")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ClientTest.jsonPlayers))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
