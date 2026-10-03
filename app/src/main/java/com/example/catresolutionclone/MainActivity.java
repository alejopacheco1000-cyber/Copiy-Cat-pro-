package com.example.catresolutionclone;

import android.app.*;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity {
  CatView v;
  public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.WHITE);getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);v=new CatView(this);setContentView(v);}
  class CatView extends View{
    Paint p=new Paint(3); int page=0; float factor=1.11f; boolean active=false, dark=false, overlay=false, demand=true;
    int purple=Color.rgb(123,31,227);
    CatView(Context c){super(c);}
    void box(Canvas c,float l,float t,float r,float b,int col,float rad){p.setColor(col);p.setStyle(Paint.Style.FILL);c.drawRoundRect(l,t,r,b,rad,rad,p);}
    void tx(Canvas c,String s,float x,float y,float size,int col,boolean bold){p.setColor(col);p.setTextSize(size);p.setTypeface(Typeface.create("sans",bold?1:0));p.setStyle(Paint.Style.FILL);c.drawText(s,x,y,p);}
    int fg(){return dark?Color.WHITE:Color.rgb(28,25,32);} int muted(){return dark?0xffbdb8c2:0xff7d7884;}
    protected void onDraw(Canvas c){c.drawColor(dark?0xff19171c:0xfffaf8fc); header(c); if(page==0)home(c); if(page==1)presets(c); if(page==2)overlay(c); if(page==3)history(c); if(page==4)config(c); bottom(c);}
    void header(Canvas c){box(c,0,0,getWidth(),76,dark?0xff2b282e:Color.WHITE,0);tx(c,"◈",28,45,34,purple,true);tx(c,"CAT RESOLUTION",92,31,21,fg(),true);box(c,92,40,162,67,purple,6);tx(c,"PRO",106,59,13,Color.WHITE,true);tx(c,"E",getWidth()-66,50,20,Color.WHITE,true);box(c,getWidth()-82,12,getWidth()-22,72,purple,30);tx(c,"⚙",getWidth()-48,51,27,purple,false);}
    void title(Canvas c,String a,String b){tx(c,a,36,122,34,fg(),true);tx(c,b,36,151,16,muted(),false);}
    void home(Canvas c){title(c,"TELA ESTICADA","Alongamento inteligente para mais visão, projeção precisa e controle rápido.");box(c,32,178,getWidth()-32,340,dark?0xff312d34:Color.WHITE,12);tx(c,"NATIVA",60,211,13,purple,true);tx(c,"PROJEÇÃO",getWidth()/2+10,211,13,purple,true);int n=getResources().getDisplayMetrics().widthPixels,s=getResources().getDisplayMetrics().heightPixels;int lo=Math.max(n,s),sh=Math.min(n,s);tx(c,lo+" × "+sh,60,241,20,fg(),true);tx(c,Math.round(lo*factor)+" × "+sh,getWidth()/2+10,241,20,fg(),true);box(c,54,267,getWidth()/2-10,321,purple,27);tx(c,active?"ATIVO":"ATIVAR",95,301,17,Color.WHITE,true);box(c,getWidth()/2+10,267,getWidth()-54,321,dark?0xff454047:Color.WHITE,27);tx(c,"RESTAURAR",getWidth()/2+42,301,17,fg(),true);tx(c,"PRESETS RÁPIDOS",36,387,16,purple,true);String[] f={"1,05x","1,10x","1,11x","1,15x"};for(int i=0;i<4;i++){float x=36+i*91;box(c,x,405,x+82,454,i==2?purple:(dark?0xff312d34:Color.WHITE),10);tx(c,f[i],x+18,436,14,i==2?Color.WHITE:purple,true);}tx(c,"RECURSOS PRINCIPAIS",36,501,16,purple,true);feature(c,36,"◫","OVERLAY FLUTUANTE");feature(c,205,"↔","PROJEÇÃO EM TEMPO REAL");feature(c,374,"↻","APLICAR / RESTAURAR");}
    void feature(Canvas c,float x,String ic,String s){tx(c,ic,x+8,560,27,purple,true);tx(c,s,x,595,12,fg(),true);}
    void presets(Canvas c){title(c,"PRESETS","Escolha ou ajuste sua projeção");String[] n={"Equilibrado","Mais visão","Recomendado","Campo ampliado"};float[] f={1.05f,1.10f,1.11f,1.15f};for(int i=0;i<4;i++){float y=180+i*94;box(c,32,y,getWidth()-32,y+78,dark?0xff312d34:Color.WHITE,12);tx(c,String.format(Locale.US,"%.2f",f[i]),50,y+40,17,purple,true);tx(c,n[i],145,y+31,18,fg(),true);tx(c,"Projeção "+Math.round(getWidth()*f[i])+" × 720",145,y+56,13,muted(),false);if(Math.abs(factor-f[i])<.001)tx(c,"✓",getWidth()-70,y+44,26,purple,true);}tx(c,"AJUSTE PERSONALIZADO",36,584,15,purple,true);tx(c,String.format(Locale.US,"%.2fx",factor),36,629,39,fg(),true);}
    void card(Canvas c,float y,String a,String b,String r){box(c,32,y,getWidth()-32,y+104,dark?0xff312d34:0xfff7f2fc,14);tx(c,a,82,y+38,18,fg(),true);tx(c,b,82,y+65,14,muted(),false);if(r!=null)tx(c,r,getWidth()-155,y+51,14,purple,true);}
    void overlay(Canvas c){title(c,"OVERLAY","Controle flutuante durante o uso");card(c,180,"OVERLAY FLUTUANTE","Ative o controle para usar nos jogos","");card(c,296,"Overlay nos jogos","Toque para liberar a permissão",overlay?"ATIVO":"");card(c,412,"Aplicativos da launcher","Adicione jogos para iniciar com proteção","›");card(c,528,"Tecla de emergência","Restaure mesmo com o painel fechado","ATIVAR");card(c,644,"Tamanho do controle","Médio","›");card(c,760,"Transparência","85%","›");}
    void history(Canvas c){title(c,"HISTÓRICO","Ativações e restaurações recentes");card(c,180,"Sessão atual",active?"Proyección activa":"Restaurada",active?"ATIVO":"RESTAURADO");card(c,296,"Último preset",String.format(Locale.US,"%.2fx",factor),"");}
    void config(Canvas c){title(c,"CONFIGURAÇÃO","Métodos, projeção e preferências");card(c,180,"Shizuku","Toque para autorizar o aplicativo","ATIVAR");card(c,296,"Depuração Wi-Fi","Alternativa para ativação","CONFIGURAR");card(c,412,"Root","Aparelhos com root instalado","ATIVAR");card(c,528,"MODO DE PROJEÇÃO","Alongar / Corte lateral","›");card(c,644,"Execução sob demanda","Restaura ao sair do jogo",demand?"ON":"OFF");card(c,760,"Idioma do aplicativo","Español / English / Português","›");card(c,876,"Tema escuro","Interface noturna",dark?"ON":"OFF");}
    void bottom(Canvas c){int y=getHeight()-86,w=getWidth();box(c,0,y,w,getHeight(),dark?0xff242127:Color.WHITE,0);String[] n={"Inicio","Presets","Overlay","Histórico","Config"};String[] ic={"⌂","+","◇","◷","⚙"};for(int i=0;i<5;i++){float x=(i+.5f)*w/5;if(page==i)box(c,x-31,y+10,x+31,y+69,purple,15);tx(c,ic[i],x-10,y+38,25,page==i?Color.WHITE:purple,true);tx(c,n[i],x-28,y+64,11,page==i?purple:muted(),false);}}
    public boolean onTouchEvent(MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float x=e.getX(),y=e.getY();int h=getHeight(),w=getWidth();if(y>h-95){page=Math.min(4,(int)(x/(w/5)));invalidate();return true;}if(page==0&&y>265&&y<330){if(x<w/2)apply();else restore();}else if(page==0&&y>395&&y<470){int i=(int)((x-36)/91);if(i>=0&&i<4){factor=new float[]{1.05f,1.10f,1.11f,1.15f}[i];invalidate();}}else if(page==1&&y>175&&y<560){int i=(int)((y-180)/94);if(i>=0&&i<4){factor=new float[]{1.05f,1.10f,1.11f,1.15f}[i];invalidate();}}else if(page==2&&y>290&&y<405){overlay=!overlay;if(overlay&&!Settings.canDrawOverlays(MainActivity.this))try{startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())));}catch(Exception z){}invalidate();}else if(page==4&&y>850&&y<1000){dark=!dark;getWindow().setStatusBarColor(dark?0xff19171c:Color.WHITE);getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);invalidate();}return true;}
    void apply(){int n=getResources().getDisplayMetrics().widthPixels,s=getResources().getDisplayMetrics().heightPixels,lo=Math.max(n,s),sh=Math.min(n,s);int target=Math.round(lo*factor);active=run("wm size "+target+"x"+sh);invalidate();}
    void restore(){active=run("wm size reset")?false:false;invalidate();}
    boolean run(String cmd){try{Process p=Runtime.getRuntime().exec(new String[]{"su","-c",cmd});p.waitFor();return p.exitValue()==0;}catch(Exception e){return false;}}
  }
}