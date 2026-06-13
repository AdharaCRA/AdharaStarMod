package crstar.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.Rand;
import arc.math.geom.*;
import arc.util.*;
import arc.struct.*;
import mindustry.entities.*;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.meta.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;

public class CRFx {
    public static final Rand rand = new Rand();
   // public static final Vec2 v = new Vec2();
    public static final int[] signs = {-1,0,1,2};

    public static final Effect

    instBomb = new Effect(20f, 100f, e -> {
        color(CRColor.starBlue);
        stroke(e.fout()* 4f);
        circle(e.x, e.y, 20f + e.fout() * 35f);

        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 30f*e.fout(), 220f * e.fout(), i*90 + 45+e.fout()*180);
        }

        color();

        Drawf.light(e.x, e.y, 300f, CRColor.starBlue, 0.9f);
    }),

    instShoot = new Effect(30, 100f, e -> {
        color(CRColor.starBlue);
        stroke(e.fout() * 3f);
        circle(e.x, e.y, 4f + e.finpow() * 20f);
        float rot = e.rotation+90f;

        for(int i =1; i <= 2; i++){
            Drawf.tri(e.x, e.y, 3f, 40f * e.fout(), i*-60+rot);
        }

        for(int i = 0; i < 2; i++){
            Drawf.tri(e.x, e.y, 5f, 70f * e.fout(), i*180+rot);
        }

        Drawf.light(e.x, e.y, 300f, CRColor.starBlue, 0.9f * e.fout());
    }),
    star = new Effect(75f, 800f, e -> {
        if (!(e.data instanceof Float)) return;
        float len = e.data();

        color(e.color, Color.white, e.fout() * 0.3f);
        stroke(e.fout() * 4.4F);

        randLenVectors(e.id, (int) Mathf.clamp(len / 12, 10, 40), e.finpow() * len, e.rotation, 360f, (x, y) -> {
            Fill.circle(e.x+x,e.y+y,e.fout() * len * 0.02f + 2f);
        });

        float fout = e.fout(Interp.exp10Out);
        for (int i : signs) {
            Drawf.tri(e.x, e.y, len / 10f * fout * (Mathf.absin(0.8f, 0.07f) + 1), len * 1.2f * Interp.swingOut.apply(Mathf.curve(e.fin(), 0, 0.7f)) * (Mathf.absin(0.8f, 0.12f) + 1) * e.fout(0.2f), e.rotation + 90 + i * 90);
        }

    }).layer(Layer.effect - 1f),

    shieldBreak = new Effect(40, e -> {
        color(e.color);
        stroke(3f * e.fout());
        if(e.data instanceof ForceFieldAbility ab){
            Lines.poly(e.x, e.y, ab.sides, e.rotation + e.fin(), ab.rotation);
            return;
        }

        Lines.poly(e.x, e.y, 6, e.rotation + e.fin());
    }).followParent(true),

    empDespawned = new Effect(30f,e->{
        color(e.color);

        stroke(e.fout() * 3f);
        randLenVectors(e.id, 15, e.finpow() * 100f, e.rotation, 360f, (x, y) -> {
            Fill.circle(e.x + x,e.y + y,e.fout() * 7f);
        });
        randLenVectors(e.id, 20, e.finpow() * 150f, e.rotation, 360f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 35f);
        });
    });





    ;
}
