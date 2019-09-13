package parser.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ParserControllerTest {

    @Autowired
    public MockMvc mockMvc;


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
                .content("[{\"surname\":\"reallySurname\",\"role\":\"midfielder\",\"teamId\":\"1\"}" +
                        ",{\"surname\":\"secondSurname\",\"role\":\"hz\",\"teamId\":\"1\"}"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
