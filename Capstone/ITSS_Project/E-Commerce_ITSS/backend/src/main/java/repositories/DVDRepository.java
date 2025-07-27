package repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import models.DVD;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DVDRepository extends JpaRepository<DVD, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO dvds (disc_type, director, runtime, studio, language, subtitles, release_date) VALUES (:id, :discType, :director,:runtime, :studio, :language, :subtitles, :releaseDate)", nativeQuery = true)
    void customInsert(@Param("discType") String discType,
                      @Param("director") String director,
                      @Param("runtime") int runtime,
                      @Param("studio") String studio,
                      @Param("language") String language,
                      @Param("subtitles") String subtitles,
                      @Param("releaseDate") String releaseDate);

    List<DVD> findByTitleContaining(String title);
}
