/*
 *  Copyright 2021 Michał Wójcik
 */
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
        def integration = new ThinQ_Integration()
        integration.prefMain()
        integration.generateKeyAndCSR()
        integration.state.save(integration.STATE_FILE)
    }

    static void doRun() {
        def integration = new ThinQ_Integration()
        integration.interfaces.pubMqtt.connect(integration.state.pubMqttServer, integration.state.pubClientId,
                integration.state.pubUserName, integration.state.pubPassword)
        integration.prefDevices()
        integration.state.save(integration.STATE_FILE)
        println "Devices identified..."
        integration.installed()
        integration.state.save(integration.STATE_FILE)
        System.in.read()

        // TODO Shutdown
    }
}
