/*
 *  Copyright 2021 Michał Wójcik
 */
import groovy.json.JsonOutput
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMDecryptorProvider
import org.bouncycastle.openssl.PEMEncryptedKeyPair
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import java.security.KeyPair
import java.security.KeyStore
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class Mqtt {
    MqttClient client

    boolean isConnected() {
        return client?.isConnected()
    }

    void disconnect() {
        client.disconnect()
    }

    void connect(Object serverUrl, Object clientId, String userName, String password) {
        client = new MqttClient(serverUrl, clientId)
        MqttConnectOptions options = new MqttConnectOptions()
        options.setConnectionTimeout(60)
        options.setKeepAliveInterval(60)
        options.automaticReconnect = true
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1)

        if (userName && password) {
            options.setUserName(userName)
            options.setPassword(password.toCharArray())
        }
//        log.info("starting connect the server..." + serverUrl)
        client.connect(options)
//        log.info("connected!")
    }

    /*
    Using snippet from https://gist.github.com/jimrok/d25cb45b840f5a4ad700
     */
    void connect(LinkedHashMap<String, Object> map, Object serverUrl, Object clientId, def o3, def o4) {
        client = new MqttClient(serverUrl, clientId)
        MqttConnectOptions options = new MqttConnectOptions()
        options.setConnectionTimeout(60)
        options.setKeepAliveInterval(60)
        options.automaticReconnect = true
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1)


        SSLSocketFactory socketFactory = getSocketFactory(map.caCertificate,
                map.clientCertificate, map.privateKey, "")
        options.setSocketFactory(socketFactory)

//        log.info("starting connect the server..." + serverUrl)
        client.connect(options)
//        log.info("connected!")
    }

    void subscribe(String sub, int qos, def driver) {
        client.subscribe(sub, qos, (topic, msg) -> {
            driver.parse(msg)
        })
    }

    Object parseMessage(Object o) {
        def result = [:]
        result.payload = o.toString()
        return result
    }

    private static SSLSocketFactory getSocketFactory(final String caCrtFile,
                                                     final String crtFile, final String keyFile, final String password)
            throws Exception {
        Security.addProvider(new BouncyCastleProvider())

        // load CA certificate
        X509Certificate caCert = null


        InputStream fis = new ByteArrayInputStream(caCrtFile.getBytes())
        BufferedInputStream bis = new BufferedInputStream(fis)
        CertificateFactory cf = CertificateFactory.getInstance("X.509")

        while (bis.available() > 0) {
            caCert = (X509Certificate) cf.generateCertificate(bis)
            // System.out.println(caCert.toString());
        }

        // load client certificate
        bis = new BufferedInputStream(new ByteArrayInputStream(crtFile.getBytes()))
        X509Certificate cert = null
        while (bis.available() > 0) {
            cert = (X509Certificate) cf.generateCertificate(bis)
            // System.out.println(caCert.toString());
        }

        // load client private key
        PEMParser pemParser = new PEMParser(new StringReader(keyFile))
        Object object = pemParser.readObject()
        PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray())
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC")
        KeyPair key
        if (object instanceof PEMEncryptedKeyPair) {
            System.out.println("Encrypted key - we will use provided password")
            key = converter.getKeyPair(((PEMEncryptedKeyPair) object)
                    .decryptKeyPair(decProv))
        } else {
            System.out.println("Unencrypted key - no password needed")
            key = converter.getKeyPair((PEMKeyPair) object)
        }
        pemParser.close()

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType())
        caKs.load(null, null)
        caKs.setCertificateEntry("ca-certificate", caCert)
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509")
        tmf.init(caKs)

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null, null)
        ks.setCertificateEntry("certificate", cert)
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                new java.security.cert.Certificate[] { cert })
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, password.toCharArray())

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2") // tlsVersion
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null)

        return context.getSocketFactory()
    }

    void send(String topic, Object payload) {
        MqttMessage msg = new MqttMessage(JsonOutput.toJson(payload).getBytes())
        msg.setQos(0)
        msg.setRetained(true)
        client.publish(topic, msg)
    }
}
