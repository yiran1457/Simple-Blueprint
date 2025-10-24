package net.yiran.simpleblueprint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import java.util.Map;

public class RenderUtils {
    public static RandomSource randomsource = RandomSource.create();

    public static void doNBTRender(NBTData nbtData, PoseStack poseStack, BufferBuilder bufferBuilder, Camera camera){
        poseStack.pushPose();
        preRender(nbtData,poseStack,camera);
        renderBlock(nbtData,poseStack,bufferBuilder);
        renderFluid(nbtData,poseStack,bufferBuilder);
        poseStack.popPose();
    }

    public static void preRender(NBTData nbtData, PoseStack poseStack, Camera camera) {
        Vec3 position = camera.getPosition();
        poseStack.translate(-position.x, -position.y, -position.z);
        BlockPos blockPos = nbtData.offset;
        poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
    }

    public static void beginRender(BufferBuilder bufferBuilder){
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        bufferBuilder.setQuadSorting(VertexSorting.DISTANCE_TO_ORIGIN);
    }

    public static void endRender(BufferBuilder bufferBuilder){
        BufferUploader.drawWithShader(bufferBuilder.end());
    }


    public static void renderFluid(NBTData nbtData, PoseStack poseStack, BufferBuilder bufferBuilder) {
        beginRender(bufferBuilder);
        for (Map.Entry<BlockPos, FluidState> entry : nbtData.fluidRenderMap.entrySet()) {
            MyFluidRenderer.instance.tesselate(nbtData, entry.getKey(), poseStack, bufferBuilder, nbtData.blockRenderMap.get(entry.getKey()), entry.getValue());
        }
        RenderSystem.enableBlend();
        endRender(bufferBuilder);
        RenderSystem.disableBlend();
    }

    public static void renderBlock(NBTData nbtData, PoseStack poseStack, BufferBuilder  bufferBuilder) {
        beginRender(bufferBuilder);
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        nbtData.blockRenderMap.forEach((blockPos, blockstate) -> {
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                var model = blockRenderer.getBlockModel(blockstate);
                var modelData = model.getModelData(nbtData, blockPos, blockstate, ModelData.EMPTY);
                randomsource.setSeed(blockstate.getSeed(blockPos));
                for (RenderType rendertype2 : model.getRenderTypes(blockstate, randomsource, modelData)) {
                    poseStack.pushPose();
                    poseStack.translate((float) (blockPos.getX()), (float) (blockPos.getY()), (float) (blockPos.getZ()));
                    blockRenderer.renderBatched(blockstate, blockPos, nbtData, poseStack, bufferBuilder, true, randomsource, modelData, rendertype2);
                    poseStack.popPose();
                }
            }
        });
        endRender(bufferBuilder);
    }
}
