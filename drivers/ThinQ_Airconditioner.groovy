import groovy.util.logging.Slf4j

/**
 *  LG Airconditioner
 *
 *  Copyright 2021 Michał Wójcik
 *
 */
@Slf4j
class ThinQ_Airconditioner extends Device {


def processStateData(data) {
    logger("debug", "processStateData(${data})")

    // TODO Implement processing of the data

    if (parent.checkValue(data,'Error')) {
      sendEvent(name: "error", value: data["Error"].toLowerCase())
    }

}

}