package eduardocodigo0.github.com.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eduardocodigo0.github.com.data.Category
import eduardocodigo0.github.com.data.HeaderConfig
import eduardocodigo0.github.com.data.MenuData
import eduardocodigo0.github.com.data.MenuItem
import eduardocodigo0.github.com.data.MenuRepository
import eduardocodigo0.github.com.pdf.PdfMenuGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class PdfGenerationState {
    object Idle : PdfGenerationState()
    object Generating : PdfGenerationState()
    data class Success(val pdfFile: File) : PdfGenerationState()
    data class Error(val message: String) : PdfGenerationState()
}

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MenuRepository(application)
    private val pdfGenerator = PdfMenuGenerator(application)

    val menuData: StateFlow<MenuData> = repository.menuDataFlow

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _pdfState = MutableStateFlow<PdfGenerationState>(PdfGenerationState.Idle)
    val pdfState: StateFlow<PdfGenerationState> = _pdfState.asStateFlow()

    init {
        viewModelScope.launch {
            val data = repository.loadMenuData()
            if (_selectedCategoryId.value == null && data.categories.isNotEmpty()) {
                _selectedCategoryId.value = data.categories.first().id
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun updateHeader(header: HeaderConfig) {
        viewModelScope.launch {
            repository.updateHeader(header)
        }
    }

    fun saveCategory(category: Category) {
        viewModelScope.launch {
            repository.saveCategory(category)
        }
    }

    fun saveMenuItem(item: MenuItem) {
        viewModelScope.launch {
            repository.saveMenuItem(item)
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
        }
    }

    fun generatePdf() {
        viewModelScope.launch {
            _pdfState.value = PdfGenerationState.Generating
                delay(1500)
            try {
                val outputDir = File(getApplication<Application>().cacheDir, "generated_pdfs")
                if (!outputDir.exists()) outputDir.mkdirs()
                val outputFile = File(outputDir, "Cardapio_DokasLanches.pdf")
                pdfGenerator.generatePdf(menuData.value, outputFile)
                _pdfState.value = PdfGenerationState.Success(outputFile)
            } catch (e: Exception) {
                e.printStackTrace()
                _pdfState.value = PdfGenerationState.Error(e.message ?: "Erro ao gerar PDF")
            }
        }
    }

    fun resetPdfState() {
        _pdfState.value = PdfGenerationState.Idle
    }
}
