# LG Thinq MQTT Proxy

This application can connect to LG Thinq infrastructure and register for the updates coming from smart LG devices. Received messages are converted into JSON payload and sent to your private MQTT server. This way you can enhance your home automation (like OpenHab or HomeAssistant) thanks to received notifications from your fridge or washing machine.

## Credits

This application is based on the code written by `dcmeglio` at https://github.com/dcmeglio/hubitat-thinq .
He made a great job by finding all the details necessary to connect to the LG's MQTT servers and register for the messages coming from the LG's appliances.

My goal was to keep the code as close to the original one, to make any updates with upstream easier.
I have implemented by myself all the missing pieces which are essentials for this code to work properly.

## Prerequisites

1. Java 8
1. Groovy 3.0.7
1. Apache Maven 3.6.3

## Building

```shell
mvn clean package
```

## First run

```
java -jar ./target/thinq-mqtt-proxy.jar run
```

## Running

```
java -jar ./target/thinq-mqtt-proxy.jar run
```

## Items to do

1. MQTT reconnects
1. Easier setup
1. Better format of the messages after conversion
1. Provide documentation on how to run it as service
1. Friendly names for the MQTT topic (i.e. `washer` instead of long id string)
1. Code cleanup and hardening
1. Tests
