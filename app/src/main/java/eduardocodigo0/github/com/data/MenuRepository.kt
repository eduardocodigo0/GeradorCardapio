package eduardocodigo0.github.com.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class MenuRepository(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val file: File
        get() = File(context.filesDir, "menu_data.json")

    private val _menuDataFlow = MutableStateFlow(MenuData())
    val menuDataFlow: StateFlow<MenuData> = _menuDataFlow.asStateFlow()

    suspend fun loadMenuData(): MenuData = withContext(Dispatchers.IO) {
        if (!file.exists() || file.length() == 0L) {
            val initial = seedDefaultData()
            saveMenuDataInternal(initial)
            _menuDataFlow.value = initial
            return@withContext initial
        }
        return@withContext try {
            val text = file.readText()
            val data = json.decodeFromString<MenuData>(text)
            _menuDataFlow.value = data
            data
        } catch (e: Exception) {
            e.printStackTrace()
            val initial = seedDefaultData()
            saveMenuDataInternal(initial)
            _menuDataFlow.value = initial
            initial
        }
    }

    suspend fun saveMenuData(data: MenuData) = withContext(Dispatchers.IO) {
        saveMenuDataInternal(data)
        _menuDataFlow.value = data
    }

    private fun saveMenuDataInternal(data: MenuData) {
        val text = json.encodeToString(data)
        file.writeText(text)
    }

    suspend fun updateHeader(header: HeaderConfig) {
        val current = _menuDataFlow.value
        saveMenuData(current.copy(header = header))
    }

    suspend fun saveCategory(category: Category) {
        val current = _menuDataFlow.value
        val existingIndex = current.categories.indexOfFirst { it.id == category.id }
        val updatedList = if (existingIndex >= 0) {
            current.categories.toMutableList().apply { set(existingIndex, category) }
        } else {
            current.categories + category
        }
        saveMenuData(current.copy(categories = updatedList))
    }

    suspend fun saveMenuItem(item: MenuItem) {
        val current = _menuDataFlow.value
        val existingIndex = current.items.indexOfFirst { it.id == item.id }
        val updatedList = if (existingIndex >= 0) {
            current.items.toMutableList().apply { set(existingIndex, item) }
        } else {
            current.items + item
        }
        saveMenuData(current.copy(items = updatedList))
    }

    suspend fun deleteMenuItem(itemId: String) {
        val current = _menuDataFlow.value
        val updatedItems = current.items.filter { it.id != itemId }
        saveMenuData(current.copy(items = updatedItems))
    }

    fun seedDefaultData(): MenuData {
        val catRefrigerantes = Category(
            id = "cat_refrigerantes",
            name = "Refrigerantes",
            layoutType = CategoryLayoutType.DRINKS_MULTI_PRICE,
            columns = listOf("LATA", "600ml", "1 LITRO", "2 LITROS"),
            orderIndex = 1
        )
        val catBebidas = Category(
            id = "cat_bebidas",
            name = "Bebidas",
            layoutType = CategoryLayoutType.DRINKS_SINGLE_PRICE,
            columns = emptyList(),
            orderIndex = 2
        )
        val catSucos = Category(
            id = "cat_sucos",
            name = "Sucos",
            layoutType = CategoryLayoutType.DRINKS_MULTI_PRICE,
            columns = listOf("500ml", "1 LITRO"),
            orderIndex = 3
        )
        val catLanches = Category(
            id = "cat_lanches",
            name = "Lanches",
            layoutType = CategoryLayoutType.LANCHES,
            columns = emptyList(),
            orderIndex = 4
        )

        val categories = listOf(catRefrigerantes, catBebidas, catSucos, catLanches)

        val items = mutableListOf<MenuItem>()

        // Refrigerantes
        items.add(
            MenuItem(
                id = "ref_coca", categoryId = catRefrigerantes.id, name = "Coca-Cola",
                pricesByColumn = mapOf("LATA" to 7.0, "600ml" to 9.0, "1 LITRO" to 12.0)
            )
        )
        items.add(
            MenuItem(
                id = "ref_fanta", categoryId = catRefrigerantes.id, name = "Fanta",
                pricesByColumn = mapOf("LATA" to 7.0, "600ml" to 9.0)
            )
        )
        items.add(
            MenuItem(
                id = "ref_sprite", categoryId = catRefrigerantes.id, name = "Sprite",
                pricesByColumn = mapOf("LATA" to 7.0, "600ml" to 9.0)
            )
        )
        items.add(
            MenuItem(
                id = "ref_guarana", categoryId = catRefrigerantes.id, name = "Guaraná Antarctica",
                pricesByColumn = mapOf("LATA" to 7.0, "2 LITROS" to 12.0)
            )
        )
        items.add(
            MenuItem(
                id = "ref_vencetex", categoryId = catRefrigerantes.id, name = "Vencetex",
                pricesByColumn = mapOf("2 LITROS" to 10.0)
            )
        )

        // Bebidas
        items.add(
            MenuItem(
                id = "beb_skol", categoryId = catBebidas.id, name = "Skol",
                singlePrice = 6.0
            )
        )
        items.add(
            MenuItem(
                id = "beb_brahma", categoryId = catBebidas.id, name = "Brahma",
                singlePrice = 6.0
            )
        )
        items.add(
            MenuItem(
                id = "beb_antarctica", categoryId = catBebidas.id, name = "Antarctica",
                singlePrice = 6.0
            )
        )
        items.add(
            MenuItem(
                id = "beb_cristal", categoryId = catBebidas.id, name = "Cristal",
                singlePrice = 6.0
            )
        )

        // Sucos
        val sucoSabores = listOf("Abacaxi", "Abacaxi c/ Hortelã", "Acerola", "Maracujá", "Laranja")
        sucoSabores.forEachIndexed { index, sabor ->
            items.add(
                MenuItem(
                    id = "suc_$index", categoryId = catSucos.id, name = sabor,
                    pricesByColumn = mapOf("500ml" to 8.0, "1 LITRO" to 15.0)
                )
            )
        }

        // Lanches (01 a 22)
        val lanchesData = listOf(
            Triple("01", "MISTO", "Presunto e Queijo" to 14.0),
            Triple("02", "BAURU", "Presunto, Queijo e Tomate" to 14.0),
            Triple("03", "PAULISTA", "Bife, Queijo, Tomate e Alface" to 14.0),
            Triple("04", "AMERICANO", "Bife, Presunto, Queijo, Ovo, Tomate e Alface" to 17.0),
            Triple("05", "CALABRESA EGG", "Presunto, Queijo, Bacon, Calabresa, Ovo, Tomate e Alface" to 30.0),
            Triple("06", "HOT DOG SIMPLES", "2 Salsichas, Milho, Batata palha, Tomate e Alface" to 18.0),
            Triple("07", "HOT DOG ESPECIAL", "2 Salsichas, Milho, Batata palha, Bacon, Calabresa, Tomate e Alface" to 22.0),
            Triple("08", "X-PALMITO", "Presunto, Queijo, Palmito, Tomate e Alface" to 28.0),
            Triple("09", "X-BURGUER", "2 Hambúrgueres, Queijo e Tomate" to 22.0),
            Triple("10", "X-SALADA", "2 Hambúrgueres, Queijo, Tomate e Alface" to 22.0),
            Triple("11", "X-EGG", "2 Hambúrgueres, Queijo, Ovo, Tomate e Alface" to 25.0),
            Triple("12", "X-BACON", "2 Hambúrgueres, Queijo, Bacon, Tomate e Alface" to 28.0),
            Triple("13", "X-BACON COM CALABRESA", "2 Hambúrgueres, Queijo, Bacon, Calabresa, Tomate e Alface" to 29.0),
            Triple("14", "X-SALAME", "2 Hambúrgueres, Queijo, Salame, Tomate e Alface" to 30.0),
            Triple("15", "X-CALABRESA", "2 Hambúrgueres, Queijo, Calabresa, Tomate e Alface" to 28.0),
            Triple("16", "X-TUDO PROMOÇÃO", "1 Hambúrguer, Presunto, Queijo, Bacon, Calabresa, Ovo, Tomate e Alface" to 32.0),
            Triple("17", "X-TUDO ESPECIAL", "2 Hambúrgueres, Presunto, Queijo, Bacon, Calabresa, Ovo, Tomate e Alface" to 34.0),
            Triple("18", "X-TUDO COM BIFE", "Bife, Presunto, Queijo, Bacon, Calabresa, Ovo, Tomate e Alface" to 48.0),
            Triple("19", "X-TUDO ABERTO", "2 Hambúrgueres, Presunto, Queijo, Bacon, Calabresa, 2 Ovos, Tomate e Alface" to 36.0),
            Triple("20", "AMERICANO ABERTO", "3 Bifes, Presunto, Queijo, Bacon, Calabresa, 2 Ovos, Tomate e Alface" to 48.0),
            Triple("21", "X-FRANGO DESFIADO", "Frango desfiado, Queijo, Catupiry, Milho, Tomate e Alface" to 34.0),
            Triple("22", "HAMBÚRGUER SIMPLES", "1 Hambúrguer, Queijo, Milho, Tomate e Alface" to 20.0)
        )

        lanchesData.forEach { (num, name, pair) ->
            items.add(
                MenuItem(
                    id = "lan_$num",
                    categoryId = catLanches.id,
                    number = num,
                    name = name,
                    description = pair.first,
                    singlePrice = pair.second
                )
            )
        }

        return MenuData(
            header = HeaderConfig(
                restaurantName = "Doka’s Lanches",
                phone1 = "99713-9143",
                phone2 = "99111-5722",
                paymentNote = "Aceitamos cartões Visa e Mastercard"
            ),
            categories = categories,
            items = items
        )
    }
}
