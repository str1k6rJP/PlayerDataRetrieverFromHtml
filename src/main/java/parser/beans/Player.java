package parser.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class Player {

    private int id;

    private String surname;

    private String role;

    private int teamId;

    @JsonCreator
    public Player(@JsonProperty("surname") String nameAndSurname
            , @JsonProperty("role") String role
            , @JsonProperty("teamId") int teamId) {
        setSurname(nameAndSurname);
        setRole(role);
        setTeamId(teamId);
    }
}
