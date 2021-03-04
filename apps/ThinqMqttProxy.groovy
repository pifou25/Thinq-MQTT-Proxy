class ThinqMqttProxy {
    static void main(String[] args) {
        println "Starting Thinq Mqtt Proxy..."

        if (args.length != 1) {
            printHelp()
            System.exit(1)
        }
        switch (args[0]) {
            case "init" : doInit()
                break
            case "run" : doRun()
                break
            default: printHelp()
                System.exit(1)
        }
        System.exit(0)
    }

    static void printHelp() {
        println "Usage:"
        println "init"
        println "run"
    }

    static void doInit() {

    }

    static void doRun() {
        def integration = new ThinQ_Integration()
        integration.interfaces.pubMqtt.connect(integration.state.pubMqttServer, integration.state.pubClientId)
        integration.url = integration.state.prevUrl
        integration.prefDevices()
        println "Devices identified..."
        integration.installed()
        integration.state.save(integration.STATE_FILE)
        System.in.read()

        // TODO Shutdown
    }
}
