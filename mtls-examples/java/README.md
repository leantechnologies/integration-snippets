
# Lean-dev-client-template

This project is a java template to access Lean API. It contains a simple http client that loads the required certificates
(available in [dev.leantech.me](https://dev.leantech.me)) and make a call to Lean API.

## Requirements

* Maven
* Java 7

## Usage

If you want to run this template, you will a set of credentials from Lean Technologies.
1. Head to [dev.leantech.me](https://dev.leantech.me) and create an account (if you have not done it yet). This will give you access to Lean sandbox API.
2. Once you are ready, reach out to devsupport@leantech.me to ask access to the prod environment.
3. Once you have prod access
   1. Log in to [dev.leantech.me](https://dev.leantech.me)
   2. Go to ´Integration´ tab, on the menu on the left
      1. On the ´Certificates´ section, click on `Generate new certificate`, which will prompt you to save the new certificate to be saved on your local computer.
         1. Remember where these are downloaded as they will be needed for the next step.
      2. In the [application.properties](src/main/resources/application.properties), either
         1. replace the path of certificates and keys
         2. or replace the mock certificates and keys under `src/main/resources/certificates`.
            1. Note: Make sure the certificates' filenames match to the ones expected in [application.properties](src/main/resources/application.properties)
   3. Now time to set the `app token`, needed when making any call to Lean API. On the same ´Integration´ tab, on the menu on the left:
      1. go the ´Application´ section and copy the ´App token´.
      2. replace the value of `lean.api.token` in [application.properties](src/main/resources/application.properties) (by default `<add-your-own-token-here>`) with your `App token`)


If you need access to the Lean API from a service that is not written in Java, consider looking at [integration-snippets](https://github.com/leantechnologies/integration-snippets)
