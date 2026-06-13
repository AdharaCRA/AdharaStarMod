package crstar.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import crstar.content.CRColor;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

import static mindustry.Vars.tilesize;

public class CRForceFieldAbility extends Ability {
    public float radius = 60f;
    /** Shield regen speed in damage/tick. */
    public float regen = 0.1f;
    /** Maximum shield. */
    public float max = 200f;
    /** Cooldown after the shield is broken, in ticks. */
    public float cooldown = 60f * 5;
    public int sectors = 5;
    public float sectorRad = 0.1f,rotateSpeed = 0.5f;
    /** State. */
    protected float radiusScale, alpha;
    protected boolean wasBroken = true;


    private static CRForceFieldAbility paramField;
    private static Unit paramUnit;

    public CRForceFieldAbility(float radius, float regen, float max, float cooldown){
        this.radius = radius;
        this.regen = regen;
        this.max = max;
        this.cooldown = cooldown;
    }

    CRForceFieldAbility(){}

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(radius / tilesize, 2)));
        t.row();
        t.add(abilityStat("shield", Strings.autoFixed(max, 2)));
        t.row();
        t.add(abilityStat("repairspeed", Strings.autoFixed(regen * 60f, 2)));
        t.row();
        t.add(abilityStat("cooldown", Strings.autoFixed(cooldown / 60f, 2)));
    }

    @Override
    public void update(Unit unit){
        if(unit.shield <= 0f && !wasBroken){
            unit.shield -= cooldown * regen;

            Fx.shieldBreak.at(unit.x, unit.y, radius, unit.type.shieldColor(unit), this);
        }
        if(unit.shield>=0f && wasBroken){
            unit.shield = max;
        }

        wasBroken = unit.shield <= 0f;

        if(unit.shield < max){
            unit.shield += Time.delta * regen;
        }

        if(unit.shield > 0){
            paramUnit = unit;
            paramField = this;
            Groups.bullet.intersect(unit.x - radius, unit.y - radius, radius * 2f, radius * 2f, b->{
                if (b.team != unit.team && paramUnit.shield > 0) {
                    b.absorb();
                    paramUnit.shield -= Math.min(b.type.shieldDamage(b)*0.5f, 200f);
                    paramField.alpha = 1f;
                }
            });
        }else{
            radiusScale = 0f;
        }
    }

    @Override
    public void death(Unit unit){
        //self-destructing units can have a shield on death
        if(unit.shield > 0f && !wasBroken){
            Fx.shieldBreak.at(unit.x, unit.y, radius, unit.type.shieldColor(unit), this);
        }
    }

    @Override
    public void draw(Unit unit){
        float z = Draw.z();
        Draw.z(Layer.effect);

        if(unit.shield > 0) {
            Draw.color(Pal.heal);
            Lines.stroke(Math.min(unit.shield/max*20f, 2f));
            circlePercent(unit.x, unit.y, radius, 1, 0f);

            Draw.z(Layer.shields+0.001f*alpha);
            Draw.color(unit.team.color);
            Fill.circle(unit.x,unit.y,radius);

            Draw.z(Layer.effect);
            for(int i = 0; i < sectors; i++){
                float rot = i * 360f/sectors - Time.time * rotateSpeed;
                Lines.arc(unit.x, unit.y, radius+15f, sectorRad, rot);
            }
            Drawf.light(unit.x, unit.y, radius, Pal.heal, 0.9f);
        }else{
            Draw.color(unit.team.color);
            Lines.stroke(2.5f);
            circlePercent(unit.x, unit.y, unit.hitSize*1.2f, (cooldown*regen - Math.abs(unit.shield)) / (cooldown*regen), 0f);
        }
        Draw.z(z);
    }

    @Override
    public void displayBars(Unit unit, Table bars){
        bars.add(new Bar("stat.shieldhealth", Pal.accent, () -> unit.shield / max)).row();
    }

    @Override
    public void created(Unit unit){
        unit.shield = max;
    }

    private static final Vec2
            vec1 = new Vec2();
    public static void circlePercent(float x, float y, float rad, float percent, float angle) {
        float p = Mathf.clamp(percent);

        int sides = Lines.circleVertices(rad);

        float space = 360.0F / (float) sides;
        float len = 2 * rad * Mathf.sinDeg(space / 2);
        float hstep = Lines.getStroke() / 2.0F / Mathf.cosDeg(space / 2.0F);
        float r1 = rad - hstep;
        float r2 = rad + hstep;

        int i;

        for (i = 0; i < sides * p - 1; ++i) {
            float a = space * (float) i + angle;
            float cos = Mathf.cosDeg(a);
            float sin = Mathf.sinDeg(a);
            float cos2 = Mathf.cosDeg(a + space);
            float sin2 = Mathf.sinDeg(a + space);
            Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2, y + r1 * sin2, x + r2 * cos2, y + r2 * sin2, x + r2 * cos, y + r2 * sin);
        }

        float a = space * i + angle;
        float cos = Mathf.cosDeg(a);
        float sin = Mathf.sinDeg(a);
        float cos2 = Mathf.cosDeg(a + space);
        float sin2 = Mathf.sinDeg(a + space);
        float f = sides * p - i;
        vec1.trns(a, 0, len * (f - 1));
        Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2 + vec1.x, y + r1 * sin2 + vec1.y, x + r2 * cos2 + vec1.x, y + r2 * sin2 + vec1.y, x + r2 * cos, y + r2 * sin);
    }
}
