package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.Food;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class ThingParser<TBuilder extends BaseBuilder<?>> extends JsonReloadListener
{
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<ResourceLocation, TBuilder> buildersByName = Maps.newHashMap();
    private final List<TBuilder> builders = Lists.newArrayList();
    private final String thingType;
    private final Gson gson;

    public ThingParser(Gson gson, String thingType)
    {
        super(gson, thingType);
        this.gson = gson;
        this.thingType = thingType;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        objectIn.forEach(this::parseFromElement);
    }

    protected abstract TBuilder processThing(ResourceLocation key, JsonObject data);

    public TBuilder parseFromElement(ResourceLocation key, JsonElement json)
    {
        try
        {
            TBuilder builder = processThing(key, json.getAsJsonObject());
            buildersByName.put(key, builder);
            builders.add(builder);
            return builder;
        }
        catch (Exception e)
        {
            CrashReport crashReport = CrashReport.forThrowable(e, "Error while parsing " + thingType + " from " + key);

            CrashReportCategory reportCategory = crashReport.addCategory("Thing", 1);
            reportCategory.setDetail("Resource name", key);

            throw new ReportedException(crashReport);
        }
    }

    public List<TBuilder> getBuilders()
    {
        return Collections.unmodifiableList(builders);
    }

    public Map<ResourceLocation, TBuilder> getBuildersMap()
    {
        return Collections.unmodifiableMap(buildersByName);
    }

    protected StackContext parseStackContext(JsonObject item)
    {
        StackContext ctx = new StackContext(null);

        if (item.has("count"))
        {
            int meta = item.get("count").getAsInt();
            ctx = ctx.withCount(meta);
        }

        if (item.has("nbt"))
        {
            try
            {
                JsonElement element = item.get("nbt");
                CompoundNBT nbt;
                if (element.isJsonObject())
                    nbt = JsonToNBT.parseTag(GSON.toJson(element));
                else
                    nbt = JsonToNBT.parseTag(element.getAsString());
                ctx = ctx.withTag(nbt);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to parse NBT json.", e);
            }
        }

        return ctx;
    }

    protected ResourceLocation makeResourceLocation(ResourceLocation key, String name)
    {
        if (name.contains(":"))
            return new ResourceLocation(name);
        return new ResourceLocation(key.getNamespace(), name);
    }

    public void finishLoading()
    {
    }

    public String getThingType()
    {
        return thingType;
    }

    public TBuilder getOrCrash(ResourceLocation name)
    {
        TBuilder b = buildersByName.get(name);
        if (b == null)
            throw new RuntimeException("There is no known " + thingType + " with name " + name);
        return b;
    }
}
