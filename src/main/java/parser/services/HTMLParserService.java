package parser.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parser.Application;
import parser.database.repositories.PlayerRepository;
import parser.database.repositories.TeamRepository;
import parser.database.tables.Player;
import parser.database.tables.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author str1k6rJP
 * @version 1.0.0
 */
@Service
public class HTMLParserService {


    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private String lastURLToTeamList;

    public Document getWebDoc(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns list of all the players which have been managed to be retrieved by this parser method.
     * Mechanics lays down in retrieving the whole html document with table containing teams' names and hyperlink
     * to its' own pages containing information about players.
     * Then the team is to be added to `teams` table and if succeeds, then the team's page is retrieved and parsed in order to retrieve players' data
     * Every player successfully retrieved from the page and set to `players` table is to be added to the list to be retrieved
     *
     * @param urlToTeamLists hyper reference to the Wikipedia page containing table of Spain football clubs
     * @return list of players successfully set to the database in form they were retrieved from the one
     */
    public List<String> getPlayersStringBySiteWithTeamList(String urlToTeamLists) {
        lastURLToTeamList = urlToTeamLists;
        Document document = getWebDoc(lastURLToTeamList);
        Element laliga = document.select("table.wikitable").first();

        Elements rows = laliga.getElementsByTag("tr");
if (Application.consoleWriterMode) {
    System.out.println("Html rows with team names specification");
    System.out.println(rows);
}
        List<String> playerStrings = new LinkedList<>();

        for (Element row : rows) {
            if (row.toString().contains("<th>")) {
                continue;
            }

            String[] playersFirstTablePart = null, playersSecondTablePart = null;
            try {
                String[] playersTable
                        = Jsoup.connect(String.format("https://en.wikipedia.org%s", row.toString().split("\\n+")[1].split(">")[1].split("\"")[1]))
                        .get().toString().split("<h[23]>");
                for (String s : playersTable
                ) {
                    if (s.contains("\"Current_squad\"")) {
                        playersFirstTablePart = s.split("<tbody>")[2].split("</td>");
                        playersSecondTablePart = s.split("<tbody>")[3].split("</td>");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(row.text());
            int currentTeamId ;
            //currentTeamId= teamRepository.save(new Team(row.text().split("\\s+")[0])).getId();
            currentTeamId= teamRepository.save(new Team(row.toString().split("title=\"")[1].split("\"")[0])).getId();
            playerStrings.addAll(getPlayerLayoutsFromHTMLTableArray(playersFirstTablePart,currentTeamId));
            playerStrings.addAll(getPlayerLayoutsFromHTMLTableArray(playersSecondTablePart,currentTeamId));


        }
        return playerStrings;
    }

    public List<Player> saveDirectlyToDatabase(List<Player> playerPrefabs){
        return playerRepository.saveAll(playerPrefabs);
    }

    /**
     * Returns intermediate data which is represented by partial data retrieved from the web-page in form convenient for parsing
     * and creating <code>Player</code> entities based on it
     *
     * @param htmlTableRows array containing html players' table split into rows
     * @param currentTeamId id retrieved from current team set into database
     * @return convenient layouts for <code>Player</code> entities creation
     */
    private List<String> getPlayerLayoutsFromHTMLTableArray(String[] htmlTableRows,int currentTeamId) {
        int arrayLength= htmlTableRows.length / 4;
        List<String> playerNameAndRoleRows = new ArrayList<>(arrayLength);
        int requiredRowsCounter = 1, playerIndex = 0;
        String temp="";

        for (String htmlRow : htmlTableRows) {
            if (playerIndex>=arrayLength){
                break;
            }
            requiredRowsCounter++;
            switch (requiredRowsCounter) {
                default:
                    break;
                case 4:
                    temp=htmlRow.split("title=\"")[1].split("[\"(]")[0]+"::";
                    break;
                case 5:
                    temp += htmlRow.split("title=\"")[1].split("[\"(]")[0];
                    temp += "::"+currentTeamId;
                    playerNameAndRoleRows.add(temp);
                    temp="";
                    requiredRowsCounter = 1;
                    playerIndex++;
                    break;
            }
        }
        return playerNameAndRoleRows;
    }

    /**
     * Returns <code>List<Player></code> containing all the players retrieved from the current club' html file
     * (!!NOTE!! The list of players would contain only player models built5o set into database. They doesn't contain
     * its' own id and ARE NOT SET into database yet)
     *
     * @param playerLayouts templates for <code>Player</code> models creation
     *
     * @return List<Player>
     */
    public List<Player> getPlayersByPlayerLayouts(List<String> playerLayouts){
         List<Player> playerPrefabs = new ArrayList<>(playerLayouts.size());
        for (int i = 0; i < playerLayouts.size(); i++) {
            String[] temp = playerLayouts.get(i).split("::");
            playerPrefabs.add(new Player(temp[1],temp[0],Integer.parseInt(temp[2])));
        }
        return playerPrefabs;
    }

    public String getPlayersInJsonFormat(List<String> playerLayouts){
        StringBuilder sb= new StringBuilder();

        sb.append('[');
        for (String playerLayout:playerLayouts
             ) {
            String[] values=playerLayout.split("::");
            sb.append("{surname:").append(values[1]).append(",role:").append(values[0]).append(",teamId:").append(values[2]).append("},");
        }
        if (sb.charAt(sb.length()-1)==',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(']');
return sb.toString();
    }


}
