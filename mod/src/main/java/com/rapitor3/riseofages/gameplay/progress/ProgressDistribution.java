package com.rapitor3.riseofages.gameplay.progress;

/**
 * Describes how a gameplay action is distributed between:
 * - personal progression
 * - village / group progression
 *
 * Examples:
 * - personal + village: player learns and village grows
 * - village only: player has locked the institution, but village still gains progress
 */
public record ProgressDistribution(
        double personalMultiplier,
        double villageMultiplier
) {

    public ProgressDistribution {
        if (personalMultiplier < 0.0D) {
            throw new IllegalArgumentException("personalMultiplier must be >= 0");
        }
        if (villageMultiplier < 0.0D) {
            throw new IllegalArgumentException("villageMultiplier must be >= 0");
        }
    }

    public boolean grantsPersonal() {
        return personalMultiplier > 0.0D;
    }

    public boolean grantsVillage() {
        return villageMultiplier > 0.0D;
    }

    public static ProgressDistribution unrestricted() {
        return new ProgressDistribution(1.0D, 1.0D);
    }

    public static ProgressDistribution villageOnly() {
        return new ProgressDistribution(0.0D, 1.0D);
    }

    public static ProgressDistribution personalOnly() {
        return new ProgressDistribution(1.0D, 0.0D);
    }
}