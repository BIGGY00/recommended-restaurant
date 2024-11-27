package com.net.service;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
// import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
// import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.rdf.model.Model;
// import org.apache.jena.rdf.model.InfModel;
// import org.apache.jena.util.FileManager;
import org.springframework.stereotype.Service;
import java.io.InputStream;
// import java.util.List;

@Service
public class OntologyService {
    private OntModel ontologyModel;

    public OntologyService() {
        // Load ontology using class loader to handle resource paths better
        ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
    
        // Using getClass().getClassLoader() to load from resources
        InputStream ontologyStream = getClass().getClassLoader().getResourceAsStream("RestaurantOntologyFinal.rdf");
        
        if (ontologyStream == null) {
            // Log an error if the file is not found
            throw new RuntimeException("Ontology file 'RestaurantOntologyFinal.rdf' not found in resources");
        }
    
        try {
            // Try reading the ontology
            ontologyModel.read(ontologyStream, null);
        } catch (Exception e) {
            // Log the exception that occurred during RDF parsing
            throw new RuntimeException("Error reading ontology file 'RestaurantOntologyFinal.rdf'", e);
        }
    }
    
    

    // public InfModel applyRules(String rulesFile) {
    //     // Load rules using class loader
    //     InputStream rulesStream = getClass().getClassLoader().getResourceAsStream(rulesFile);
    //     if (rulesStream == null) {
    //         throw new RuntimeException("Rules file '" + rulesFile + "' not found in resources");
    //     }

    //     List<Rule> ruleList = Rule.rulesFromURL(rulesFile);
    //     GenericRuleReasoner reasoner = new GenericRuleReasoner(ruleList);
    //     return ModelFactory.createInfModel(reasoner, ontologyModel);
    // }

    public Model getOntologyModel() {
        return ontologyModel;
    }
}
