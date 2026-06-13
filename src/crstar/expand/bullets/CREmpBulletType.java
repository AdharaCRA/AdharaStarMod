package crstar.expand.bullets;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.StatusEffect;

import static mindustry.Vars.state;

//电浆球
public class CREmpBulletType extends EmpBulletType{
    private static final Seq<Healthc> all = new Seq<>();

    public float bdamage = 1, reload = 100, range = 60;
    public Effect healEffect = Fx.heal, hitEffect = Fx.none, damageEffect = Fx.chainLightning;
    public StatusEffect status = StatusEffects.electrified;
    public Sound shootSound = Sounds.spark;
    public float statusDuration = 60f * 6f;
    public boolean targetGround = true, targetAir = true, hitBuildings = true, hitUnits = true;
    public int maxTargets = 25;
    public float healPercent = 3f;
    /** Multiplies healing to units of the same type by this amount. */

    public float layer = Layer.bullet - 0.001f, blinkScl = 20f, blinkSize = 0.1f;
    public float effectRadius = 5f;
    public Color color = Pal.heal;

    protected float timer, curStroke;
    protected boolean anyNearby = false;

    @Override
    public void update(Bullet bu){
        super.update(bu);

        curStroke = Mathf.lerpDelta(curStroke, anyNearby ? 1 : 0, 0.09f);

        if((timer += Time.delta) >= reload){
            anyNearby = false;

            all.clear();

            if(hitUnits){
                Units.nearby(null, bu.x, bu.y, range, other -> {
                    if(other.checkTarget(targetAir, targetGround) && other.targetable(bu.team) && (other.team != bu.team || other.damaged())){
                        all.add(other);
                    }
                });
            }

            if(hitBuildings && targetGround){
                Units.nearbyBuildings(bu.x, bu.y, range, b -> {
                    if((b.team != Team.derelict || state.rules.coreCapture) && ((b.team != bu.team && b.block.targetable) || b.damaged()) && !b.block.privileged){
                        all.add(b);
                    }
                });
            }

            all.sort(h -> h.dst2(bu.x, bu.y));
            int len = Math.min(all.size, maxTargets);
            for(int i = 0; i < len; i++){
                Healthc other = all.get(i);

                //lightning gets absorbed by plastanium
                var absorber = Damage.findAbsorber(bu.team, bu.x, bu.y, other.getX(), other.getY());
                if(absorber != null){
                    other = absorber;
                }

                if(((Teamc)other).team() == bu.team){
                    if(other.damaged()){
                        anyNearby = true;
                        other.heal(healPercent / 100f * other.maxHealth());
                        healEffect.at(other);
                        damageEffect.at(bu.x, bu.y, 0f, color, other);
                        hitEffect.at(bu.x, bu.y, bu.angleTo(other), color);

                        if(other instanceof Building b){
                            Fx.healBlockFull.at(b.x, b.y, 0f, color, b.block);
                        }
                    }
                }else{
                    anyNearby = true;
                    if(other instanceof Building b){
                        b.damage(bu.team, bdamage);
                    }else{
                        other.damage(bdamage);
                    }
                    if(other instanceof Statusc s){
                        s.apply(status, statusDuration);
                    }
                    hitEffect.at(other.x(), other.y(), bu.angleTo(other), color);
                    damageEffect.at(bu.x, bu.y, 0f, color, other);
                    hitEffect.at(bu.x, bu.y, bu.angleTo(other), color);
                }
            }
            if(anyNearby){
                shootSound.at(bu);
            }

            timer = 0f;
        }
    }


    @Override
    public void draw(Bullet b){
        super.draw(b);

        Draw.z(layer);
        Draw.color(color);
        float orbRadius = effectRadius * (1f + Mathf.absin(blinkScl, blinkSize));

        Fill.circle(b.x, b.y, orbRadius);
        Draw.color();
        Fill.circle(b.x, b.y, orbRadius / 2f);

        Drawf.light(b.x, b.y, range * 1.5f, color, curStroke * 0.8f);

        Draw.reset();
    }
}
