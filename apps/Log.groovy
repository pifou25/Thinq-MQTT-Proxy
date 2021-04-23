import java.time.LocalDateTime

/*
 *  Copyright 2021 Michał Wójcik
 */
class Log {
    void error(Object message) {
        println(LocalDateTime.now().toString() + ":" + message.toString())
    }
    void warn(Object message) {
        println(LocalDateTime.now().toString() + ":" + message.toString())
    }
    void info(Object message) {
        println(LocalDateTime.now().toString() + ":" + message.toString())
    }
    void debug(Object message) {
        println(LocalDateTime.now().toString() + ":" + message.toString())
    }
    void trace(Object message) {
        println(LocalDateTime.now().toString() + ":" + message.toString())
    }

}
