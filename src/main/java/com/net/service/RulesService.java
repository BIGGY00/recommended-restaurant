package com.net.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RulesService {
    @Autowired
    private OntologyService ontologyService;

    public String executeQuery(String sparqlQuery) {
        Model infModel = ontologyService.getOntologyModel();

        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, infModel)) {
            ResultSet results = qexec.execSelect();
            return ResultSetFormatter.asText(results);
        }
    }
}
