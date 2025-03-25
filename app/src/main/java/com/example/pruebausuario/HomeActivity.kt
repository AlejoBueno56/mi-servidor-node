package com.example.pruebausuario

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.content.Context
import android.content.ClipboardManager
import android.content.ClipData
import android.widget.Toast


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}
val DarkBlue = Color(0xFF265CAF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf("Inicio") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Menú", fontSize = 22.sp, color = Color.Blue, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                DrawerOption("Planes", Icons.Filled.Devices) { currentScreen = "Planes" }
                DrawerOption("Paga tu factura", Icons.Filled.Payment) { currentScreen = "Paga tu factura" }
                DrawerOption("¡Quienes Somos?", Icons.Filled.CoPresent) { currentScreen = "¿Quienes somos?" }
                DrawerOption("Cobertura", Icons.Filled.LocationOn) { currentScreen = "Cobertura" }
                DrawerOption("SpeedTest", Icons.Filled.Speed) { currentScreen = "SpeedTest" }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Menú", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú", tint =DarkBlue)
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/3024538585"))
                        context.startActivity(intent)
                    },
                    containerColor = Color.Green,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Filled.Whatsapp, contentDescription = "WhatsApp", tint = Color.White)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentScreen) {
                    "Planes" -> PlansScreen()
                    "Paga tu factura" -> BankTransferScreen()
                    "¿Quienes somos?" -> AboutUsScreen()
                    "Cobertura" -> Text("Pantalla de Reportes", fontSize = 24.sp, color = DarkBlue)
                    "SpeedTest" -> SpeedTestScreen()
                    else -> {
                        PromoCard()
                        Image(
                            painter = painterResource(id = R.drawable.imagen1),
                            contentDescription = "Imagen de promoción",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FiberOpticAdvantages()
                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.imagen3),
                            contentDescription = "Otra imagen",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerOption(text: String, icon: ImageVector, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, tint = DarkBlue )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 18.sp, color = DarkBlue)
        }
    }
}

@Composable
fun SpeedTestScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f) // Mantiene relación de aspecto horizontal
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    settings.userAgentString =
                        "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Mobile Safari/537.36"
                    loadUrl("https://vconexiontest.fireprobe.net/")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun PromoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Disfruta el año con nosotros.",
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "¡Inicia el año con nosotros! En este nuevo año disfruta de un 25% de descuento en nuestro servicio de internet...",
                color= Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun FiberOpticAdvantages() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ventajas de tener acceso a internet por fibra óptica",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tu conexión es más rápida: La transmisión de datos a través de fibra óptica ocurre a velocidades elevadas.\n\n" +
                        "Tu ancho de banda es mayor: Esto permite alcanzar velocidades superiores en tu conexión a Internet.\n\n" +
                        "La fibra óptica minimiza las interferencias: Las posibilidades de experimentar interferencias en tu conexión son significativamente menores.",
                color=Color.White,
                textAlign = TextAlign.Start
            )
        }
    }
}
@Composable
fun AboutUsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título Principal
        Text(
            text = "Acerca de Nosotros",
            fontSize = 28.sp,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Descripción General
        Text(
            text = "En Vconexion, nos dedicamos a brindarte la mejor experiencia en conectividad y tecnología para tu hogar. Desde Internet rápido y confiable hasta soluciones de domótica y seguridad, estamos aquí para hacer tu vida más fácil y segura.",
            fontSize = 18.sp,
            color = Color.Black,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Tarjeta de Misión
        InfoCard(
            title = "Misión",
            description = "Vconexión es una empresa que presta el servicio en sistemas de comunicación por fibra óptica, utilizando equipos y redes de última tecnología, brindando a sus clientes una excelente conectividad, aportando a su crecimiento tecnológico, competitivo y económico en su región y alrededores."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de Visión
        InfoCard(
            title = "Visión",
            description = "En el 2025 Vconexión será reconocida en la región y grupos de interés como una empresa en constante crecimiento y expansión, referente en aplicación de nuevas tecnologías de comunicación por fibra óptica, una empresa honesta y transparente que ofrece a sus clientes un portafolio de soluciones integrales para sus comunicaciones en red contribuyendo de esta forma a su crecimiento y posicionamiento en el mercado."
        )
    }
}

@Composable
fun InfoCard(title: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                color = Color(0xFFCFD7E8),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Justify
            )
        }
    }
}
@Composable
fun PlanCard(
    title: String,
    speed: String,
    price: String,
    features: List<String>,
    buttonColor: Color = Color(0xFF007BFF)
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$speed MB", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00008B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = price, fontSize = 16.sp, color = Color.Gray)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            features.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Green)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = it, fontSize = 14.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val message = "Me interesa el $title"
                    val phoneNumber = "3024538585" // Número de WhatsApp
                    val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text(text = "Contáctanos", color = Color.White)
            }
        }
    }
}


@Composable
fun PlansScreen() {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(listOf(
            PlanData("Plan Familiar", "50", "$65,000 COP", listOf("Ideal para navegación básica", "Calidad SD", "Encriptación avanzada")),
            PlanData("Plan Preferencial", "100", "$100,000 COP", listOf("Ideal para múltiples dispositivos", "Calidad HD", "Soporte técnico")),
            PlanData("Plan Mejorado", "120", "$140,000 COP", listOf(" Simetria en la velocidad", "Calidad HD", "Soporte técnico")),
            PlanData("Plan Ultra", "150", "$180,000 COP", listOf(" Simetria en la velocidad", " Latencia optimizada para una experiencia de videojuego online", "Soporte técnico")),
            PlanData("Plan Empresarial", "400", "$250,000 COP", listOf("Streaming HD", "Baja latencia", "Soporte técnico prioritario"))
        )) { plan ->
            PlanCard(
                title = plan.title,
                speed = plan.speed,
                price = plan.price,
                features = plan.features
            )
        }
    }
}

data class PlanData(val title: String, val speed: String, val price: String, val features: List<String>)

@Composable
fun BankTransferScreen() {
    val context = LocalContext.current
    val accountNumber = "47600000740"
    val bankName = "Bancolombia"
    val accountType = "Ahorros"
    val nit = "901398501"
    val companyName = "VConexion SAS"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Realiza una transferencia a la siguiente cuenta:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Banco: $bankName", fontSize = 16.sp)
        Text("Tipo de cuenta: $accountType", fontSize = 16.sp)
        Text("Número de cuenta: $accountNumber", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("NIT: $nit", fontSize = 16.sp)
        Text("Titular: $companyName", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Cuenta bancaria", accountNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Número de cuenta copiado", Toast.LENGTH_SHORT).show()
        }) {
            Text("Copiar número de cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pse.com.co"))
            context.startActivity(intent)
        }) {
            Text("Pagar con PSE")
        }
    }
}
