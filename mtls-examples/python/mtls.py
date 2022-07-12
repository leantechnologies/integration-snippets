import requests

getBanksUrl = 'https://api.leantech.me/banks/v1/'

headers = {'lean-app-token': '<LEAN_APP_TOKEN>', 'Content-Type': 'application/json'}

result = requests.get(
    getBanksUrl,
    headers=headers,
    cert=('cert.crt', 'key.pem'), # Use the path to your own certificate and private key here
    verify='ca.pem'
    )

if (result.status_code == 200) :
    print (result.json())
else :
    print (f"Error - {result.json()['status']}: {result.json()['message']}")
