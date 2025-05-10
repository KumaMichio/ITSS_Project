package repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import models.Book;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO dvds (id, author, publisher, coverType, publicationDate, pagesNumber, language, genre) VALUES (:id, :author, :publisher, :coverType, :publicationDate, :pagesNumber, :language, :genre)", nativeQuery = true)
    void customInsert(@Param("id") long id,
                      @Param("author") String author,
                      @Param("publisher") String publisher,
                      @Param("coverType") String coverType,
                      @Param("publicationDate") LocalDate publicationDate,
                      @Param("pages") int pages,
                      @Param("language") String language
    );

    List<Book> findByTitleContaining(String title);
}
