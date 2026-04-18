package com.rapitor3.riseofages.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.rapitor3.riseofages.bootstrap.CoreServices;
import com.rapitor3.riseofages.core.era.EraDefinition;
import com.rapitor3.riseofages.core.era.EraKey;
import com.rapitor3.riseofages.core.institution.InstitutionDefinition;
import com.rapitor3.riseofages.core.institution.InstitutionKey;
import com.rapitor3.riseofages.core.institution.InstitutionState;
import com.rapitor3.riseofages.core.profession.ProfessionDefinition;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.ProfessionState;
import com.rapitor3.riseofages.core.profession.ProfessionTitle;
import com.rapitor3.riseofages.core.progress.ActivityType;
import com.rapitor3.riseofages.core.progress.ProgressEvent;
import com.rapitor3.riseofages.core.progress.SubjectProgressData;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.Optional;

/**
 * Debug command for testing the progression pipeline.
 * <p>
 * Supported flow:
 * /roa debug add <institution> <amount>
 * /roa debug info
 * /roa debug reset
 * /roa debug setera <era>
 * <p>
 * Examples:
 * /roa debug add smithing 5
 * /roa debug info
 * /roa debug reset
 * /roa debug setera iron_age
 */
public final class DebugProgressCommand {

    /**
     * Utility class.
     */
    private DebugProgressCommand() {
    }

    /**
     * Registers the debug progression command tree.
     *
     * @param dispatcher   command dispatcher
     * @param coreServices initialized core services container
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CoreServices coreServices) {
        dispatcher.register(
                Commands.literal("roa")
                        .then(Commands.literal("debug")
                                .requires(source -> source.hasPermission(2))

                                .then(Commands.literal("add")
                                        .then(Commands.argument("institution", StringArgumentType.word())
                                                .suggests(institutionSuggestions(coreServices))
                                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0D))
                                                        .executes(context -> executeAdd(
                                                                context.getSource(),
                                                                coreServices,
                                                                StringArgumentType.getString(context, "institution"),
                                                                DoubleArgumentType.getDouble(context, "amount")
                                                        ))
                                                )
                                        )
                                )

                                .then(Commands.literal("info")
                                        .executes(context -> executeInfo(
                                                context.getSource(),
                                                coreServices
                                        ))
                                )

                                .then(Commands.literal("reset")
                                        .executes(context -> executeReset(
                                                context.getSource(),
                                                coreServices
                                        ))
                                )

                                .then(Commands.literal("setera")
                                        .then(Commands.argument("era", StringArgumentType.word())
                                                .suggests(eraSuggestions(coreServices))
                                                .executes(context -> executeSetEra(
                                                        context.getSource(),
                                                        coreServices,
                                                        StringArgumentType.getString(context, "era")
                                                ))
                                        )
                                )
                                .then(Commands.literal("profession")

                                        .then(Commands.literal("addxp")
                                                .then(Commands.argument("profession", StringArgumentType.word())
                                                        .suggests(professionSuggestions(coreServices))
                                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                                .executes(context -> executeProfessionAddXp(
                                                                        context.getSource(),
                                                                        coreServices,
                                                                        StringArgumentType.getString(context, "profession"),
                                                                        IntegerArgumentType.getInteger(context, "amount")
                                                                ))
                                                        )
                                                )
                                        )

                                        .then(Commands.literal("invest")
                                                .then(Commands.argument("profession", StringArgumentType.word())
                                                        .suggests(professionSuggestions(coreServices))
                                                        .executes(context -> executeProfessionInvest(
                                                                context.getSource(),
                                                                coreServices,
                                                                StringArgumentType.getString(context, "profession")
                                                        ))
                                                )
                                        )

                                        .then(Commands.literal("info")
                                                .executes(context -> executeProfessionInfo(
                                                        context.getSource(),
                                                        coreServices
                                                ))
                                        )
                                )
                        )
        );
    }

    /**
     * Suggestion provider for registered institutions.
     * <p>
     * This enables TAB completion for:
     * /roa debug add <institution>
     *
     * @param coreServices initialized core services
     * @return brigadier suggestion provider
     */
    private static SuggestionProvider<CommandSourceStack> institutionSuggestions(CoreServices coreServices) {
        return (context, builder) -> SharedSuggestionProvider.suggest(
                coreServices.getInstitutionRegistry()
                        .getAll()
                        .stream()
                        .map(InstitutionDefinition::getKey)
                        .map(InstitutionKey::id),
                builder
        );
    }

    /**
     * Suggestion provider for registered eras.
     * <p>
     * This enables TAB completion for:
     * /roa debug setera <era>
     *
     * @param coreServices initialized core services
     * @return brigadier suggestion provider
     */
    private static SuggestionProvider<CommandSourceStack> eraSuggestions(CoreServices coreServices) {
        return (context, builder) -> SharedSuggestionProvider.suggest(
                coreServices.getEraRegistry()
                        .getAll()
                        .stream()
                        .map(EraDefinition::getKey)
                        .map(EraKey::id),
                builder
        );
    }

