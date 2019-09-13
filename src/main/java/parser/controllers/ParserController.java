package parser.controllers;


import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parser.services.HTMLParserService;

@RestController
@RequestMapping("/autofill")
public class ParserController {

    @Autowired
    private HTMLParserService parserService;


    @PostMapping()
    public String putPlayersToAppViaAPI(@RequestBody(required = false) String jsonToSave) {
        return String.format("{\"is-success\":\"%s\"}",jsonToSave == null ? parserService
                .savePlayersViaControllerAPI(parserService
                        .getPlayersInJsonFormat(parserService
                                .getPlayersStringBySiteWithTeamList()))
                : parserService
                .savePlayersViaControllerAPI(jsonToSave));
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

    @PutMapping(value = {"/service/{var1}/{uvar2}", "/service"})
    public String setServiceLink(@PathVariable(name = "var1",required = false) String var1
            , @PathVariable(name = "uvar2", required = false) String var2
            , @RequestParam(name = "p",required = false) String hostColonPort) {
        return String.format("{\"s-link\":\"%s\"}", var1==null||var2 == null ? parserService.setConnectionParams(hostColonPort) : parserService.setConnectionParams(var1, var2));
    }
}

