package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

// Compose ve UI bileşenleri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

fun showToastMessage(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, UnstableApi::class)
@Composable
fun HobiesScreen(navController: NavController, sharedViewModel: SharedViewModel,categoryViewModel: ExploreViewModel = viewModel()) {
    val interests by categoryViewModel.categories

    val selectedInterests = remember { mutableStateListOf<PlaceTypeDto>() } // Matching type with PlaceType
    val backgroundImage: Painter = painterResource(id = R.drawable.hobies)

    var isHovered by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)) // blur
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Gezmeye doyamadığınız yerleri bizimle paylaşın!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = "İlgi alanlarınızı seçin (Birden fazla olabilir).",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxHeight(0.7f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(interests) { interest ->
                    val isSelected = interest in selectedInterests
                    Button(
                        onClick = {
                            if (isSelected) {
                                selectedInterests.remove(interest)
                            } else {
                                selectedInterests.add(interest)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.LightGray else Color.White,
                            contentColor = if (isSelected) Color.White else Color.Black
                        ),
                    ) {
                        Text(interest.placeTypeName)
                    }
                }
            }

            Button(
                onClick = {
                    val apiService = RetrofitClient.apiService
                    val request = UserPlaceTypeDto(
                        placeTypeNames = selectedInterests.map { it.placeTypeName }
                    )

                    apiService.addUserPlaceTypes(request).enqueue(object : Callback<AddPlaceTypeResponse> {
                        override fun onResponse(
                            call: Call<AddPlaceTypeResponse>,
                            response: Response<AddPlaceTypeResponse>
                        ) {
                            if (response.isSuccessful) {
                                showToastMessage(context, "Başarıyla kaydedildi!")
                                navController.navigate("explore")
                            } else {
                                // Hata kodunu ve mesajını loglayın
                                Log.e("API_ERROR", "Error Code: ${response.code()}, Message: ${response.message()}")
                                showToastMessage(context, "Kaydetme işlemi başarısız. Hata: ${response.code()}")
                            }
                        }


                        override fun onFailure(call: Call<AddPlaceTypeResponse>, t: Throwable) {
                            showToastMessage(context, "Hata oluştu: ${t.message}")
                        }
                    })
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHovered) Color(0xFF1C28E0) else Color(0xFF117ED0), // Hover ve normal renkler
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                isHovered = event.changes.any { it.pressed }
                            }
                        }
                    }
            ) {
                Text(text = "Haydi Başlayalım!")
            }
        }
    }
}