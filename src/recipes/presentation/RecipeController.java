package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.business.Recipe;
import recipes.persistence.RecipeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class RecipeController {
    RecipeRepository recipeRepository;

    @Autowired
    private void setRecipeRepository(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @PostMapping("/api/recipe/new")
    public Map<String, Integer> postRecipe(@RequestBody Recipe recipe) {
        recipe.setDate(LocalDateTime.now());
        recipe = recipeRepository.save(recipe);
        return Collections.singletonMap("id", recipe.getId());
    }

    @GetMapping("/api/recipe/{id}")
    public Recipe getRecipe(@PathVariable int id) {
        return recipeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    @DeleteMapping("/api/recipe/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable int id) {
        recipeRepository.delete(getRecipe(id));
    }

    @PutMapping("/api/recipe/{id}")
    public void putRecipe(@PathVariable int id, @RequestBody Recipe recipe) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        } else {
            recipeRepository.delete(getRecipe(id));
            recipe.setDate(LocalDateTime.now());
            recipeRepository.save(recipe);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Recipe updated");
        }
    }

    @GetMapping("/api/recipe/search/")
    public List<Recipe> findRecipe(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String category) {
        ArrayList<Recipe> matchingRecipes = new ArrayList<>();

        if ((name != null && category != null) || (name == null && category == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passed more than one parameter");
        }

        if (name != null) {
            for (Recipe recipe : recipeRepository.findAll()) {
                if (recipe.getName().toLowerCase().contains(name.toLowerCase())) {
                    matchingRecipes.add(recipe);
                }
            }
        } else {
            for (Recipe recipe : recipeRepository.findAll()) {
                if (recipe.getCategory().toLowerCase().contains(category.toLowerCase())) {
                    matchingRecipes.add(recipe);
                }
            }
        }

        return matchingRecipes;
    }

}