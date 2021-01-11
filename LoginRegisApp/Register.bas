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

	Private nameET As EditText
	Private passET As EditText
	Private phoneET As EditText
	Private alamatET As EditText
	Private hobiET As EditText
	Private takeSelfie As Button
	Private ImageView1 As ImageView
	Private registerButt As Button
	Private emailET As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("regis")
	
	Activity.Title = "Register"
	
	ImageView1.Visible = False

End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub InputRegis
	
	ShowLoading("Loading...")

	
	Dim data As Map
	data.Initialize
	data.Put("nama", nameET.Text)
	data.Put("email", emailET.Text)
	data.Put("password", emailET.Text)
	data.Put("phone_number", phoneET.Text)
	data.Put("alamat", alamatET.Text)
	data.Put("nik", hobiET.Text)
	
	postAPI(data)
	

End Sub

Sub ShowLoading(msg As String)
	ProgressDialogShow2(msg,False)
End Sub

Sub JobDone(job As HttpJob)
	
	
	If job.SUCCESS Then
		Dim Res As String
		Res = job.GetString
		Log("Response from server: " & Res)
		
		StartActivity(Main)
		Activity.Finish
	Else
		ToastMessageShow("Gagal Login: " & job.ErrorMessage, True)
	End If
	job.Release
		
	
End Sub

Sub postAPI(dataMap As Map)
	
	ProgressDialogHide
	
	Dim job As HttpJob
	job.Initialize("regis", Me)
	Dim path As String = "UserRegisPresensi.php"
	
	Dim myLink As String = Main.ipServer  & path
	job.PostMultipart(myLink,dataMap,Null)

	job.GetRequest.Timeout = 15000
	
End Sub


Sub registerButt_Click
	InputRegis
End Sub

Sub takeSelfie_Click
	
End Sub