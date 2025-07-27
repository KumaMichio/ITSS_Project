package repositories;

import jakarta.transaction.Transactional;
import models.CDLP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CDLPRepository extends JpaRepository<CDLP, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO dvds (artist, recordLabel, trackList) VALUES (:artist, :recordLabel,:trackList)", nativeQuery = true)
    void customInsert(@Param("artist") String artist,
                      @Param("recordLabel") String recordLabel,
                      @Param("trackList") String trackList
                      );
    List<CDLP> findByTitleContaining(String title);
}
