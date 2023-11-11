# Swiss Budget 💰🇨🇭 <img src="https://github.com/M1chaCH/swiss-budget/assets/67689103/3aa9b887-93fc-4ebc-82ef-8d1e1b0cdc14" alt="app-icon" height="80px" style="float: right" />

A WebApp (tried making it a IOS App, but I'd rather go back to the old web stack) that lets you
manage your money in a breeze. It features automatic transaction import via E-Mail!



There are a lot of money managers out there, but none of them support automatic transaction import in Switzerland. The reason for this is probably that none of the Banks really support access to transactions from a third party. My solution to this problem sounds a bit sketchy but it works perfectly fine.  
For the most modern bank accounts you can enable an E-Mail notification for each transaction that occured. I plan on reading these E-Mails and importing them into my money manager. This means that you have to grant my app access to your IMAP Server.

## Progress
_should be ready for personal use: 28.04.2024_  
_should be publicly available at: 01.08.2024_

### Transaction Page
- [x] filter
  - [x] filter by tags
  - [x] improve filter UI design
- [ ] transaction list
  - [ ] change tag for transaction
    - change only for this transaction and automatically remove matching keyword. bulk changes can be made in configuration page.
  - [ ] implement transaction splitting
    - you can split a transaction into multiple tags, this way, f.e. cash retrieval can be split into the actual tags. 

### Configuration Page
- [ ] CRUD for tags, including bulk changes into the past
  - [ ] color & icon picker
  - [ ] add keyword to tag & apply into past
- [ ] CURD for keywords, including bulk changes into the past
  - [ ] move keyword & its transactions to different tag
- [ ] manage mail notifications
- [ ] rerun entire transaction -> tag mapping
- [ ] reset budget data
- [ ] reset saving data
- [ ] delete account
- [ ] exit demo mode 

### Home Page
- [ ] graphs with money spent in budgets
- [ ] current saldo in favourite budgets 
- [ ] saving state
- [ ] last 5 transactions

### Budget Page
- [ ] add budget plan for tag
    - monthly & yearly & custom time span?
- [ ] view available money for every budget in their timespan
- [ ] TODO figure out how to handle if too much money has been spent in budget
  - probably subtract proportionally from other budgets
    - (spent 100 bucks too much ob budget a -> budget b 100 bucks/month: subtract 66 buchs -> budget c 50 b/m: subtract 33 bucks...)

### Saving Page
- [ ] define x saving goals
  - a saving "budget" is also a tag: income & expense will add up to currently saved
- [ ] see how long it takes until goal achieved
- [ ] TODO, what if saving is not a transaction?
  - just subtract from monthly available?

### Demo accounts
- [ ] option to start with demo account
- [ ] load default data, including user, but user is marked as demo
- [ ] dont allow certain things
  - importing transactions
  - 

### Framework
- [ ] write tests for complicated data manipulations (assign tag / resolve tag conflicts -> they have a lot of edge cases)
- [ ] db connection pooling 
  - one db connection per request
  - limit max requests handled at the same time (maybe make "getConnection" blocking if no "slot" is open)
- [ ] one db transaction per request, if one statement fails rollback all changes from request
- [ ] fix page transitions
  - page WAS positioned absolutely, removing this broke transitions
- [ ] allow opening dialog in dialog
- [ ] TODOs all over the project for improvements
- [ ] dockerize
- [ ] load test
- [ ] detailed tests that user can't access data he's not allowed to
- [ ] add support for more banks
  - post, migros. NAB, UBS, ZKB, CS
- [ ] fronted: handle unauthorized response (send to login) 
