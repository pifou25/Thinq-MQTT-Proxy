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
    }

    void pauseExecution(int time) {

    }

    void runIn(int integer, String s) {

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
