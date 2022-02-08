package fr.insee.vtl.provtl;

import fr.insee.vtl.parser.VtlBaseListener;
import fr.insee.vtl.parser.VtlLexer;
import fr.insee.vtl.parser.VtlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvenanceProvider {

    Logger logger = LogManager.getLogger();

    // Return a Jena model from a VTL expression
    // Use directly Antlr
    //        unionData := union(ds1[keep id, measure1, measure2], ds2[keep id, measure1, measure2]);
    //        ds1_keep := ds1[keep id, color];
    //        joinData := left_join(unionData, ds1_keep);
    //
    // prendre statement, voir eval dans l'engine (VtlScriptEngine)
    // expression en arbre
    // listener mieux que visitor
    // enlever toutes les fonctions dans la grammaire pour optimiser

    /**
     * Returns basic lineage information corresponding to a VTL script.
     * The information will only contain derivations from right-side variables
     * to left-side variables.
     *
     * @param script the VTL script
     * @return a Jena model containing the lineage information.
     */
    public Model getOverallLineage(String script) {

        // Check if expression is affectation
        // Retrieve left variable and right variables
        // Create triples: DS1 prov:wasGeneratedFrom DSn

        CodePointCharStream stream = CharStreams.fromString(script);
        VtlLexer lexer = new VtlLexer(stream);
        VtlParser parser = new VtlParser(new CommonTokenStream(lexer));

        logger.debug("Starting search for derivation links in script: '" + script + "'");
        ParseTreeWalker walker = new ParseTreeWalker();
        ProvenanceListener listener = new ProvenanceListener();
        walker.walk(listener, parser.start());

        Map<String, List<String>> derivationLinks = listener.getLinks();
        if (derivationLinks.isEmpty()) {
            logger.debug("No derivation links found in script");
            return null;
        }
        logger.debug("Derivation links found: " + listener.getLinks());

        Model model = ModelFactory.createDefaultModel();
        derivationLinks.forEach((key, value) -> {
            Resource derived = model.createResource(getURI(key));
            logger.debug("Creating 'wasDerivedFrom' statements for resource " + derived.getURI());
            for (String x : value) {
                derived.addProperty(PROVO.wasDerivedFrom, model.createResource(getURI(x)));
            }
        });

        return model;
    }

    public static String getURI(String variableName) {
        // Very basic implementation for now. Are VTL variable names URI-safe?
        return "http://example.org/variable/" + variableName;
    }

    // More complicated cases: qualified derivation (https://www.w3.org/TR/prov-o/#qualifiedDerivation)
    // :ds1 a prov:Entity;
    //      prov:wasDerivedFrom :ds2;
    //      prov:qualifiedDerivation [
    //           a prov:Derivation;
    //           prov:entity :ds2
    //           prov:hadActivity :trevas_execution_123456 ;
    //      ]

    // TODO This class should probably not be static
    static class ProvenanceListener extends VtlBaseListener {

        Logger logger = LogManager.getLogger();
        private String currentDerived;
        private Map<String, List<String>> links = new HashMap<>();

        @Override
        public void enterTemporaryAssignment(VtlParser.TemporaryAssignmentContext context) {

            logger.debug("Entering temporary assignment");
            // First child of context is a VarID context, which correspond to a new derived variable
            currentDerived = null;
        }

        @Override
        public void enterVarID(VtlParser.VarIDContext context) {

            String variable = context.children.get(0).toString();
            logger.debug("Entering varId for variable " + variable);
            if (currentDerived == null) {
                // If no current derived variable is defined, this is the new one
                currentDerived = variable;
                links.put(currentDerived, new ArrayList<>());
            } else {
                // Else the variable is contributes to the current derived variable
                links.get(currentDerived).add(variable);
            }
        }

        /**
         * Returns the derivation links captured by the listener.
         * Will return an empty map if no links are found.
         *
         * @return a map with derived variable names as keys and lists of contributing variables as values.
         */
        public Map<String, List<String>> getLinks() {
            return links;
        }
    }
}
