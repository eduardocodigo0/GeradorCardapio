package eduardocodigo0.github.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import eduardocodigo0.github.com.ui.HomeScreen
import eduardocodigo0.github.com.ui.MenuViewModel
import eduardocodigo0.github.com.ui.PdfViewerScreen
import eduardocodigo0.github.com.ui.theme.GeradorCardapioTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private val viewModel: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeradorCardapioTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MenuViewModel) {
    var pdfFileToShow by remember { mutableStateOf<File?>(null) }

    if (pdfFileToShow != null && pdfFileToShow!!.exists()) {
        PdfViewerScreen(
            pdfFile = pdfFileToShow!!,
            onBack = { pdfFileToShow = null }
        )
    } else {
        HomeScreen(
            viewModel = viewModel,
            onNavigateToPdfViewer = { file ->
                pdfFileToShow = file
            }
        )
    }
}
