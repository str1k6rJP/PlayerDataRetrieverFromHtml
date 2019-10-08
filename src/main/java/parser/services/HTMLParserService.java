package parser.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import parser.Application;
import parser.beans.Player;
import parser.beans.Team;
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;
import parser.services.client.implementations.AbstractHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author str1k6rJP
 * @version 1.0.0
 */
@Service
@Slf4j
public class HTMLParserService {


    private AbstractHttpClient httpClient;

    private List<String> teamList;

    private List<String> playerList;

    private String lastURLToTeamList;

    private String linkToSiteWithTeams;

    private int sizeOfArrayDesiredToBeSet = 500;

    public void setHttpClient(AbstractHttpClient httpClient) {
        this.httpClient = httpClient;
    }

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
    public List<Player> getPlayersStringBySiteWithTeamList() {
        if (teamList == null) {
            teamList = new ArrayList<>(sizeOfArrayDesiredToBeSet);
        }

        Document document = getWebDoc();
        Element laliga = document.select("table.wikitable").first();

        Elements rows = laliga.getElementsByTag("tr");

        List<Player> players = new LinkedList<>();

        for (Element row : rows) {
            if (row.toString().contains("<th>")) {
                continue;
            }

            String[] playersFirstTablePart = null;
            String[] playersSecondTablePart = null;
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
            Team currentTeam;

            try {
                currentTeam = new Team(row.toString().split("title=\"")[1].split("\"")[0]);

                currentTeam.setId(httpClient.saveTeam(currentTeam));
                log.info(String.format("Team instance %s was successfully saved to db",currentTeam.toString()));
            } catch (AuthenticationException e) {
                log.error("Credentials weren't set correctly!!\nPlease reset credentials!",e.getMessage(), e);
                break;
            } catch (IOException e) {
                log.error("IOException has occured!\nThat means something was wrong with performing the data of the current team\nIt will be skipped and the application will continue from next loop."
                , e.getMessage(), e);
                continue;
            }
            players.addAll(getPlayerLayoutsFromHTMLTableArray(playersFirstTablePart, currentTeam.getId()));
            players.addAll(getPlayerLayoutsFromHTMLTableArray(playersSecondTablePart, currentTeam.getId()));

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
        int requiredRowsCounter = 1, playerIndex = 0;
        String role = "";
        String surname = "";
        for (String htmlRow : htmlTableRows) {
            if (playerIndex >= arrayLength) {
                break;
            }
            requiredRowsCounter++;
            switch (requiredRowsCounter) {
                default:
                    break;
                case 4:
                    role = htmlRow.split("title=\"")[1].split("[\"(]")[0];
                    break;
                case 5:
                    surname = htmlRow.split("title=\"")[1].split("[\"(]")[0];

                    playerNameAndRoleRows.add(new Player(surname, role, currentTeamId));
                    requiredRowsCounter = 1;
                    playerIndex++;
                    break;
            }
        }
        return playerNameAndRoleRows;
    }

    /**
     * Returns JSON string containing all the players' entities
     *
     * @param playerLayouts list of all the player string layouts
     * @return JSON string
     */
    public List<Player> getPlayersInJsonFormat(List<Player> playerLayouts) {
        //StringBuilder sb = new StringBuilder();
/*        sb.append('[');
        for (String playerLayout : playerLayouts
        ) {
            String[] values = playerLayout.split("::");
            sb.append("{\"surname\":\"").append(values[1]).append("\",\"role\":\"").append(values[0]).append("\",\"teamId\":\"").append(values[2]).append("\"},");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(']');*/
        return (playerLayouts);
    }

    public String setConnectionParams(String host, String port) {
        if (httpClient.setInitialConnPath(host,port)) {
            return httpClient.getInitialConnectionPath().toString();
        }
        return null;
    }

    public String setUsernamePasswordCredentials(String username, String password) {
        httpClient.setCredentials(username,password);
        return String.format("Username: %s ;\nPassword: %s", httpClient.getUsername(),httpClient.getPassword());
    }

    public boolean savePlayersViaControllerAPI(List<Player> players) throws InvalidInputError {
        try {
            return httpClient.savePlayers(players);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidInputError("An Error occurred while passing data to data storing responsible application : "
                    + e.getMessage() + "\n" + e.getCause());
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new InvalidInputError("Wrong credentials were entered! This source can't be accessed with these username and password : "
                    + e.getMessage() + "\n" + e.getCause());
        }
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
