package main

import (
	"crypto/tls"
	"crypto/x509"
	"flag"
	"io/ioutil"
	"log"
	"net/http"
)

var (
	// cert file MUST be a concatenation of certificate provided by Lean AND the certificate chain!!!
	certFile = flag.String("cert", "cert_and_cert_chain.crt", "Your application certificate provided by Lean concatenated with cert chain")
	keyFile  = flag.String("key", "key.pem", "Your private key attached to application certificate")
	caFile   = flag.String("CA", "ca.pem", "Lean's public certificate chain")
)

func main() {
	flag.Parse()

	// Load client cert
	cert, err := tls.LoadX509KeyPair(*certFile, *keyFile)
	if err != nil {
		log.Fatal(err)
	}

	// Load CA cert
	caCert, err := ioutil.ReadFile(*caFile)
	if err != nil {
		log.Fatal(err)
	}
	caCertPool := x509.NewCertPool()
	caCertPool.AppendCertsFromPEM(caCert)

	// Setup HTTPS client
	tlsConfig := &tls.Config{
		Certificates: []tls.Certificate{cert},
		RootCAs:      caCertPool,
	}
	tlsConfig.BuildNameToCertificate()
	transport := &http.Transport{TLSClientConfig: tlsConfig}
	client := &http.Client{Transport: transport}

	// Please change the end point as required.
	res, err := client.Get("https://api.leantech.me/customers/v1")
	if err != nil {
		log.Fatal(err)
	}
	contents, err := ioutil.ReadAll(res.Body)
	log.Print(string(contents))
 
}
