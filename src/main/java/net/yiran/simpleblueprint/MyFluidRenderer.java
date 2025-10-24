package net.yiran.simpleblueprint;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.block.LiquidBlockRenderer.shouldRenderFace;

public class MyFluidRenderer {
    public static MyFluidRenderer instance = new MyFluidRenderer();

    private static boolean isNeighborSameFluid(FluidState firstState, FluidState secondState) {
        return secondState.getType().isSame(firstState.getType());
    }

    private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state) {
        if (state.canOcclude()) {
            VoxelShape voxelshape = Shapes.box(0.0, 0.0, 0.0, 1.0, height, 1.0);
            VoxelShape voxelshape1 = state.getOcclusionShape(level, pos);
            return Shapes.blockOccudes(voxelshape, voxelshape1, face);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState blockState) {
        return isFaceOccludedByState(level, side, height, pos.relative(side), blockState);
    }


    public void tesselate(BlockAndTintGetter level, BlockPos pos, PoseStack pose, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        boolean flag = fluidState.is(FluidTags.LAVA);
        TextureAtlasSprite[] atextureatlassprite = ForgeHooksClient.getFluidSprites(level, pos, fluidState);
        var m4 = pose.last().pose();
        int i = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
        float alpha = (float) (i >> 24 & 255) / 255.0F;
        float f = (float) (i >> 16 & 255) / 255.0F;
        float f1 = (float) (i >> 8 & 255) / 255.0F;
        float f2 = (float) (i & 255) / 255.0F;
        BlockState blockstate = level.getBlockState(pos.relative(Direction.DOWN));
        FluidState fluidstate = blockstate.getFluidState();
        BlockState blockstate1 = level.getBlockState(pos.relative(Direction.UP));
        FluidState fluidstate1 = blockstate1.getFluidState();
        BlockState blockstate2 = level.getBlockState(pos.relative(Direction.NORTH));
        FluidState fluidstate2 = blockstate2.getFluidState();
        BlockState blockstate3 = level.getBlockState(pos.relative(Direction.SOUTH));
        FluidState fluidstate3 = blockstate3.getFluidState();
        BlockState blockstate4 = level.getBlockState(pos.relative(Direction.WEST));
        FluidState fluidstate4 = blockstate4.getFluidState();
        BlockState blockstate5 = level.getBlockState(pos.relative(Direction.EAST));
        FluidState fluidstate5 = blockstate5.getFluidState();
        boolean flag1 = !isNeighborSameFluid(fluidState, fluidstate1);
        boolean flag2 = shouldRenderFace(level, pos, fluidState, blockState, Direction.DOWN, fluidstate) && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN, 0.8888889F, blockstate);
        boolean flag3 = shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, fluidstate2);
        boolean flag4 = shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, fluidstate3);
        boolean flag5 = shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, fluidstate4);
        boolean flag6 = shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, fluidstate5);
        if (flag1 || flag2 || flag6 || flag5 || flag3 || flag4) {
            float f3 = level.getShade(Direction.DOWN, true);
            float f4 = level.getShade(Direction.UP, true);
            float f5 = level.getShade(Direction.NORTH, true);
            float f6 = level.getShade(Direction.WEST, true);
            Fluid fluid = fluidState.getType();
            float f11 = this.getHeight(level, fluid, pos, blockState, fluidState);
            float f7;
            float f8;
            float f9;
            float f10;
            if (f11 >= 1.0F) {
                f7 = 1.0F;
                f8 = 1.0F;
                f9 = 1.0F;
                f10 = 1.0F;
            } else {
                float f12 = this.getHeight(level, fluid, pos.north(), blockstate2, fluidstate2);
                float f13 = this.getHeight(level, fluid, pos.south(), blockstate3, fluidstate3);
                float f14 = this.getHeight(level, fluid, pos.east(), blockstate5, fluidstate5);
                float f15 = this.getHeight(level, fluid, pos.west(), blockstate4, fluidstate4);
                f7 = this.calculateAverageHeight(level, fluid, f11, f12, f14, pos.relative(Direction.NORTH).relative(Direction.EAST));
                f8 = this.calculateAverageHeight(level, fluid, f11, f12, f15, pos.relative(Direction.NORTH).relative(Direction.WEST));
                f9 = this.calculateAverageHeight(level, fluid, f11, f13, f14, pos.relative(Direction.SOUTH).relative(Direction.EAST));
                f10 = this.calculateAverageHeight(level, fluid, f11, f13, f15, pos.relative(Direction.SOUTH).relative(Direction.WEST));
            }

            double fluidPosX = (pos.getX());
            double fluidPosY = (pos.getY());
            double fluidPosZ = (pos.getZ());
            float f16 = 0.001F;
            float f17 = flag2 ? 0.001F : 0.0F;
            if (flag1 && !isFaceOccludedByNeighbor(level, pos, Direction.UP, Math.min(Math.min(f8, f10), Math.min(f9, f7)), blockstate1)) {
                f8 -= 0.001F;
                f10 -= 0.001F;
                f9 -= 0.001F;
                f7 -= 0.001F;
                Vec3 vec3 = fluidState.getFlow(level, pos);
                float f18;
                float f19;
                float f20;
                float f21;
                float f22;
                float f23;
                float f24;
                float f25;
                if (vec3.x == 0.0 && vec3.z == 0.0) {
                    TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
                    f18 = textureatlassprite1.getU(0.0);
                    f22 = textureatlassprite1.getV(0.0);
                    f19 = f18;
                    f23 = textureatlassprite1.getV(16.0);
                    f20 = textureatlassprite1.getU(16.0);
                    f24 = f23;
                    f21 = f20;
                    f25 = f22;
                } else {
                    TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
                    float f26 = (float) Mth.atan2(vec3.z, vec3.x) - ((float) Math.PI / 2F);
                    float f27 = Mth.sin(f26) * 0.25F;
                    float f28 = Mth.cos(f26) * 0.25F;
                    float f29 = 8.0F;
                    f18 = textureatlassprite.getU((8.0F + (-f28 - f27) * 16.0F));
                    f22 = textureatlassprite.getV((8.0F + (-f28 + f27) * 16.0F));
                    f19 = textureatlassprite.getU((8.0F + (-f28 + f27) * 16.0F));
                    f23 = textureatlassprite.getV((8.0F + (f28 + f27) * 16.0F));
                    f20 = textureatlassprite.getU((8.0F + (f28 + f27) * 16.0F));
                    f24 = textureatlassprite.getV((8.0F + (f28 - f27) * 16.0F));
                    f21 = textureatlassprite.getU((8.0F + (f28 - f27) * 16.0F));
                    f25 = textureatlassprite.getV((8.0F + (-f28 - f27) * 16.0F));
                }

                float f49 = (f18 + f19 + f20 + f21) / 4.0F;
                float f50 = (f22 + f23 + f24 + f25) / 4.0F;
                float f51 = atextureatlassprite[0].uvShrinkRatio();
                f18 = Mth.lerp(f51, f18, f49);
                f19 = Mth.lerp(f51, f19, f49);
                f20 = Mth.lerp(f51, f20, f49);
                f21 = Mth.lerp(f51, f21, f49);
                f22 = Mth.lerp(f51, f22, f50);
                f23 = Mth.lerp(f51, f23, f50);
                f24 = Mth.lerp(f51, f24, f50);
                f25 = Mth.lerp(f51, f25, f50);
                int l = this.getLightColor(level, pos);
                float f52 = f4 * f;
                float f30 = f4 * f1;
                float f31 = f4 * f2;
                this.vertex(vertexConsumer, m4, fluidPosX + 0.0, fluidPosY + f8, fluidPosZ + 0.0, f52, f30, f31, alpha, f18, f22, l);
                this.vertex(vertexConsumer, m4, fluidPosX + 0.0, fluidPosY + f10, fluidPosZ + 1.0, f52, f30, f31, alpha, f19, f23, l);
                this.vertex(vertexConsumer, m4, fluidPosX + 1.0, fluidPosY + f9, fluidPosZ + 1.0, f52, f30, f31, alpha, f20, f24, l);
                this.vertex(vertexConsumer, m4, fluidPosX + 1.0, fluidPosY + f7, fluidPosZ + 0.0, f52, f30, f31, alpha, f21, f25, l);
                if (fluidState.shouldRenderBackwardUpFace(level, pos.above())) {
                    this.vertex(vertexConsumer, m4, fluidPosX + 0.0, fluidPosY + f8, fluidPosZ + 0.0, f52, f30, f31, alpha, f18, f22, l);
                    this.vertex(vertexConsumer, m4, fluidPosX + 1.0, fluidPosY + f7, fluidPosZ + 0.0, f52, f30, f31, alpha, f21, f25, l);
                    this.vertex(vertexConsumer, m4, fluidPosX + 1.0, fluidPosY + f9, fluidPosZ + 1.0, f52, f30, f31, alpha, f20, f24, l);
                    this.vertex(vertexConsumer, m4, fluidPosX + 0.0, fluidPosY + f10, fluidPosZ + 1.0, f52, f30, f31, alpha, f19, f23, l);
                }
            }

            if (flag2) {
                float f40 = atextureatlassprite[0].getU0();
                float f41 = atextureatlassprite[0].getU1();
                float f42 = atextureatlassprite[0].getV0();
                float f43 = atextureatlassprite[0].getV1();
                int k = this.getLightColor(level, pos.below());
                float f46 = f3 * f;
                float f47 = f3 * f1;
                float f48 = f3 * f2;
                this.vertex(vertexConsumer, m4, fluidPosX, fluidPosY + f17, fluidPosZ + 1.0, f46, f47, f48, alpha, f40, f43, k);
                this.vertex(vertexConsumer, m4, fluidPosX, fluidPosY + f17, fluidPosZ, f46, f47, f48, alpha, f40, f42, k);
                this.vertex(vertexConsumer, m4, fluidPosX + 1.0, fluidPosY + f17, fluidPosZ, f46, f47, f48, alpha, f41, f42, k);
                this.vertex(vertexConsumer, m4, fluidPosX + 1.0, fluidPosY + f17, fluidPosZ + 1.0, f46, f47, f48, alpha, f41, f43, k);
            }

            int j = this.getLightColor(level, pos);

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                float f44;
                float f45;
                double d3;
                double d4;
                double d5;
                double d6;
                boolean flag7;
                switch (direction) {
                    case NORTH:
                        f44 = f8;
                        f45 = f7;
                        d3 = fluidPosX;
                        d5 = fluidPosX + 1.0;
                        d4 = fluidPosZ + 0.001;
                        d6 = fluidPosZ + 0.001;
                        flag7 = flag3;
                        break;
                    case SOUTH:
                        f44 = f9;
                        f45 = f10;
                        d3 = fluidPosX + 1.0;
                        d5 = fluidPosX;
                        d4 = fluidPosZ + 1.0 - 0.001;
                        d6 = fluidPosZ + 1.0 - 0.001;
                        flag7 = flag4;
                        break;
                    case WEST:
                        f44 = f10;
                        f45 = f8;
                        d3 = fluidPosX + 0.001;
                        d5 = fluidPosX + 0.001;
                        d4 = fluidPosZ + 1.0;
                        d6 = fluidPosZ;
                        flag7 = flag5;
                        break;
                    default:
                        f44 = f7;
                        f45 = f9;
                        d3 = fluidPosX + 1.0 - 0.001;
                        d5 = fluidPosX + 1.0 - 0.001;
                        d4 = fluidPosZ;
                        d6 = fluidPosZ + 1.0;
                        flag7 = flag6;
                }

                if (flag7 && !isFaceOccludedByNeighbor(level, pos, direction, Math.max(f44, f45), level.getBlockState(pos.relative(direction)))) {
                    BlockPos blockpos = pos.relative(direction);
                    TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
                    if (atextureatlassprite[2] != null && level.getBlockState(blockpos).shouldDisplayFluidOverlay(level, blockpos, fluidState)) {
                        textureatlassprite2 = atextureatlassprite[2];
                    }

                    float f53 = textureatlassprite2.getU(0.0);
                    float f32 = textureatlassprite2.getU(8.0);
                    float f33 = textureatlassprite2.getV(((1.0 - f44) * 16.0 * 0.5));
                    float f34 = textureatlassprite2.getV(((1.0 - f45) * 16.0 * 0.5));
                    float f35 = textureatlassprite2.getV(8.0);
                    float f36 = direction.getAxis() == Direction.Axis.Z ? f5 : f6;
                    float f37 = f4 * f36 * f;
                    float f38 = f4 * f36 * f1;
                    float f39 = f4 * f36 * f2;
                    this.vertex(vertexConsumer, m4, d3, fluidPosY + f44, d4, f37, f38, f39, alpha, f53, f33, j);
                    this.vertex(vertexConsumer, m4, d5, fluidPosY + f45, d6, f37, f38, f39, alpha, f32, f34, j);
                    this.vertex(vertexConsumer, m4, d5, fluidPosY + f17, d6, f37, f38, f39, alpha, f32, f35, j);
                    this.vertex(vertexConsumer, m4, d3, fluidPosY + f17, d4, f37, f38, f39, alpha, f53, f35, j);
                    this.vertex(vertexConsumer, m4, d3, fluidPosY + f17, d4, f37, f38, f39, alpha, f53, f35, j);
                    this.vertex(vertexConsumer, m4, d5, fluidPosY + f17, d6, f37, f38, f39, alpha, f32, f35, j);
                    this.vertex(vertexConsumer, m4, d5, fluidPosY + f45, d6, f37, f38, f39, alpha, f32, f34, j);
                    this.vertex(vertexConsumer, m4, d3, fluidPosY + f44, d4, f37, f38, f39, alpha, f53, f33, j);
                }
            }
        }

    }

    private float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid, float currentHeight, float height1, float height2, BlockPos pos) {
        if (!(height2 >= 1.0F) && !(height1 >= 1.0F)) {
            float[] afloat = new float[2];
            if (height2 > 0.0F || height1 > 0.0F) {
                float f = this.getHeight(level, fluid, pos);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                this.addWeightedHeight(afloat, f);
            }

            this.addWeightedHeight(afloat, currentHeight);
            this.addWeightedHeight(afloat, height2);
            this.addWeightedHeight(afloat, height1);
            return afloat[0] / afloat[1];
        } else {
            return 1.0F;
        }
    }

    private void addWeightedHeight(float[] output, float height) {
        if (height >= 0.8F) {
            output[0] += height * 10.0F;
            output[1] += 10.0F;
        } else if (height >= 0.0F) {
            output[0] += height;
            int var10002 = (int) output[1]++;
        }

    }

    private float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        return this.getHeight(level, fluid, pos, blockstate, blockstate.getFluidState());
    }

    private float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluid.isSame(fluidState.getType())) {
            BlockState blockstate = level.getBlockState(pos.above());
            return fluid.isSame(blockstate.getFluidState().getType()) ? 1.0F : fluidState.getOwnHeight();
        } else {
            return !blockState.isSolid() ? 0.0F : -1.0F;
        }
    }

    private void vertex(VertexConsumer p_110985_, Matrix4f m4, double p_110986_, double p_110987_, double p_110988_, float p_110989_, float p_110990_, float p_110991_, float alpha, float p_110992_, float p_110993_, int p_110994_) {
        p_110985_.vertex(m4, (float) p_110986_, (float) p_110987_, (float) p_110988_).color(p_110989_, p_110990_, p_110991_, alpha).uv(p_110992_, p_110993_).uv2(p_110994_).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    private int getLightColor(BlockAndTintGetter level, BlockPos pos) {
        int i = LevelRenderer.getLightColor(level, pos);
        int j = LevelRenderer.getLightColor(level, pos.above());
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }
}
