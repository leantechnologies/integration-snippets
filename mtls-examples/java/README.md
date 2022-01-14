**How to:**
- Create a p12 file from the certificates:
- Go to Lean's dev portal
- Select "Integration" from the left side
- Download the Certification Authority give it a name, ex: ca
- Download the public/private keys zip file by clicking on "Download New Certificate"
- Extract the zip file, rename the private key to key.pem, rename the public key to cert.crt
- Copy all the certificates in a new folder, name it /certs
- Run the following command, you will be asked to put on a password, please remember it
```bash
     * openssl pkcs12 -export -in cert.crt  -inkey key.pem  -certfile ca.pem -out yourp12filename.p12
```
- A new file will be generated called yourp12filename.p12, move it to the resources folder
- Replace the "p12.filename", "p12.password", "app.token" dummy properties with your own.
- Run the main class, you should receive "HTTP/1.1 200" and a valid response in the console.
