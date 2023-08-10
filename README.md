# Swiss Budget 💰🇨🇭 <img src="https://github.com/M1chaCH/swiss-budget/assets/67689103/3aa9b887-93fc-4ebc-82ef-8d1e1b0cdc14" alt="app-icon" height="80px" style="float: right" />
An IOS App that lets you manage your money in a breeze with a focus on your privacy. It features automatic transaction import via E-Mail!



There are a lot of money managers out there, but none of them support automatic transaction import in Switzerland. The reason for this is probably that none of the Banks really support access to transactions from a third party. My solution to this problem sounds a bit sketchy but it works perfectly fine.  
For the most modern bank accounts you can enable an E-Mail notification for each transaction that occured. I plan on reading these E-Mails and importing them into my money manager. This means that you have to grant my app access to your IMAP Server, which probably will raise privacy issues for most people. But this is why I am making this project open source (everybody can verify my statements).  
Following are facts that I will comply with: 
- All data is stored on your device or encrypted in the iCloud (I will never see your data & apple has it already anyways)
- I don't have a backend server (everything stays in your apple eco system)
- You can define an E-Mail folder for all your transactions and the app will only read this folder
- You choose if you want the app to delete the transaction mail once it was imported (minimal "lifetime" of transaction mails)
- the app uses IMAPS (so there is not really a chance for a MitM attack)
