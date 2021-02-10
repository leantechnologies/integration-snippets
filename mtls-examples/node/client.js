const fs = require('fs')
const https = require('https')
const axios = require('axios')

const httpsAgent = new https.Agent({
  cert: fs.readFileSync('cert.crt'),
  key: fs.readFileSync('key.pem'),
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
