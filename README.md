# LG Thinq MQTT Proxy

This application can connect to LG Thinq infrastructure and register for the updates coming from smart LG devices. Received messages are converted into JSON payload and sent to your private MQTT server. This way you can enhance your home automation (like OpenHab or HomeAssistant) thanks to received notifications from your fridge or washing machine.

## Credits

This application is based on the code written by `dcmeglio` at https://github.com/dcmeglio/hubitat-thinq .
He made a great job by finding all the details necessary to connect to the LG's MQTT servers and register for the messages coming from the LG's appliances.

My goal was to keep the code as close to the original one, to make any updates with upstream easier.
I have implemented a thin layer which is providing necessary infrastructure for this code to work as standalone application.

## Prerequisites

1. Java 8
1. Apache Maven 3.6.3

## Building

```shell
mvn clean package
```

## First run

Copy `state-example.json` to `state.json` and correct your language, region and local MQTT server settings.

```
java -jar ./target/thinq-mqtt-proxy.jar init
```

## Running

```
java -jar ./target/thinq-mqtt-proxy.jar run
```

## Items to do

1. MQTT reconnects
1. Error handling   
1. Easier setup
1. Better format of the messages after conversion
1. Provide documentation on how to run it as service
1. Friendly names for the MQTT topic (i.e. `washer` instead of long id string)
1. Code cleanup and hardening
1. Tests

## Disclaimer

* As I have only `v2` devices, I have not corrected the code to handle `v1` ones. Original code is written to handle them, so adding support should be doable.
* I have tested only washer and fridge, because I don't have more Thinq enabled devices. There might be some fixes necessary for other devices.
* This application is a hobby project for other hobbyists, provided as-is. I am not responsible for any issues caused by its use.