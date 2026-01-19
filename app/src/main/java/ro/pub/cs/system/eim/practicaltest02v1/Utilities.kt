package ro.pub.cs.system.eim.practicaltest02v1

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.jvm.Throws

object Utilities {
    @JvmStatic
    @Throws(IOException::class)
    fun getReader(socket: Socket): BufferedReader {
        return BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getWriter(socket: Socket): PrintWriter {
        return PrintWriter(socket.getOutputStream(), true)
    }
}

