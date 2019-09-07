package parser.database.repositories;

import parser.database.tables.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Provides means of use {@link Team} entities
 *
 *
 *
 * @author str1k6rJP
 * @version 1.0.0
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {


    /**
     * Returns an {@link Optional<Team>} entity which embeds <code>Team</code> entity corresponding to value of <code>teamName</code>
     *
     * @param teamName value of #team_name field of row stored in table assigned to <code>Team</code>
     * @return <code>Team</code> ; <code>null</code> if no appropriate entities were found
     */
    Team findByTeamName(String teamName);
}
