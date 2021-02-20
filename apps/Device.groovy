class Device {
    def deviceNetworkId
    def meta = [:]
    def driver
    def displayName = "TODO"

    void updateDataValue(String key, String value) {
        meta[key] = value
    }

    void initialize() {
        driver.initialize()
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

    def processStateData(LinkedHashMap data) {
        driver.processStateData(data)
    }

    String currentValue(String s) {
        null
    }
}
