package crstar.content;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Tmp;
import crstar.CloudRainStar;
import crstar.entities.abilities.CRForceFieldAbility;
import crstar.entities.abilities.ShockWaveDefenseAbility;
import crstar.expand.bullets.CREmpBulletType;
import crstar.expand.bullets.EffectBulletType;
import crstar.expand.units.unitEntity.CRNaviEntity;
import crstar.expand.units.unitType.CRNaviUnitType;
import crstar.type.weapons.TractorBeamWeapon;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Sized;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.SuppressionFieldAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.ui.Bar;

import static arc.graphics.g2d.Lines.circle;
import static mindustry.gen.Sounds.*;

@SuppressWarnings("unchecked")
public class CRUnitTypes {
    public static BulletType
        StarOfCRNavi,ChainLightningBolt;
    public static Weapon
        twilightLaser,SmiteWeapon;

    public static UnitType
            //seaUnit
            scale,fin,leap,whark,polarise,twilight,
            //flying
            FlyingAttackT8,FlyingSupportT5;

    public static Seq<StatusEffect> statuses,status2;
    static {
        EntityMapping.nameMap.put(CloudRainStar.name("twilight"), CRNaviEntity::new);
    }

    private static void LoadWeapons() {
        SmiteWeapon = new Weapon(CloudRainStar.name("naval-swarmer")){{
            reload =45f;
            mirror=false;
            rotate=true;
            rotateSpeed = 1.5f;
            shootSound = Sounds.shootSmite;
            bullet = new BasicBulletType(7f,433,"large-orb"){{
                width = 17f;
                height = 21f;
                hitSize = 8f;
                splashDamage = 545f;
                splashDamageRadius = 56f;
                shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                    colorFrom = colorTo = Pal.accent;
                    lifetime = 12f;
                    sizeTo = 15f;
                    strokeFrom = 3f;
                    strokeTo = 0.3f;
                }});
                smokeEffect = Fx.shootSmokeSmite;
                pierceCap = 4;
                pierce = pierceBuilding = true;
                hitColor = backColor = trailColor = Pal.accent;
                frontColor = Color.white;
                trailWidth = 2.8f;
                trailLength = 9;
                lifetime = 60f;

                hitEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                    sizeTo = 20f;
                    colorFrom = colorTo = Pal.accent;
                    lifetime = 12f;
                }});
                trailRotation = true;
                trailEffect = Fx.disperseTrail;
                trailInterval = 3f;
                intervalBullet = new LightningBulletType(){{
                    damage = 187.5f;
                    ammoMultiplier = 1f;
                    lightningColor = Pal.accent;
                    lightningLength = 5;
                    lightningLengthRand = 10;
                    pierceArmor = true;
                }};
                bulletInterval = 3f;
            }};
            shoot = new ShootMulti(new ShootAlternate(){{
                spread = 2.2f;
                shots = barrels = 4;
            }}, new ShootHelix(){{
                scl = 4f;
                mag = 3f;
            }});
        }};
        twilightLaser = new Weapon(CloudRainStar.name("twilight-laser")) {{
            float brange = 784f;
            reload = 85f;
            rotate = true;
            rotateSpeed = 3f;
            mirror = false;
            shootSound = pulseBlast;
            predictTarget = false;
            alternate = false;
            bullet = new PointBulletType() {
                {
                    shootEffect = CRFx.instShoot;
                    hitEffect = Fx.instHit;
                    smokeEffect = Fx.smokeCloud;
                    despawnEffect = CRFx.instBomb;
                    trailEffect = new ParticleEffect() {{
                        line = true;
                        sizeInterp = Interp.slope;
                        lenFrom = lenTo = 7;
                        length = baseLength = 1;
                        strokeFrom = 0;
                        strokeTo = 7;
                        randLength = false;
                        lifetime = 20f;
                        colorFrom = colorTo = CRColor.starBlue;
                        cone = 0;
                    }};
                    damage = 1875f;
                    speed = range = brange;
                    lifetime = 1;
                    hitShake = 6f;
                    lightning = 2;
                    lightningDamage = 1200f;
                    lightningLength = 35;
                    lightningLengthRand = 5;
                }
                @Override
                public void despawned(Bullet b){
                    despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
                    despawnSound.at(b);
                    Effect.shake(despawnShake, despawnShake, b);
                }
                @Override
                public void hitEntity(Bullet b, Hitboxc entity,float health){
                    super.hitEntity(b,entity,health);
                    if(entity instanceof Unit unit){
                        unit.shield -= unit.shield*0.01f;
                    }
                }
            };
        }};
    }

    public static void LoadBullets(){
        StarOfCRNavi = new EffectBulletType(20) {
            {
                despawnHit = true;
                hitColor = CRColor.starBlue;
                hitShake = 80f;
                splashDamageRadius = 36;

                lightningDamage = 2000;
                lightning = 2;
                lightningLength = 4;
                lightningLengthRand = 8;

                collidesAir = collidesGround = collidesTiles = true;
                splashDamage = 0;
                damage = 10000;
                status = CRStatusEffects.energyPhased;
                statusDuration = 200f;
            }
            @Override
            public void draw(Bullet b) {
                if (!(b.data instanceof Seq)) return;
                Seq<Sized> data = (Seq<Sized>) b.data;

                Draw.color(CRColor.starBlue, Color.white, b.fin() * 0.7f);
                Draw.alpha(b.fin(Interp.pow3Out) * 1.1f);
                Lines.stroke(2 * b.fout());
                for (Sized s : data) {
                    circle(s.getX(), s.getY(), b.fout() * s.hitSize() * 5f);
                }

                Drawf.light(b.x, b.y, b.fdata, hitColor, 0.3f + b.fin() * 0.8f);
            }

            @Override
            public void update(Bullet b) {
                super.update(b);

                if (!(b.data instanceof Seq) || b.timer(0, 5)) return;
                //noinspection unchecked
                Seq<Sized> data = (Seq<Sized>) b.data;
                data.remove(d -> !((Healthc) d).isValid());
            }

            @Override
            public void despawned(Bullet b) {
                super.despawned(b);

                if (!(b.data instanceof Seq )) return;
                Seq<Sized> data = (Seq<Sized>) b.data;
                for (Sized s : data) {
                    float size = s.hitSize();
                    if(size<35f) return;
                    if (Mathf.chance(0.32) || data.size < 8) {
                        float sd = Mathf.random(size * 2f, size * 6f);
                        CRFx.star.at(s.getX() + Mathf.range(size*0.25f), s.getY() + Mathf.range(size*0.25f), 45, CRColor.starBlue, sd);

                    }
                }
                createSplashDamage(b, b.x, b.y);
            }

            @Override
            public void init(Bullet b) {
                super.init(b);
                b.fdata = splashDamageRadius;
                Seq<Sized> data = new Seq<>();
                Vars.indexer.eachBlock(null, b.x, b.y, b.fdata, bu -> bu.team != b.team, data::add);
                Groups.unit.intersect(b.x - b.fdata / 2, b.y - b.fdata / 2, b.fdata, b.fdata, u -> {
                    if (u.team != b.team) data.add(u);
                });
                b.data = data;

            }
        };
        ChainLightningBolt = new LaserBulletType(){
            {
                length = 200f;
                width = 25f;
                damage = 1472f;
                pierce = pierceArmor = pierceBuilding = true;
                lifetime = 65f;
                shieldDamageMultiplier = 2f;

                splashDamage = 650f;
                splashDamageRadius = 32f;
                largeHit = scaledSplashDamage = true;

                lightningSpacing = 35f;
                lightningLength = 8;
                lightningDelay = 1.1f;
                lightningLengthRand = 15;
                lightningDamage = 380;
                lightningAngleRand = 40f;
                lightningColor = CRColor.starG;

                colors = new Color[]{CRColor.starG,Pal.accent,Color.white};
                //TODO Status

                sideAngle = 15f;
                sideWidth = 0f;
                sideLength = 0f;
            }
            @Override
            public void despawned(Bullet b){ }

            @Override
            public void update(Bullet b){
                super.update(b);
                if(b.fin() >= 0.2f && b.data == null){
                    b.data = new Object();

                    float endX = b.x + Angles.trnsx(b.rotation(), b.fdata);
                    float endY = b.y + Angles.trnsy(b.rotation(), b.fdata);

                    createUnits(b, endX, endY);
                    createFrags(b, endX, endY);
                }
            }

            @Override
            public void draw(Bullet b) {
                float realLength = b.fdata;

                float f = Mathf.curve(b.fin(), 0f, 0.2f);
                float baseLen = realLength * f;
                float cwidth = width;

                Tmp.v1.trns(b.rotation(), baseLen);

                for (Color color : colors) {
                    Draw.color(color);
                    Lines.stroke((cwidth *= lengthFalloff) * b.fout());
                    Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);

                    Fill.circle(Tmp.v1.x + b.x, Tmp.v1.y + b.y, Lines.getStroke() * 1.5f);
                    Fill.circle(b.x, b.y, 1.5f * cwidth * b.fout());

                }
                Draw.reset();
                Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, width * 1.4f * b.fout(), colors[0], 0.6f);
            }
        };
    }

    public static void load(){
        LoadBullets();
        LoadWeapons();
        //region naval attack
        scale = new CRNaviUnitType("scale"){{
            constructor = UnitWaterMove::create;
            health = 700;
            speed = 0.8f;
            drag = 0.15f;
            hitSize = 13f;
            armor = 3f;
            accel = 0.3f;
            rotateSpeed = 2.6f;
            faceTarget = false;
            ammoType = new ItemAmmoType(CRItems.chromium);
            outlineRadius = 1;

            trailLength =30;
            waveTrailX = 5.5f;
            waveTrailY = -4f;
            trailScl = 1.9f;

            weapons.add(new Weapon("mount-weapon"){{
                reload = 10f;
                x = 5f;
                y = -4.5f;
                rotate = true;
                rotateSpeed = 5f;
                inaccuracy = 8f;
                ejectEffect = Fx.casing1;
                shootSound = Sounds.shoot;
                bullet = new FlakBulletType(4.2f, 3){{
                    lifetime = 60f;
                    ammoMultiplier = 4f;
                    shootEffect = Fx.shootSmall;
                    width = 6f;
                    height = 8f;
                    hitEffect = Fx.flakExplosion;
                    splashDamage = 27f * 1.5f;
                    splashDamageRadius = 15f;
                }};
            }});
        }};

        fin = new CRNaviUnitType("fin"){{
            constructor = scale.constructor;
            health = 1500;
            armor = 5f;
            speed = 0.75f;
            hitSize = 12.5f;
            ammoType = new ItemAmmoType(Items.graphite);
            weapons.add(new Weapon(CloudRainStar.name("fin-weapon")){{
                reload =45f;
                shoot = new ShootAlternate(){{
                   shots = 3;
                   shotDelay = 8f;
                }};
                x = y =0;
                mirror = false;
                rotate = true;
                rotateSpeed = 5.5f;
                bullet = new BasicBulletType(){{
                    damage = 25f;
                    splashDamage = 35f;
                    hitEffect = Fx.explosion;
                    x = 3f;
                    y = 6f;
                    frontColor = CRColor.starBlue.cpy().b(200f);
                    trailColor = CRColor.starBlue.cpy().a(100f);
                    trailWidth = 2f;
                    trailLength = 12;
                    speed = 8f;
                    lifetime = 20f;
                }};
            }});
        }};

        leap = new CRNaviUnitType("leap"){{
            constructor = scale.constructor;
            health = 3200;
            armor = 7f;
            speed = 0.55f;
            hitSize = 25f;
            ammoType = new ItemAmmoType(Items.thorium);
            weapons.add(new Weapon("leap-weapon"){{
                shootSound = Sounds.bolt;
                layerOffset = 0.0001f;
                reload = 18f;
                shootY = 10f;
                recoil = 1f;
                rotate = true;
                rotateSpeed = 1.4f;
                mirror = false;
                shootCone = 2f;
                x = 0f;
                y = 0f;
                heatColor = Color.valueOf("f9350f");
                cooldownTime = 30f;

                shoot = new ShootAlternate(3.5f);

                bullet = new RailBulletType(){{

                }};
            }});
        }};
        whark = new CRNaviUnitType("whark"){{
            constructor = scale.constructor;
            health = 13700;
            armor = 9f;
            speed = 0.35f;
            hitSize = 40f;
            ammoType = new ItemAmmoType(Items.thorium);
            /*weapons.add(new Weapon("whark-weapon"){{
                x =20f;
                y = 0f;
                reload = 60f;
                rotate = true;
                rotateSpeed = 3f;
                shootSound = lasershoot;
                shootY = 8f;
                bullet = new BasicBulletType(9f,487.5f,"crstar-spear"){{
                    width = 24f;
                    height = 80f;
                    hitSize = 16f;
                    lifetime = 45f;
                    shootEffect = CRFx.instShoot;
                    pierce=true;
                    hitEffect = Fx.scatheExplosion;
                    intervalBullet = new LightningBulletType(){{
                        damage = 25f;
                        lightningLength = 15;
                        lightningLengthRand = 10;
                        lightningColor = CRColor.starBlue;
                    }};
                    hitSound = explosionbig;
                    bulletInterval = 3f;
                    trailLength = 10;
                    trailWidth = 3f;
                    trailColor = CRColor.starBlue;
                    trailEffect = new WaveEffect(){{
                        lifetime = 30f;
                        colorFrom = CRColor.starBlue;
                        colorTo = Color.valueOf("b8c9ff40");
                        strokeFrom = 2f;
                        strokeTo = 1f;
                        sizeFrom = 5f;
                        sizeTo = 50f;
                    }};
                    trailInterval = 4;
                }};
            }});*/
            weapons.add(new Weapon(CloudRainStar.name("")){{
                x =20f;
                y = 0f;
                reload = 16f;
                rotate = true;
                rotateSpeed = 3f;
                //TODO Sounds to the Neutron Star
                shootSound = bigshot;
                shootY = 8f;
                ejectEffect = Fx.casing1;
                shoot = new ShootSpread(){{
                    shots = 3;
                    spread = 10f;
                }};
                bullet = new ShrapnelBulletType(){{
                    damage = 75f;
                    width = 12f;
                    length =110f;
                    pierceArmor = pierceBuilding = true;
                    serrations = 7;
                    serrationLenScl = 5;
                    fromColor = toColor = Color.valueOf("97acff");
                }};
            }});
            weapons.add(new TractorBeamWeapon("cleroi-point-defense"){{
                range = 320f;
                x = 20f;
                y = 10f;
                force = 10f;
                scaledForce = 2f;
                rotateSpeed = 7f;
                damage = 2f;
            }});

        }};
        polarise = new CRNaviUnitType("polarise"){{
            constructor = scale.constructor;
            health = 34500;
            speed = 0.3f;
            rotateSpeed = 1.2f;
            armor = 26f;
            hitSize = 72f;
            weapons.add(new Weapon(CloudRainStar.name("naval-swarmer")){{
                x = 0f;
                y = 15f;
                reload = 100f;
                rotate = true;
                rotateSpeed = 4f;
                mirror = false;
                shadow = 20f;
                shootY = 4.5f;
                recoil = 3f;
                velocityRnd = 0.4f;
                inaccuracy = 7f;
                ejectEffect = Fx.casing2;
                shake = 1f;
                shootSound = Sounds.missile;

                shoot = new ShootAlternate(){{
                    shots = 12;
                    shotDelay = 2.5f;
                    spread = 4f;
                    barrels = 3;
                }};

                bullet = new MissileBulletType(7f, 47f){{
                    width = 8f;
                    height = 8f;
                    homingPower = 0.12f;
                    homingRange = 80f;
                    splashDamageRadius = 45f;
                    splashDamage = 135f;
                    lifetime = 56f;
                    trailColor = backColor = CRColor.starBlue;
                    frontColor = new Color(0.5f,0.5f,1f,1f);
                    despawnEffect = Fx.blastExplosion;
                    status = StatusEffects.blasted;
                }};
            }});
            weapons.add(new Weapon("polarise-weapon"){{
                x = 0f;
                y = -10f;
                reload =87f;
                mirror=false;
                rotate=true;
                rotateSpeed = 6f;
                shootSound = pulseBlast;
                bullet = new BasicBulletType(24f,1550){{
                    width = 17f;
                    height = 21f;
                    hitSize = 8f;
                    splashDamage = 250f;
                    splashDamageRadius = 56f;
                    shootEffect = new MultiEffect(Fx.instShoot, Fx.colorSparkBig);
                    smokeEffect = Fx.shootSmokeSmite;
                    pierce = true;
                    hitColor = backColor = trailColor = Pal.accent;
                    frontColor = Color.white;
                    trailWidth = 2.8f;
                    trailLength = 9;
                    hitEffect = Fx.hitBulletColor;
                    lifetime = 20f;
                    despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                        sizeTo = 20f;
                        colorFrom = colorTo = Pal.accent;
                        lifetime = 12f;
                    }});
                    trailRotation = true;
                    trailEffect = Fx.disperseTrail;
                    trailInterval = 3f;

                    lightning = 10;
                    lightningDamage = 18f;
                    lightningLength = lightningLengthRand = 4;
                }};
                shoot = new ShootHelix();
            }});

            weapons.add(new TractorBeamWeapon(CloudRainStar.name("twilight-laser")){{
                range = 320f;
                force =3f;
                scaledForce = 0.5f;
                x = 20f;
                y = -20f;
                damage = 5f;
            }});
        }};

        twilight = new CRNaviUnitType("twilight"){
            {
                health = 829000f;
                speed = 0.25f;
                hitSize = 140f;
                armor = 235f;
                accel = 0.3f;
                rotateSpeed = 0.6f;
                faceTarget = false;
                ammoType = new ItemAmmoType(CRItems.cloudRainAlloy);
                trailLength = 30;
                waveTrailX = 40f;
                abilities.addAll(
                        new ShockWaveDefenseAbility(260,40f,220f),
                        new Ability(){
                            @Override
                            public void displayBars(Unit unit, Table bars){
                                bars.add(new Bar("stat.damageMultiplier", CRColor.starBlue, () -> Math.min(Math.max(unit.maxHealth-unit.health , 0)/(unit.maxHealth*0.5f) , 1)  )).row();
                            }
                        }
                        //new AutomaticRepairAbility(240f,340f,1500f,5f)
                        //new ExecutorFieldAbility(7.5f,900f,256f)
                );
                weapons.add(new Weapon("reign-weapon") {{
                    x =25f;
                    y = -60f;
                    reload = 105f;
                    rotate = true;
                    rotateSpeed = 3f;
                    shootSound = shockBlast;
                    shootY = 8f;
                    bullet = new ArtilleryBulletType(18f,640f,"crstar-spear"){
                        {
                            splashDamage = 862f;
                            splashDamageRadius = 96f;
                            width = 24f;
                            height = 80f;
                            hitSize = 20f;
                            lifetime = 45f;
                            shootEffect = CRFx.instShoot;
                            hitSound = explosionbig;
                            fragBullets = 3;

                            fragSpread = 120f;
                            fragRandomSpread = 45f;

                            trailWidth = 3f;
                            trailLength = 10;
                            trailColor=CRColor.starBlue;

                            fragBullet = cpy2bu(ChainLightningBolt,b->{
                                b.hitShake = 50f;
                                b.fragBullet = ChainLightningBolt;
                                b.fragBullets = 1;
                                b.fragBullet.lifetime = ChainLightningBolt.lifetime*0.64f;
                            });
                        }
                    };
                }});
                weapons.add(cpy(twilightLaser,60,-45));
                weapons.add(cpy(twilightLaser,-60,-45));
                weapons.add(cpy(twilightLaser,40,10));
                weapons.add(cpy(twilightLaser,-40,10));
                weapons.add(cpy(twilightLaser,30,30));
                weapons.add(cpy(twilightLaser,-30,30));

                weapons.add(cpy(SmiteWeapon,-30,62));
                weapons.add(cpy(SmiteWeapon,30,62));
                weapons.add(cpy(SmiteWeapon,0,20));
                weapons.add(cpy(SmiteWeapon,55,-75));
                weapons.add(cpy(SmiteWeapon,-55,-75));

                weapons.add(new Weapon(CloudRainStar.name("twilight-weapon")){{
                    reload = 110f;
                    x = 0;
                    y = -35f;
                    mirror = false;
                    shootSound = laserblast;
                    rotate = true;
                    rotateSpeed = 1.5f;
                    shake = 50f;
                    recoil = 3f;
                    shoot = new ShootBarrel(){{
                        shots = 2;
                        barrels = new float[]{-15, 20, 0, 15, 20, 0};
                    }};
                    parts.addAll(
                            new RegionPart("-barrel"){{
                                progress = PartProgress.recoil;
                                heatProgress = PartProgress.recoil;
                                under = true;
                                moveY = -5.5f;
                                mirror = true;
                            }},new RegionPart("-mid"){{
                                heatProgress = PartProgress.heat.blend(PartProgress.warmup, 0.5f);
                                mirror = false;
                            }}
                    );
                    bullet = new BasicBulletType(20f,2250){
                        {
                            width = 20f;
                            height = 40f;
                            hitSize = 32;
                            frontColor = backColor = hitColor = trailColor = CRColor.starBlue;
                            lifetime = 50f;
                            trailWidth = 3f;
                            trailLength = 35;
                            hitShake = 80f;
                            shootEffect = Fx.instShoot;
                            despawnEffect = Fx.titanExplosionLarge;
                            pierce = pierceBuilding = pierceArmor = true;
                            fragOnHit = true;
                            fragBullet = StarOfCRNavi;
                            fragBullets = 1;
                            absorbable = false;
                        }

                        @Override
                        public void hitEntity(Bullet b,Hitboxc entity,float health){
                            super.hitEntity(b,entity,health);
                            if(entity instanceof Unit u){
                                float percentDamage = Math.abs(u.maxHealth-u.health) * 0.12f;
                                float finalPercentDamage = Math.min(percentDamage , u.maxHealth*0.5f);
                                float fatalDamage = finalPercentDamage / u.healthMultiplier;
                                u.damagePierce(fatalDamage);
                            }
                        }
                    };
                }});


            }
        };
        //region end

        //Flying region
        FlyingSupportT5 = new UnitType("FlyingSupportT5"){
            {
                constructor = PayloadUnit::create;
                health = 93000;
                speed = 1f;
                hitSize = 72f;
                armor = 45f;
                accel = 0.3f;
                rotateSpeed = 2f;
                faceTarget = false;
                flying = true;
                payloadCapacity = 6400f;
                ammoType = new ItemAmmoType(CRItems.chromium);
                buildSpeed = 20f;
                lowAltitude = true;
                abilities.addAll(
                        new SuppressionFieldAbility(){
                            {
                                color = Pal.heal;
                            }
                            @Override
                            public void draw(Unit unit){
                                super.draw(unit);
                            }
                        },
                        new CRForceFieldAbility(220f,25f,28000f,300f)
                );
                weapons.add(new Weapon("emp-cannon-mount"){{
                    x = 20f;
                    reload = 45f;
                    rotate = true;
                    rotateSpeed = 3.2f;
                    Color totalColor  = Color.valueOf("b8ff99");
                    inaccuracy = 5f;
                    bullet = new PointBulletType(){
                        {
                            damage = 235f;
                            splashDamage = 450f;
                            splashDamageRadius = 92f;
                            lightning = 5;
                            lightningDamage = 180f;
                            trailEffect = new ParticleEffect() {{
                                line = true;
                                sizeInterp = Interp.slope;
                                lenFrom = lenTo = 3;
                                length = baseLength = 1;
                                strokeFrom = 0;
                                strokeTo = 3;
                                randLength = false;
                                lifetime = 18f;
                                colorFrom = colorTo = Pal.heal.cpy().a(200f);
                                cone = 0;
                            }};
                            speed = range = 300f;
                            lifetime = 1f;
                            healPercent = 30f;
                            fragBullet = new ArtilleryBulletType(8f,175f){{
                                damage = 175f;
                                splashDamage = 220f;
                                splashDamageRadius = 56f;
                                lifetime = 30f;
                                absorbable = false;
                                lightning = 3;
                                lightningDamage = 30f;
                                trailEffect = new ParticleEffect(){{
                                    particles = 8;
                                    sizeFrom = 3f;
                                    sizeTo = 0f;
                                    lifetime = 6f;
                                    colorFrom = totalColor;
                                }};
                                trailInterval = 3;
                                trailWidth = 2f;
                                trailLength = 25;
                                trailColor = totalColor;
                                despawnSound = none;
                                hitEffect = despawnEffect = Fx.hitMeltHeal;
                                hitColor = Pal.heal;
                                healPercent = 13f;
                            }};
                            fragBullets = 8;
                            fragSpread = 45f;
                            fragRandomSpread = 10f;
                            fragLifeMax = 0.57f;
                            fragLifeMin = 0.55f;
                            despawnEffect = CRFx.empDespawned;
                            shootSound = laser;
                            hitSound = plasmaboom;
                        }
                        @Override
                        public void despawned(Bullet b){
                            //super.despawned(b);
                            if(despawnHit){
                                hit(b);
                            }
                            if(!fragOnHit){
                                createFrags(b, b.x, b.y);
                            }
                            Color resultColor = Color.rgb(Mathf.random(100,255), Mathf.random(100,255),Mathf.random(100,255));
                            despawnEffect.at(b.x, b.y, b.rotation(), resultColor);
                            despawnSound.at(b);
                            Effect.shake(despawnShake, despawnShake, b);
                        }
                        @Override
                        public void hitEntity(Bullet b,Hitboxc entity,float health){
                            super.hitEntity(b,entity,health);
                            //to damage the unit "twilight"
                            //because "twilight" refers to the author(AdharaCRA) and it's too strong to be destroyed by normal units
                            if(entity instanceof Unit twi && twi.maxHealth ==729000f){
                                twi.health -= b.damage + twi.maxHealth*0.05f;
                                twi.apply(CRStatusEffects.energyPhased,36f);
                            }
                        }
                    };
                }});
                weapons.add(new Weapon("plasma-laser-mount"){{
                    reload = 30f;
                    rotate = true;
                    rotateSpeed = 1f;
                    mirror = true;
                    x = 40f;
                    y = -10f;
                    shootSound = lasershoot;
                    inaccuracy = 15f;
                    shoot = new ShootAlternate(){{
                        shots = 3;
                        shotDelay = 8f;
                    }};
                    bullet = new CREmpBulletType(){{
                        width = 10f;
                        height = 40f;
                        hitSize = 8f;
                        sprite = "crstar-spear";
                        lightning= 2;
                        lightningDamage = 120f;
                        lightningLength = 5;
                        lightningLengthRand = 3;
                        speed = 7f;
                        lifetime = 40f;
                        healPercent = 0.25f;
                        reload = 15f;
                        trailWidth = 2f;
                        trailLength = 7;
                        trailColor = Pal.heal;
                        homingPower = 2f;
                        homingRange = 80f;

                        //for config of this bullet type
                        maxTargets = 8;
                        range = 100f;
                        bdamage = 25f;
                        damage = 75f;
                        pierce = pierceArmor = true;
                        pierceCap = 3;
                        //status = CRFx.crElectricfied;
                        //for effects
                        effectRadius = 2.5f;
                    }};
                }});

                weapons.add(new RepairBeamWeapon("repair-beam-weapon-center-large"){{
                    x = 35f;
                    y = 15f;
                    mirror = true;
                    rotate = true;
                    controllable = false;
                    rotateSpeed = 7f;
                    targetBuildings = true;
                    range = 280f;
                    repairSpeed = 7.5f;
                    beamWidth = 0.75f;
                }});
            }
            @Override
            public void init() {
                super.init();
                CRUnitTypes.immunise(this);
            }
        };
        //region end

        //boss
        FlyingAttackT8 = new UnitType("LianLong"){{
            constructor = UnitEntity::create;
            health = 42000;
            speed = 0.2f;
            hitSize = 92f;
            armor = 26f;
            accel = 0.3f;
            rotateSpeed = 0.6f;
            faceTarget = false;
            flying = true;
            //ammoType = new ItemAmmoType(Items.surgeAlloy);
        }};
    }

    public static Weapon cpy(Weapon weapon, float x, float y) {
        Weapon movingWeapon = weapon.copy();
        movingWeapon.x = x;
        movingWeapon.y = y;
        return movingWeapon;
    }
    public static Weapon cpy2(Weapon weapon, float x, float y, Cons<Weapon> modifier) {
        Weapon changingWeapon = weapon.copy();
        changingWeapon.x = x;
        changingWeapon.y = y;
        modifier.get(changingWeapon);
        return changingWeapon;
    }
    public static BulletType cpy2bu(BulletType b, Cons<BulletType> modifier) {
        BulletType bu = b.copy();
        modifier.get(bu);
        return bu;
    }

    public static void immunise(UnitType type) {
        if (statuses == null) {
            statuses = Vars.content.statusEffects().copy();
            statuses.retainAll(s -> s.disarm || s.damage > 0 || s.healthMultiplier * s.reloadMultiplier * s.speedMultiplier * s.damageMultiplier < 0.8);
            statuses.remove(StatusEffects.overclock);
            statuses.remove(StatusEffects.overdrive);
            statuses.remove(CRStatusEffects.energyPhased);
        }

        type.immunities.addAll(statuses);
    }
    public static void immunise2(UnitType type) {
        if (status2 == null) {
            status2 = Vars.content.statusEffects().copy();
            status2.retainAll(s -> s.disarm || s.damage > 1 || s.healthMultiplier * s.reloadMultiplier * s.speedMultiplier * s.damageMultiplier < 0.6);
            status2.remove(CRStatusEffects.energyPhased);
        }

        type.immunities.addAll(status2);
    }
}
