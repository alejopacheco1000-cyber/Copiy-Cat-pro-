package com.example.catresolutionclone;

import android.app.*;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.view.*;
import android.widget.Toast;
import android.content.pm.PackageManager;
import java.io.*;
import java.util.*;
import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
    CatView v;
    static final int SHIZUKU_REQ = 100;
    boolean shizukuGranted = false;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        try {
            Shizuku.addRequestPermissionResultListener((requestCode, grantResult) -> {
                if (requestCode == SHIZUKU_REQ) {
                    shizukuGranted = grantResult == PackageManager.PERMISSION_GRANTED;
                    if (v != null) v.invalidate();
                    toast(shizukuGranted ? "Shizuku autorizado" : "Permiso de Shizuku rechazado");
                }
            });
        } catch (Throwable ignored) {}
        v = new CatView(this);
        setContentView(v);
    }

    @Override protected void onResume() {
        super.onResume();
        try { shizukuGranted = Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED; }
        catch (Throwable ignored) { shizukuGranted = false; }
        if (v != null) v.invalidate();
    }

    void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    void authorizeShizuku() {
        try {
            if (!Shizuku.pingBinder()) {
                toast("Abre Shizuku y activa su servicio primero");
                try { startActivity(getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api")); } catch (Exception ignored) {}
                return;
            }
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                shizukuGranted = true;
                toast("Shizuku ya está autorizado");
                v.invalidate();
                return;
            }
            Shizuku.requestPermission(SHIZUKU_REQ);
        } catch (Throwable e) {
            toast("Shizuku no está disponible");
        }
    }

    boolean canUseShizuku() {
        try { return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED; }
        catch (Throwable e) { return false; }
    }

    String shizukuShell(String command) {
        try {
            Process p = Shizuku.newProcess(new String[]{"sh","-c",command}, null, null);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder out = new StringBuilder(); String line;
            while ((line = br.readLine()) != null) out.append(line).append("\n");
            p.waitFor();
            return out.toString();
        } catch (Throwable e) { return ""; }
    }

    boolean rootShell(String command) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su","-c",command});
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Throwable e) { return false; }
    }

    String shell(String command) {
        if (canUseShizuku()) return shizukuShell(command);
        return "";
    }

    boolean execute(String command) {
        if (canUseShizuku()) {
            try {
                Process p = Shizuku.newProcess(new String[]{"sh","-c",command}, null, null);
                p.waitFor();
                if (p.exitValue() == 0) return true;
            } catch (Throwable ignored) {}
        }
        return rootShell(command);
    }

    String nativeSize() {
        String out = shell("wm size");
        if (out.isEmpty()) {
            try {
                android.util.DisplayMetrics m = getResources().getDisplayMetrics();
                int w = Math.max(m.widthPixels, m.heightPixels), h = Math.min(m.widthPixels, m.heightPixels);
                return w + " × " + h;
            } catch (Exception e) { return "—"; }
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("Physical size:\\s*(\\d+)x(\\d+)").matcher(out);
        if (m.find()) {
            int a=Integer.parseInt(m.group(1)), b=Integer.parseInt(m.group(2));
            return Math.max(a,b) + " × " + Math.min(a,b);
        }
        return "—";
    }

    class CatView extends View {
        Paint p = new Paint(3);
        int page = 0;
        float factor = 1.11f;
        boolean active=false, dark=true, overlay=false, demand=true;
        int purple=Color.rgb(123,31,227);

        CatView(Context c){ super(c); }

        void box(Canvas c,float l,float t,float r,float b,int col,float rad){
            p.setColor(col); p.setStyle(Paint.Style.FILL); c.drawRoundRect(l,t,r,b,rad,rad,p);
        }
        void tx(Canvas c,String s,float x,float y,float size,int col,boolean bold){
            p.setColor(col); p.setTextSize(size); p.setTypeface(Typeface.create("sans",bold?1:0));
            p.setStyle(Paint.Style.FILL); c.drawText(s,x,y,p);
        }
        int fg(){return dark?Color.WHITE:Color.rgb(28,25,32);}
        int muted(){return dark?0xffaaa5ad:0xff77727e;}
        int surface(){return dark?0xff312d34:Color.WHITE;}
        int bg(){return dark?0xff19171c:0xfffaf8fc;}

        protected void onDraw(Canvas c){
            c.drawColor(bg()); header(c);
            if(page==0)home(c); else if(page==1)presets(c); else if(page==2)overlayPage(c); else if(page==3)history(c); else config(c);
            bottom(c);
        }

        void header(Canvas c){
            box(c,0,0,getWidth(),76,dark?0xff2b282e:Color.WHITE,0);
            tx(c,"◇",29,46,32,purple,true);
            tx(c,"CAT RESOLUTION",90,31,21,fg(),true);
            box(c,90,40,160,67,purple,6); tx(c,"PRO",105,59,13,Color.WHITE,true);
            box(c,getWidth()-82,12,getWidth()-22,72,purple,30);
        }

        void title(Canvas c,String a,String b){
            tx(c,a,36,122,34,fg(),true); tx(c,b,36,151,16,muted(),false);
        }

        void home(Canvas c){
            title(c,"TELA ESTICADA","Alongamento inteligente para mais visão, projeção precisa e controle rápido.");
            box(c,32,178,getWidth()-32,340,surface(),12);
            tx(c,"NATIVA",60,211,13,purple,true); tx(c,"PROJEÇÃO",getWidth()/2+10,211,13,purple,true);
            String nativeRes=nativeSize(); tx(c,nativeRes,60,241,20,fg(),true);
            String[] parts=nativeRes.replace(" ","").split("×");
            String projection="—";
            try { int a=Integer.parseInt(parts[0]), b=Integer.parseInt(parts[1]); projection=Math.round(a*factor)+" × "+b; } catch(Exception ignored){}
            tx(c,projection,getWidth()/2+10,241,20,fg(),true);
            box(c,54,267,getWidth()/2-10,321,purple,27);
            tx(c,active?"ACTIVO":"ACTIVAR",92,301,17,Color.WHITE,true);
            box(c,getWidth()/2+10,267,getWidth()-54,321,dark?0xff454047:0xffe8e3e9,27);
            tx(c,"RESTAURAR",getWidth()/2+42,301,17,fg(),true);
            tx(c,"PRESETS RÁPIDOS",36,387,16,purple,true);
            String[] f={"1,05x","1,10x","1,11x","1,15x"};
            for(int i=0;i<4;i++){float x=36+i*91;box(c,x,405,x+82,454,Math.abs(factor-new float[]{1.05f,1.10f,1.11f,1.15f}[i])<.001?purple:surface(),10);tx(c,f[i],x+18,436,14,Math.abs(factor-new float[]{1.05f,1.10f,1.11f,1.15f}[i])<.001?Color.WHITE:purple,true);}
        }

        void presets(Canvas c){
            title(c,"PRESETS","Elige o ajusta tu proyección");
            String[] n={"Equilibrado","Más visión","Recomendado","Campo ampliado"};
            float[] f={1.05f,1.10f,1.11f,1.15f};
            for(int i=0;i<4;i++){float y=180+i*94;box(c,32,y,getWidth()-32,y+78,surface(),12);tx(c,String.format(Locale.US,"%.2f",f[i]),50,y+40,17,purple,true);tx(c,n[i],145,y+31,18,fg(),true);}
            box(c,32,570,getWidth()-32,680,surface(),12);
            tx(c,"PERSONALIZADO",52,605,15,purple,true);
            tx(c,"Factor: "+String.format(Locale.US,"%.2fx",factor),52,638,17,fg(),true);
            tx(c,"Hasta 1.30x",getWidth()-145,638,14,muted(),false);
        }

        void overlayPage(Canvas c){
            title(c,"OVERLAY","Control flotante durante el uso");
            card(c,180,"OVERLAY FLOTANTE","Activa el control para usarlo en juegos",overlay?"ACTIVO":"");
            card(c,296,"Overlay en juegos",Settings.canDrawOverlays(MainActivity.this)?"Permiso concedido":"Toca para liberar el permiso",Settings.canDrawOverlays(MainActivity.this)?"ACTIVO":"PERMITIR");
        }

        void history(Canvas c){
            title(c,"HISTORIAL","Activaciones y restauraciones recientes");
            card(c,180,"Sesión actual",active?"Proyección activa":"Restaurada",active?"ACTIVO":"RESTAURADO");
            if (canUseShizuku()) card(c,296,"Método","Shizuku autorizado","LISTO");
            else card(c,296,"Método","Shizuku no autorizado","PENDIENTE");
        }

        void config(Canvas c){
            title(c,"CONFIGURACIÓN","Métodos, proyección y preferencias");
            card(c,180,"Shizuku",canUseShizuku()?"Servicio autorizado":"Toca para autorizar el aplicativo",canUseShizuku()?"LISTO":"ACTIVAR");
            card(c,296,"Depuración Wi-Fi","Alternativa para activación","CONFIGURAR");
            card(c,412,"Root","Dispositivos con root instalado","ACTIVAR");
            card(c,528,"MODO DE PROYECCIÓN","Alargar / Corte lateral","›");
            card(c,644,"Ejecución bajo demanda","Restaura al salir del juego",demand?"ON":"OFF");
            card(c,760,"Idioma de la aplicación","Español / English / Português","›");
            card(c,876,"Tema oscuro","Interfaz nocturna",dark?"ON":"OFF");
        }

        void card(Canvas c,float y,String a,String b,String r){
            box(c,32,y,getWidth()-32,y+104,surface(),14);
            tx(c,a,82,y+38,18,fg(),true); tx(c,b,82,y+65,14,muted(),false);
            if(r!=null&&!r.isEmpty()) tx(c,r,getWidth()-155,y+51,14,purple,true);
        }

        void bottom(Canvas c){
            int y=getHeight()-86,w=getWidth(); box(c,0,y,w,getHeight(),dark?0xff242127:Color.WHITE,0);
            String[] n={"Inicio","Presets","Overlay","Historial","Config"};
            for(int i=0;i<5;i++){float x=(i+.5f)*w/5;tx(c,n[i],x-25,y+58,11,i==page?purple:muted(),false);}
        }

        public boolean onTouchEvent(MotionEvent e){
            if(e.getAction()!=MotionEvent.ACTION_UP)return true;
            float x=e.getX(),y=e.getY(); int h=getHeight(),w=getWidth();
            if(y>h-95){page=Math.min(4,(int)(x/(w/5)));invalidate();return true;}

            if(page==0&&y>260&&y<335){ if(x<w/2)apply(); else restore(); }
            else if(page==0&&y>395&&y<470){int i=(int)((x-36)/91);choose(i);}
            else if(page==1&&y>175&&y<560){int i=(int)((y-180)/94);choose(i);}
            else if(page==2&&y>175&&y<430){
                if(y<285){overlay=!overlay; if(overlay)requestOverlay(); invalidate();}
                else {requestOverlay(); invalidate();}
            }
            else if(page==4){
                if(y>175&&y<285)authorizeShizuku();
                else if(y>285&&y<400)configureWifi();
                else if(y>400&&y<510){toast("Root se usa automáticamente como respaldo");}
                else if(y>620&&y<750){demand=!demand;invalidate();}
                else if(y>840&&y<1000){dark=!dark; getWindow().setStatusBarColor(dark?0xff19171c:Color.WHITE); getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);invalidate();}
            }
            return true;
        }

        void choose(int i){
            if(i<0||i>3)return;
            factor=new float[]{1.05f,1.10f,1.11f,1.15f}[i]; invalidate();
        }

        void apply(){
            String nativeRes=nativeSize().replace(" ","");
            try{
                String[] q=nativeRes.split("×"); int w=Integer.parseInt(q[0]),h=Integer.parseInt(q[1]);
                int target=Math.min(Math.round(w*factor),Math.round(w*1.30f));
                active=execute("wm size "+target+"x"+h);
                toast(active?"Resolución aplicada: "+target+"x"+h:"No se pudo aplicar. Autoriza Shizuku o Root.");
            }catch(Exception e){toast("No se pudo leer la resolución nativa");}
            invalidate();
        }

        void restore(){
            boolean ok=execute("wm size reset");
            active=false; toast(ok?"Resolución restaurada":"No se pudo restaurar"); invalidate();
        }

        void requestOverlay(){
            if(!Settings.canDrawOverlays(MainActivity.this)){
                try{startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())));}
                catch(Exception e){startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));}
            } else toast("Permiso de overlay concedido");
        }

        void configureWifi(){
            try{startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));}
            catch(Exception e){toast("Abre Ajustes > Opciones de desarrollador > Depuración inalámbrica");}
        }
    }
}
