package eduardocodigo0.github.com.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eduardocodigo0.github.com.data.Category
import eduardocodigo0.github.com.data.CategoryLayoutType
import java.util.UUID

@Composable
fun EditCategoryDialog(
    category: Category?,
    existingCategoriesCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var layoutType by remember { mutableStateOf(category?.layoutType ?: CategoryLayoutType.LANCHES) }
    var columnsStr by remember { mutableStateOf(category?.columns?.joinToString(", ") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Nova Categoria" else "Editar Categoria") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Categoria") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tipo de Layout:")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    RadioButton(
                        selected = layoutType == CategoryLayoutType.LANCHES,
                        onClick = { layoutType = CategoryLayoutType.LANCHES }
                    )
                    Text("Lanches (Preço Único + Descrição)")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = layoutType == CategoryLayoutType.DRINKS_MULTI_PRICE,
                        onClick = { layoutType = CategoryLayoutType.DRINKS_MULTI_PRICE }
                    )
                    Text("Bebidas com Tamanhos/Volume")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = layoutType == CategoryLayoutType.DRINKS_SINGLE_PRICE,
                        onClick = { layoutType = CategoryLayoutType.DRINKS_SINGLE_PRICE }
                    )
                    Text("Bebidas Preço Único")
                }

                if (layoutType == CategoryLayoutType.DRINKS_MULTI_PRICE) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = columnsStr,
                        onValueChange = { columnsStr = it },
                        label = { Text("Colunas/Tamanhos (separados por vírgula)") },
                        placeholder = { Text("Ex: LATA, 600ml, 1 LITRO, 2 LITROS") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val columns = if (layoutType == CategoryLayoutType.DRINKS_MULTI_PRICE) {
                            columnsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        } else emptyList()

                        val newCategory = Category(
                            id = category?.id ?: UUID.randomUUID().toString(),
                            name = name,
                            layoutType = layoutType,
                            columns = columns,
                            orderIndex = category?.orderIndex ?: (existingCategoriesCount + 1)
                        )
                        onConfirm(newCategory)
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
