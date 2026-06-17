package com.clinicaltrial.ddd.common.model;

/**
 * Thrown when a business rule (invariant) is violated.
 * Used for state transition violations, duplicate detection, etc.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }

    public BusinessRuleViolationException(String rule, String detail) {
        super("BUSINESS_RULE_VIOLATION", String.format("Rule [%s] violated: %s", rule, detail));
    }
}
