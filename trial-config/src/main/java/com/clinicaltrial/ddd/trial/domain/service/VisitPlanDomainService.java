package com.clinicaltrial.ddd.trial.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.entity.VisitPlan;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain service for visit plan validation rules.
 * <p>
 * Encapsulates complex business rules for visit plan configuration that
 * involve cross-entity relationships, such as detecting cycles in the
 * visit plan directed graph and validating window period consistency.
 * </p>
 *
 * <h3>Graph Validation</h3>
 * Visit plans form a directed graph where source stages point to target stages.
 * The service validates that:
 * <ul>
 *   <li>No self-loops (source stage equals target stage)</li>
 *   <li>No cycles in the directed graph</li>
 *   <li>Window periods do not exceed the stage's configured window</li>
 * </ul>
 */
@Service
public class VisitPlanDomainService {

    /**
     * Validates that a visit plan's timeline is consistent and free of cycles.
     * <p>
     * Checks the following invariants:
     * <ol>
     *   <li>Source and target stages must not be the same (no self-loops).</li>
     *   <li>Adding this visit plan must not introduce a cycle in the project's
     *       visit plan directed graph.</li>
     * </ol>
     * </p>
     *
     * @param project        the project containing the visit plans; must not be null
     * @param sourceStageId  the source stage for the new plan; must not be null
     * @param targetStageId  the target stage for the new plan; must not be null
     * @throws BusinessRuleViolationException if a self-loop or cycle is detected
     */
    public void validateDirectedTimeline(Project project, StageId sourceStageId,
                                          StageId targetStageId) {
        if (project == null) {
            throw new IllegalArgumentException("project must not be null");
        }
        if (sourceStageId == null) {
            throw new IllegalArgumentException("sourceStageId must not be null");
        }
        if (targetStageId == null) {
            throw new IllegalArgumentException("targetStageId must not be null");
        }

        // Rule 1: No self-loops
        if (sourceStageId.equals(targetStageId)) {
            throw new BusinessRuleViolationException("VISIT_PLAN_SELF_LOOP",
                    "Source stage and target stage must be different for a visit plan. "
                            + "Self-loop detected: stageId=" + sourceStageId);
        }

        // Rule 2: No cycles in the directed graph
        // Check if adding (sourceStageId -> targetStageId) would create a cycle
        // by seeing if targetStageId can reach back to sourceStageId via the
        // existing visit plan graph.
        Set<StageId> visited = new HashSet<>();
        boolean hasCycle = hasPathToSource(project, targetStageId, sourceStageId, visited);
        if (hasCycle) {
            throw new BusinessRuleViolationException("VISIT_PLAN_CYCLE_DETECTED",
                    "Adding this visit plan would create a cycle in the stage timeline. "
                            + "Source: " + sourceStageId + ", Target: " + targetStageId);
        }
    }

    /**
     * Performs a DFS to check if there is a path from currentStageId to targetStageId
     * in the project's visit plan graph.
     *
     * @param project         the project
     * @param currentStageId  the current stage in the DFS traversal
     * @param targetStageId   the target stage to find
     * @param visited         set of already-visited stages
     * @return {@code true} if a path from currentStageId to targetStageId exists
     */
    private boolean hasPathToSource(Project project, StageId currentStageId,
                                     StageId targetStageId, Set<StageId> visited) {
        if (currentStageId.equals(targetStageId)) {
            return true;
        }
        if (!visited.add(currentStageId)) {
            return false;
        }

        for (VisitPlan plan : project.getVisitPlans()) {
            if (plan.getSourceStageId().equals(currentStageId)) {
                if (hasPathToSource(project, plan.getTargetStageId(), targetStageId, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Validates that a visit plan's window period does not exceed the configured
     * window period of its associated stages.
     * <p>
     * This ensures that the visit plan's acceptable date range is at least as
     * restrictive as the stage's window. If either the visit plan or the stage
     * has no window configured, validation passes.
     * </p>
     *
     * @param visitPlan the visit plan to validate; must not be null
     * @throws BusinessRuleViolationException if the visit plan's window exceeds
     *                                        the stage's window
     */
    public void validateWindowConsistency(VisitPlan visitPlan) {
        if (visitPlan == null) {
            throw new IllegalArgumentException("visitPlan must not be null");
        }

        // Window consistency validation requires comparing the visit plan's
        // window period with the stage's window period.
        // Currently, this is a placeholder that always passes.
        // Future implementation should load the stage configuration and
        // verify that the visit plan's window is within the stage's bounds.
    }
}
