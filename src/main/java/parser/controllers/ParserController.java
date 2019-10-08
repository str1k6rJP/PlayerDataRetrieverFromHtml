package parser.controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import parser.beans.Player;
import parser.services.HTMLParserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/autofill")
public class ParserController {

    @Autowired
    @Qualifier("configuredHtmlParserService")
    private HTMLParserService parserService;


    @PostMapping()
    public String putPlayersToAppViaAPI(@RequestBody(required = false) String jsonToSave) throws IOException {
        return String.format("{\"is-success\":\"%s\"}", jsonToSave == null ? parserService
                .savePlayersViaControllerAPI(
                        parserService.getPlayersStringBySiteWithTeamList())
                : parserService
                .savePlayersViaControllerAPI(new ObjectMapper().readValue(jsonToSave, new TypeReference<List<Player>>() {
                })));
    }

    @PostMapping("/setLink")
    public String setNewTeamListSite(@RequestParam String link) {
        return String.format("{\"link\":\"%s\"}", parserService.setLinkToSiteWithTeams(link));
    }

    @PutMapping("/log-as/{username}/{password}")
    public String setCurrentUserCredentials(@PathVariable(name = "username") String userName, @PathVariable(name = "password") String password) {
        UsernamePasswordCredentials credentials = parserService.setUsernamePasswordCredentials(userName, password);
        return String.format("{\"username\":\"%s\",\"password\":\"%s\"}", credentials.getUserName(), credentials.getPassword());
    }

    @PutMapping(value = {"/service", "/service/{var1}/{uvar2}"})
    public String setServiceLink(@PathVariable(name = "var1", required = false) String var1
            , @PathVariable(name = "uvar2", required = false) String var2
            , @RequestParam(name = "p", required = false) String hostColonPort) {
        if (var1 == null || var2 == null) {
            return String.format("{\"s-link\":\"%s\"}", parserService.setConnectionParams(hostColonPort));
        }
        return String.format("{\"s-link\":\"%s\"}", parserService.setConnectionParams(var1, var2));
    }
}

