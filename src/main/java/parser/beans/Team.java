package parser.beans;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class Team {

    private int id;

    private String teamName;

    public Team(String teamName) {
        setTeamName(teamName);
    }

    public Team() {

    }

    public Team(String teamName, int id) {
        setId(id);
        setTeamName(teamName);
    }

}
