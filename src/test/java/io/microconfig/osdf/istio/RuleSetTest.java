package io.microconfig.osdf.istio;

import io.microconfig.osdf.istio.rules.HeaderRule;
import io.microconfig.osdf.istio.rules.MainRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.microconfig.osdf.istio.Destination.destination;
import static io.microconfig.osdf.istio.WeightRoute.weightRoute;
import static io.microconfig.osdf.istio.rules.HeaderRule.headerRule;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleSetTest {
    private final Destination first = destination("host", "v1");
    private final Destination second = destination("host", "v2");

    @Test
    void testSerialization() {
        HeaderRule headerRule = headerRule(second, "headerName", "headerValue");
        MainRule mainRule = new MainRule(List.of(weightRoute(first, 100)), second);
        List<Object> yaml = List.of(
                headerRule.toYaml(),
                mainRule.toYaml()
        );

        List<Object> result = RuleSet.from(yaml).toYaml();
        assertEquals(2, result.size());
        assertEquals(headerRule.toYaml(), result.get(0));
        assertEquals(mainRule.toYaml(), result.get(1));
    }

    @Test
    void testAddDeleteHeaderRule() {
        HeaderRule headerRule = headerRule(second, "headerName", "headerValue");
        MainRule mainRule = new MainRule(List.of(weightRoute(first, 100)), second);
        List<Object> yaml = List.of(mainRule.toYaml());

        RuleSet ruleSet = RuleSet.from(yaml);
        ruleSet.addHeaderRule(headerRule);
        List<Object> ruleSetAfterAdd = ruleSet.toYaml();

        assertEquals(2, ruleSetAfterAdd.size());
        assertEquals(headerRule.toYaml(), ruleSetAfterAdd.get(0));
        assertEquals(mainRule.toYaml(), ruleSetAfterAdd.get(1));

        ruleSet.deleteHeaderRule("v2");

        List<Object> ruleSetAfterDelete = ruleSet.toYaml();
        assertEquals(1, ruleSetAfterDelete.size());
        assertEquals(mainRule.toYaml(), ruleSetAfterDelete.get(0));
    }

    @Test
    void testGetEmptyTraffic() {
        MainRule mainRule = new MainRule(List.of(weightRoute(first, 100)), null);
        List<Object> yaml = List.of(mainRule.toYaml());

        RuleSet ruleSet = RuleSet.from(yaml);
        assertEquals("-", ruleSet.getTrafficStatus("v2"));
    }

    @Test
    void testGetMirrorTraffic() {
        MainRule mainRule = new MainRule(List.of(weightRoute(first, 100)), second);
        List<Object> yaml = List.of(mainRule.toYaml());

        RuleSet ruleSet = RuleSet.from(yaml);
        assertEquals("mirror", ruleSet.getTrafficStatus("v2"));
    }

    @Test
    void testGetHeaderMirrorTraffic() {
        HeaderRule headerRule = headerRule(second, "headerName", "headerValue");
        MainRule mainRule = new MainRule(List.of(weightRoute(first, 100)), second);
        List<Object> yaml = List.of(headerRule.toYaml(), mainRule.toYaml());

        RuleSet ruleSet = RuleSet.from(yaml);
        assertEquals("mirror,header", ruleSet.getTrafficStatus("v2"));
    }
}