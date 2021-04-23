/*
 *  Copyright 2021 Michał Wójcik
 */

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttClientSslConfig
import com.hivemq.client.mqtt.MqttClientState
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import groovy.json.JsonOutput
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMDecryptorProvider
import org.bouncycastle.openssl.PEMEncryptedKeyPair
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import java.security.KeyPair
import java.security.KeyStore
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class Mqtt {
    Mqtt3AsyncClient client

    boolean isConnected() {
        return client?.getState()?.equals(MqttClientState.CONNECTED)
    }

    void disconnect() {
        client.disconnect()
    }

    void connect(String serverUrl, String clientId, String userName, String password) {
        URI uri = new URI(serverUrl)
        client = MqttClient.builder()
                .identifier(clientId)
                .serverHost(uri.getHost())
                .serverPort(uri.getPort())
                .useMqttVersion3()
                .automaticReconnectWithDefaultConfig()
                .buildAsync()

        if (userName && password) {
            client.connectWith().simpleAuth().username(userName).password(password.getBytes()).applySimpleAuth()
        } else {
            client.connect()
        }
    }

    /*
    Using snippet from https://gist.github.com/jimrok/d25cb45b840f5a4ad700
     */
    void connect(LinkedHashMap<String, Object> map, Object serverUrl, Object clientId, def o3, def o4) {
        MqttClientSslConfig sslConfig  = getSocketFactory(map.caCertificate,
                map.clientCertificate, map.privateKey, "")
        URI uri = new URI(serverUrl)

        client = MqttClient.builder()
                .identifier(clientId)
                .serverHost(uri.getHost())
                .serverPort(uri.getPort())
                .useMqttVersion3()
                .automaticReconnectWithDefaultConfig()
                .sslConfig(sslConfig)
                .buildAsync()


//        log.info("starting connect the server..." + serverUrl)
        client.connect()
//        log.info("connected!")
    }

    void subscribe(String sub, int qos, def driver) {
        client.subscribeWith()
                .topicFilter(sub)
                .qos(MqttQos.AT_MOST_ONCE)
                .callback(publish -> driver.parse(new String(publish.getPayloadAsBytes())))
                .send()
    }

    Object parseMessage(Object o) {
        def result = [:]
        result.payload = o.toString()
        return result
    }

    private static MqttClientSslConfig getSocketFactory(final String caCrtFile,
                                                     final String crtFile, final String keyFile, final String password)
            throws Exception {
        Security.addProvider(new BouncyCastleProvider())

        CertificateFactory cf = CertificateFactory.getInstance("X.509")
        // load CA certificate
        X509Certificate caCert = getCaCert(caCrtFile, cf)
        // load client certificate
        X509Certificate cert = getCert(crtFile, cf)
        // load client private key
        KeyPair key = getKey(keyFile, password)
        // CA certificate is used to authenticate server
        TrustManagerFactory tmf = getTrustManagerFactory(caCert)
        // client key and certificates are sent to server so it can authenticate us
        KeyManagerFactory kmf = getKeyManagerFactory(cert, key, password)

        MqttClientSslConfig sslConfig = MqttClientSslConfig.builder()
                .keyManagerFactory(kmf)
                .trustManagerFactory(tmf)
                .protocols(Collections.singletonList("TLSv1.2"))
                .build()

        return sslConfig
    }

    private static X509Certificate getCert(String crtFile, CertificateFactory cf) {
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(crtFile.getBytes()))
        X509Certificate cert = null
        while (bis.available() > 0) {
            cert = (X509Certificate) cf.generateCertificate(bis)
        }
        cert
    }

    private static X509Certificate getCaCert(String caCrtFile, CertificateFactory cf) {
        X509Certificate caCert = null
        InputStream fis = new ByteArrayInputStream(caCrtFile.getBytes())
        BufferedInputStream bis = new BufferedInputStream(fis)

        while (bis.available() > 0) {
            caCert = (X509Certificate) cf.generateCertificate(bis)
        }
        caCert
    }

    private static KeyPair getKey(String keyFile, String password) {
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
        key
    }

    private static KeyManagerFactory getKeyManagerFactory(X509Certificate cert, KeyPair key, String password) {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null, null)
        ks.setCertificateEntry("certificate", cert)
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                new Certificate[]{cert})
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, password.toCharArray())
        kmf
    }

    private static TrustManagerFactory getTrustManagerFactory(X509Certificate caCert) {
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType())
        caKs.load(null, null)
        caKs.setCertificateEntry("ca-certificate", caCert)
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509")
        tmf.init(caKs)
        tmf
    }

    void send(String topic, Object payload) {
        client.publishWith()
                .topic(topic)
                .qos(MqttQos.AT_MOST_ONCE)
                .retain(true)
                .payload(JsonOutput.toJson(payload).getBytes())
                .send()
    }
}
