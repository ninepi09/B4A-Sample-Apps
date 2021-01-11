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

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
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
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
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
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public static de.amberhome.objects.preferenceactivity.PreferenceManager _manager = null;
public static String _ipserver = "";
public anywheresoftware.b4a.objects.EditTextWrapper _emailet = null;
public anywheresoftware.b4a.objects.EditTextWrapper _passwordet = null;
public anywheresoftware.b4a.objects.ButtonWrapper _loginbutt = null;
public anywheresoftware.b4a.objects.ButtonWrapper _regisbutt = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkbox1 = null;
public b4a.example.starter _starter = null;
public b4a.example.dashboard _dashboard = null;
public b4a.example.register _register = null;
public b4a.example.httputils2service _httputils2service = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (dashboard.mostCurrent != null);
vis = vis | (register.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="Activity.LoadLayout(\"Login\")";
mostCurrent._activity.LoadLayout("Login",mostCurrent.activityBA);
 //BA.debugLineNum = 41;BA.debugLine="Activity.Title = \"Login\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Login"));
 //BA.debugLineNum = 42;BA.debugLine="Log(\"email : \" & emailET.text)";
anywheresoftware.b4a.keywords.Common.LogImpl("0131078","email : "+mostCurrent._emailet.getText(),0);
 //BA.debugLineNum = 43;BA.debugLine="Log(\"email : \" & passwordET.text)";
anywheresoftware.b4a.keywords.Common.LogImpl("0131079","email : "+mostCurrent._passwordet.getText(),0);
 //BA.debugLineNum = 46;BA.debugLine="CheckLoginSession";
_checkloginsession();
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 124;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 126;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 120;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return "";
}
public static String  _checkbox1_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 167;BA.debugLine="Sub CheckBox1_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 169;BA.debugLine="End Sub";
return "";
}
public static String  _checkloginsession() throws Exception{
boolean _islogin = false;
 //BA.debugLineNum = 50;BA.debugLine="Sub CheckLoginSession()";
 //BA.debugLineNum = 51;BA.debugLine="Dim isLogin As Boolean";
_islogin = false;
 //BA.debugLineNum = 52;BA.debugLine="isLogin = manager.GetBoolean(\"is_login\")";
_islogin = _manager.GetBoolean("is_login");
 //BA.debugLineNum = 54;BA.debugLine="If isLogin Then";
if (_islogin) { 
 //BA.debugLineNum = 55;BA.debugLine="StartActivity(Dashboard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._dashboard.getObject()));
 //BA.debugLineNum = 56;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 25;BA.debugLine="Private emailET As EditText";
mostCurrent._emailet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private passwordET As EditText";
mostCurrent._passwordet = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private loginButt As Button";
mostCurrent._loginbutt = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private regisButt As Button";
mostCurrent._regisbutt = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private CheckBox1 As CheckBox";
mostCurrent._checkbox1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _inputlogin() throws Exception{
String _useremail = "";
String _password = "";
anywheresoftware.b4a.objects.collections.Map _data = null;
 //BA.debugLineNum = 60;BA.debugLine="Sub InputLogin";
 //BA.debugLineNum = 62;BA.debugLine="ShowLoading(\"Loading...\")";
_showloading("Loading...");
 //BA.debugLineNum = 64;BA.debugLine="Dim userEmail As String";
_useremail = "";
 //BA.debugLineNum = 65;BA.debugLine="userEmail = emailET.text";
_useremail = mostCurrent._emailet.getText();
 //BA.debugLineNum = 66;BA.debugLine="Dim password As String";
_password = "";
 //BA.debugLineNum = 67;BA.debugLine="password = passwordET.text";
_password = mostCurrent._passwordet.getText();
 //BA.debugLineNum = 69;BA.debugLine="Log(\"email :\" & userEmail)";
anywheresoftware.b4a.keywords.Common.LogImpl("0851977","email :"+_useremail,0);
 //BA.debugLineNum = 70;BA.debugLine="Log(\"pass :\" & password)";
anywheresoftware.b4a.keywords.Common.LogImpl("0851978","pass :"+_password,0);
 //BA.debugLineNum = 72;BA.debugLine="Dim data As Map";
_data = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 73;BA.debugLine="data.Initialize";
_data.Initialize();
 //BA.debugLineNum = 74;BA.debugLine="data.Put(\"email\", emailET.Text)";
_data.Put((Object)("email"),(Object)(mostCurrent._emailet.getText()));
 //BA.debugLineNum = 75;BA.debugLine="data.Put(\"password\", passwordET.Text)";
_data.Put((Object)("password"),(Object)(mostCurrent._passwordet.getText()));
 //BA.debugLineNum = 77;BA.debugLine="postAPI(data)";
_postapi(_data);
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(b4a.example.httpjob _job) throws Exception{
String _res = "";
 //BA.debugLineNum = 86;BA.debugLine="Sub JobDone(job As HttpJob)";
 //BA.debugLineNum = 89;BA.debugLine="If job.SUCCESS Then";
if (_job._success /*boolean*/ ) { 
 //BA.debugLineNum = 90;BA.debugLine="Dim Res As String";
_res = "";
 //BA.debugLineNum = 91;BA.debugLine="Res = job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 92;BA.debugLine="Log(\"Response from server: \" & Res)";
anywheresoftware.b4a.keywords.Common.LogImpl("0917510","Response from server: "+_res,0);
 //BA.debugLineNum = 94;BA.debugLine="StartActivity(Dashboard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._dashboard.getObject()));
 //BA.debugLineNum = 95;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 }else {
 //BA.debugLineNum = 97;BA.debugLine="ToastMessageShow(\"Gagal Login: \" & job.ErrorMess";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Gagal Login: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 99;BA.debugLine="job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public static String  _loginbutt_click() throws Exception{
String _strusername = "";
String _strpassword = "";
 //BA.debugLineNum = 136;BA.debugLine="Sub loginButt_Click";
 //BA.debugLineNum = 139;BA.debugLine="Dim strUsername As String = emailET.Text.Trim";
_strusername = mostCurrent._emailet.getText().trim();
 //BA.debugLineNum = 140;BA.debugLine="If strUsername = \"\" Then";
if ((_strusername).equals("")) { 
 //BA.debugLineNum = 141;BA.debugLine="ToastMessageShow(\"Please enter User Email\", \"Err";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Please enter User Email"),BA.ObjectToBoolean("Error"));
 //BA.debugLineNum = 142;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 144;BA.debugLine="Dim strPassword As String = passwordET.Text.Trim";
_strpassword = mostCurrent._passwordet.getText().trim();
 //BA.debugLineNum = 145;BA.debugLine="If strPassword = \"\" Then";
if ((_strpassword).equals("")) { 
 //BA.debugLineNum = 146;BA.debugLine="ToastMessageShow(\"Please enter Password\", \"Error";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Please enter Password"),BA.ObjectToBoolean("Error"));
 //BA.debugLineNum = 147;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 150;BA.debugLine="InputLogin";
_inputlogin();
 //BA.debugLineNum = 152;BA.debugLine="If CheckBox1.Checked Then";
if (mostCurrent._checkbox1.getChecked()) { 
 //BA.debugLineNum = 153;BA.debugLine="SetLoginSession(True)";
_setloginsession(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 155;BA.debugLine="SetLoginSession(False)";
_setloginsession(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
public static String  _postapi(anywheresoftware.b4a.objects.collections.Map _datamap) throws Exception{
b4a.example.httpjob _job = null;
String _path = "";
String _mylink = "";
 //BA.debugLineNum = 104;BA.debugLine="Sub postAPI(dataMap As Map)";
 //BA.debugLineNum = 106;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 108;BA.debugLine="Dim job As HttpJob";
_job = new b4a.example.httpjob();
 //BA.debugLineNum = 109;BA.debugLine="job.Initialize(\"login\", Me)";
_job._initialize /*String*/ (processBA,"login",main.getObject());
 //BA.debugLineNum = 110;BA.debugLine="Dim path As String = \"UserLogin3.php\"";
_path = "UserLogin3.php";
 //BA.debugLineNum = 112;BA.debugLine="Dim myLink As String = ipServer  & path";
_mylink = _ipserver+_path;
 //BA.debugLineNum = 113;BA.debugLine="job.PostMultipart(myLink,dataMap,Null)";
_job._postmultipart /*String*/ (_mylink,_datamap,(anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(anywheresoftware.b4a.keywords.Common.Null)));
 //BA.debugLineNum = 115;BA.debugLine="job.GetRequest.Timeout = 15000";
_job._getrequest /*anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest*/ ().setTimeout((int) (15000));
 //BA.debugLineNum = 117;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
dashboard._process_globals();
register._process_globals();
httputils2service._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 19;BA.debugLine="Dim manager As AHPreferenceManager";
_manager = new de.amberhome.objects.preferenceactivity.PreferenceManager();
 //BA.debugLineNum = 20;BA.debugLine="Dim ipServer As String = \"https://yukmarry.com/te";
_ipserver = "https://yukmarry.com/tertandaid/tertanda/";
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
public static String  _regisbutt_click() throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub regisButt_Click";
 //BA.debugLineNum = 164;BA.debugLine="StartActivity(Register)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._register.getObject()));
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _setloginsession(boolean _checked) throws Exception{
 //BA.debugLineNum = 128;BA.debugLine="Sub SetLoginSession(Checked As Boolean)";
 //BA.debugLineNum = 129;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 130;BA.debugLine="manager.SetBoolean(\"is_login\", True)";
_manager.SetBoolean("is_login",anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 132;BA.debugLine="manager.SetBoolean(\"is_login\", False)";
_manager.SetBoolean("is_login",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
public static String  _showloading(String _msg) throws Exception{
 //BA.debugLineNum = 82;BA.debugLine="Sub ShowLoading(msg As String)";
 //BA.debugLineNum = 83;BA.debugLine="ProgressDialogShow2(msg,False)";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow2(mostCurrent.activityBA,BA.ObjectToCharSequence(_msg),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
}
