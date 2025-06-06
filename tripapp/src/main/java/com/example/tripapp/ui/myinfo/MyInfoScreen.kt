package com.example.tripapp.ui.myinfo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.tripapp.R
import com.example.tripapp.data.myinfo.MyInfoData
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInfoScreen(
    pop: (infoData: MyInfoData) -> Unit,
    viewModel: MyInfoViewModel
) {
    var email by remember { mutableStateOf("a@a.com") }
    var phone by remember { mutableStateOf("1111-1111") }
    var photo by remember { mutableStateOf("") }

    // lifecycle owner 획득, 상황에 따라서 다른 객체일 수 있음
    // activity, fragment
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraFileUri: Uri? = null
    var tempFilePath: String? = null

    // 최초에 한번 실행되는 코드
    // viewModel 에 일을 시켜서 B/L 실행되게 하고 결과 데이터를 초기 출력
    LaunchedEffect(true) {
        val viewModelInfoData = viewModel.getMyInfoData()
        email = viewModelInfoData?.email ?: ""
        phone = viewModelInfoData?.phone ?: ""
        photo = viewModelInfoData?.photo ?: ""
    }

    // 컴포저블에서 다른 액티비티 실행 - 결과 되돌려 받기
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        try {
            // 갤러리 앱에서 되돌아 온 순간
            var uri = it.data!!.data!!
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(
                uri, proj, null, null, null
            )
            // file 경로를 갤러리 앱에게 받아서 photo 에 저장
            cursor?.let {
                if (cursor.moveToFirst()) {
                    photo = cursor.getString(0)
                }
            }
            // 되돌아오자마자 화면에도 출력되어야 해서
            imageUri = uri
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // permission 조정 launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            // 갤러리 앱 실행
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }
    }

    // camera app 연동 시의 파일 준비
    fun createImageFile(): File {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${stamp}"
        return File.createTempFile(
            imageFileName,
            ".jpg",
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            imageUri = cameraFileUri
            photo = tempFilePath ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    TextButton(
                        onClick = {
                            // 저장 데이터 준비
                            val infoData = MyInfoData(0, email, phone, photo)
                            // viewModel 에게 일 시켜서 db 에 데이터 저장
                            viewModel.updateMyInfoData(infoData)
                                // 결과에 따라 화면 다르게 처리
                                .observe(lifecycleOwner) {
                                    if (it) {
                                        // 성공했으면 이전 화면으로 자동 전환
                                        pop(infoData)
                                    } else {
                                        // 실패
                                        Toast.makeText(context, "데이터 저장 실패", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                        }
                    ) {
                        Text("저장")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else if (photo.isNotEmpty()) {
                // 이미지 파일 경로가 있다면
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photo)
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 저장된 값이 없는 경우 리소스 이미지
                Image(
                    painter = painterResource(R.drawable.user_basic),
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                // permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }) {
                Text("Gallery App")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                try {
                    val photoFile = createImageFile()
                    tempFilePath = photoFile.absolutePath
                    cameraFileUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                    // 카메라 앱 실행
                    cameraLauncher.launch(cameraFileUri!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Text("Camera App")
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("phone") },
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )
        }
    }
}