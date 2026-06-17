package com.clinicaltrial.ddd.common.model;

import java.io.Serializable;

/**
 * DDD Value Object marker interface.
 * Value objects have no identity; they are defined by their attributes.
 * All fields must be used in equals/hashCode.
 *
 * Implementations must be immutable or effectively immutable.
 */
public interface ValueObject extends Serializable {
}
