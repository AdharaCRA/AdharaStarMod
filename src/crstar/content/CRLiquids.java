package crstar.content;

import arc.graphics.*;
import mindustry.content.StatusEffects;
import mindustry.type.*;

public class CRLiquids{
    public static Liquid
            plasmaLiquid,helium;

    public static void load(){
        plasmaLiquid = new Liquid("plasmaLiquid",Color.valueOf("a6bbff")){{
            color = Color.valueOf("a6bbff");
            heatCapacity = 2f;
            boilPoint = 0.5f;
            gasColor = Color.grays(0.9f);
            alwaysUnlocked = true;
        }};
        helium = new Liquid("helium"){{

        }};
    }
}
