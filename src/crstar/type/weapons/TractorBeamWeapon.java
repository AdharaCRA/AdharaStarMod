package crstar.type.weapons;

import arc.Core;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class TractorBeamWeapon extends Weapon {
    public boolean targetAir = true,targetGround = false;

    public float damage = 0.3f;
    public float fractionRepairSpeed = 0f;
    public float beamWidth = 0.4f;

    public float force = 0.3f;
    public float scaledForce = 0f;

    public TextureRegion laser, laserEnd, laserTop;

    public TractorBeamWeapon(String name){
        super(name);
    }

    public TractorBeamWeapon(){
    }

    {
        //must be >0 to prevent various bugs
        reload = 1f;
        predictTarget = false;
        autoTarget = true;
        controllable = false;
        rotate = true;
        useAmmo = false;
        mountType = TractorBeamMount::new;
        recoil = 0f;
        noAttack = true;
        useAttackRange = false;
    }

    @Override
    public void addStats(UnitType u, Table w){
        w.row();
        w.add("[lightgray]" + Stat.damage.localized() + ": " + (mirror ? "2x " : "") + "[white]" + (int)(damage * 60) + " " + StatUnit.perSecond.localized());
    }

    @Override
    public float dps(){
        return damage*60f*(mirror ? 2 : 1);
    }

    @Override
    public void load(){
        super.load();

        laser = Core.atlas.find("parallax-laser");
        laserEnd = Core.atlas.find("parallax-laser-end");
        laserTop = Core.atlas.find("parallax-laser-end");
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground){
        return Units.closestEnemy(unit.team, x, y, range, u -> u.checkTarget(targetAir,targetGround));
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range){
        super.checkTarget(unit,target,x,y,range);
        return !(target.within(unit, range + unit.hitSize/2f) && target.team() != unit.team && target instanceof Healthc u && u.isValid());
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
        //does nothing, shooting is handled in update()
    }

    @Override
    public void update(Unit unit, WeaponMount mount){
        super.update(unit, mount);

        TractorBeamMount parallax = (TractorBeamMount)mount;

        boolean canShoot = mount.shoot;
        parallax.strength = Mathf.lerpDelta(parallax.strength, Mathf.num(autoTarget ? mount.target != null : canShoot), 0.2f);

        if(canShoot && mount.target instanceof Unit u){
            float baseAmount = damage * parallax.strength * Time.delta + fractionRepairSpeed * parallax.strength * Time.delta * u.maxHealth() / 100f;
            u.damageContinuousPierce(baseAmount);
            u.impulseNet(Tmp.v1.set(unit).sub(u).limit((force + (1f - u.dst(unit) / range()) * scaledForce)*2f));
        }
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);
        TractorBeamMount parallax = (TractorBeamMount)mount;

        if(unit.canShoot()&&mount.target != null){
            float
                    weaponRotation = unit.rotation - 90,
                    wx = unit.x + Angles.trnsx(weaponRotation, x, y),
                    wy = unit.y + Angles.trnsy(weaponRotation, x, y),
                    z = Draw.z();
            Draw.z(Layer.effect);
            Drawf.laser(laser, laserTop, laserEnd,
                    wx, wy,
                    mount.target.getX(), mount.target.getY(), parallax.strength * beamWidth);
            Draw.z(z);
        }
    }

    @Override
    public void init(){
        super.init();
        bullet.healPercent = fractionRepairSpeed;
    }

    public static class TractorBeamMount extends WeaponMount{
        public float strength;

        public TractorBeamMount(Weapon weapon){
            super(weapon);
        }
    }
}