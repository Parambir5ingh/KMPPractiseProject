package org.prm.drica.navigation.settings

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual object ExportAppData {
    @OptIn(ExperimentalForeignApi::class)
    actual fun exportAppData(data: String) {
        println("CALLED ACTUAL -IOS- METHOD USING EXPECT IN COMMON MAIN : $data")

        val dateFormatter = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd"
        }
        val today = dateFormatter.stringFromDate(NSDate())

        // 📄 File name with date
        val fileName = "dricaAppData_$today.txt"

        val tempDir = NSTemporaryDirectory()
        val filePath = tempDir + fileName

        val nsString = data as NSString
        nsString.writeToFile(
            filePath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )

        val fileUrl = NSURL.fileURLWithPath(filePath)

        val activityController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null
        )

        val rootVC = UIApplication.sharedApplication
            .keyWindow
            ?.rootViewController

        rootVC?.presentViewController(
            activityController,
            animated = true,
            completion = null
        )
    }
}