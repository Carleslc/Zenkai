package ai.zenkai.zenkai.common

import ai.zenkai.zenkai.presentation.messages.MessagesPresenter
import android.content.pm.PackageManager

private typealias PermissionCallback = (Boolean) -> Unit

object AndroidPermissions {
    
    object Code {
        val RECORD_AUDIO = 1
    }
    
    fun onRequestMicrophone(presenter: MessagesPresenter, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, permissions, grantResults, mapOf(Code.RECORD_AUDIO to presenter::onMicrophonePermission))
    }
    
    private fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, checking: Map<Int, PermissionCallback>) {
        if (permissions.isNotEmpty()) { // Otherwise is cancelled
            val allowed = grantResults[0] == PackageManager.PERMISSION_GRANTED
            checking.getOrDefault(requestCode, {}).invoke(allowed)
        }
    }
    
}