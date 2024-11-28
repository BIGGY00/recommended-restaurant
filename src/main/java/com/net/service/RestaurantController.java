package com.net.service;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/restaurants")
public class RestaurantController {

    // private static final Logger logger =
    // LoggerFactory.getLogger(RestaurantController.class);

    // @Autowired
    // private RulesService rulesService;

    // @PostMapping("/recommend")
    // public List<String> recommendRestaurants(@RequestBody UserInput input) {
    // // Log the incoming user input
    // // logger.info("Received user input: runnerType = {}, budget = {},
    // restaurantType = {}, foodTypes = {}",
    // // input.getRunnerType(), input.getBudget(), input.getRestaurantType(),
    // input.getFoodTypes());

    // // Create SPARQL query based on input
    // String sparqlQuery = """
    // PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    // PREFIX owl: <http://www.w3.org/2002/07/owl#>
    // PREFIX re:
    // <http://www.semanticweb.org/acer/ontologies/2567/8/restaurantontologyfinal#>

    // SELECT ?Restaurant
    // WHERE {
    // """;

    // // Log the SPARQL query
    // // logger.debug("Generated SPARQL query: {}", sparqlQuery);

    // // Execute query and get results
    // List<String> restaurantResults = rulesService.executeQuery(sparqlQuery);

    // // Log the results
    // // logger.info("Number of restaurants found: {}", restaurantResults.size());

    // // Return the results as a list of restaurant names (or IDs, depending on
    // your
    // // data)
    // return restaurantResults;
    // }
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

        // If Budget parameters are provided, include the FILTER in the subquery
        String budgetFilter = "";
        if (input.getBudgetMin() != null && input.getBudgetMax() != null) {
            budgetFilter = String.format("FILTER(?Budget >= %s && ?Budget <= %s).", input.getBudgetMin(),
                    input.getBudgetMax());
        }

        // Replace placeholder with the actual FILTER condition if needed
        sparqlQuery = String.format(sparqlQuery, budgetFilter);

        // Adding other filters based on user input
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

        // Close the query
        sparqlQuery += "}";

        // Debug: Print the generated query
        System.out.println(sparqlQuery);

        // Execute the query
        List<JSONObject> resultSet = Utils.findRestaurants(sparqlQuery);
        return resultSet;
    }

}
