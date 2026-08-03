package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render3DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "矿物透视", category = Category.Render)
public class XRay extends Module {

    public BooleanValue barrel = new BooleanValue("木桶",this,false);
    public BooleanValue bedrock = new BooleanValue("基岩",this,false);
    public BooleanValue chest = new BooleanValue("箱子",this,false);
    public BooleanValue lava = new BooleanValue("岩浆",this,false);
    public BooleanValue water = new BooleanValue("水",this,false);
    public BooleanValue obsidian = new BooleanValue("黑曜石",this,false);
    public BooleanValue coal = new BooleanValue("煤炭",this,false);
    public BooleanValue diamond = new BooleanValue("钻石",this,false);
    public BooleanValue emerald = new BooleanValue("绿宝石",this,false);
    public BooleanValue gold = new BooleanValue("金",this,false);
    public BooleanValue iron = new BooleanValue("铁",this,false);
    public BooleanValue copper = new BooleanValue("铜",this,false);
    public BooleanValue lapis = new BooleanValue("青金石",this,false);
    public BooleanValue redstone = new BooleanValue("红石",this,false);
    public BooleanValue nether = new BooleanValue("合金",this,false);
    public BooleanValue nether_gold = new BooleanValue("金粒",this,false);
    public BooleanValue quartz = new BooleanValue("石英",this,false);
    public BooleanValue amethyst = new BooleanValue("水晶",this,false);



    public XRay(){

        bedrock.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:bedrock");
            }else {
                CreeperBox.removeXRayBlock("minecraft:bedrock");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        barrel.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:barrel");
            }else {
                CreeperBox.removeXRayBlock("minecraft:barrel");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        lava.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:lava");
            }else {
                CreeperBox.removeXRayBlock("minecraft:lava");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        water.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:water");
            }else {
                CreeperBox.removeXRayBlock("minecraft:water");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });


        obsidian.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:obsidian");
            }else {
                CreeperBox.removeXRayBlock("minecraft:obsidian");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });


        coal.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:coal_block");
                CreeperBox.addXRayBlock("minecraft:coal_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_coal_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:coal_block");
                CreeperBox.removeXRayBlock("minecraft:coal_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_coal_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        diamond.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:diamond_block");
                CreeperBox.addXRayBlock("minecraft:diamond_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_diamond_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:diamond_block");
                CreeperBox.removeXRayBlock("minecraft:diamond_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_diamond_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        emerald.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:emerald_block");
                CreeperBox.addXRayBlock("minecraft:emerald_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_emerald_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:emerald_block");
                CreeperBox.removeXRayBlock("minecraft:emerald_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_emerald_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        gold.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:gold_block");
                CreeperBox.addXRayBlock("minecraft:gold_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_gold_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:gold_block");
                CreeperBox.removeXRayBlock("minecraft:gold_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_gold_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });


        iron.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:iron_block");
                CreeperBox.addXRayBlock("minecraft:iron_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_iron_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:iron_block");
                CreeperBox.removeXRayBlock("minecraft:iron_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_iron_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });


        lapis.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:lapis_block");
                CreeperBox.addXRayBlock("minecraft:lapis_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_lapis_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:lapis_block");
                CreeperBox.removeXRayBlock("minecraft:lapis_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_lapis_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });


        copper.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:copper_block");
                CreeperBox.addXRayBlock("minecraft:copper_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_copper_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:copper_block");
                CreeperBox.removeXRayBlock("minecraft:copper_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_copper_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        redstone.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:redstone_block");
                CreeperBox.addXRayBlock("minecraft:redstone_ore");
                CreeperBox.addXRayBlock("minecraft:deepslate_redstone_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:redstone_block");
                CreeperBox.removeXRayBlock("minecraft:redstone_ore");
                CreeperBox.removeXRayBlock("minecraft:deepslate_redstone_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        nether.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:ancient_debris");
            }else {
                CreeperBox.removeXRayBlock("minecraft:ancient_debris");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        nether_gold.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:nether_gold_ore");
            }else {
                CreeperBox.removeXRayBlock("minecraft:nether_gold_ore");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        quartz.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:quartz_ore");
                CreeperBox.addXRayBlock("minecraft:quartz_block");
            }else {
                CreeperBox.removeXRayBlock("minecraft:quartz_ore");
                CreeperBox.addXRayBlock("minecraft:quartz_block");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });

        amethyst.setOnValueChange((value)->{
            if(value){
                CreeperBox.addXRayBlock("minecraft:amethyst_cluster");
            }else {
                CreeperBox.removeXRayBlock("minecraft:amethyst_cluster");
            }
            if(isEnable()){
                RenderHelperComponent.refreshNextFrame = true;
            }
            return value;
        });


        chest.setOnValueChange((value)->{
            if(value){
                CreeperBox.setChestXRay(true);
            }else {
                CreeperBox.setChestXRay(false);
            }
            return value;
        });

    }


    @Override
    public void onEnable() {
        RenderHelperComponent.refreshNextFrame = true;
        CreeperBox.setXRay(true);
    }

    @Override
    public void onDisable() {
        RenderHelperComponent.refreshNextFrame = true;
        CreeperBox.setXRay(false);
    }

}
