/*
  Please make sure the system running this code has lean root certificate installed in system or user store for more info please read this
*/

using System;
using System.Threading.Tasks;
using System.Net.Http;
using System.Security.Cryptography.X509Certificates;

namespace HTTP_Test
{
  class program
  {
    static void Main()
    {
      Task t = new Task(HTTP_GET);
      t.Start();
      Console.ReadLine();
    }


    static async void HTTP_GET()
    {
      // path of pfx file generate by 
      // $ openssl pkcs12 -export -out certificate.pfx -inkey privateKey.key -in certificate.crt -certfile CAcert 
      var certificateLocation = "";
      // password of pfx file
      var certificatePassword = "";
      // lean app token
      var leanAppToken = "";
      // create a new HttpClientHandler
      var handler = new HttpClientHandler();
      // create a new certficite using location and password
      var certificate = new X509Certificate2(certificateLocation, certificatePassword);
      // add certificate to client certificates
      handler.ClientCertificates.Add(certificate);
      // ignore server checks (Caution: You should implement a check to confirm server certificate)
      handler.ServerCertificateCustomValidationCallback = (message, cert, chain, errors) => { return true; };
      // create a new HttpClient using the handler and setting the default BaseAddress as https://api.leantech.me/
      var client = new HttpClient(handler)
      {
        BaseAddress = new Uri("https://api.leantech.me/")
      };
      // add lea app token default request header
      client.DefaultRequestHeaders.Add("lean-app-token", leanAppToken);

      // request the banks endpoint
      HttpResponseMessage response = await client.GetAsync("/banks/v1");

      // output to console status and JSON
      Console.WriteLine("Response StatusCode: " + (int)response.StatusCode);
      HttpContent content = response.Content;
      string result = await content.ReadAsStringAsync();
      Console.WriteLine(result);
    }
  }
}
