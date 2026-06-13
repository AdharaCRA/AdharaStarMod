package crstar.entities.abilities;

import arc.Core;
import arc.Events;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class ShockWaveDefenseAbility extends Ability {
    public float range;
    public float reload;
    public float bulletDamage;
    public float falloffCount = 20f;
    public float shake = 2f;
    //checking for bullets every frame is costly, so only do it at intervals even when ready.
    public Sound shootSound = Sounds.bang;
    public Color waveColor = Pal.accent, heatColor = Pal.turretHeat, shapeColor = Color.valueOf("f29c83");
    public float cooldownMultiplier = 1f;
    public Effect hitEffect = Fx.hitSquaresColor;
    public Effect waveEffect = Fx.pointShockwave;
    public float reloadCounter = Mathf.random(reload);
    public float heat = 0f;
    public Seq<Bullet> targets = new Seq<>();

    public ShockWaveDefenseAbility(float bulletDamage, float reload, float range){
        this.bulletDamage = bulletDamage;
        this.reload = reload;
        this.range = range;
    }

    @Override
    public void addStats(Table t){
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / 8,2)));
        t.row();
        t.add(abilityStat("firingrate", Strings.autoFixed(60f / reload, 2)));
        t.row();
        t.add(Core.bundle.format("bullet.damage", bulletDamage));
    }
    @Override
    public void update(Unit unit) {
        if ((reloadCounter += Time.delta) >= reload) {
            targets.clear();

            Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2, range * 2, b -> {
                if (b.team != unit.team && b.type.hittable) {
                    targets.add(b);
                }
            });

            if(targets.size > 0){
                heat = 1f;
                reloadCounter = 0f;
                waveEffect.at(unit.x, unit.y, range, waveColor);
                shootSound.at(unit.x,unit.y);
                Effect.shake(shake, shake, unit.x, unit.y);
                float waveDamage = Math.min(bulletDamage, bulletDamage * falloffCount / targets.size);

                for(var target : targets){
                    if(target.damage > waveDamage){
                        target.damage -= waveDamage;
                    }else{
                        target.remove();
                    }
                    hitEffect.at(target.x, target.y, waveColor);
                }

                if(unit.team == state.rules.defaultTeam){
                    Events.fire(EventType.Trigger.shockwaveTowerUse);
                }
            }
            heat = Mathf.clamp(heat - Time.delta / reload * cooldownMultiplier);
        }
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        Draw.reset();
    }
}
