package ai.zenkai.zenkai

import kotlinx.coroutines.experimental.Unconfined
import org.zenkai.common.UI

abstract class BaseUnitTest {
    init {
        UI = Unconfined
    }
}