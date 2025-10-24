package net.yiran.simpleblueprint;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtIo;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;

public class RenderEventTest {
    public static NBTData RenderNBTData;

    static {
        try {
            var nbt = NbtIo.readCompressed(FMLPaths.GAMEDIR.get().getParent().resolve("src/main/test.nbt").toFile());
            RenderNBTData = new NBTData(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS) return;
        RenderNBTData.offset = new BlockPos(0,77,0);
        RenderUtils.doNBTRender(RenderNBTData, event.getPoseStack(), Tesselator.getInstance().getBuilder(), event.getCamera());
    }
}
