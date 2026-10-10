package eduardocodigo0.github.com.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eduardocodigo0.github.com.data.HeaderConfig

@Composable
fun EditHeaderDialog(
    header: HeaderConfig,
    onDismiss: () -> Unit,
    onConfirm: (HeaderConfig) -> Unit
) {
    var restaurantName by remember { mutableStateOf(header.restaurantName) }
    var phone1 by remember { mutableStateOf(header.phone1) }
    var phone2 by remember { mutableStateOf(header.phone2) }
    var paymentNote by remember { mutableStateOf(header.paymentNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cabeçalho e Pagamentos") },
        text = {
            Column {
                OutlinedTextField(
                    value = restaurantName,
                    onValueChange = { restaurantName = it },
                    label = { Text("Nome do Estabelecimento") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = phone1,
                    onValueChange = { phone1 = it },
                    label = { Text("Telefone 1") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = phone2,
                    onValueChange = { phone2 = it },
                    label = { Text("Telefone 2 (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = paymentNote,
                    onValueChange = { paymentNote = it },
                    label = { Text("Nota de Pagamento / Bandeiras") },
                    placeholder = { Text("Ex: Aceitamos cartões Visa e Mastercard") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(HeaderConfig(restaurantName, phone1, phone2, paymentNote))
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
