package crstar.content;

import arc.graphics.Color;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class CRStatusEffects {
    public static StatusEffect
        energyPhased,shieldPlus,crRepairing;

    public static void load() {
        energyPhased = new StatusEffect("energy-phased"){
            {
                color = Color.valueOf("ff795e");
                damage = 300f;
                speedMultiplier = 0f;
                disarm = true;
                show = true;
            }
            @Override
            public void update(Unit unit, float time){
                super.update(unit,time);
                if(unit.health<=unit.maxHealth*0.05f){
                    unit.kill();
                }
                //if(unit.controller()==player){

                //}
            }
        };

        crRepairing = new StatusEffect("crRepairing"){{
            damage = -2f;

        }};


        shieldPlus = new StatusEffect("shield-plus"){{
            color = CRColor.starG;
            speedMultiplier = 5f;
        }};
    }
}
