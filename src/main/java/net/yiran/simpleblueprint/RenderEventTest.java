package net.yiran.simpleblueprint;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;

public class RenderEventTest {
    public static NBTData RenderNBTData;

    static {
        try {
            var nbt = NbtIo.readCompressed(FMLPaths.GAMEDIR.get().getParent().resolve("src/main/test.nbt").toFile());
            RenderNBTData = new NBTData(nbt);
            RenderNBTData.offset = new BlockPos(0, 77, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS) return;
        RenderUtils.doNBTRender(RenderNBTData, event.getPoseStack(), Tesselator.getInstance().getBuilder(), event.getCamera());
    }

    @SubscribeEvent
    public static void onBlockRender(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if(event.getHand()== InteractionHand.MAIN_HAND) {
            if (player.isCrouching()) {
                if (!event.getLevel().isClientSide())
                    RenderNBTData.setInWorld(event.getLevel(), false);
            } else {
                RenderNBTData.offset = event.getPos().above();
            }
        }
    }
}
