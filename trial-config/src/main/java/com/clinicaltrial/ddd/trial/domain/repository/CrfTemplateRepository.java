package com.clinicaltrial.ddd.trial.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.CrfTemplate;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;

import java.util.List;

/**
 * Repository interface for the {@link CrfTemplate} aggregate root.
 * <p>
 * Manages persistence of CRF (Case Report Form) templates. Templates are
 * standalone aggregates that can be independently versioned and published
 * before being bound to trial stages.
 * </p>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@link #findById(CrfTemplateId)} — Returns a template or empty.</li>
 *   <li>{@link #getById(CrfTemplateId)} — Returns a template or throws
 *       {@link AggregateNotFoundException}.</li>
 *   <li>{@link #save(CrfTemplate)} — Persists a new or updated template.</li>
 *   <li>{@link #findAll(int, int)} — Paged retrieval of all templates.</li>
 *   <li>{@link #findByCategory(String)} — Finds templates by category.</li>
 * </ul>
 */
public interface CrfTemplateRepository {

    /**
     * Finds a CRF template by its identity.
     *
     * @param id the template identity; must not be null
     * @return the template, or empty if not found
     */
    java.util.Optional<CrfTemplate> findById(CrfTemplateId id);

    /**
     * Gets a CRF template by its identity, throwing if not found.
     *
     * @param id the template identity; must not be null
     * @return the template
     * @throws AggregateNotFoundException if the template is not found
     */
    CrfTemplate getById(CrfTemplateId id);

    /**
     * Persists a CRF template aggregate (insert or update).
     * <p>
     * After successful persistence, the caller should call
     * {@link com.clinicaltrial.ddd.common.infrastructure.EventBus#publishAll(com.clinicaltrial.ddd.common.model.AggregateRoot)}
     * to publish any pending domain events.
     * </p>
     *
     * @param template the template to save; must not be null
     * @return the saved template (with generated ID if new)
     */
    CrfTemplate save(CrfTemplate template);

    /**
     * Finds all CRF templates with paging support.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return list of templates on the requested page
     */
    List<CrfTemplate> findAll(int page, int size);

    /**
     * Finds all CRF templates in the given category.
     *
     * @param category the category to filter by; must not be null
     * @return list of matching templates (empty if none)
     */
    List<CrfTemplate> findByCategory(String category);
}
