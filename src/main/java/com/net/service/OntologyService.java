package com.net.service;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;
import java.io.InputStream;

@Service
public class OntologyService {
    private OntModel ontologyModel;

    public OntologyService() {
        ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);

        InputStream ontologyStream = getClass().getClassLoader().getResourceAsStream("RestaurantOntologyFinal.rdf");

        if (ontologyStream == null) {
            throw new RuntimeException("Ontology file 'RestaurantOntologyFinal.rdf' not found in resources");
        }

        try {
            ontologyModel.read(ontologyStream, null);
        } catch (Exception e) {
            throw new RuntimeException("Error reading ontology file 'RestaurantOntologyFinal.rdf'", e);
        }
    }

    public Model getOntologyModel() {
        return ontologyModel;
    }
}
