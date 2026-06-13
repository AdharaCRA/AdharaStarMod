package crstar;

import crstar.content.CRBlocks;
import crstar.expand.units.CRUnitRegister;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import crstar.content.*;

public class CloudRainStar extends Mod{
    public static final String MOD_NAME = "crstar";

    public CloudRainStar(){
        /*Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("frog");
                dialog.cont.add("behold").row();
                dialog.cont.image(Core.atlas.find("crstar-frog")).pad(20f).row();
                dialog.cont.button("I see", dialog::hide).size(100f, 50f);
                dialog.show();
            });
        });*/
    }

    public static String name(String name) {
        return MOD_NAME + "-" + name;
    }

    @Override
    public void loadContent(){
        super.loadContent();
        CRUnitRegister.load();

        CRItems.load();
        CRLiquids.load();
        CRStatusEffects.load();
        CRUnitTypes.load();
        CRBlocks.load();
        CRPlanets.load();
    }

}
