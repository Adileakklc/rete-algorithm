package algoritmaanalizjese;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ReteAlgoritmasi {
    private List<Rule> rules;
    private Map<String, AlphaMemory> alphaMemories;
    private BetaMemory betaMemory;

    public ReteAlgoritmasi() {
        this.rules = new ArrayList<>();
        this.alphaMemories = new HashMap<>();
        this.betaMemory = new BetaMemory();
    }

    public void addRule(Rule rule) {
        rules.add(rule);
        for (Condition condition : rule.getConditions()) {
            alphaMemories.putIfAbsent(condition.getFactSlot(), new AlphaMemory());
        }
    }

    public void addFact(Fact fact) {
        for (Map.Entry<String, AlphaMemory> entry : alphaMemories.entrySet()) {
            if (fact.hasSlot(entry.getKey())) {
                entry.getValue().addFact(fact);
            }
        }
        betaMemory.addFact(fact);
    }

    public void runRules() {
        for (Rule rule : rules) {
            if (rule.evaluate(betaMemory)) {
                rule.fire();
            }
        }
    }

    public static void main(String[] args) {
        ReteAlgoritmasi engine = new ReteAlgoritmasi();

        // Kurallarý tanýmla ve ekle
        engine.addRule(new Rule("Rule1", List.of(
                new Condition("income", Operator.GREATER_THAN, 5000),
                new Condition("creditHistory", Operator.EQUALS, "excellent")), "Approved"));

        engine.addRule(new Rule("Rule2", List.of(
                new Condition("income", Operator.LESS_THAN_OR_EQUAL, 5000)), "Denied"));

        // Gerçekleri ekle
        Fact personFact = new Fact();
        personFact.setSlot("income", 6000);
        personFact.setSlot("creditHistory", "excellent");

        engine.addFact(personFact);

        // Kurallarý çalýþtýr
        engine.runRules();
    }
}

class Fact {
    private Map<String, Object> slots;

    public Fact() {
        this.slots = new HashMap<>();
    }

    public void setSlot(String slotName, Object value) {
        slots.put(slotName, value);
    }

    public Object getSlot(String slotName) {
        return slots.get(slotName);
    }

    public boolean hasSlot(String slotName) {
        return slots.containsKey(slotName);
    }
}

class Condition {
    private String factSlot;
    private Operator operator;
    private Object value;

    public Condition(String factSlot, Operator operator, Object value) {
        this.factSlot = factSlot;
        this.operator = operator;
        this.value = value;
    }

    public String getFactSlot() {
        return factSlot;
    }

    public boolean evaluate(Fact fact) {
        Object factValue = fact.getSlot(factSlot);
        if (operator == Operator.EQUALS) {
            return factValue.equals(value);
        } else if (operator == Operator.GREATER_THAN) {
            return (int) factValue > (int) value;
        } else if (operator == Operator.LESS_THAN_OR_EQUAL) {
            return (int) factValue <= (int) value;
        }
        return false;
    }
}

enum Operator {
    EQUALS, GREATER_THAN, LESS_THAN_OR_EQUAL
}

class Rule {
    private String name;
    private List<Condition> conditions;
    private String action;

    public Rule(String name, List<Condition> conditions, String action) {
        this.name = name;
        this.conditions = conditions;
        this.action = action;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public boolean evaluate(BetaMemory betaMemory) {
        for (Fact fact : betaMemory.getFacts()) {
            boolean allConditionsMet = true;
            for (Condition condition : conditions) {
                if (!condition.evaluate(fact)) {
                    allConditionsMet = false;
                    break;
                }
            }
            if (allConditionsMet) {
                return true;
            }
        }
        return false;
    }

    public void fire() {
        System.out.println("Action: " + action);
    }
}

class AlphaMemory {
    private List<Fact> facts;

    public AlphaMemory() {
        this.facts = new ArrayList<>();
    }

    public void addFact(Fact fact) {
        facts.add(fact);
    }

    public List<Fact> getFacts() {
        return facts;
    }
}

class BetaMemory {
    private List<Fact> facts;

    public BetaMemory() {
        this.facts = new ArrayList<>();
    }

    public void addFact(Fact fact) {
        facts.add(fact);
    }

    public List<Fact> getFacts() {
        return facts;
    }
}
