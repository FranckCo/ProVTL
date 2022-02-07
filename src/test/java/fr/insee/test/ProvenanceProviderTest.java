package fr.insee.test;

import fr.insee.vtl.engine.VtlScriptEngine;
import fr.insee.vtl.provtl.ProvenanceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProvenanceProviderTest {

    private VtlScriptEngine engine;
    private ProvenanceProvider provenanceProvider = new ProvenanceProvider();

    @BeforeEach
    public void setUp() {
        engine = (VtlScriptEngine) new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testSimpleOverallLineage() throws ScriptException {

        String script = "a := b + 42;";

        assertNotNull(provenanceProvider.getOverallLineage(script));
    }

    @Test
    public void testSimpleEval() throws ScriptException {

        String script = "a := b + 42;";

        ScriptContext context = engine.getContext();
        context.setAttribute("b", Integer.valueOf(5), ScriptContext.ENGINE_SCOPE);
        Object result = engine.eval(script);
        assertEquals(result.getClass(), Long.class);
    }

    @Test
    public void testSimpleEvalFailing() throws ScriptException {

        String script = "a := b + 42;";

        Bindings bindings = new SimpleBindings(Map.of("b", 1));
        ScriptContext context = engine.getContext();
        // The next line throws UnsupportedOperationException
        // Problem, it is directly taken from https://inseefr.github.io/Trevas/en/engine/
        context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        Object result = engine.eval(script);
        assertEquals(result.getClass(), Long.class);
    }

    @Test
    public void testComplexScript() {

        // ds2 ___ (keep) ___ (union) ___ unionData ___ (left_join) __ joinData
        // ds1 ___ (keep) __/                         /
        //   \____ (keep) ___ ds1_keep _____________ /

        StringBuilder script = new StringBuilder();
        script.append("unionData := union(ds1[keep id, measure1, measure2], ds2[keep id, measure1, measure2]);");
        script.append("ds1_keep := ds1[keep id, color];");
        script.append("joinData := left_join(unionData, ds1_keep);");
    }
}
