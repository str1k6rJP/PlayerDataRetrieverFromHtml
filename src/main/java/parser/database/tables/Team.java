package parser.database.tables;

import lombok.Data;

import javax.persistence.*;

/**
 * Model representing in-parser.database entities stored in column stated by <code>@Table</code> annotation
 *
 * @author str1k6rJP
 * @version 1.0.0
 */
@Entity
@Table(name = "teams")
@Data
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "team_name")
    String teamName;

    public Team() {

    }

    /**
     * Is appointed to solve return cases when specific exceptions messages passed to front of user as parameters of this class
     * since it can't be applied to set entity in parser.database because <code>id</code> is unique field
     *
     * @param id       id of assigned error or requested id
     * @param teamName this field is used as placeholder for error message details
     */
    public Team(int id, String teamName) {
        setId(id);
        setTeamName(teamName);
    }

    /**
     * Creates new entity of <code>Team</code>
     * <code>id</code> is generated on runtime since it's declared as autoincremented value
     *
     * @param teamName parameter to be set in the #team_name field of #teams table
     */
    public Team(String teamName) {
        setTeamName(teamName);
    }

}