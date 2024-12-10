package com.net.service;

import org.springframework.web.bind.annotation.*;

import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/restaurants")
public class RestaurantController {
    @PostMapping("/filter")
    public List<JSONObject> filterRestaurants(@RequestBody UserInput input) {
        String sparqlQuery = """
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX owl: <http://www.w3.org/2002/07/owl#>
                PREFIX re: <http://www.semanticweb.org/acer/ontologies/2567/8/restaurantontologyfinal#>

                SELECT DISTINCT ?Restaurant ?RestaurantType ?FoodType ?RestaurantNationality ?District
                                ?CleanMinBudget ?CleanMaxBudget ?Carbohydrates ?Protein ?Fat
                WHERE {
                    # Main Query
                    ?Restaurant re:RestaurantName ?RestaurantName.
                    ?Restaurant re:hasRestaurantType ?RestaurantType.
                    ?Restaurant re:hasFoodType ?FoodType.
                    ?Restaurant re:hasRestaurantNationality ?RestaurantNationality.
                    ?Restaurant re:hasRestaurantPlace ?Location.
                    ?Location re:District ?District.
                    ?FoodType re:Protein ?Protein.
                    ?FoodType re:Carbohydrates ?Carbohydrates.
                    ?FoodType re:Fat ?Fat.

                    # Subquery for Budget Aggregation
                    {
                        SELECT ?Restaurant (STR(MIN(?Budget)) AS ?CleanMinBudget) (STR(MAX(?Budget)) AS ?CleanMaxBudget)
                        WHERE {
                            ?Restaurant re:Budget ?Budget.
                            %s
                        }
                        GROUP BY ?Restaurant
                    }
                """;

        String budgetFilter = "";
        if (input.getBudgetMin() != null && input.getBudgetMax() != null) {
            budgetFilter = String.format("FILTER(?Budget >= %s && ?Budget <= %s).", input.getBudgetMin(),
                    input.getBudgetMax());
        }

        sparqlQuery = String.format(sparqlQuery, budgetFilter);

        if (input.getRestaurantType() != null && !input.getRestaurantType().isEmpty()) {
            sparqlQuery += String.format("\tFILTER(?RestaurantType IN (re:%s)).\n", input.getRestaurantType());
        }

        if (input.getFoodTypes() != null && !input.getFoodTypes().isEmpty()) {
            StringBuilder filterClause = new StringBuilder("\tFILTER(?FoodType IN (");
            for (int i = 0; i < input.getFoodTypes().size(); i++) {
                filterClause.append("re:").append(input.getFoodTypes().get(i));
                if (i < input.getFoodTypes().size() - 1) {
                    filterClause.append(", ");
                }
            }
            filterClause.append(")).\n");
            sparqlQuery += filterClause.toString();
        }

        List<String> nutrientConditions = new ArrayList<>();

        if (input.getCarb() != null && !input.getCarb().isEmpty()) {
            nutrientConditions.add(String.format("?Carbohydrates = \"%s\"", input.getCarb()));
        }
        if (input.getProtein() != null && !input.getProtein().isEmpty()) {
            nutrientConditions.add(String.format("?Protein = \"%s\"", input.getProtein()));
        }
        if (input.getFat() != null && !input.getFat().isEmpty()) {
            nutrientConditions.add(String.format("?Fat = \"%s\"", input.getFat()));
        }

        if (!nutrientConditions.isEmpty()) {
            String combinedConditions = String.join(" || ", nutrientConditions);
            sparqlQuery += String.format("\tFILTER(%s).\n", combinedConditions);
        }

        sparqlQuery += "}";

        System.out.println(sparqlQuery);

        List<JSONObject> resultSet = Utils.findRestaurants(sparqlQuery);
        return resultSet;
    }

}
