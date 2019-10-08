package parser.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"nameAndSurname", "role", "teamId"})
public class Player {

    private int id;

    private String nameAndSurname;

    private String role;

    private int teamId;

    @JsonCreator
    public Player(@JsonProperty("surname") String surname
            , @JsonProperty("role") String role
            , @JsonProperty("teamId") int teamId) {
        setNameAndSurname(surname);
        setRole(role);
        setTeamId(teamId);
    }
}
