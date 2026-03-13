package org.prm.drica

import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication

actual fun showMessage(message: String) {
    val alert = UIAlertController.alertControllerWithTitle(
        title = "Message",
        message = message,
        preferredStyle = UIAlertControllerStyleAlert
    )
    alert.addAction(
        UIAlertAction.actionWithTitle(
        title = "OK",
        style = UIAlertActionStyleDefault,
        handler = null
    ))

    // Get top UIViewController
    val keyWindow = UIApplication.sharedApplication.keyWindow
    val topController = keyWindow?.rootViewController
    topController?.presentViewController(alert, animated = true, completion = null)
}