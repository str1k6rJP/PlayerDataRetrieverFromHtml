package parser.beans;

import lombok.Data;

@Data
public class Team {

    private int id;

    private String teamName;

    public Team(String teamName) {
        setTeamName(teamName);
    }

    public Team() {
        //Necessary for JacksonMarshaller (sending and retrieving objects via SpringBoot's RESTful API
    }

    public Team(String teamName, int id) {
        setId(id);
        setTeamName(teamName);
    }

}
