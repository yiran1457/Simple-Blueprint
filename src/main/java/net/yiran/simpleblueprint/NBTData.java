package net.yiran.simpleblueprint;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBTData implements BlockAndTintGetter {

    public CompoundTag compoundTag;
    public Map<BlockPos, BlockState> blockRenderMap;
    public Map<BlockPos, FluidState> fluidRenderMap;
    public Map<BlockPos, BlockEntity> blockEntityMap;
    public BlockPos offset = new BlockPos(0, 0, 0);
    public int x;
    public int y;
    public int z;

    public NBTData(CompoundTag tag) {
        this.compoundTag = tag;
        init();
    }

    public void init() {
        Map<BlockPos, BlockState> renderMap = new HashMap<>();
        Map<BlockPos, BlockEntity> entityMap = new HashMap<>();
        Map<BlockPos, FluidState> fluidMap = new HashMap<>();

        var bg = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BLOCK);
        List<BlockState> states = new ArrayList<>();
        for (Tag palette : compoundTag.getList("palette", Tag.TAG_COMPOUND)) {
            states.add(NbtUtils.readBlockState(bg, (CompoundTag) palette));
        }

        for (Tag block : compoundTag.getList("blocks", Tag.TAG_COMPOUND)) {
            BlockPos key = BlockPos.CODEC.parse(NbtOps.INSTANCE, ((CompoundTag) block).get("pos")).result().get();
            BlockState value = states.get(((CompoundTag) block).getInt("state"));
            //非空BlockState
            if (value != Blocks.AIR.defaultBlockState()) {
                renderMap.put(key, value);

                //FluidState
                if(!value.getFluidState().isEmpty()){
                    fluidMap.put(key, value.getFluidState());
                }

                //BlockEntity
                if(((CompoundTag) block).contains("nbt", Tag.TAG_COMPOUND)) {
                    entityMap.put(key,BlockEntity.loadStatic(key,value,((CompoundTag) block).getCompound("nbt")));
                }
            }
        }
        blockRenderMap = renderMap;
        blockEntityMap = entityMap;
        fluidRenderMap = fluidMap;
        var size = compoundTag.getList("size", Tag.TAG_INT);
        x = size.getInt(0);
        y = size.getInt(1);
        z = size.getInt(2);
    }


    @Override
    public float getShade(Direction direction, boolean b) {
        return Minecraft.getInstance().level.getShade(direction, b);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return Minecraft.getInstance().level.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return Minecraft.getInstance().level.getBlockTint(blockPos, colorResolver);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return blockEntityMap.get(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return blockRenderMap.getOrDefault(blockPos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return getBlockState(blockPos).getFluidState();
    }

    @Override
    public int getHeight() {
        return Minecraft.getInstance().level.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return Minecraft.getInstance().level.getMinBuildHeight();
    }
}
