package repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import models.Book;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.sql.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO books (author, publisher, coverType, publicationDate, pagesNumber, language, genre) VALUES (:author, :publisher, :coverType, :publicationDate, :pagesNumber, :language, :genre)", nativeQuery = true)
    void customInsert(@Param("author") String author,
                      @Param("publisher") String publisher,
                      @Param("coverType") String coverType,
                      @Param("publicationDate") Date publicationDate,
                      @Param("pagesNumber") int pagesNumber,
                      @Param("language") String language,
                      @Param("genre") String genre);

    List<Book> findByTitleContaining(String title);
}
