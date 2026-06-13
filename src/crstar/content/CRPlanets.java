package crstar.content;

import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

public class CRPlanets
{
    public static Planet
        Adhara;
    public static Planet defaultPlanet;

    public static void load(){
        Adhara = new Planet("Adhara", null, 8f){{
            bloom = true;
            accessible = false;
            alwaysUnlocked = true;
            lightColor = CRColor.starBlue;
            meshLoader = () -> new SunMesh(
                    this, 4,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("aabfff"),
                    CRColor.starBlue,
                    Color.white,
                    Color.valueOf("d0deff"),
                    Color.valueOf("a8c5ff"),
                    Color.valueOf("e6ebff"),
                    Color.black
            );
        }};
        defaultPlanet = new Planet("Adhara2", Adhara, 2f,2){{
            generator = new AdharaPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("b8c9ff").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("a5b3ff").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );
            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("b8c9ff");
            atmosphereColor = Color.valueOf("b8c9ff");
            startSector = 0;
            defaultCore = Blocks.coreNucleus;
            iconColor = Color.valueOf("b8c9ff");
            allowLaunchToNumbered = true;
        }};
    }
}

