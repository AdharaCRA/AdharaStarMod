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

public class AutomaticRepairAbility extends Ability {
    public float range;
    public float reload;
    public float percentHeal = 5f,percentHealMultiplier;
    public float numberHeal = 200f;
    public Effect waveEffect = Fx.healWave;
    public float reloadCounter = Mathf.random(reload);

    public AutomaticRepairAbility(float reload, float range,float numberHeal,float percentHealMultiplier){
        this.reload = reload;
        this.range = range;
        this.numberHeal = numberHeal;
        this.percentHealMultiplier = percentHealMultiplier;
    }

    @Override
    public void addStats(Table t){
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / 8,2)));
        t.row();
        t.add(abilityStat("firingrate", Strings.autoFixed(60f / reload, 2)));
        t.row();
        t.add(Core.bundle.format("bullet.damage", percentHealMultiplier));
    }
    @Override
    public void update(Unit unit) {
        if ((reloadCounter += Time.delta) >= reload) {
            if(unit.damaged()){
                reloadCounter = 0f;
                waveEffect.at(unit.x, unit.y, range);
                percentHeal = (unit.maxHealth - unit.health)*unit.healthMultiplier*percentHealMultiplier*0.01f;
                unit.heal(percentHeal+numberHeal);
            }
        }
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        Draw.reset();
    }
}
