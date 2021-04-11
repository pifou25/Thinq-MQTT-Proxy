/*
 *  Copyright 2021 Michał Wójcik
 */
class Device {
    def deviceNetworkId
    def meta = [:]
    def displayName = "TODO"

    def parent
    Log log
    def logLevel
    Device device = this
    Interfaces interfaces
    boolean logDescText
    def location = [timeZone: TimeZone.default]

    void sendEvent(LinkedHashMap<String, Object> eventMap) {
        log.info(eventMap)
        interfaces.pubMqtt.send("thinq/" + deviceNetworkId.replace("thinq:", "") + "/event/" + eventMap.name, eventMap.value)
    }

    void pauseExecution(int timeInMs) {

    }

    void runIn(int timeInSeconds, String method) {
        log.info("Sleeping for $timeInSeconds before calling $method")
        sleep(timeInSeconds * 1000)
        "${method}"()
    }

    void updateDataValue(String key, String value) {
        meta[key] = value
    }

    String getDataValue(String key) {
        return meta[key]
    }

    Boolean hasCapability(String capability) {
        return true
    }

    void removeDataValue(String key) {
        meta.remove(key)
    }

    String currentValue(String s) {
        null
    }

    static String getTemperatureScale() {
        "C"
    }

    static def fahrenheitToCelsius(def temp) {
        null
    }

    static def celsiusToFahrenheit(def temp) {
        null
    }


}
