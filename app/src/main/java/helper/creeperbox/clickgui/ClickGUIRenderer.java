package helper.creeperbox.clickgui;

import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import helper.creeperbox.clickgui.component.clickgui.CBMainPanelComponent;
import helper.creeperbox.clickgui.component.clickgui.CBSettingListComponent;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.modules.render.ModuleArrayList;
import helper.creeperbox.utils.render.MatrixUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;
import helper.creeperbox.utils.render.bloom.BloomUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ClickGUIRenderer implements GLSurfaceView.Renderer {

    public static float width;

    public static float height;
    private static ClickGUIRenderer INSTANCE;
    private CBMainPanelComponent panel;

    public static CBSettingListComponent setting;
    public static boolean toggleSetting = false;

    public static Animation fadeAnimation = new Animation(Easing.FADE_BEZIER,200);
    public static Animation guiAnimation = new Animation(Easing.FADE_BEZIER,200);
    public static Animation blurAnimation = new Animation(Easing.Decelerate,200);
    public static boolean toggleGUI = true;

    public static void toggleSetting(Module module){
        setting = new CBSettingListComponent(module);
        toggleSetting = true;
    }

    public static ClickGUIRenderer getINSTANCE() {
        return INSTANCE;
    }

    public ClickGUIRenderer(float width, float height){
        INSTANCE = this;

        ClickGUIRenderer.width = width;
        ClickGUIRenderer.height = height;
        time = System.currentTimeMillis();

        panel = new CBMainPanelComponent();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0, 0, 0, 0);
        CreeperBox.INSTANCE.getConfigManager().load();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixUtil.matrixMode(MatrixUtil.PROJECTION_MATRIX);
        MatrixUtil.loadIdentity();
        MatrixUtil.ortho(0, width, height, 0, -100, 100);
        MatrixUtil.matrixMode(MatrixUtil.MODEL_MATRIX);
        ClickGUIRenderer.height = height;
        ClickGUIRenderer.width = width;
    }


    public static boolean onKey(int action,float x,float y,int count){

//        if(!VerifyManager.isVerify){
//            return true;
//        }

        if(!toggleGUI) return true;

        if(INSTANCE!=null){
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if(toggleSetting){
                        if(setting.isInSide(x,y)){
                            setting.onPress(x,y);
                        }else{
                            toggleSetting = false;
                        }
                    }else{
                        INSTANCE.panel.onPress(x,y);
                    }
                    return false;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(toggleSetting){
                        setting.onRelease(x,y,false);
                    }else{
                        INSTANCE.panel.onRelease(x,y,false);
                    }
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if(toggleSetting){
                        setting.onMove(x,y);
                    }else{
                        INSTANCE.panel.onMove(x,y);
                    }
                    return !INSTANCE.isToggleGUI();
            }
            String actionStr = MotionEvent.actionToString(action);
            if(actionStr.contains("ACTION_POINTER_DOWN")) {
                if(toggleSetting){
                    if(setting.isInSide(x,y)){
                        setting.onPress(x,y);
                    }else{
                        toggleSetting = false;
                    }
                }else{
                    INSTANCE.panel.onPress(x,y);
                }
            }else if(actionStr.contains("ACTION_POINTER_UP")){
                if(toggleSetting){
                    setting.onRelease(x,y,false);
                }else{
                    INSTANCE.panel.onRelease(x,y,false);
                }
            }
        }

        return true;
    }

    public boolean isToggleSetting(){
        return setting != null && (toggleSetting || !fadeAnimation.isFinished());
    }

    public boolean isToggleGUI(){
        return toggleGUI || !guiAnimation.isFinished();
    }

    private long time;

    private boolean blur = false;

    private boolean first = true;

    public static BloomUtil bloomUtil;

    @Override
    public void onDrawFrame(GL10 gl) {

        if(bloomUtil==null){
            bloomUtil = new BloomUtil();
        }

        glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        guiAnimation.run(toggleGUI?1f:0f);
        blurAnimation.run(toggleGUI?1f:0f);


        if(System.currentTimeMillis()-time>1000*30){
            CreeperBox.INSTANCE.getConfigManager().save();
            time = System.currentTimeMillis();
        }

        if(toggleGUI || !guiAnimation.isFinished()){

            blur = true;
            if(!blurAnimation.isFinished()){
                StringBuilder sb = new StringBuilder("import mod.client.extraClientApi as clientApi\n" +
                        "comp = clientApi.GetEngineCompFactory().CreatePostProcess(clientApi.GetLevelId())\n" +
                        "comp.SetEnableGaussianBlur(True)\n" +
                        "comp.SetGaussianBlurRadius(");
                sb.append(blurAnimation.getValue()*3f);
                sb.append(")");
                PythonCallerComponent.addQueue(sb.toString());
            }


            if(toggleGUI){
                guiAnimation.setEasing(Easing.FADE_BEZIER);
            }else{
                guiAnimation.setEasing(Easing.EASE_OUT_EXPO);
            }

            MatrixUtil.matrixMode(MatrixUtil.PROJECTION_MATRIX);
            MatrixUtil.loadIdentity();
            MatrixUtil.ortho(0, width, height, 0, -100, 100);
            MatrixUtil.matrixMode(MatrixUtil.MODEL_MATRIX);


            Render2DUtil.startScale(width/2f,height/2f,toggleGUI?0.5f+0.5f*guiAnimation.getValue():guiAnimation.getValue());

            if(toggleSetting){
                fadeAnimation.setEasing(Easing.FADE_BEZIER);
            }else{
                fadeAnimation.setEasing(Easing.EASE_OUT_EXPO);
            }

            fadeAnimation.run(toggleSetting?1f:0f);
            panel.draw();
            if(isToggleSetting()){
                Render2DUtil.startScale(width/2f,height/2f,toggleSetting?0.5f+0.5f*fadeAnimation.getValue():fadeAnimation.getValue());
                setting.draw();
                Render2DUtil.endScale();
            }

            Render2DUtil.endScale();
            MatrixUtil.popMatrix();
        }else if(blur){
            blur = false;
            PythonCallerComponent.addQueue("import mod.client.extraClientApi as clientApi\n" +
                    "comp = clientApi.GetEngineCompFactory().CreatePostProcess(clientApi.GetLevelId())\n" +
                    "comp.SetEnableGaussianBlur(False)\n");
        }


        ModuleArrayList.draw();

        bloomUtil.draw();




    }

}
