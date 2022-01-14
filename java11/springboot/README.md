# Requirements

1. Create a p12 file from the certificate, key and ca obtained from lean's dev portal
```bash
openssl pkcs12 -export -in cert.crt  -inkey key.pem  -certfile ca.pem -out lean.p12
```
2. When prompted add a password ex: 123456
3. Move new p12 file into resources folder
4. Update the file name and password in SSLCustomizer class
