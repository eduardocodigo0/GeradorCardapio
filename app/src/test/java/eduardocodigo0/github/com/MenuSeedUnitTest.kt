package eduardocodigo0.github.com

import eduardocodigo0.github.com.data.CategoryLayoutType
import eduardocodigo0.github.com.data.HeaderConfig
import eduardocodigo0.github.com.data.MenuData
import eduardocodigo0.github.com.data.MenuItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MenuSeedUnitTest {

    @Test
    fun testHeaderDefaults() {
        val header = HeaderConfig()
        assertEquals("Doka’s Lanches", header.restaurantName)
        assertEquals("99713-9143", header.phone1)
        assertEquals("99111-5722", header.phone2)
    }

    @Test
    fun testMenuDataStructure() {
        val menuData = MenuData()
        assertNotNull(menuData.header)
        assertNotNull(menuData.categories)
        assertNotNull(menuData.items)
    }
}
