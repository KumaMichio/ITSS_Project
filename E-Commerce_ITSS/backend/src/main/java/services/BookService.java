package services;

import exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookService {
    @Autowired
    private final BookRepository bookRepository;

    public void addBook(Book book) {
        bookRepository.customInsert(
                book.getAuthor(),
                book.getPublisher(),
                book.getCoverType(),
                book.getPublicationDate(),
                book.getPagesNumber(),
                book.getLanguage(),
                book.getGenre()
        );
    }

    public List<Book> getBookByTitleContaining(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book updateBook(int id, Book book) {
        Book book1 = bookRepository.findById(id).orElse(null);
        if (book1 != null) {
            book1.setAuthor(book.getAuthor());
            book1.setCoverType(book.getCoverType());
            book1.setPublisher(book.getPublisher());
            book1.setPublicationDate(book.getPublicationDate());
            book1.setPagesNumber(book.getPagesNumber());
            book1.setLanguage(book.getLanguage());
            return bookRepository.save(book1);
        }
        else {
            throw new ResourceNotFoundException("Book not found with id " + id);
        }
    }

    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    public void deleteAllBooks() {
        bookRepository.deleteAll();
    }

}
