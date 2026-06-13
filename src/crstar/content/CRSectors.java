package crstar.content;

import mindustry.type.SectorPreset;

public class CRSectors {
    public static SectorPreset
            origin;

    public static void load() {
        //region serpulo

        origin = new SectorPreset("groundZero", CRPlanets.defaultPlanet, 10) {{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 40;
            difficulty = 8;
            overrideLaunchDefaults = true;
            noLighting = true;
            startWaveTimeMultiplier = 3f;
        }};
    }
}
