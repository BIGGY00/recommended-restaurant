package com.net.service;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    // private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    // @Autowired
    // private RulesService rulesService;

    // @PostMapping("/recommend")
    // public List<String> recommendRestaurants(@RequestBody UserInput input) {
    //     // Log the incoming user input
    //     // logger.info("Received user input: runnerType = {}, budget = {}, restaurantType = {}, foodTypes = {}",
    //     //         input.getRunnerType(), input.getBudget(), input.getRestaurantType(), input.getFoodTypes());

    //     // Create SPARQL query based on input
    //     String sparqlQuery = """
    //             PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    //             PREFIX owl: <http://www.w3.org/2002/07/owl#>
    //             PREFIX re: <http://www.semanticweb.org/acer/ontologies/2567/8/restaurantontologyfinal#>

    //             SELECT ?Restaurant
    //             WHERE {
    //             """;

    //     // Log the SPARQL query
    //     // logger.debug("Generated SPARQL query: {}", sparqlQuery);

    //     // Execute query and get results
    //     List<String> restaurantResults = rulesService.executeQuery(sparqlQuery);

    //     // Log the results
    //     // logger.info("Number of restaurants found: {}", restaurantResults.size());

    //     // Return the results as a list of restaurant names (or IDs, depending on your
    //     // data)
    //     return restaurantResults;
    // }

    @RequestMapping("/filter")
    public List<JSONObject> filterRestaurants(@RequestBody UserInput input) {
        String sparqlQuery =
        """
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX owl: <http://www.w3.org/2002/07/owl#>
            PREFIX re: <http://www.semanticweb.org/acer/ontologies/2567/8/restaurantontologyfinal#>

            SELECT DISTINCT ?Restaurant ?RestaurantType ?FoodType ?RestaurantNationality ?CleanBudget ?District ?Carbohydrates ?Protein ?Fat
            WHERE {
                ?Restaurant re:RestaurantName ?RestaurantName.
                ?Restaurant re:hasRestaurantType ?RestaurantType.
                ?Restaurant re:hasFoodType ?FoodType.
                ?Restaurant re:hasRestaurantNationality ?RestaurantNationality.
                ?Restaurant re:Budget ?Budget.
                ?Restaurant re:hasRestaurantPlace ?Location.
                ?Location re:District ?District.
                ?FoodType re:Protein ?Protein.
                ?FoodType re:Carbohydrates ?Carbohydrates.
                ?FoodType re:Fat ?Fat.
                BIND (STR(?Budget) AS ?CleanBudget).
        """;

        if (input.getBudgetMin() != null &&  input.getBudgetMax() != null) {
            sparqlQuery += (String.format("\tFILTER(?Budget >= %s && ?Budget <= %s).\n", input.getBudgetMin(), input.getBudgetMax() ));
        }

        if (input.getRestaurantType() != null) {
            sparqlQuery += (String.format("\tFILTER(?RestaurantType IN (re:%s)).\n", input.getRestaurantType()));
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
        

        sparqlQuery += ("}");

        System.out.println(sparqlQuery);

        List<JSONObject> resultSet = Utils.findRestaurants(sparqlQuery);

        return resultSet;
    }
}