    /**
     * Executes debug progression addition.
     *
     * @param source        command source
     * @param coreServices  core service container
     * @param institutionId institution id string
     * @param amount        amount of progression to add
     * @return command result
     */
    private static int executeAdd(
            CommandSourceStack source,
            CoreServices coreServices,
            String institutionId,
            double amount
    ) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        InstitutionKey institutionKey = InstitutionKey.of(institutionId);

        // Final validation on execution.
        if (!coreServices.getInstitutionRegistry().contains(institutionKey)) {
            source.sendFailure(Component.literal(
                    "Unknown institution: " + institutionId
            ));
            return 0;
        }

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        ProgressEvent event = ProgressEvent.now(
                player.getUUID(),
                subjectRef,
                institutionKey,
                ActivityType.GENERIC,
                amount,
                "debug_command"
        );

        coreServices.getProgressService().record(player.serverLevel(), event);

        source.sendSuccess(
                () -> Component.literal(
                        "Added " + amount +
                                " progression to institution '" + institutionId +
                                "' for subject " + subjectRef.id()
                ),
                false
        );

        return 1;
    }

    /**
     * Displays current progression information for the executing player subject.
     *
     * @param source       command source
     * @param coreServices core service container
     * @return command result
     */
    private static int executeInfo(
            CommandSourceStack source,
            CoreServices coreServices
    ) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        Optional<SubjectProgressData> optionalData =
                coreServices.getProgressService().find(player.serverLevel(), subjectRef);

        if (optionalData.isEmpty()) {
            source.sendFailure(Component.literal("No progression data found for this subject."));
            return 0;
        }

        SubjectProgressData data = optionalData.get();

        source.sendSuccess(
                () -> Component.literal("=== Rise of Ages Debug Info ==="),
                false
        );

        source.sendSuccess(
                () -> Component.literal("Subject ID: " + data.getSubjectRef().id()),
                false
        );

        source.sendSuccess(
                () -> Component.literal("Subject Type: " + data.getSubjectRef().type()),
                false
        );

        source.sendSuccess(
                () -> Component.literal("Current Era: " + data.getEraState().getCurrentEra().id()),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Progress To Next Era: " +
                                String.format("%.2f", data.getEraState().getProgressToNextEra())
                ),
                false
        );

        if (data.getInstitutions().isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal("Institutions: none"),
                    false
            );
            return 1;
        }

        source.sendSuccess(
                () -> Component.literal("Institutions:"),
                false
        );

        data.getInstitutions().stream()
                .sorted(Comparator.comparing(state -> state.getKey().id()))
                .forEach(state -> sendInstitutionLine(source, state));

        return 1;
    }

    /**
     * Resets progression data for the executing player's subject.
     *
     * @param source       command source
     * @param coreServices core service container
     * @return command result
     */
    private static int executeReset(
            CommandSourceStack source,
            CoreServices coreServices
    ) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        boolean removed = coreServices.getProgressRepository()
                .remove(player.serverLevel(), subjectRef)
                .isPresent();

        if (!removed) {
            source.sendFailure(Component.literal("No progression data found to reset."));
            return 0;
        }

        source.sendSuccess(
                () -> Component.literal(
                        "Progression data reset for subject " + subjectRef.id()
                ),
                false
        );

        return 1;
    }

    /**
     * Sets the current era manually for the executing player's subject.
     *
     * @param source       command source
     * @param coreServices core service container
     * @param eraId        target era id
     * @return command result
     */
    private static int executeSetEra(
            CommandSourceStack source,
            CoreServices coreServices,
            String eraId
    ) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        EraKey targetEraKey = EraKey.of(eraId);

        if (!coreServices.getEraRegistry().contains(targetEraKey)) {
            source.sendFailure(Component.literal("Unknown era: " + eraId));
            return 0;
        }

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        SubjectProgressData data = coreServices.getProgressRepository()
                .getOrCreate(player.serverLevel(), subjectRef);

        data.getEraState().advanceTo(targetEraKey);
        data.getEraState().setProgressToNextEra(0.0D);
        data.touch();

        coreServices.getProgressRepository().save(player.serverLevel(), data);

        source.sendSuccess(
                () -> Component.literal(
                        "Set era to '" + eraId + "' for subject " + subjectRef.id()
                ),
                false
        );

        return 1;
    }

    /**
     * Sends a formatted debug line for a single institution.
     *
     * @param source command source
     * @param state  institution state
     */
    private static void sendInstitutionLine(CommandSourceStack source, InstitutionState state) {
        source.sendSuccess(
                () -> Component.literal(
                        "- " + state.getKey().id() +
                                " | level=" + state.getLevel() +
                                " | progress=" + String.format("%.2f", state.getProgress()) +
                                " | total=" + state.getTotalValue()
                ),
                false
        );
    }


    /**
     * Suggestion provider for registered professions.
     * <p>
     * This enables TAB completion for:
     * /roa debug profession addxp <profession>
     * /roa debug profession invest <profession>
     *
     * @param coreServices initialized core services
     * @return brigadier suggestion provider
     */
    private static SuggestionProvider<CommandSourceStack> professionSuggestions(CoreServices coreServices) {
        return (context, builder) -> SharedSuggestionProvider.suggest(
                coreServices.getProfessionRegistry()
                        .getAll()
                        .stream()
                        .map(ProfessionDefinition::getKey)
                        .map(ProfessionKey::id),
                builder
        );
    }

    /**
     * Adds mod-specific profession experience to the executing player's subject.
     * <p>
     * Example:
     * /roa debug profession addxp smithing 100
     *
     * @param source       command source
     * @param coreServices initialized core services
     * @param professionId profession id string
     * @param amount       profession XP amount
     * @return command result
     */
    private static int executeProfessionAddXp(
            CommandSourceStack source,
            CoreServices coreServices,
            String professionId,
            int amount
    ) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        ProfessionKey professionKey = ProfessionKey.of(professionId);

        if (!coreServices.getProfessionRegistry().contains(professionKey)) {
            source.sendFailure(Component.literal("Unknown profession: " + professionId));
            return 0;
        }

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        coreServices.getProfessionService().addExperience(
                player.serverLevel(),
                subjectRef,
                professionKey,
                amount
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Added " + amount +
                                " profession XP to '" + professionId +
                                "' for subject " + subjectRef.id()
                ),
                false
        );

        return 1;
    }

    /**
     * Invests one profession point into the selected profession track
     * for the executing player's subject.
     * <p>
     * Example:
     * /roa debug profession invest smithing
     *
     * @param source       command source
     * @param coreServices initialized core services
     * @param professionId profession id string
     * @return command result
     */
    private static int executeProfessionInvest(
            CommandSourceStack source,
            CoreServices coreServices,
            String professionId
    ) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        ProfessionKey professionKey = ProfessionKey.of(professionId);

        if (!coreServices.getProfessionRegistry().contains(professionKey)) {
            source.sendFailure(Component.literal("Unknown profession: " + professionId));
            return 0;
        }

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        boolean canInvest = coreServices.getProfessionService().canInvestPoint(
                player.serverLevel(),
                subjectRef,
                professionKey
        );

        if (!canInvest) {
            source.sendFailure(Component.literal(
                    "Cannot invest point into profession '" + professionId +
                            "'. Not enough profession XP, profession max reached, or global point cap reached."
            ));
            return 0;
        }

        coreServices.getProfessionService().investPoint(
                player.serverLevel(),
                subjectRef,
                professionKey
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Invested 1 point into profession '" + professionId +
                                "' for subject " + subjectRef.id()
                ),
                false
        );

        return 1;
    }

    /**
     * Displays profession debug information for the executing player's subject.
     * <p>
     * Includes:
     * - current resolved profession title
     * - spent and remaining points
     * - profession XP and invested points per registered track
     *
     * @param source       command source
     * @param coreServices initialized core services
     * @return command result
     */
    private static int executeProfessionInfo(
            CommandSourceStack source,
            CoreServices coreServices
    ) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        Optional<ProfessionState> optionalState = coreServices.getProfessionService()
                .findState(player.serverLevel(), subjectRef);

        if (optionalState.isEmpty()) {
            source.sendFailure(Component.literal("No profession data found for this subject."));
            return 0;
        }

        ProfessionState state = optionalState.get();
        ProfessionTitle title = coreServices.getProfessionService()
                .resolveTitle(player.serverLevel(), subjectRef);

        source.sendSuccess(
                () -> Component.literal("=== Rise of Ages Profession Debug Info ==="),
                false
        );

        source.sendSuccess(
                () -> Component.literal("Subject ID: " + subjectRef.id()),
                false
        );

        source.sendSuccess(
                () -> Component.literal("Current Profession Title: " + title.displayName()),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Spent Points: " + state.getTotalSpentPoints() +
                                "/" + ProfessionState.GLOBAL_POINT_CAP
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Remaining Points: " + state.getRemainingPoints()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal("Professions:"),
                false
        );

        coreServices.getProfessionRegistry()
                .getAll()
                .stream()
                .sorted(Comparator.comparing(def -> def.getKey().id()))
                .forEach(definition -> sendProfessionLine(
                        source,
                        coreServices,
                        player,
                        subjectRef,
                        state,
                        definition
                ));

        return 1;
    }

    /**
     * Sends one formatted debug line for a single profession track.
     *
     * @param source       command source
     * @param coreServices initialized core services
     * @param player       executing player
     * @param subjectRef   resolved subject
     * @param state        profession state
     * @param definition   profession definition
     */
    private static void sendProfessionLine(
            CommandSourceStack source,
            CoreServices coreServices,
            ServerPlayer player,
            SubjectRef subjectRef,
            ProfessionState state,
            ProfessionDefinition definition
    ) {
        boolean canInvest = coreServices.getProfessionService().canInvestPoint(
                player.serverLevel(),
                subjectRef,
                definition.getKey()
        );

        long xp = state.getExperience(definition.getKey());
        int points = state.getInvestedPoints(definition.getKey());

        source.sendSuccess(
                () -> Component.literal(
                        "- " + definition.getKey().id() +
                                " | xp=" + xp +
                                " | points=" + points +
                                "/" + definition.getMaxPoints() +
                                " | canInvest=" + canInvest
                ),
                false
        );
    }

}