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
@Table(name = "players")
@Data
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "role")
    String role;

    @Column(name = "surname")
    String surname;

    @Column(name = "team_id")
    Integer teamId;

    public Player() {

    }

    /**
     * Is appointed to solve return cases when specific exceptions messages passed to front of user as parameters of this class
     * since it can't be applied to set entity in parser.database because <code>id</code> is unique field
     *
     * @param id      id of assigned error or requested id
     * @param surname this field is used as placeholder for error message details
     */
    public Player(int id, String surname) {
        this.surname = surname;
    }

    /**
     * Constructor building entity according to parameters specified in parentheses and using plugs for unspecified parameters
     * <code>id</code> is specified on runtime as autoincremented value
     *
     * @see #Player(String)
     * @see #Player(String, String)
     * @see #Player(String, String, int)
     */
    public Player(String surname) {
        setSurname(surname);
        setRole("n/a");
        setTeamId(1);
    }

    /**
     * @see #Player(String)
     */
    public Player(String surname, String role) {
        setSurname(surname);
        setRole(role);
        setTeamId(1);
    }

    /**
     * @see #Player(String)
     */
    public Player(String surname, String role, int teamId) {
        setSurname(surname);
        setRole(role);
        setTeamId(teamId);
    }

}
