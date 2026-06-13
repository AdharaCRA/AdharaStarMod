package crstar.content;

import mindustry.type.Item;

public class CRItems {
    public static Item
        chromium,cloudRainAlloy,plutonium;
    public static void load(){
        chromium = new Item("chromium"){{
            hardness = 2;
            cost= 0.5f;
            healthScaling = 0.2f;
            alwaysUnlocked=true;
        }};



        cloudRainAlloy = new Item("cloud-rain-alloy"){{
            cost = 1.2f;
            radioactivity = 0.1f;
            charge = 1.5f;
            healthScaling = 0.8f;
        }};
        plutonium = new Item("plutonium"){{
            cost = 1.2f;
            radioactivity = 1.0f;
            healthScaling = 0.5f;
        }};
    }
}
