package eduardocodigo0.github.com

import eduardocodigo0.github.com.data.HeaderConfig
import org.junit.Assert.assertNotNull
import org.junit.Test

class MenuGeneratorUnitTest {

    @Test
    fun testHeaderConfig() {
        val header = HeaderConfig("Doka's Lanches", "99713-9143", "99111-5722")
        assertNotNull(header)
    }
}
