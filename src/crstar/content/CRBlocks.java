package crstar.content;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.util.Nullable;
import crstar.CloudRainStar;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.DrawPart.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.content.*;

import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class CRBlocks {
    public static Block
            //drill
            electricLightningDrill,
            //turret
            nuclearLauncher,
            //defense
            chromiumWall,chromiumWallLarge,
            //unitFactory
            naviAssembler,
            //wuuuu
        alloySmelter,

            //enviroment
            plasmaLiquid, deepPlasmaLiquid,CRwater, orangeFloor, yellowFloor, greenFloor, snowFloor, iceFloor

            ;
    public static void load(){
        //turret
        nuclearLauncher = new ItemTurret("nuclear-launcher"){{
            requirements(Category.defense, with(CRItems.chromium, 450,CRItems.plutonium,200 ,CRItems.cloudRainAlloy,80));

            Effect sfe = new MultiEffect(Fx.shootBigColor, Fx.colorSparkBig);

            ammo(
                    CRItems.plutonium, new ArtilleryBulletType(7.5f, 1000f){{
                        width = 12f;
                        hitSize = 7f;
                        height = 20f;
                        splashDamage = 8500f;
                        splashDamageRadius = 200f;
                        shootEffect = sfe;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 1;
                        hitColor = backColor = trailColor = Pal.berylShot;
                        frontColor = Color.white;
                        trailWidth = 2.1f;
                        trailLength = 10;
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                    }}
            );

            coolantMultiplier = 6f;
            shootSound = Sounds.shootAlt;

            targetUnderBlocks = false;
            shake = 1f;
            ammoPerShot = 50;
            drawer = new DrawTurret("reinforced-");
            shootY = -2;
            outlineColor = Pal.darkOutline;
            size = 8;
            envEnabled |= Env.space;
            reload = 600f;
            recoil = 2f;
            range = 190;
            shootCone = 3f;
            scaledHealth = 180;
            rotateSpeed = 0.5f;
            researchCostMultiplier = 0.05f;

            coolant = consume(new ConsumeLiquid(Liquids.water, 15f / 60f));
            limitRange();
        }};

        //drill
        electricLightningDrill = new Drill("electric-lightning-drill"){{

        }};

        //factory
        alloySmelter = new HeatCrafter("cloudrain-alloy-smelter"){
            {
                requirements(Category.crafting, with(Items.silicon, 100, Items.graphite, 80, Items.tungsten, 80, Items.oxide, 80));

                size = 3;

                itemCapacity = 20;
                heatRequirement = 10f;
                craftTime = 60f * 0.8f;
                hasLiquids = true;
                liquidCapacity = 50f * 5;
                maxEfficiency = 1f;

                ambientSound = Sounds.smelter;
                ambientSoundVolume = 0.9f;

                outputItem = new ItemStack(CRItems.cloudRainAlloy, 4);

                craftEffect = new RadialEffect(Fx.surgeCruciSmoke, 4, 90f, 5f);

                drawer = new DrawMulti(
                        new DrawRegion("-bottom"),
                        new DrawCircles(){{
                            color = CRColor.starBlue.cpy().a(0.24f);
                            strokeMax = 2.5f;
                            radius = 10f;
                            amount = 3;
                        }},
                        new DrawLiquidRegion(CRLiquids.plasmaLiquid),
                        new DrawDefault(),
                        new DrawHeatInput(),
                        new DrawHeatRegion(){{
                            color = Color.valueOf("ff6060ff");
                        }},
                        new DrawHeatRegion("-vents"){{
                            color.a = 1f;
                        }});

                consumeItem(CRItems.chromium, 2);
                consumeLiquid(CRLiquids.plasmaLiquid, 50f / 60f);
                consumePower(1.5f);
            }
        };

        //defense
        chromiumWall = new Wall("chromium-wall"){{
            health = 850;
            requirements(Category.defense, with(CRItems.chromium, 6));
        }};
        chromiumWallLarge = new Wall("chromium-wall-large"){{
            health = 3400;
            requirements(Category.defense, ItemStack.mult(chromiumWall.requirements, 4));
            size = 2;
        }};

        //units
        naviAssembler = new UnitAssembler("navi-assembler"){
            {
                requirements(Category.units, with(
                        CRItems.chromium, 500, Items.oxide, 150, Items.carbide, 80, Items.silicon, 650
                ));
                regionSuffix = "-dark";
                size = 5;
                health = 5800;
                plans.add(
                        new AssemblerUnitPlan(
                                CRUnitTypes.twilight,
                                60f * 182f,
                                PayloadStack.list(
                                        CRUnitTypes.polarise, 1,
                                        chromiumWallLarge, 10
                                )
                        ){{
                            itemReq = new ItemStack[]{
                                    new ItemStack(CRItems.cloudRainAlloy, 850)
                            };
                        }}
                );
                areaSize = 38;
                researchCostMultiplier = 0.4f;
                hasItems = true;
                liquidCapacity = 700f;


                consumePower(25f);
                consumeLiquid(CRLiquids.plasmaLiquid, 650f / 60f);
            }
        };

        plasmaLiquid = new Floor("pooled-plasmaLiquid"){{
            status = CRStatusEffects.crRepairing;
            statusDuration = 240f;
            speedMultiplier = 0.8f;
            drownTime = 600f;
            variants = 3;
            liquidDrop = CRLiquids.plasmaLiquid;
            liquidMultiplier = 1f;
            cacheLayer = CacheLayer.water;
            isLiquid = true;

            emitLight = true;
            lightRadius = 25f;
            lightColor = CRColor.starBlue.cpy().a(0.2f);
        }};
    }
}
