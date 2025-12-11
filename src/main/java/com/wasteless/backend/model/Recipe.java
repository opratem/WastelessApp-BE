package com.wasteless.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spoonacular_id", unique = true)
    private Long spoonacularId; // ID from Spoonacular API

    private String title;

    @Column(length = 1000)
    private String image;

    @Column(name = "ready_in_minutes")
    private Integer readyInMinutes;

    private Integer servings;

    @Column(length = 5000)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recipe_used_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "used_ingredient")
    private List<String> usedIngredients = new ArrayList<>(); // Ingredients user has

    @ElementCollection
    @CollectionTable(name = "recipe_missed_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "missed_ingredient")
    private List<String> missedIngredients = new ArrayList<>(); // Ingredients user needs

    @Column(name = "used_ingredient_count")
    private Integer usedIngredientCount = 0;

    @Column(name = "missed_ingredient_count")
    private Integer missedIngredientCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;

    @Column(name = "cooked_at")
    private LocalDateTime cookedAt;

    @Column(name = "is_cooked")
    private Boolean isCooked = false;
}
