package org.prm.drica.navigation.settings

import com.mohamedrejeb.calf.io.KmpFile

expect suspend fun readJsonFromFile(file: KmpFile): String