package tech.thatgravyboat.goodall.common.registry;

import net.minecraft.entity.EntityType;

public record SpawnData(EntityType<?> entityType, int weight, int min, int max) {}
