package crstar.content;

import arc.func.Boolf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import mindustry.core.World;
import mindustry.game.Team;
import mindustry.gen.Building;

import static mindustry.Vars.world;

public class CRFunction {
    private static final Vec2
            vec21 = new Vec2(),
            vec22 = new Vec2();
    private static Building tmpBuilding;
    public static Position collideBuild(Team team, float x1, float y1, float x2, float y2, Boolf<Building> boolf) {
        tmpBuilding = null;

        boolean found = World.raycast(World.toTile(x1), World.toTile(y1), World.toTile(x2), World.toTile(y2),
                (x, y) -> (tmpBuilding = world.build(x, y)) != null && tmpBuilding.team != team && boolf.get(tmpBuilding));

        return found ? tmpBuilding : vec21.set(x2, y2);
    }

    public static Position collideBuildOnLength(Team team, float x1, float y1, float length, float ang, Boolf<Building> boolf) {
        vec22.trns(ang, length).add(x1, y1);
        return collideBuild(team, x1, y1, vec22.x, vec22.y, boolf);
    }
}
