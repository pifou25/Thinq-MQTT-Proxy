class Log {
    void error(GString message) {
        println(message)
    }
    void warn(GString message) {
        println(message)
    }
    void info(GString message) {
        println(message)
    }
    void debug(Object message) {
        println(message.toString())
    }
    void trace(GString message) {
        println(message)
    }
}
