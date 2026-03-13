package org.prm.drica.navigation.settings

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfURL

@OptIn(ExperimentalForeignApi::class)
actual suspend fun readJsonFromFile(file: KmpFile): String {
    val url = file.url

    return NSString.stringWithContentsOfURL(
        url = url,
        encoding = NSUTF8StringEncoding,
        error = null
    )?.toString() ?: ""
}