package com.net.service;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.*;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private static OntologyService ontologyService = new OntologyService();

    private static String getNameFromURI(String uri) {
        if (uri != null && uri.contains("#")) {
            return uri.substring(uri.lastIndexOf("#") + 1);
        }
        return uri;
    }

    public static List<JSONObject> findRestaurants(String queryString) {
        Model ontoModel = ontologyService.getOntologyModel();
        if (ontoModel == null) {
            logger.error("Ontology model is not available.");
            return new ArrayList<>();
        }

        Query query = QueryFactory.create(queryString);

        QueryExecution qexec = QueryExecutionFactory.create(query, ontoModel);
        ResultSet resultSet = qexec.execSelect();

        if (resultSet == null || !resultSet.hasNext()) {
            System.out.println("No results found for the query.");
            return new ArrayList<>();
        }

        List<JSONObject> list = new ArrayList<>();
        int x = 0;

        while (resultSet.hasNext()) {
            x++;
            JSONObject obj = new JSONObject();
            QuerySolution solution = resultSet.nextSolution();

            System.out.println("Result " + x + ":");
            solution.varNames().forEachRemaining(var -> {
                System.out.println("  " + var + ": " + solution.get(var));
            });

            String restaurantName = solution.get("Restaurant") != null ? solution.get("Restaurant").toString()
                    : "No Name";
            String restaurantType = solution.get("RestaurantType") != null ? solution.get("RestaurantType").toString()
                    : "No Type";
            String foodType = solution.get("FoodType") != null ? solution.get("FoodType").toString() : "No Food Type";
            String restaurantNationality = solution.get("RestaurantNationality") != null
                    ? solution.get("RestaurantNationality").toString()
                    : "No Nationality";
            String district = solution.get("District") != null ? solution.get("District").toString() : "No District";
            String protein = solution.get("Protein") != null ? solution.get("Protein").toString() : "No Protein Info";
            String carbohydrates = solution.get("Carbohydrates") != null
                    ? solution.get("Carbohydrates").toString()
                    : "No Carbohydrates Info";
            String fat = solution.get("Fat") != null ? solution.get("Fat").toString() : "No Fat Info";
            String cleanMinBudget = solution.get("CleanMinBudget") != null ? solution.get("CleanMinBudget").toString()
                    : "No Min Budget";
            String cleanMaxBudget = solution.get("CleanMaxBudget") != null ? solution.get("CleanMaxBudget").toString()
                    : "No Max Budget";

            obj.put("id", x);
            obj.put("restaurant_name", getNameFromURI(restaurantName));
            obj.put("restaurant_type", getNameFromURI(restaurantType));
            obj.put("food_type", getNameFromURI(foodType));
            obj.put("restaurant_nationality", getNameFromURI(restaurantNationality));
            obj.put("district", district);
            obj.put("protein", protein);
            obj.put("carbohydrates", carbohydrates);
            obj.put("fat", fat);
            obj.put("clean_min_budget", cleanMinBudget);
            obj.put("clean_max_budget", cleanMaxBudget);

            list.add(obj);
        }

        return list;
    }

}
