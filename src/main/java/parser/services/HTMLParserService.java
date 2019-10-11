package parser.services;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import parser.beans.Player;
import parser.beans.Team;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author str1k6rJP
 * @version 1.0.0
 */
@Service
@Slf4j
public class HTMLParserService {

    private static final int TEAM_PLAYERS_MULTIPLIER = 30;
    private static final String TITLE_ATTR = "title=\"";

    private String lastURLToTeamList;

    private String linkToSiteWithTeams;


    /**
     * Returns web document by hyper reference predefined or defined by {@link #setLinkToSiteWithTeams(String)}
     *
     * @return webpage
     */
    public Document getWebDoc() {
        try {
            return Jsoup.connect(linkToSiteWithTeams).get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            try {
                return Jsoup.connect(lastURLToTeamList).get();
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
        return null;
    }


    public Map<URI, Team> retrieveTeams(@NotNull Document document) {
        Element laliga = document.select("table.wikitable").first();
        if (laliga == null) {
            return null;
        }
        HashMap<URI, Team> returnMap = new HashMap<>();
        Elements rows = laliga.getElementsByTag("tr");
        URI url;
        Team currentTeam;

        for (Element row : rows) {
            if (row.toString().contains("<th>")) {
                continue;
            }
            currentTeam = new Team(row.toString().split(TITLE_ATTR)[1].split("\"")[0]);

            try {
                url = new URI(String.format("https://en.wikipedia.org%s", row.toString().split("\n+")[1].split(">")[1].split("\"")[1]));

                returnMap.put(url, currentTeam);
            } catch (URISyntaxException e) {
                log.error(e.getMessage(), e);
            }

        }
        return returnMap;
    }

    /**
     * Returns list of all the players templates retrieved by this parser method.
     * Mechanics lays down in retrieving the whole html document with table containing teams' names and hyperlink
     * to its' own pages containing information about players.
     *
     * @return list of players retrieved from the web document
     */
    public List<Player> getPlayersListBySiteWithTeamList(Map<URI, Team> savedTeamMap) {
        List<Player> players = new ArrayList<>(savedTeamMap.size() * TEAM_PLAYERS_MULTIPLIER);

        String[] playersFirstTablePart = null;
        String[] playersSecondTablePart = null;

        for (Map.Entry<URI, Team> entry : savedTeamMap.entrySet()) {

            if (entry.getValue().getId() < 1) {
                log.error("Unsaved Team instance was accidentally detected in the map!!!\\%nThough it will be skipped, but this is a major issue so please connect the author at dmytro.maliovanyi@gmail.com\\%nIt would be reviewed and resolved");
                continue;
            }

            String[] playersTable;
            try {
                playersTable = Jsoup.connect(entry.getKey().toString())
                        .get().toString().split("<h[23]>");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                continue;
            }
            for (String s : playersTable
            ) {
                if (s.contains("\"Current_squad\"")) {
                    playersFirstTablePart = s.split("<tbody>")[2].split("</td>");
                    playersSecondTablePart = s.split("<tbody>")[3].split("</td>");
                    break;
                }
            }

            players.addAll(getPlayerLayoutsFromHTMLTableArray(playersFirstTablePart, entry.getValue().getId()));
            players.addAll(getPlayerLayoutsFromHTMLTableArray(playersSecondTablePart, entry.getValue().getId()));
        }

        lastURLToTeamList = linkToSiteWithTeams;
        return players;
    }

    /**
     * Returns intermediate data which is represented by partial data retrieved from the web-page in form convenient for parsing
     * and creating <code>Player</code> entities based on it
     *
     * @param htmlTableRows array containing html players' table split into rows
     * @param currentTeamId id retrieved from current team set into database
     * @return convenient layouts for <code>Player</code> entities creation
     */
    private List<Player> getPlayerLayoutsFromHTMLTableArray(String[] htmlTableRows, int currentTeamId) {
        int arrayLength = htmlTableRows.length / 4;
        List<Player> playerNameAndRoleRows = new ArrayList<>(arrayLength);
        int requiredRowsCounter = 1;
        int playerIndex = 0;
        String role = "";
        String surname;
        for (String htmlRow : htmlTableRows) {
            if (playerIndex >= arrayLength) {
                break;
            }
            requiredRowsCounter++;
            switch (requiredRowsCounter) {

                case 4:
                    role = htmlRow.split(TITLE_ATTR)[1].split("[\"(]")[0];
                    break;
                case 5:
                    surname = htmlRow.split(TITLE_ATTR)[1].split("[\"(]")[0];

                    playerNameAndRoleRows.add(new Player(surname, role, currentTeamId));
                    requiredRowsCounter = 1;
                    playerIndex++;
                    break;
                default:
                    break;
            }
        }
        return playerNameAndRoleRows;
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

    public String getLinkToSiteWithTeams() {
        return linkToSiteWithTeams;
    }
}
