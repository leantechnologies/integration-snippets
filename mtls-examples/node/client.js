const fs = require('fs')
const https = require('https')
const axios = require('axios')

const httpsAgent = new https.Agent({
  // This is your application certificate
  cert: fs.readFileSync('cert.crt'),
  // This is your private key associated with application certificate
  key: fs.readFileSync('key.pem'),
  // This is Lean's public certificate chain.
  ca: fs.readFileSync('ca.pem'),
})

const start = async () => {
  try {
    const request = await axios({
      method: 'post',
      headers: {
        'lean-app-token': 'LEAN_APP_TOKEN',
      },
      httpsAgent,
      // You can change the end point per your need. This endpoint is good for
      // testing mTLS
      url: 'https://api.leantech.me/customers/v1',
      withCredentials: true,
      jar: true,
    })
    console.log(request)
  } catch (error) {
    console.log(error)
  }
}

start()
