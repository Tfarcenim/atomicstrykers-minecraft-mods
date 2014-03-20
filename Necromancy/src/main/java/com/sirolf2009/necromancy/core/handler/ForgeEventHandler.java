package com.sirolf2009.necromancy.core.handler;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;

import com.sirolf2009.necromancy.Necromancy;
import com.sirolf2009.necromancy.achievement.AchievementNecromancy;
import com.sirolf2009.necromancy.block.RegistryBlocksNecromancy;
import com.sirolf2009.necromancy.entity.EntityIsaacNormal;
import com.sirolf2009.necromancy.entity.EntityNightCrawler;
import com.sirolf2009.necromancy.item.ItemGeneric;
import com.sirolf2009.necromancy.item.ItemNecroSkull;
import com.sirolf2009.necromancy.item.RegistryNecromancyItems;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class ForgeEventHandler
{
    
    private final SpawnListEntry nightCrawlerEntry = new SpawnListEntry(EntityNightCrawler.class, 10, 1, 2);
    private final SpawnListEntry isaacEntry = new SpawnListEntry(EntityIsaacNormal.class, 5, 1, 1);
    
    @SubscribeEvent
    public void onPotentialSpawns(PotentialSpawns event)
    {
        boolean nightCrawler = false;
        boolean isaac = false;

        for (SpawnListEntry spawn : event.list)
        {
            if (spawn.entityClass.equals(EntityZombie.class))
            {
                nightCrawler = true;
            }
            else if (spawn.entityClass.equals(EntitySkeleton.class))
            {
                isaac = true;
            }
        }

        if ((nightCrawler || isaac) && event.world.rand.nextInt(6) == 0)
        {
            if (nightCrawler)
            {
                event.list.add(nightCrawlerEntry);
            }
            if (isaac)
            {
                event.list.add(isaacEntry);
            }
            // maybe this will fix nightCrawlers and isaac overriding
            Collections.shuffle(event.list);
        }
    }
    
    @SubscribeEvent
    public void LivingDropsEvent(LivingDeathEvent evt)
    {
        if (evt.entity instanceof EntityLiving && !evt.entity.worldObj.isRemote)
        {
            switch (evt.entity.worldObj.rand.nextInt(100))
            {
            case 0:
                evt.entity.entityDropItem(new ItemStack(RegistryNecromancyItems.organs, 1, 0), 1);
                break; // brains
            case 1:
                evt.entity.entityDropItem(new ItemStack(RegistryNecromancyItems.organs, 1, 1), 1);
                break; // heart
            case 2:
                evt.entity.entityDropItem(new ItemStack(RegistryNecromancyItems.organs, 1, 2), 1);
                break; // muscle
            case 3:
                evt.entity.entityDropItem(new ItemStack(RegistryNecromancyItems.organs, 1, 2), 1);
                break; // muscle
            case 4:
                evt.entity.entityDropItem(new ItemStack(RegistryNecromancyItems.organs, 1, 2), 1);
                break; // muscle
            case 5:
                evt.entity.entityDropItem(new ItemStack(RegistryNecromancyItems.organs, 1, 2), 1);
                break; // muscle
            case 6:
                evt.entity.entityDropItem(new ItemStack(RegistryNecromancyItems.organs, 1, 3), 1);
                break; // lungs
            default:
                break;
            }
        }
    }

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event)
    {
        ItemStack item = event.crafting;
        EntityPlayer player = event.player;
        IInventory craftMatrix = event.craftMatrix;
        if (item != null)
        {
            if (item.getItem() == RegistryNecromancyItems.necronomicon)
            {
                player.addStat(AchievementNecromancy.NecronomiconAchieve, 1);
            }
            else if (item.isItemEqual(ItemGeneric.getItemStackFromName("Jar of Blood")))
            {
                player.inventory.addItemStackToInventory(new ItemStack(Items.bucket));
            }
            else if (item.getItem() == RegistryNecromancyItems.bucketBlood)
            {
                player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle, 8));
            }
            else if (item.getItem() instanceof ItemBlock)
            {
                Block block = ((ItemBlock) item.getItem()).field_150939_a;
                if (block == RegistryBlocksNecromancy.sewing)
                {
                    player.addStat(AchievementNecromancy.SewingAchieve, 1);
                }
                else if (block == RegistryBlocksNecromancy.skullWall)
                {
                    Necromancy.loggerNecromancy.info(craftMatrix.getStackInSlot(0) + " is in " + craftMatrix.getStackInSlot(0).getUnlocalizedName());
                    item.stackTagCompound.setString("Base", craftMatrix.getStackInSlot(1).getUnlocalizedName());
                    item.stackTagCompound.setString("Skull1", ItemNecroSkull.skullTypes[craftMatrix.getStackInSlot(1).getItemDamage()]);
                    item.stackTagCompound.setString("Skull2", ItemNecroSkull.skullTypes[craftMatrix.getStackInSlot(4).getItemDamage()]);
                    item.stackTagCompound.setString("Skull3", ItemNecroSkull.skullTypes[craftMatrix.getStackInSlot(5).getItemDamage()]);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event)
    {
        ItemStack result = fillCustomBucket(event.world, event.target);
        if (result != null)
        {
            event.result = result;
            event.setResult(Result.ALLOW);
        }
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos)
    {
        Block blockID = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);

        if ((blockID == RegistryBlocksNecromancy.fluidBlood.getBlock() || blockID == RegistryBlocksNecromancy.blood)
                && world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0)
        {

            world.setBlock(pos.blockX, pos.blockY, pos.blockZ, Blocks.air, 0, 3);

            return new ItemStack(RegistryNecromancyItems.bucketBlood);
        }
        else
            return null;
    }
}
