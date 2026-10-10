package eduardocodigo0.github.com.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eduardocodigo0.github.com.data.Category
import eduardocodigo0.github.com.data.CategoryLayoutType
import eduardocodigo0.github.com.data.MenuItem
import java.util.UUID

@Composable
fun EditItemDialog(
    item: MenuItem?,
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (MenuItem) -> Unit
) {
    var number by remember { mutableStateOf(item?.number ?: "") }
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var singlePriceStr by remember { mutableStateOf(if (item != null && item.singlePrice > 0) item.singlePrice.toString() else "") }

    val pricesByColumn = remember {
        mutableStateMapOf<String, String>().apply {
            category.columns.forEach { colName ->
                val valDouble = item?.pricesByColumn?.get(colName)
                put(colName, if (valDouble != null && valDouble > 0) valDouble.toString() else "")
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Novo Item em ${category.name}" else "Editar Item") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                if (category.layoutType == CategoryLayoutType.LANCHES) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = number,
                            onValueChange = { number = it },
                            label = { Text("Nº") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome do Lanche") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Ingredientes / Descrição") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = singlePriceStr,
                        onValueChange = { singlePriceStr = it.replace(',', '.') },
                        label = { Text("Preço (R$)") },
                        placeholder = { Text("Ex: 22.00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                } else if (category.layoutType == CategoryLayoutType.DRINKS_SINGLE_PRICE) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome da Bebida") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = singlePriceStr,
                        onValueChange = { singlePriceStr = it.replace(',', '.') },
                        label = { Text("Preço (R$)") },
                        placeholder = { Text("Ex: 6.00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                } else {
                    // Multi-price columns
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome da Bebida / Sabor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preços por Tamanho/Volume (R$):", modifier = Modifier.padding(bottom = 6.dp))
                    category.columns.forEach { colName ->
                        val currentVal = pricesByColumn[colName] ?: ""
                        OutlinedTextField(
                            value = currentVal,
                            onValueChange = { pricesByColumn[colName] = it.replace(',', '.') },
                            label = { Text(colName) },
                            placeholder = { Text("Ex: 7.00") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val singlePrice = singlePriceStr.toDoubleOrNull() ?: 0.0
                        val pricesMap = mutableMapOf<String, Double>()
                        pricesByColumn.forEach { (col, valStr) ->
                            val parsed = valStr.toDoubleOrNull()
                            if (parsed != null && parsed > 0) {
                                pricesMap[col] = parsed
                            }
                        }

                        val newItem = MenuItem(
                            id = item?.id ?: UUID.randomUUID().toString(),
                            categoryId = category.id,
                            number = number,
                            name = name,
                            description = description,
                            singlePrice = singlePrice,
                            pricesByColumn = pricesMap
                        )
                        onConfirm(newItem)
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
