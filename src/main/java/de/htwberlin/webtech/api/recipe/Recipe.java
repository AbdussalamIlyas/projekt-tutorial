package de.htwberlin.webtech.api.recipe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.htwberlin.webtech.api.ingredient.Ingredient;
import de.htwberlin.webtech.authentication.models.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipes")
public class Recipe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recipe_ingredient",
            joinColumns = { @JoinColumn(name = "recipe_id") },
            inverseJoinColumns = { @JoinColumn(name = "ingredient_id") }
    )
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    //Constructor
    public Recipe() {}

    public Recipe(String name) {
        this.name = name;
    }

    //Getter und Setter

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void removeIngredient(long ingredientId) {
        Ingredient ingredient = this.ingredients.stream().filter(t -> t.getId() == ingredientId).findFirst().orElse(null);
        if (ingredient != null) {
            this.ingredients.remove(ingredient);
            ingredient.getRecipes().remove(this);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
