const express = require('express');
const fetch = require('node-fetch')
const app = express()
app.use(express.json())

app.listen(process.env.PORT || 3000, 
	() => console.log("Server is running..."));

const appId = "YOUR_APP_ID"

app.post('/', (request, response) => {
    // check that the webhook conforms to the correct structure to process
    if (!request.body && !request.body.type) { return };
    console.log(request.body)
    
    // handle webhooks
	switch(request.body.type) {
		case 'entity.created':
            return entityCreated(request.body.payload.id, request.body.payload.app_user_id, response);
        // add additional webhooks you would like to handle here - this could be expanded into a separate webhook service in your application.
        default:
			return console.log(`❌ webhook of type ${request.body.type} not supported`); 
	}
})

const entityCreated = async (entityId, userId, response) => {
	console.log(`new entity for user ${userId} created: ${entityId}`)
    
    /* ------------
    This would be where you save your entity in your database
    --------------*/
    
    // fetch accounts
    try {
       var accounts = await fetchAccounts(entityId)
    } catch (e) {
        console.log(`error: ${e}`)
    }

    // fetch transactions
    fetchTransactions(accounts, entityId)
    
    // respond to webhook
    response.send({status: 'OK'})
}

const fetchAccounts = async (entityId) => {
    let storedAccounts;

    // fetch accounts from the /accounts endpoint
    try {
        var accounts = await fetch('https://sandbox.leantech.me/v1/accounts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'lean-app-token': appId
            },
            body: JSON.stringify({
                entity_id: entityId,
                async: false
            })
        })
        var json = await accounts.json();
        storedAccounts = await json.payload.accounts;
    } catch (e) {
        console.log(`error: ${e}`)
    }

    // return the accounts values
    console.log('ACCOUNTS: ', storedAccounts)
    return storedAccounts
}

const fetchTransactions = async (accounts, entityId) => {
    let transactions;

    // Only fetch transactions for the first account in the array.
    const accountId = accounts[0].account_id
	var request = await fetch('https://sandbox.leantech.me/v1/transactions', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'lean-app-token': appId
		},
		body: JSON.stringify({
            entity_id: entityId,
            account_id: accountId,
			async: false
		})
    })
    var json = await request.json();
    transactions = await json.payload.transactions
    
    // log the result
    return console.log(transactions)
}