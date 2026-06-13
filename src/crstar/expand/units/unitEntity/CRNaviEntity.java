package crstar.expand.units.unitEntity;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import crstar.content.CRColor;
import crstar.content.CRFx;
import crstar.entities.abilities.CRForceFieldAbility;
import crstar.expand.units.CRUnitRegister;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;

public class CRNaviEntity extends UnitWaterMove {
    public static final float finalMinScaledDamage = 200f,finalMaxDamageAmount = 45000f;
    public float minScaledDamage = finalMinScaledDamage,maxDamageAmount = finalMaxDamageAmount;

    public static final float healthDelta=0.01f,damageDelta=0.03f;

    public float recentDamage = maxDamageAmount;
    public float RecentDamageResume = maxDamageAmount / 60f;


    public static float executor_cooldown = Time.toSeconds * 5f;
    public float executorReload = 0f,eRadius = 400f;

    protected float warmup=0f,rot,rotSpeed=1f,randLen;
    protected boolean any = false;

    public Seq<StatusEffect> statuses;

    @Override
    public int classId(){
        return CRUnitRegister.getID(CRNaviEntity.class);
    }
    public void ExecuteApply(Unit u) {
        u.health -= Math.max(u.health , (u.maxHealth - u.health) )*0.16f;
        statuses = Vars.content.statusEffects();
        statuses.retainAll(s -> s.disarm || (s.healthMultiplier*s.damageMultiplier*s.speedMultiplier*s.reloadMultiplier > 0.5&&s.damage>0));
        for(StatusEffect s : statuses){
            u.apply(s,300f);
        }
    }

    @Override
    public void update() {
        super.update();
        any = false;
        recentDamage += RecentDamageResume * Time.delta;
        if (recentDamage >= maxDamageAmount) {
            recentDamage = maxDamageAmount;
        }
        warmup = Mathf.lerpDelta(this.warmup,1.0F, 0.005F);

        rot +=Time.delta*rotSpeed;
        if(rot>=360f) rot = 0f;

        randLen = Mathf.random(0,this.hitSize*0.25f);

        if(this.damaged()){
            this.damageMultiplier = 1+Math.min(Math.max(this.maxHealth-this.health,0f),this.maxHealth*0.5f)/this.maxHealth/healthDelta*damageDelta;
        }

        executorReload += (this.healthf()<=0.5f && executorReload<executor_cooldown) ? Time.delta : 0f;
        if (executorReload >= executor_cooldown) {
            Groups.unit.intersect(this.x - eRadius, this.y - eRadius, eRadius * 2f, eRadius * 2f, e->{
                if(e.team !=this.team && e.type.hittable){
                    any = true;
                    ExecuteApply(e);
                    CRFx.star.at(e.x,e.y,45,CRColor.starG,e.hitSize*5f);
                }
            });
            executorReload = 0f;
        }
    }

    @Override
    public void draw(){
        super.draw();
        float z = Draw.z();
        Draw.z(Layer.effect);

        //draw core
        Draw.color(CRColor.starBlue);
        float len = this.hitSize;
        for(int i=0;i<4;i++){
            Drawf.tri(this.x, this.y, len / 11f * warmup, len + (len * 1.5f +randLen)*warmup, rot*rotSpeed*0.5f + i * 90);
        }
        //draw reloadCounter
        Lines.stroke(2.5f*warmup);
        CRForceFieldAbility.circlePercent(this.x,this.y,this.hitSize*1.5f*warmup,executorReload/executor_cooldown,0);

        //draw Sword
        Draw.z(z);
    }

    @Override
    public void rawDamage(float amount) {
        if(this.healthf()<=0.5f){
            maxDamageAmount = finalMaxDamageAmount*0.5f;
        }

        boolean hadShields = this.shield > 1.0E-4F;
        if (hadShields) {
            this.shieldAlpha = 1.0F;
        }

        if(amount<=minScaledDamage || amount>= 5000f*(this.healthf()<0.5f ? 0.75 : 1) ) return;

        float shieldDamage = Math.min(Math.max(this.shield, 0.0F), amount);
        this.shield -= shieldDamage;
        this.hitTime = 1.0F;

        amount -= shieldDamage;
        amount = Math.min(recentDamage / healthMultiplier, amount);
        recentDamage -= amount * 1.5f * healthMultiplier;

        if (amount > 0.0F && this.type.killable) {
            this.health -= amount;
            if (this.health <= 0.0F && !this.dead) {
                this.kill();
            }

            if (hadShields && this.shield <= 1.0E-4F) {
                Fx.unitShieldBreak.at(this.x, this.y, 0.0F, this.team.color, this);
            }
        }
    }
}
