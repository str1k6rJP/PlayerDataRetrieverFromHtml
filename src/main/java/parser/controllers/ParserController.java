package parser.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import parser.database.tables.Player;
import parser.services.HTMLParserService;

import java.util.List;

@RestController
@RequestMapping("/autofill")
public class ParserController {

    @Autowired
    private HTMLParserService parserService;


    @PostMapping
    public List<Player> putPlayersToAppViaAPI() {
        return null;
    }

    @PostMapping("/setLink/{link}")
    public String setNewTeamListSite(@RequestParam String link) {
        return null;
    }
}
