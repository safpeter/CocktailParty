package com.codecool.cocktail.controller;

import com.codecool.cocktail.data.CocktailRepository;
import com.codecool.cocktail.data.RatingRepository;
import com.codecool.cocktail.data.UserRepository;
import com.codecool.cocktail.model.*;
import com.codecool.cocktail.service.IngredientsMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

@Slf4j
@CrossOrigin
@RestController
public class Controller {

    @Autowired
     RatingRepository ratingRepository;

    @Autowired
    IngredientsMaker ingredientsMaker;


    @Autowired
    CocktailRepository cocktailRepository;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/")
    public Set<String> mainPage() {
        return cocktailRepository.getAllCocktailNames();
    }

    @GetMapping("/cocktail/{cocktailName}")
    public Cocktail getCocktailByName(@PathVariable(value = "cocktailName") String cocktailName) {
        return cocktailRepository.getCocktailByName(cocktailName);
    }

    @GetMapping("/all-ingredients")
    public Set<String> csikocsor() {
        ingredientsMaker.generateIngredients();
        return ingredientsMaker.getAllIngredients();
    }

    @GetMapping("/ratings/{cocktailName}")
    public Rating[]  getRatingByCocktailName(@PathVariable("cocktailName") String cocktailName){
        Long cocktailId = cocktailRepository.getIdByName(cocktailName);
        return ratingRepository.getRatingsByCocktailId(cocktailId);
    }

    @GetMapping("/avgrating/{cocktailName}")
    public double getAverageRating(@PathVariable("cocktailName") String cocktailName){
        Long cocktailId = cocktailRepository.getIdByName(cocktailName);
        Rating[] ratings =  ratingRepository.getRatingsByCocktailId(cocktailId);
        double sumRating = 0.0;
        for (Rating rating: ratings) {
            sumRating += rating.getRating();
        }
        return sumRating/ratings.length;
    }

    @PostMapping("/newrating/{cocktailName}")
    public boolean saveNewRating(@PathVariable("cocktailName") String cocktailName, @RequestBody NewRating newRating) {
        Long cocktailId = cocktailRepository.getIdByName(cocktailName);
       AppUser appUser = userRepository.getAppUserByUsername(newRating.getUserName());
        ratingRepository.saveAndFlush(Rating.builder()
                .cocktailId(cocktailId)
                .userId(appUser.getId())
                .userName(appUser.getUsername())
                .rating(newRating.getRating())
                .comment(newRating.getComment())
                .date(LocalDate.now())
                .build());
        return true;
    }

}