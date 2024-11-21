package com.yourname.ancientdebrisfinder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        // Log a message to the console to confirm the mod is loaded
        System.out.println("Ancient Debris Finder Mod Initialized!");

        // Register the finddebris command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("finddebris")
                .then(CommandManager.argument("seed", CommandManager.argument("seed", LongArgumentType.longArg()))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    long seed = LongArgumentType.getLong(context, "seed");

                    BlockPos playerPos = player.getBlockPos();
                    List<BlockPos> debris = findAncientDebrisWithSeed(player.getWorld(), seed, playerPos, 100);
                    if (!debris.isEmpty()) {
                        // Output the first found ancient debris location to the player
                        BlockPos nearestDebris = debris.get(0);
                        player.sendMessage(Text.of("Nearest Ancient Debris found at: " + nearestDebris.toShortString()), false);
                    } else {
                        player.sendMessage(Text.of("No Ancient Debris found nearby."), false);
                    }
                    return 1;
                })));
        });
    }

    /**
     * Find ancient debris in the world using the provided seed.
     *
     * @param world The Minecraft world.
     * @param seed The world seed.
     * @param center The center position to start searching from.
     * @param radius The radius (in blocks) around the center to search.
     * @return A list of positions of ancient debris.
     */
    public List<BlockPos> findAncientDebrisWithSeed(World world, long seed, BlockPos center, int radius) {
        List<BlockPos> debrisPositions = new ArrayList<>();

        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                for (int y = 8; y <= 22; y++) { // Ancient debris is found between Y=8 and Y=22
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getBlock() == Blocks.ANCIENT_DEBRIS) {
                        debrisPositions.add(pos);
                    }
                }
            }
        }

        return debrisPositions;
    }
}
