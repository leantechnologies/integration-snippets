import json
import requests

getBanksUrl = 'https://api.leantech.me/banks/v1/'

headers = {'lean-app-token': '4028ed2c76b498400176b4d7465d0001', 'Content-Type': 'application/json'}

result = requests.get(
    getBanksUrl,
    headers=headers,
    cert=('your_apps.crt', 'your_apps.pem'), # Use the path to your own certificate and private key here
    verify='lean_certificate_chain.pem'
    )

if (result.status_code == 200) :
    print (result.json())
else :
    print (f"Error - {result.json()['status']}: {result.json()['message']}")
