package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.events.IEventRunner;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.block.Block;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nullable;

public interface IFlexBlock extends IEventRunner<ActionResultType>
{
    default Block self()
    {
        return (Block) this;
    }

    void setGeneralShape(@Nullable DynamicShape shape);

    void setCollisionShape(@Nullable DynamicShape shape);

    void setRaytraceShape(@Nullable DynamicShape shape);

    void setRenderShape(@Nullable DynamicShape shape);
}
