package crstar.content;

import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import mindustry.content.Blocks;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;

import crstar.content.*;

import static mindustry.Vars.state;

public class AdharaPlanetGenerator extends SerpuloPlanetGenerator {
    Block[][] arr;
    ObjectMap<Block, Block> dec;
    ObjectMap<Block, Block> tars;
    float water;
    Vec3 basePos;

    public AdharaPlanetGenerator() {
        this.arr = new Block[][]{
                {CRBlocks.plasmaLiquid,Blocks.water,Blocks.sand,Blocks.sand},
                {CRBlocks.plasmaLiquid,Blocks.water,Blocks.sand,Blocks.sand},
                {CRBlocks.plasmaLiquid,Blocks.water,Blocks.sand,Blocks.sand},
                {CRBlocks.plasmaLiquid,Blocks.water,Blocks.sand,Blocks.sand}
        };
        this.dec = ObjectMap.of(new Object[]{Blocks.sporeMoss, Blocks.sporeCluster, Blocks.moss, Blocks.sporeCluster, CRBlocks.plasmaLiquid});
        this.tars = ObjectMap.of(new Object[]{Blocks.sporeMoss, Blocks.shale, Blocks.moss, Blocks.shale});
        this.water = 2.0F / (float)this.arr[0].length;
        this.basePos = new Vec3(0.9341721, (double)0.0F, 0.3568221);
    }

    @Override
    public boolean allowLanding(Sector sector){
        return sector.planet.allowLaunchToNumbered && (sector.hasBase() || sector.near().contains(s -> s.hasBase() && (s.isBeingPlayed())));
    }
}
