class Device {
    def deviceNetworkId
    def meta = [:]

    void updateDataValue(String key, String value) {
        meta[key] = value
    }

    void initialize() {

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
}
