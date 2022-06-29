package de.htwberlin.webtech.api.recipe;

import de.htwberlin.webtech.api.ingredient.Ingredient;
import de.htwberlin.webtech.api.ingredient.IngredientRepository;
import de.htwberlin.webtech.exception.ResourceNotFoundException;
import de.htwberlin.webtech.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>(recipeRepository.findAll());
        if (recipes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @PostMapping("/user/{userId}/recipes")
    public ResponseEntity<Recipe> createRecipe(@PathVariable(value = "userId") Long userId,
                                               @RequestBody Recipe recipeRequest) {
        Recipe recipe = userRepository.findById(userId).map(user -> {
            recipeRequest.setUser(user);
            return recipeRepository.save(recipeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found User with id = " + userId));
        return new ResponseEntity<>(recipe, HttpStatus.CREATED);
    }

    @GetMapping("/recipes/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable("id") long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Recipe with id = " + id));
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    //Add Ingredient to a Recipe
    @PutMapping("/recipes/{recipeId}/ingredients/{ingredientId}")
    ResponseEntity<Recipe> addIngredientToRecipe(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId
    ) {
        Recipe recipe = recipeRepository.findById(recipeId).get();
        Ingredient ingredient = ingredientRepository.findById(ingredientId).get();
        recipe.getIngredients().add(ingredient);
        return new ResponseEntity<>(recipeRepository.save(recipe), HttpStatus.OK);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Recipe> updateComment(@PathVariable("id") long id, @RequestBody Recipe recipeRequest) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RecipeId: " + id + "not found"));
        recipe.setName(recipeRequest.getName());
        recipe.setDirection(recipeRequest.getDirection());
        return new ResponseEntity<>(recipeRepository.save(recipe), HttpStatus.OK);
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<HttpStatus> deleteRecipe(@PathVariable("id") long id) {
        recipeRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/recipes")
    public ResponseEntity<HttpStatus> deleteAllRecipes() {
        recipeRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Recipe and User (One-To-Many Relationship)
    @GetMapping("/user/{userId}/recipes")
    public ResponseEntity<List<Recipe>> getAllRecipesByUserId(@PathVariable(value = "userId") Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Not found User with id = " + userId);
        }
        List<Recipe> recipes = recipeRepository.findByUserId(userId);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}/recipes")
    public ResponseEntity<List<Recipe>> deleteAllRecipesOfUser(@PathVariable(value = "userId") Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Not found User with id = " + userId);
        }
        recipeRepository.deleteByUserId(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
