package helper.creeperbox.feature.module;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.modules.build.*;
import helper.creeperbox.feature.module.modules.build.ChatHelper;
import helper.creeperbox.feature.module.modules.combat.*;
import helper.creeperbox.feature.module.modules.movement.*;
import helper.creeperbox.feature.module.modules.render.*;
import helper.creeperbox.feature.module.modules.survival.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private List<Module> registeredModule = new ArrayList<>();

    public ModuleManager(){
        init();
    }

    private void init(){
        registeredDefault();
    }

    private void registeredDefault(){

        if(CreeperBox.INSTANCE.debug){
            return;
        }

        //战斗
        registerModule(new Aimbot());
        registerModule(new AntiBot());
        registerModule(new AutoClicker());
        registerModule(new CpsBoost());
        registerModule(new Criticals());
        registerModule(new CrystalAura());
        registerModule(new GApple());
        registerModule(new HitBox());
        registerModule(new InfiniteAura());
        registerModule(new InfiniteMace());
        registerModule(new SelfAttack());
        registerModule(new KillAura());
        registerModule(new LockBack());
        registerModule(new NoHurtCam());
        registerModule(new Surround());
        registerModule(new Target());
        registerModule(new Team());
        registerModule(new TriggerBot());
        registerModule(new Velocity());

        //建筑
        registerModule(new AdventureTag());
        registerModule(new AutoSpawn());
        registerModule(new ChatHelper());
        registerModule(new AutoTool());
        registerModule(new ClearAccount());
        registerModule(new ClickDestroy());
//        registerModule(new CrackEC());
        registerModule(new ExecuteCommand());
        registerModule(new ExportBuilding());
        registerModule(new FastBuild());
        registerModule(new ForcePos());
        registerModule(new Fucker());
        registerModule(new GameModeChange());
        registerModule(new Login4399());
        registerModule(new Nuker());
        registerModule(new PictureImport());
        registerModule(new Scaffold());
        registerModule(new TeleportHelper());
        registerModule(new Timer());

        
        //渲染
//        registerModule(new LoadSkin());
        registerModule(new RealSkin());
        registerModule(new ModuleArrayList());
        registerModule(new ArmorHud());
        registerModule(new BarrierHelper());
        registerModule(new CommandBlockOutput());
        registerModule(new Compass());
        registerModule(new CustomFog());
        registerModule(new CustomSkin());
        registerModule(new DepartCamera());
        registerModule(new Derp());
        registerModule(new EffectHud());
        registerModule(new ESP());
        registerModule(new FullBright());
        registerModule(new HitParticle());
        registerModule(new ItemPhysics());
        registerModule(new JumpCircle());
        registerModule(new KillEffect());
        registerModule(new MotionCamera());
        registerModule(new Notification());
        registerModule(new NameTag());
        registerModule(new Particles());
        registerModule(new PlayAnimate());
        registerModule(new PlayerHolo());
        registerModule(new Projectiles());
        registerModule(new Radar());
        registerModule(new RenderInterface());
        registerModule(new TargetESP());
        registerModule(new SwingAnimation());
        registerModule(new TargetHud());
        registerModule(new TimeChange());
        registerModule(new Tracers());
        registerModule(new Trail());
        registerModule(new WaterMark());
        registerModule(new XRay());
        registerModule(new Zoom());

        //生存
        registerModule(new KickPlayer());

        registerModule(new AddEffect());
        registerModule(new AntiChat());
        registerModule(new AntiKick());
        registerModule(new AutoChat());
        registerModule(new Blink());
        registerModule(new CancelPacket());
        registerModule(new ChatBypass());
        registerModule(new ChestAura());
        registerModule(new ChestStealer());
        registerModule(new FreeCam());
        registerModule(new HorseSpawn());
        registerModule(new InvManager());
        registerModule(new IPConnect());
        registerModule(new IPPrint());
        registerModule(new IRC());
//        registerModule(new ModInject());
        registerModule(new NoSlow());
        registerModule(new ParticleAura());
        registerModule(new PlayerTeleport());
        registerModule(new Reach());
        registerModule(new RemoteShop());
        registerModule(new RepeatPacket());
        registerModule(new ServerCrasher());
        registerModule(new StopPacket());

        //移动
        registerModule(new AirJump());
        registerModule(new AntiVoid());
        registerModule(new Bhop());
        registerModule(new BoatFly());
        registerModule(new ClickTeleport());
        registerModule(new Disabler());
        registerModule(new Fly());
        registerModule(new FlyAbility());
        registerModule(new GodMode());
        registerModule(new Jetpack());
        registerModule(new KeepSprint());
        registerModule(new NoClip());
        registerModule(new NoFall());
        registerModule(new NoWeb());
        registerModule(new SafeWalk());
        registerModule(new Spider());
        registerModule(new SprintPack());

    }



    public List<Module> getModule(Category category) {
        List<Module> list = new ArrayList<>();
        for(Module m : registeredModule){
            if(m.getCategory() == category){
                list.add(m);
            }
        }
        return list;
    }


    public <T extends Module> T get(final Class<T> clazz) {
        // noinspection unchecked
        return (T) registeredModule.stream()
                .filter(module -> module.getClass() == clazz)
                .findAny().orElse(null);
    }

    public List<Module> getRegisteredModule() {
        return registeredModule;
    }


    public void registerModule(Module module){
        registeredModule.add(module);
        CreeperBox.INSTANCE.getEventManager().register(module);
    }


    public void unregisterModule(Module module){
        registeredModule.remove(module);
        CreeperBox.INSTANCE.getEventManager().unregister(module);
    }

}