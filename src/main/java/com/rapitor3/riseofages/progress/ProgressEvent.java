package com.rapitor3.riseofages.progress;

import com.rapitor3.riseofages.institution.InstitutionKey;
import com.rapitor3.riseofages.subject.SubjectRef;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable input event representing progression gained by a subject.
 *
 * Core progression flow is expected to look like this:
 *
 * integration / gameplay hook
 *      -> create ProgressEvent
 *      -> pass event into ProgressService
 *      -> ProgressService updates SubjectProgressData
 *
 * IMPORTANT:
 * This class does not contain progression rules.
 * It only describes what happened.
 *
 * @param actorId       player UUID who performed the action
 * @param subjectRef    subject that receives the progression
 * @param institutionKey institution that should be progressed
 * @param activityType  high-level category of activity
 * @param amount        raw progression amount
 * @param source        technical source identifier (for debugging/integrations)
 * @param occurredAt    event timestamp in epoch millis
 */
public record ProgressEvent(
        UUID actorId,
        SubjectRef subjectRef,
        InstitutionKey institutionKey,
        ActivityType activityType,
        double amount,
        String source,
        long occurredAt
) {

    /**
     * Compact constructor with validation.
     */
    public ProgressEvent {
        Objects.requireNonNull(actorId, "ProgressEvent.actorId must not be null");
        Objects.requireNonNull(subjectRef, "ProgressEvent.subjectRef must not be null");
        Objects.requireNonNull(institutionKey, "ProgressEvent.institutionKey must not be null");
        Objects.requireNonNull(activityType, "ProgressEvent.activityType must not be null");

        if (amount < 0.0D) {
            throw new IllegalArgumentException("ProgressEvent.amount must not be negative");
        }
    }

    // =========================================================
    // ===================== FACTORIES ==========================
    // =========================================================

    /**
     * Creates a new progression event with current timestamp.
     *
     * This is the most convenient entry point for normal gameplay usage.
     *
     * @param actorId         player who performed the action
     * @param subjectRef      subject receiving progression
     * @param institutionKey  target institution
     * @param activityType    category of activity
     * @param amount          progression amount
     * @param source          integration or system source id
     * @return new progress event
     */
    public static ProgressEvent now(
            UUID actorId,
            SubjectRef subjectRef,
            InstitutionKey institutionKey,
            ActivityType activityType,
            double amount,
            String source
    ) {
        return new ProgressEvent(
                actorId,
                subjectRef,
                institutionKey,
                activityType,
                amount,
                source,
                System.currentTimeMillis()
        );
    }

    /**
     * Creates a simple generic event.
     *
     * Useful for tests or cases where activity type is not important yet.
     *
     * @param actorId         player who performed the action
     * @param subjectRef      subject receiving progression
     * @param institutionKey  target institution
     * @param amount          progression amount
     * @return new generic progress event
     */
    public static ProgressEvent generic(
            UUID actorId,
            SubjectRef subjectRef,
            InstitutionKey institutionKey,
            double amount
    ) {
        return new ProgressEvent(
                actorId,
                subjectRef,
                institutionKey,
                ActivityType.GENERIC,
                amount,
                "core",
                System.currentTimeMillis()
        );
    }

    /**
     * @return true if this event has zero progression amount
     */
    public boolean isZero() {
        return amount == 0.0D;
    }

    /**
     * @return true if this event contributes positive progression
     */
    public boolean isPositive() {
        return amount > 0.0D;
    }
}
