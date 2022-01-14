
# Lean-dev-client-template

This project is written in java to access Lean API. It contains a simple http client and a spring boot application that load the required certificates
(available in [dev.leantech.me](https://dev.leantech.me)) to make a call to Lean API.

## Requirements

* Maven
* Java 8 (minimum)

## Usage

If you want to run this template, you will need a set of credentials from Lean Technologies.
1. Head to [dev.leantech.me](https://dev.leantech.me) and create an account (if you have not done it yet). This will give you access to Lean sandbox API.
2. Once you are ready, reach out to [devsupport@leantech.me](mailto:devsupport@leantech.me) to ask access to the production environment.
3. Once you have production access:
   1. Log in to [dev.leantech.me](https://dev.leantech.me)
   2. Go to `Integration` tab, on the left hand side menu:
      1. On the `Certificates` section, click on `Generate new certificate`, which will prompt you to save them. Do it in a new folder, name it `/certificates`.
         1. Unzip the download file, which should contain 2 files (one with `crt` and other with `pem` extensions)
         2. Remember where these are downloaded as they will be needed for the next step.
      2. Still on `Certificates` section, `Download` the `Certificate chain`
         1. Again, download the file and unzip it.

At this point, there are two options:
1. Create the `keystore` required to make a secure connection in memory (`src/main/java/me/lean/tech/dev/apacheclient/MtlsUsingApacheClient.java`), using a [helper library](https://github.com/Hakky54/sslcontext-kickstart) to read lean certificates:
   1. In the [application.properties](src/main/resources/application.properties),
      1. replace the path of certificates `<ABSOLUTE_FOLDER_PATH_WHERE_CERTIFICATES_ARE_STORED>` with the absolute path of `certs` folder
      2. replace the mock certificates filenames:
         1. `lean.certificate.name` is the filename ending with `crt`
         2. `lean.private.key.name` is the filename ending with `pem` that is private
         3. `lean.public.key.name` is the filename ending with `pem` that is public
      3. Now time to set the `app token`, needed when making any call to Lean API. On the same ´Integration´ tab, on the menu on the left:
         1. go the ´Application´ section and copy the ´App token´.
         2. replace the value of `lean.api.token` in [application.properties](src/main/resources/application.properties) (`<LEAN_APP_TOKEN>`) with your `App token`)
2. Create the `keystore` required to make a secure connection in a file using [openssl](https://www.openssl.org/) (`src/main/java/me/lean/tech/dev/springboot/MtlsUsingRestTemplate.java`):
   1. Run the following command, you will be asked to put on a password, please remember it
   ```bash
   openssl pkcs12 -export -in cert.crt  -inkey key.pem  -certfile ca.pem -out yourp12filename.p12
   ```
   2. A new file will be generated called `yourp12filename.p12`, move it to the resources folder
   3. Replace the "`p12.filename`", "`p12.password`", "`app.token`" dummy properties with your own.

If all went well, you should be able to run the main class and receive "HTTP/1.1 200" and a valid response in the console:
   ```bash
   HTTP/1.1 200
   [{"id":13,"identifier":"FAB_UAE","name":"First Abu Dhabi Bank","logo"...
   ```
