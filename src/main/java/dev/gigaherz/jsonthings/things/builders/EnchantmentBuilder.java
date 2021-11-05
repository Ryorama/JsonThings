package dev.gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import dev.gigaherz.jsonthings.things.misc.FlexEnchantment;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EnchantmentBuilder extends BaseBuilder<FlexEnchantment>
{
    private Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
    private EnchantmentType type = EnchantmentType.BREAKABLE;
    private EquipmentSlotType[] slots = EquipmentSlotType.values();
    private int minLevel = 1;
    private int maxLevel = 1;
    private int baseCost = 1;
    private int perLevelCost = 10;
    private int randomCost = 5;
    private List<ResourceLocation> blackList = Lists.newArrayList();
    private ItemPredicate itemCompatibility;
    private boolean isTreasure = false;
    private boolean isCurse = false;
    private boolean isTradeable = true;
    private boolean isDiscoverable = true;
    private boolean isAllowedOnBooks = true;

    private EnchantmentBuilder(ResourceLocation registryName)
    {
        super(registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Enchantment";
    }

    public static EnchantmentBuilder begin(ResourceLocation registryName)
    {
        return new EnchantmentBuilder(registryName);
    }

    public void setRarity(Enchantment.Rarity rarity)
    {
        this.rarity = rarity;
    }

    public void setEnchantmentType(EnchantmentType type)
    {
        this.type = type;
    }

    public void setMinLevel(int minLevel)
    {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int macLevel)
    {
        this.maxLevel = macLevel;
    }

    public void setBaseCost(int baseCost)
    {
        this.baseCost = baseCost;
    }

    public void setPerLevelCost(int perLevelCost)
    {
        this.perLevelCost = perLevelCost;
    }

    public void setRandomCost(int randomCost)
    {
        this.randomCost = randomCost;
    }

    public void setIsTreasure(boolean treasure)
    {
        this.isTreasure = treasure;
    }

    public void setIsCurse(boolean curse)
    {
        this.isCurse = curse;
    }

    public void setIsTradeable(boolean tradeable)
    {
        this.isTradeable = tradeable;
    }

    public void setIsDiscoverable(boolean discoverable)
    {
        this.isDiscoverable = discoverable;
    }

    public void setItemCompatibility(ItemPredicate item_compatibility)
    {
        this.itemCompatibility = item_compatibility;
    }

    public void setBlacklist(List<ResourceLocation> blacklist)
    {
        this.blackList = blacklist;
    }

    @Override
    protected FlexEnchantment buildInternal()
    {
        FlexEnchantment flexEnchantment = new FlexEnchantment(rarity, type, slots);

        flexEnchantment.setMinLevel(minLevel);
        flexEnchantment.setMaxLevel(maxLevel);
        flexEnchantment.setBaseCost(baseCost);
        flexEnchantment.setPerLevelCost(perLevelCost);
        flexEnchantment.setRandomCost(randomCost);
        flexEnchantment.setItemCompatibility(itemCompatibility);
        flexEnchantment.setTreasure(isTreasure);
        flexEnchantment.setCurse(isCurse);
        flexEnchantment.setTradeable(isTradeable);
        flexEnchantment.setDiscoverable(isDiscoverable);
        flexEnchantment.setAllowedOnBooks(isAllowedOnBooks);
        flexEnchantment.setBlackList(blackList.stream().map(loc -> {
            RegistryObject<?> ro = RegistryObject.of(loc, ForgeRegistries.ENCHANTMENTS);
            return (Predicate<Enchantment>) ((enchantment) -> ro.filter(en -> en == enchantment).isPresent());
        }).collect(Collectors.toList()));

        return flexEnchantment;
    }

    public EnchantmentBuilder setIsAllowedOnBooks(boolean allow_on_books)
    {
        this.isAllowedOnBooks = allow_on_books;
        return this;
    }
}
