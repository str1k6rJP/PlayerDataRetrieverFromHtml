package parser.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import parser.database.tables.Player;
import parser.services.client.implementations.HttpClient;

import java.util.List;

@RestController
@RequestMapping("/autofill")
public class ParserController {

@Autowired
    HttpClient httpClient;


    @PostMapping
    public List<Player> putPlayersToAppViaAPI(){
        return null;
    }

    @PostMapping("/setLink/{link}")
    public String setNewTeamListSite(@RequestParam String link){
     return null;
    }
}
