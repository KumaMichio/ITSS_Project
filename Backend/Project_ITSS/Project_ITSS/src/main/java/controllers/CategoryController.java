package controllers;


import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/categories")
public class CategoryController {
    // Hiện tất cả các categories

    @GetMapping("")
    public ResponseEntity<String> getAllCategories(){
        return ResponseEntity.ok("hehe");
    }
}
