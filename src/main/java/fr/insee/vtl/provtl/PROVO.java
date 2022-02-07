/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.insee.vtl.provtl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class PROVO {

    /**
     *  PROV-O is a W3C Recommendation for the representation of provenance information
     *  <p>
     *	See <a href="https://www.w3.org/TR/prov-o/">W3C</a>.
     *  <p>
     *  <a href="http://www.w3.org/ns/prov#">Base URI and namespace</a>.
     */
    /**
     * The RDF model that holds the PROV entities
     */
    private static final Model m = ModelFactory.createDefaultModel();
    /**
     * The namespace of the PROV vocabulary as a string
     */
    public static final String NS = "http://www.w3.org/ns/prov#";

    /**
     * The namespace of the PROV ontology as a resource
     */
    public static final Resource NAMESPACE = m.createResource(NS);

    /**
     * Returns the namespace of the PROV ontology as a string
     *
     * @return the namespace of the PROV ontology
     */
    public static String getURI() {
        return NS;
    }

    /* ########################################################## *
     * PROV Classes                                               *
     * ########################################################## */


    public static final Resource Activity = m.createResource(NS+"Activity");
    public static final Resource Agent = m.createResource(NS+"Agent");
    public static final Resource Entity = m.createResource(NS+"Entity");

    /* ########################################################## *
     * PROV Properties                                               *
     * ########################################################## */

    public static final Property wasAttributedTo = m.createProperty(NS+"wasAttributedTo");
    public static final Property wasDerivedFrom = m.createProperty(NS+"wasDerivedFrom");
    public static final Property wasGeneratedBy = m.createProperty(NS+"wasGeneratedBy");
}
