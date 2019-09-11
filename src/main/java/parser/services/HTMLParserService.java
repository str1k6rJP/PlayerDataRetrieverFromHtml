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

    private String linkToSiteWithTeams="https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain";

    /**
     * Returns web document by hyper reference predefined or defined by {@link #setLinkToSiteWithTeams(String)}
     *
     * @return webpage
     */
    public Document getWebDoc() {
        try {
           return Jsoup.connect(linkToSiteWithTeams).get();
        } catch (IOException e) {
            try {
                return Jsoup.connect(lastURLToTeamList).get();
            } catch (IOException e1) {
                e.printStackTrace();
                System.out.println("\n\n\n");
                e1.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns list of all the players templates retrieved by this parser method.
     * Mechanics lays down in retrieving the whole html document with table containing teams' names and hyperlink
     * to its' own pages containing information about players.
     *
     * @return list of players retrieved from the web document
     */
    public List<String> getPlayersStringBySiteWithTeamList() {

        Document document = getWebDoc();
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
        lastURLToTeamList = linkToSiteWithTeams;
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
     * (!!NOTE!! The list of players would contain only player models built to be set into database. They doesn't contain
     * its' own id and ARE NOT SET into database yet)
     *
     * @param playerLayouts templates for <code>Player</code> models creation
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

    /**
     * Retorns JSON string containing all the players' entities
     *
     * @param playerLayouts list of all the player string layouts
     * @return JSON string
     */
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

    /**
     * Sets link to be used when method {@link #getWebDoc()} called
     *
     * @param linkToSiteWithTeams link to site with table containing teams and references to its'pages
     * @return link contained by <code>this.linkToSiteWithTeams</code> after method's execution
     */
    public String setLinkToSiteWithTeams(String linkToSiteWithTeams) {
        this.linkToSiteWithTeams = linkToSiteWithTeams;
        return linkToSiteWithTeams;
    }
}
