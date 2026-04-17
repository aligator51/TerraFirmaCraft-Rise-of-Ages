package com.rapitor3.riseofages.gameplay.progress;

import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * Default no-op resolver used until village / clan progression is implemented.
 */
public class NoVillageSubjectResolver implements VillageSubjectResolver {

    @Override
    public Optional<SubjectRef> resolveVillage(ServerPlayer player) {
        return Optional.empty();
    }
}