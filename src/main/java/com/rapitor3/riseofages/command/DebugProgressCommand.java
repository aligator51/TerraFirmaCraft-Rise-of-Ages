package com.rapitor3.riseofages.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.rapitor3.riseofages.bootstrap.CoreServices;
import com.rapitor3.riseofages.institution.InstitutionDefinition;
import com.rapitor3.riseofages.institution.InstitutionKey;
import com.rapitor3.riseofages.institution.InstitutionState;
import com.rapitor3.riseofages.progress.ActivityType;
import com.rapitor3.riseofages.progress.ProgressEvent;
import com.rapitor3.riseofages.progress.SubjectProgressData;
import com.rapitor3.riseofages.subject.SubjectRef;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.Optional;

/**
 * Debug command for testing the progression pipeline.
 *
 * Supported flow:
 * /roa debug add <institution> <amount>
 * /roa debug info
 *
 * Example:
 * /roa debug add smithing 5
 * /roa debug info
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
     * @param dispatcher command dispatcher
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
                        )
        );
    }

    /**
     * Suggestion provider for registered institutions.
     *
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
     * Executes debug progression addition.
     *
     * @param source command source
     * @param coreServices core service container
     * @param institutionId institution id string
     * @param amount amount of progression to add
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
     * @param source command source
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
     * Sends a formatted debug line for a single institution.
     *
     * @param source command source
     * @param state institution state
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

}