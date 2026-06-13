package crstar.entities.abilities;

import arc.Core;
import arc.Events;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import crstar.content.CRColor;
import crstar.content.CRFx;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.Vars.content;


public class ExecutorFieldAbility extends Ability {
    public float range;
    public float reload;
    public float minHealthPercent;

    public float shake = 2f;
    public Sound shootSound = Sounds.bang;
    public Color waveColor = Pal.accent;

    public Effect hitEffect = CRFx.star;
    public Effect waveEffect = Fx.pointShockwave;

    public float reloadCounter = Mathf.random(reload);
    public Seq<Unit> targets = new Seq<>();

    public ExecutorFieldAbility(float minhealth, float reload, float range){
        this.minHealthPercent = minhealth;
        this.reload = reload;
        this.range = range;
    }

    @Override
    public void addStats(Table t){
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / 8,2)));
        t.row();
        t.add(abilityStat("firingrate", Strings.autoFixed(60f / reload, 2)));
        t.row();
        t.add(abilityStat("percent"), minHealthPercent);
    }
    @Override
    public void update(Unit unit) {
        if ((reloadCounter += Time.delta) >= reload) {
            targets.clear();

            Groups.unit.intersect(unit.x - range, unit.y - range, range * 2, range * 2, u -> {
                if (u.team != unit.team) {
                    targets.add(u);
                }
            });

            if(targets.size > 0){
                reloadCounter = 0f;
                //waveEffect.at(unit.x, unit.y, range, waveColor);
                //shootSound.at(unit.x,unit.y);
                Effect.shake(shake, shake, unit.x, unit.y);

                for(var target : targets){
                    float size = target.hitSize();
                    float sd = Mathf.random(size * 3f, size * 12f);
                    if (target.health <= target.maxHealth * minHealthPercent*0.01f){
                        hitEffect.at(target.x, target.y, 45, unit.team.color, sd);
                        target.health*=0f;
                    }

                }
            }
        }
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);

        float z = Draw.z();
        Draw.z(Layer.bullet);

        Tmp.c1.set(CRColor.starBlue).lerp(Color.white, Mathf.absin(4f, 0.15f));
        Draw.color(Tmp.c1);
        Lines.stroke(3f);
        circlePercent(unit.x, unit.y, unit.hitSize * 1.15f, reloadCounter / reload, 0);

        Draw.z(z);
        Draw.reset();
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
