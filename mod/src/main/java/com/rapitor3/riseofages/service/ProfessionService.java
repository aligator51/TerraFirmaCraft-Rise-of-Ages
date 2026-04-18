package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.ProfessionTitle;
import com.rapitor3.riseofages.core.profession.ProfessionState;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

/**
 * Application service responsible for profession progression.
 *
 * Responsibilities:
 * - grant mod-specific profession experience
 * - validate profession point investment
 * - invest profession points
 * - resolve current profession title
 *
 * IMPORTANT:
 * This service uses mod/world persistence through ProgressRepository.
 * It does not access SavedData directly.
 */
public interface ProfessionService {

    /**
     * Grants profession experience to a subject.
     *
     * This is mod-specific profession XP.
     * It is NOT vanilla Minecraft XP.
     *
     * @param level server level
     * @param subjectRef target subject
     * @param professionKey target profession track
     * @param amount positive experience amount
     */
    void addExperience(ServerLevel level, SubjectRef subjectRef, ProfessionKey professionKey, long amount);

    /**
     * Checks whether one more profession point can be invested
     * into the given profession track.
     *
     * @param level server level
     * @param subjectRef target subject
     * @param professionKey target profession track
     * @return true if one more point can be invested
     */
    boolean canInvestPoint(ServerLevel level, SubjectRef subjectRef, ProfessionKey professionKey);

    /**
     * Invests one profession point into the given profession track.
     *
     * @param level server level
     * @param subjectRef target subject
     * @param professionKey target profession track
     */
    void investPoint(ServerLevel level, SubjectRef subjectRef, ProfessionKey professionKey);

    /**
     * Resolves the current profession title of the subject.
     *
     * @param level server level
     * @param subjectRef target subject
     * @return resolved profession title
     */
    ProfessionTitle resolveTitle(ServerLevel level, SubjectRef subjectRef);

    /**
     * Finds profession state for a subject if subject progression exists.
     *
     * @param level server level
     * @param subjectRef target subject
     * @return optional profession state
     */
    Optional<ProfessionState> findState(ServerLevel level, SubjectRef subjectRef);
}