package repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import models.DVD;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DVDRepository extends JpaRepository<DVD, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO dvds (id, disc_type, director, runtime, studio, language, subtitles, release_date) VALUES (:id, :discType, :director,:runtime, :studio, :language, :subtitles, :releaseDate)", nativeQuery = true)
    void customInsert(@Param("id") Long id,
                      @Param("discType") String discType,
                      @Param("director") String director,
                      @Param("runtime") int runtime,
                      @Param("studio") String studio,
                      @Param("language") String language,
                      @Param("subtitles") String subtitles,
                      @Param("releaseDate") String releaseDate);

    List<DVD> findByTitleContaining(String title);
}
