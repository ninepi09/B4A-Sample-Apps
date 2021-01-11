package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class register extends Activity implements B4AActivity{
	public static register mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.register");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (register).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.register");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.register", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (register) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (register) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return register.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (register) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (register) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            register mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (register) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.EditTextWrapper _nameet = null;
public anywheresoftware.b4a.objects.EditTextWrapper _passet = null;
public anywheresoftware.b4a.objects.EditTextWrapper _phoneet = null;
public anywheresoftware.b4a.objects.EditTextWrapper _alamatet = null;
public anywheresoftware.b4a.objects.EditTextWrapper _hobiet = null;
public anywheresoftware.b4a.objects.ButtonWrapper _takeselfie = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _registerbutt = null;
public anywheresoftware.b4a.objects.EditTextWrapper _emailet = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.dashboard _dashboard = null;
public b4a.example.httputils2service _httputils2service = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="Activity.LoadLayout(\"regis\")";
mostCurrent._activity.LoadLayout("regis",mostCurrent.activityBA);
 //BA.debugLineNum = 31;BA.debugLine="Activity.Title = \"Register\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Register"));
 //BA.debugLineNum = 33;BA.debugLine="ImageView1.Visible = False";
mostCurrent._imageview1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 41;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Private nameET As EditText";
mostCurrent._nameet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private passET As EditText";
mostCurrent._passet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private phoneET As EditText";
mostCurrent._phoneet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private alamatET As EditText";
mostCurrent._alamatet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private hobiET As EditText";
mostCurrent._hobiet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private takeSelfie As Button";
mostCurrent._takeselfie = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private registerButt As Button";
mostCurrent._registerbutt = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private emailET As EditText";
mostCurrent._emailet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _inputregis() throws Exception{
anywheresoftware.b4a.objects.collections.Map _data = null;
 //BA.debugLineNum = 45;BA.debugLine="Sub InputRegis";
 //BA.debugLineNum = 47;BA.debugLine="ShowLoading(\"Loading...\")";
_showloading("Loading...");
 //BA.debugLineNum = 50;BA.debugLine="Dim data As Map";
_data = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 51;BA.debugLine="data.Initialize";
_data.Initialize();
 //BA.debugLineNum = 52;BA.debugLine="data.Put(\"nama\", nameET.Text)";
_data.Put((Object)("nama"),(Object)(mostCurrent._nameet.getText()));
 //BA.debugLineNum = 53;BA.debugLine="data.Put(\"email\", emailET.Text)";
_data.Put((Object)("email"),(Object)(mostCurrent._emailet.getText()));
 //BA.debugLineNum = 54;BA.debugLine="data.Put(\"password\", emailET.Text)";
_data.Put((Object)("password"),(Object)(mostCurrent._emailet.getText()));
 //BA.debugLineNum = 55;BA.debugLine="data.Put(\"phone_number\", phoneET.Text)";
_data.Put((Object)("phone_number"),(Object)(mostCurrent._phoneet.getText()));
 //BA.debugLineNum = 56;BA.debugLine="data.Put(\"alamat\", alamatET.Text)";
_data.Put((Object)("alamat"),(Object)(mostCurrent._alamatet.getText()));
 //BA.debugLineNum = 57;BA.debugLine="data.Put(\"nik\", hobiET.Text)";
_data.Put((Object)("nik"),(Object)(mostCurrent._hobiet.getText()));
 //BA.debugLineNum = 59;BA.debugLine="postAPI(data)";
_postapi(_data);
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(b4a.example.httpjob _job) throws Exception{
String _res = "";
 //BA.debugLineNum = 68;BA.debugLine="Sub JobDone(job As HttpJob)";
 //BA.debugLineNum = 71;BA.debugLine="If job.SUCCESS Then";
if (_job._success /*boolean*/ ) { 
 //BA.debugLineNum = 72;BA.debugLine="Dim Res As String";
_res = "";
 //BA.debugLineNum = 73;BA.debugLine="Res = job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 74;BA.debugLine="Log(\"Response from server: \" & Res)";
anywheresoftware.b4a.keywords.Common.LogImpl("05439494","Response from server: "+_res,0);
 //BA.debugLineNum = 76;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 77;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 }else {
 //BA.debugLineNum = 79;BA.debugLine="ToastMessageShow(\"Gagal Login: \" & job.ErrorMess";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Gagal Login: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 81;BA.debugLine="job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _postapi(anywheresoftware.b4a.objects.collections.Map _datamap) throws Exception{
b4a.example.httpjob _job = null;
String _path = "";
String _mylink = "";
 //BA.debugLineNum = 86;BA.debugLine="Sub postAPI(dataMap As Map)";
 //BA.debugLineNum = 88;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 90;BA.debugLine="Dim job As HttpJob";
_job = new b4a.example.httpjob();
 //BA.debugLineNum = 91;BA.debugLine="job.Initialize(\"regis\", Me)";
_job._initialize /*String*/ (processBA,"regis",register.getObject());
 //BA.debugLineNum = 92;BA.debugLine="Dim path As String = \"UserRegisPresensi.php\"";
_path = "UserRegisPresensi.php";
 //BA.debugLineNum = 94;BA.debugLine="Dim myLink As String = Main.ipServer  & path";
_mylink = mostCurrent._main._ipserver /*String*/ +_path;
 //BA.debugLineNum = 95;BA.debugLine="job.PostMultipart(myLink,dataMap,Null)";
_job._postmultipart /*String*/ (_mylink,_datamap,(anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(anywheresoftware.b4a.keywords.Common.Null)));
 //BA.debugLineNum = 97;BA.debugLine="job.GetRequest.Timeout = 15000";
_job._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().setTimeout((int) (15000));
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _registerbutt_click() throws Exception{
 //BA.debugLineNum = 102;BA.debugLine="Sub registerButt_Click";
 //BA.debugLineNum = 103;BA.debugLine="InputRegis";
_inputregis();
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _showloading(String _msg) throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub ShowLoading(msg As String)";
 //BA.debugLineNum = 65;BA.debugLine="ProgressDialogShow2(msg,False)";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow2(mostCurrent.activityBA,BA.ObjectToCharSequence(_msg),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _takeselfie_click() throws Exception{
 //BA.debugLineNum = 106;BA.debugLine="Sub takeSelfie_Click";
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
}
