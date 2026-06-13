package crstar.expand.units.unitType;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import crstar.content.CRUnitTypes;

import java.util.function.Consumer;

public class CRNaviUnitType extends UnitType {
    //public static final Color OColor = Color.valueOf("b8c9ff");


    public CRNaviUnitType(String name) {
        super(name);
    }
    @Override
    public void init() {
        super.init();

        float maxWeaponRange = 0;
        for (Weapon weapon : weapons) {
            if (weapon.range() > maxWeaponRange) {
                maxWeaponRange = weapon.range();
            }
        }
        fogRadius = maxWeaponRange / 8;

        CRUnitTypes.immunise(this);
    }
}
