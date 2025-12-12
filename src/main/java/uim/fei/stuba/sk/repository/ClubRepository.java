package uim.fei.stuba.sk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uim.fei.stuba.sk.model.Club;

import java.util.List;


public interface ClubRepository extends JpaRepository<Club, Long> {
    @Query("SELECT c FROM Club c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Club> searchClubs(@Param("query") String query);
}