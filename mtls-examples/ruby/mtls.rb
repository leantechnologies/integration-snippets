require 'openssl'
require 'net/http'

options = {
  use_ssl: true,
  verify_mode: OpenSSL::SSL::VERIFY_PEER,
  cert: OpenSSL::X509::Certificate.new(File.read('cert.crt')),
  key: OpenSSL::PKey::RSA.new(File.read('key.pem')),
  ca_file: 'ca.pem'
}

uri = URI("https://api.leantech.me/banks/v1")
req = Net::HTTP::Get.new(uri)
req["lean-app-token"] = "<LEAN_APP_TOKEN>"

res = Net::HTTP.start(uri.hostname, uri.port, options) { |http|
  http.request(req)
}

puts res.body