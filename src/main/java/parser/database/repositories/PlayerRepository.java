package parser.database.repositories;

import parser.database.tables.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides means of use {@link Player} entities
 *
 *
 *
 * @author str1k6rJP
 * @version 1.0.0
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

}