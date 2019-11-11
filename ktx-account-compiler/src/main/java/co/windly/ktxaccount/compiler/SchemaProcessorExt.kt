package co.windly.ktxaccount.compiler

import javax.annotation.processing.Messager
import javax.tools.Diagnostic.Kind.ERROR
import javax.tools.Diagnostic.Kind.NOTE

//region Error

internal fun Messager.errorMessage(message: () -> String) {
  printMessage(ERROR, message())
}

//endregion

//region Note

internal fun Messager.noteMessage(message: () -> String) {
  printMessage(NOTE, message())
}

//endregion
