# Swiss Budget 💰🇨🇭 <img src="https://github.com/M1chaCH/swiss-budget/assets/67689103/3aa9b887-93fc-4ebc-82ef-8d1e1b0cdc14" alt="app-icon" height="80px" style="float: right" />

A WebApp (tried making it an IOS App, but I'd rather go back to the old web stack) that lets you
manage your money in a breeze. It features automatic transaction import via E-Mail!

There are a lot of money managers out there, but none of them support automatic transaction import in Switzerland.
The reason for this is probably that none of the Banks really support access to transactions from a third party.
My solution to this problem sounds a bit sketchy, but it works perfectly fine.  
For the most modern bank accounts you can enable an E-Mail notification for each transaction that occurred.
I plan on reading these E-Mails and importing them into my money manager.
This means that you have to grant my app access to your IMAP Server.

## Progress

This is one of these project ideas that I absolutely want to complete one day but can't find the time to do so.
Since the first commit in the project, I've changed the fundamental implementation Idea twice.  
Initially I wanted to create an IOS mobile app. But after some time I got so fed up with SwiftUI that I changed the entire stack.
I've decided to use the stack that I know best.
At the time this was a Java Helidon REST server with an Angular frontend.
Using this stack, I've made quite good progress.
But again, after some time I've realized that what I am doing is way too close to what I do on my Job.
And I don't want to work eight hours a day for a company and then continue doing the same thing again in the evenings for another two hours.
So I've come to the decision to start over for a second time.
It is a bit sad to just throw away all the progress I've made.
But I do not only want to have a good finished product but also fun while building it.
So the current decision is to focus on the business logic and framework work rather than good looks and perfect UX.
This means I'll build the app as a console application.

### Planned business features

- [ ] automatic transaction import
- [ ] automatic transaction grouping
- [ ] some mechanism that helps users to save money
- [ ] some mechanism to track spending
- [ ] some mechanism to create a budget

### Planned technical features

- Backend: Kotlin Ktor server
    - Endpoint to execute commands from the "Terminal"
    - Websocket for Terminal autocomplete suggestions
- TransactionImporter: Kotlin App
    - check for new transactions periodically
- Console App: Kotlin console application
    - frontend in the native terminal
- Web Console App: Angular (?)
    - frontend in browser
- Native Mobile Apps (?)
    - frontend as native mobile apps
- Integrated OAuth with deployment-controller
- proper observability

Goal: Keep the "frontends" as tiny as possible.
Try to do as much as possible of the "rendering" in the backend using Ascii.
Most views will be tables.