package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import androidx.compose.material3.CircularProgressIndicator
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.theme.BitirmeProjesi1Theme
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
data class ForgotPasswordRequest(val email: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false)
    }
    val backgroundImage: Painter = painterResource(id = R.drawable.password)


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize() // Arka plan resmini ekranın tamamına yay
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            
        ) {
            Text(
                text = "Şifremi Sıfırla",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.White
                )
            )


            // E-posta adresi alanı
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email Adresi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            // Yükleniyor durumu
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                // Şifre sıfırlama butonu
                Button(
                    onClick = {
                        if (email.value.isNotEmpty()) {
                            isLoading = true

                            // Şifre sıfırlama isteği
                            val forgotPasswordRequest = ForgotPasswordRequest(email.value)

                            RetrofitClient.apiService.forgotPassword(forgotPasswordRequest).enqueue(object : Callback<ForgotPasswordResponse> {
                                override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        val tcKimlik = response.body()?.tckimlik
                                        val message = response.body()?.message
                                        if (!tcKimlik.isNullOrEmpty()) {
                                            // TC Kimlik numarası alındı, şifreyi güncellemek için çağrı yapılacak
                                            RetrofitClient.apiService.forgotPassword(forgotPasswordRequest).enqueue(object : Callback<ForgotPasswordResponse> {
                                                override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                                                    isLoading = false
                                                    if (response.isSuccessful) {
                                                        val tcKimlik = response.body()?.tckimlik

                                                        if (!tcKimlik.isNullOrEmpty()) {
                                                            Toast.makeText(context, "Şifreniz başarıyla sıfırlandı ve yeni şifreniz TC Kimlik Numarası olarak ayarlandı.", Toast.LENGTH_SHORT).show()
                                                            navController.navigate("login") // Giriş ekranına yönlendir
                                                        } else {
                                                            Toast.makeText(context, "TC Kimlik numarası alınamadı.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        val errorBody = response.errorBody()?.string()
                                                        Log.e("ForgotPasswordError", "Hata Kodu: ${response.code()}, Mesaj: $errorBody")
                                                        Toast.makeText(context, "Şifre sıfırlama işlemi başarısız: $errorBody", Toast.LENGTH_SHORT).show()
                                                    }
                                                }

                                                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                                                    isLoading = false
                                                    Log.e("ForgotPasswordError", "Bağlantı Hatası: ${t.message}")
                                                    Toast.makeText(context, "Bağlantı Hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            })

                                        } else {
                                            Toast.makeText(context, "Şifre sıfırlama başarısız: TC Kimlik numarası alınamadı.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        Log.e("ForgotPasswordError", "Hata Kodu: ${response.code()}, Mesaj: $errorBody")
                                        Toast.makeText(context, "Şifre sıfırlama işlemi başarısız: $errorBody", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                                    isLoading = false
                                    Log.e("ForgotPasswordError", "Bağlantı Hatası: ${t.message}")
                                    Toast.makeText(context, "Bağlantı Hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })

                        } else {
                            Toast.makeText(context, "Lütfen e-posta adresinizi girin.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Şifremi Sıfırla")
                }
            }
        }
    }
}
