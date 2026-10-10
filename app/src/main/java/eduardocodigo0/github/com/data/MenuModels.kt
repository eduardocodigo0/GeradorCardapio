package eduardocodigo0.github.com.data

import kotlinx.serialization.Serializable

@Serializable
enum class CategoryLayoutType {
    LANCHES,
    DRINKS_MULTI_PRICE,
    DRINKS_SINGLE_PRICE
}

@Serializable
data class HeaderConfig(
    val restaurantName: String = "Doka’s Lanches",
    val phone1: String = "99713-9143",
    val phone2: String = "99111-5722",
    val paymentNote: String = "Aceitamos cartões Visa e Mastercard"
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val layoutType: CategoryLayoutType,
    val columns: List<String> = emptyList(),
    val orderIndex: Int
)

@Serializable
data class MenuItem(
    val id: String,
    val categoryId: String,
    val number: String = "",
    val name: String,
    val description: String = "",
    val singlePrice: Double = 0.0,
    val pricesByColumn: Map<String, Double> = emptyMap()
)

@Serializable
data class MenuData(
    val header: HeaderConfig = HeaderConfig(),
    val categories: List<Category> = emptyList(),
    val items: List<MenuItem> = emptyList()
)
