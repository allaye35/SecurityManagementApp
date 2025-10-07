// src/main/java/com/boulevardsecurity/securitymanagementapp/controller/ArticleContratController.java
package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.ArticleContratCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.ArticleContratDto;
import com.boulevardsecurity.securitymanagementapp.service.ArticleContratService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles-contrat")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ArticleContratController {

    private final ArticleContratService service;

    @GetMapping
    public ResponseEntity<List<ArticleContratDto>> getAll() {
        List<ArticleContratDto> list = service.getAllArticles();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleContratDto> getById(@PathVariable Long id) {
        return service.getArticleById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/contrat/{contratId}")
    public ResponseEntity<List<ArticleContratDto>> getByContrat(@PathVariable Long contratId) {
        List<ArticleContratDto> list = service.getArticlesByContrat(contratId);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<ArticleContratDto> create(@RequestBody ArticleContratCreateDto dto) {
        ArticleContratDto created = service.createArticle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleContratDto> update(
            @PathVariable Long id,
            @RequestBody ArticleContratCreateDto dto
    ) {
        try {
            ArticleContratDto updated = service.updateArticle(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
