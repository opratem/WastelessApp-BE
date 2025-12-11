package com.wasteless.backend.repository;

import com.wasteless.backend.model.Recipe;
import com.wasteless.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Find all recipes saved by a user
    List<Recipe> findByUser(User user);

    // Find all cooked recipes by a user
    List<Recipe> findByUserAndIsCooked(User user, Boolean isCooked);

    // Find a specific recipe by Spoonacular ID and user
    Optional<Recipe> findByUserAndSpoonacularId(User user, Long spoonacularId);

    // Check if user has already saved a recipe from Spoonacular
    boolean existsByUserAndSpoonacularId(User user, Long spoonacularId);
}
