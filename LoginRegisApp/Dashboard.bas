B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=10.2
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private Button1 As Button
	Private Label1 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("dashboard")
	
	Activity.Title = "Dashboard"
	Activity.AddMenuItem("Logout", "Logout")

End Sub




Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub Logout_Click()
	'Set is_login ke false
	Main.manager.SetBoolean("is_login", False)
	
	'Buka activity Main (Halaman Login)
	StartActivity(Main)
	Activity.Finish
	
	ToastMessageShow("Logout berhasil", False)

End Sub

