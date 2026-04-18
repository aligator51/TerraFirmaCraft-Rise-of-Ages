package com.rapitor3.riseofages.gameplay;

import com.rapitor3.riseofages.bootstrap.CoreServices;
import com.rapitor3.riseofages.core.institution.InstitutionKey;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.progress.ActivityType;
import com.rapitor3.riseofages.core.progress.ProgressEvent;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import com.rapitor3.riseofages.gameplay.progress.DefaultPlayerInstitutionFocusService;
import com.rapitor3.riseofages.gameplay.progress.NoVillageSubjectResolver;
import com.rapitor3.riseofages.gameplay.progress.PlayerInstitutionFocusService;
import com.rapitor3.riseofages.gameplay.progress.ProgressDistribution;
import com.rapitor3.riseofages.gameplay.progress.VillageSubjectResolver;
import com.rapitor3.riseofages.service.ProfessionService;
import com.rapitor3.riseofages.service.ProgressService;
import com.rapitor3.riseofages.service.SubjectService;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class GameplayProgressEventHandler {

    private static final InstitutionKey EXTRACTION = InstitutionKey.of("extraction");
    private static final InstitutionKey METALLURGY = InstitutionKey.of("metallurgy");
    private static final InstitutionKey SMITHING = InstitutionKey.of("smithing");
    private static final InstitutionKey AGRICULTURE = InstitutionKey.of("agriculture");
    private static final InstitutionKey ANIMAL_HUSBANDRY = InstitutionKey.of("animal_husbandry");
    private static final InstitutionKey FOODCRAFT = InstitutionKey.of("foodcraft");
    private static final InstitutionKey CRAFTS = InstitutionKey.of("crafts");
    private static final InstitutionKey CONSTRUCTION = InstitutionKey.of("construction");
    private static final InstitutionKey ENGINEERING = InstitutionKey.of("engineering");

    private static final ProfessionKey EXTRACTION_PROFESSION = ProfessionKey.of("extraction");

    private final ProgressService progressService;
    private final SubjectService subjectService;
    private final ProfessionService professionService;

    private final PlayerInstitutionFocusService focusService;
    private final VillageSubjectResolver villageSubjectResolver;

    public GameplayProgressEventHandler(CoreServices coreServices) {
        this(
                coreServices,
                new DefaultPlayerInstitutionFocusService(),
                new NoVillageSubjectResolver()
        );
    }

    public GameplayProgressEventHandler(
            CoreServices coreServices,
            PlayerInstitutionFocusService focusService,
            VillageSubjectResolver villageSubjectResolver
    ) {
        Objects.requireNonNull(coreServices, "CoreServices must not be null");

        this.progressService = Objects.requireNonNull(coreServices.getProgressService(), "ProgressService must not be null");
        this.subjectService = Objects.requireNonNull(coreServices.getSubjectService(), "SubjectService must not be null");
        this.focusService = Objects.requireNonNull(focusService, "PlayerInstitutionFocusService must not be null");
        this.villageSubjectResolver = Objects.requireNonNull(villageSubjectResolver, "VillageSubjectResolver must not be null");
        this.professionService = Objects.requireNonNull(coreServices.getProfessionService(), "ProfessionService must not be null");
    }

    private record ProfessionXpDescriptor(
            ProfessionKey profession,
            ActivityType activityType,
            double baseAmount
    ) {
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        ProgressDescriptor progressDescriptor = resolveBlockBreakProgress(event.getState());
        if (progressDescriptor != null) {
            handleProgress(player, level, progressDescriptor, "forge:block_break");
        }

        ProfessionXpDescriptor professionDescriptor = resolveBlockBreakProfessionXp(event.getState());
        if (professionDescriptor != null) {
            handleProfessionXp(player, level, professionDescriptor, "forge:block_break");
        }
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        ItemStack stack = event.getCrafting();
        if (stack.isEmpty()) {
            return;
        }

        ProgressDescriptor descriptor = resolveCraftingProgress(stack);
        if (descriptor == null) {
            return;
        }

        handleProgress(player, level, descriptor, "forge:item_crafted");
    }

    @SubscribeEvent
    public void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        ItemStack stack = event.getSmelting();
        if (stack.isEmpty()) {
            return;
        }

        ProgressDescriptor descriptor = resolveSmeltingProgress(stack);
        if (descriptor == null) {
            return;
        }

        handleProgress(player, level, descriptor, "forge:item_smelted");
    }

    private void handleProgress(
            ServerPlayer player,
            ServerLevel level,
            ProgressDescriptor descriptor,
            String source
    ) {
        ProgressDistribution distribution = focusService.resolve(player, descriptor.institution());

        if (distribution.grantsPersonal()) {
            recordForPersonalSubject(player, level, descriptor, distribution.personalMultiplier(), source);
        }

        if (distribution.grantsVillage()) {
            recordForVillageSubject(player, level, descriptor, distribution.villageMultiplier(), source);
        }
    }

    private void recordForPersonalSubject(
            ServerPlayer player,
            ServerLevel level,
            ProgressDescriptor descriptor,
            double multiplier,
            String source
    ) {
        double amount = descriptor.baseAmount() * multiplier;
        if (amount <= 0.0D) {
            return;
        }

        SubjectRef subjectRef = subjectService.resolve(player);

        ProgressEvent progressEvent = ProgressEvent.now(
                player.getUUID(),
                subjectRef,
                descriptor.institution(),
                descriptor.activityType(),
                amount,
                source + "/personal"
        );

        progressService.record(level, progressEvent);
    }

    private void handleProfessionXp(
            ServerPlayer player,
            ServerLevel level,
            ProfessionXpDescriptor descriptor,
            String source
    ) {
        if (descriptor == null) {
            return;
        }

        double amount = descriptor.baseAmount();
        if (amount <= 0.0D) {
            return;
        }

        SubjectRef subjectRef = subjectService.resolve(player);

        professionService.addExperience(
                level,
                subjectRef,
                descriptor.profession(),
                descriptor.activityType(),
                amount,
                source + "/personal"
        );
    }

    private ProfessionXpDescriptor resolveBlockBreakProfessionXp(BlockState state) {
        if (state == null || state.isAir()) {
            return null;
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String path = blockId == null ? "" : blockId.getPath().toLowerCase(Locale.ROOT);

        if (state.is(BlockTags.LOGS)) {
            return new ProfessionXpDescriptor(
                    EXTRACTION_PROFESSION,
                    ActivityType.LOGGING,
                    1.0D
            );
        }

        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            double amount = 1.0D;

            if (path.contains("ore")) {
                amount += 2.0D;
            }

            if (path.contains("deepslate")) {
                amount += 1.0D;
            }

            return new ProfessionXpDescriptor(
                    EXTRACTION_PROFESSION,
                    ActivityType.MINING,
                    amount
            );
        }

        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                && containsAny(path, "clay", "gravel", "sand", "dirt")) {
            return new ProfessionXpDescriptor(
                    EXTRACTION_PROFESSION,
                    ActivityType.GATHERING,
                    0.5D
            );
        }

        return null;
    }


    private void recordForVillageSubject(
            ServerPlayer player,
            ServerLevel level,
            ProgressDescriptor descriptor,
            double multiplier,
            String source
    ) {
        double amount = descriptor.baseAmount() * multiplier;
        if (amount <= 0.0D) {
            return;
        }

        Optional<SubjectRef> villageSubject = villageSubjectResolver.resolveVillage(player);
        if (villageSubject.isEmpty()) {
            return;
        }

        ProgressEvent progressEvent = ProgressEvent.now(
                player.getUUID(),
                villageSubject.get(),
                descriptor.institution(),
                descriptor.activityType(),
                amount,
                source + "/village"
        );

        progressService.record(level, progressEvent);
    }

    private ProgressDescriptor resolveBlockBreakProgress(BlockState state) {
        if (state == null || state.isAir()) {
            return null;
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String path = blockId == null ? "" : blockId.getPath().toLowerCase(Locale.ROOT);

        if (state.is(BlockTags.LOGS)) {
            return new ProgressDescriptor(
                    EXTRACTION,
                    ActivityType.LOGGING,
                    1.5D
            );
        }

        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            double amount = 2.0D;

            if (path.contains("ore")) {
                amount += 3.0D;
            }

            if (path.contains("deepslate")) {
                amount += 1.0D;
            }

            return new ProgressDescriptor(
                    EXTRACTION,
                    ActivityType.MINING,
                    amount
            );
        }

        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                && containsAny(path, "clay", "gravel", "sand", "dirt")) {
            return new ProgressDescriptor(
                    EXTRACTION,
                    ActivityType.GATHERING,
                    1.0D
            );
        }

        return null;
    }

    private ProgressDescriptor resolveCraftingProgress(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = itemId == null ? "" : itemId.getPath().toLowerCase(Locale.ROOT);
        int count = Math.max(1, stack.getCount());

        if (stack.isEdible() || containsAny(path, "bread", "dough", "meal", "stew", "soup", "salad", "sandwich")) {
            return new ProgressDescriptor(
                    FOODCRAFT,
                    ActivityType.COOKING,
                    count
            );
        }

        if (containsAny(path, "ingot", "sheet", "metal", "bloom")) {
            return new ProgressDescriptor(
                    SMITHING,
                    ActivityType.FORGING,
                    count * 2.0D
            );
        }

        if (containsAny(path, "hammer", "tongs", "anvil", "mold", "chisel", "saw")) {
            return new ProgressDescriptor(
                    SMITHING,
                    ActivityType.TOOLMAKING,
                    count * 2.0D
            );
        }

        if (containsAny(path, "plank", "barrel", "chest", "crate", "loom", "wheel", "wood", "lumber", "door", "trapdoor")) {
            return new ProgressDescriptor(
                    CRAFTS,
                    ActivityType.CRAFTING,
                    count
            );
        }

        if (containsAny(path, "brick", "stairs", "slab", "wall", "gate", "beam", "support", "road", "arch")) {
            return new ProgressDescriptor(
                    CONSTRUCTION,
                    ActivityType.BUILDING,
                    count
            );
        }

        if (containsAny(path, "axle", "gear", "pump", "mechanism")) {
            return new ProgressDescriptor(
                    ENGINEERING,
                    ActivityType.ENGINEERING,
                    count * 2.0D
            );
        }

        return null;
    }

    private ProgressDescriptor resolveSmeltingProgress(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = itemId == null ? "" : itemId.getPath().toLowerCase(Locale.ROOT);
        int count = Math.max(1, stack.getCount());

        if (stack.isEdible() || containsAny(path, "cooked", "charred", "bread", "meal", "stew", "soup")) {
            return new ProgressDescriptor(
                    FOODCRAFT,
                    ActivityType.COOKING,
                    count * 2.0D
            );
        }

        if (containsAny(path, "ingot", "metal", "bloom", "sheet", "pig_iron", "steel")) {
            return new ProgressDescriptor(
                    METALLURGY,
                    ActivityType.SMELTING,
                    count * 3.0D
            );
        }

        return null;
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private record ProgressDescriptor(
            InstitutionKey institution,
            ActivityType activityType,
            double baseAmount
    ) {
    }
}