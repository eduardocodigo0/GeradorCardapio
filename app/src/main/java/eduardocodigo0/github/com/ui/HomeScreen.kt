package eduardocodigo0.github.com.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eduardocodigo0.github.com.data.Category
import eduardocodigo0.github.com.data.CategoryLayoutType
import eduardocodigo0.github.com.data.MenuItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MenuViewModel,
    onNavigateToPdfViewer: (java.io.File) -> Unit
) {
    val menuData by viewModel.menuData.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val pdfState by viewModel.pdfState.collectAsState()

    var showEditHeaderDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

    var showEditItemDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MenuItem?>(null) }

    val categories = menuData.categories.sortedBy { it.orderIndex }
    val currentCategory = categories.firstOrNull { it.id == selectedCategoryId } ?: categories.firstOrNull()

    // Handle PDF state changes
    when (val state = pdfState) {
        is PdfGenerationState.Success -> {
            onNavigateToPdfViewer(state.pdfFile)
            viewModel.resetPdfState()
        }
        else -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = menuData.header.restaurantName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.height(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val phoneText = if (menuData.header.phone2.isNotBlank())
                                "${menuData.header.phone1} | ${menuData.header.phone2}"
                            else menuData.header.phone1
                            Text(text = phoneText, fontSize = 12.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showEditHeaderDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Cabeçalho")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (currentCategory != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        itemToEdit = null
                        showEditItemDialog = true
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Novo Item") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // PDF Generation Action Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = pdfState !is PdfGenerationState.Generating) {
                            viewModel.generatePdf()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (pdfState is PdfGenerationState.Generating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GERAR PDF DO CARDÁPIO (2 PÁGINAS A4)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Category Tabs
            if (categories.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOfFirst { it.id == currentCategory?.id }.coerceAtLeast(0),
                    edgePadding = 12.dp
                ) {
                    categories.forEach { category ->
                        Tab(
                            selected = category.id == currentCategory?.id,
                            onClick = { viewModel.selectCategory(category.id) },
                            text = { Text(category.name, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            // Items List
            if (currentCategory != null) {
                val categoryItems = menuData.items.filter { it.categoryId == currentCategory.id }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentCategory.name} (${categoryItems.size} itens)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(onClick = {
                            categoryToEdit = currentCategory
                            showEditCategoryDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Categoria")
                        }
                    }
                }

                if (categoryItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum item nesta categoria.\nClique em 'Novo Item' para adicionar.",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        items(categoryItems) { item ->
                            MenuItemCard(
                                item = item,
                                category = currentCategory,
                                onEdit = {
                                    itemToEdit = item
                                    showEditItemDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteMenuItem(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showEditHeaderDialog) {
        EditHeaderDialog(
            header = menuData.header,
            onDismiss = { showEditHeaderDialog = false },
            onConfirm = { updatedHeader ->
                viewModel.updateHeader(updatedHeader)
                showEditHeaderDialog = false
            }
        )
    }

    if (showEditCategoryDialog) {
        EditCategoryDialog(
            category = categoryToEdit,
            existingCategoriesCount = categories.size,
            onDismiss = { showEditCategoryDialog = false },
            onConfirm = { category ->
                viewModel.saveCategory(category)
                showEditCategoryDialog = false
            }
        )
    }

    if (showEditItemDialog && currentCategory != null) {
        EditItemDialog(
            item = itemToEdit,
            category = currentCategory,
            onDismiss = { showEditItemDialog = false },
            onConfirm = { item ->
                viewModel.saveMenuItem(item)
                showEditItemDialog = false
            }
        )
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val title = if (item.number.isNotBlank()) "${item.number} ${item.name}" else item.name
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (category.layoutType == CategoryLayoutType.DRINKS_MULTI_PRICE) {
                    val pricesText = category.columns.mapNotNull { col ->
                        val price = item.pricesByColumn[col]
                        if (price != null && price > 0) "$col: R$ ${String.format(Locale("pt", "BR"), "%.2f", price)}" else null
                    }.joinToString(" | ")
                    Text(
                        text = if (pricesText.isNotBlank()) pricesText else "Sem preços cadastrados",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Text(
                        text = "R$ ${String.format(Locale("pt", "BR"), "%.2f", item.singlePrice)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                }
            }
        }
    }
}
