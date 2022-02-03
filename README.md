# Kry-Livi Service Poller

##Hi Everyone, Rares typing here :)

#Before

####The task begins with some information. Looks like some research was done and nothing, nothing was good enough.
####Maybe we could find a company that provides something similar solution at a cheap price until we learn more about what we <ins>Actually</ins> need.
####Maybe we use some devops (maybe more since you also send a docker container), thus we have healthcheck already in place.
####Knowing what others offer and what we need is important. Something can be created based on what we already have which can create a more reliable solution.

######What if all we needed was just some code packed as an extension?

#Coding

##What to use
I thought to use Spring as I am used to it, but I used Vert.x so that it is more challenging and at the end at least I can see if I want to use it again.
Some conferences, some tutorials here and there and I can say that it is pretty cool. Excep when you have to search for errors and find only 2016 replies :)

Regarding front-end, I am more used to Angular/Ionic, not a fan on React (got 2 projects, I think they had more files than lines of code). Before them I used Ember ( what? Ember? )


So for this I used Bootstrap and JQuery to have something simple, easy to complain about in the future because why didn't Rares use that new cool framework from Netflix which everyone uses and nobody understands until a bug appears and the heart wants you to start learning

#While Coding

######BTW, if you want to run it. I began the project in Visual Studio and Continued in IntelliJ. Mostly run from the console in order to have that quick re-deploy
```
./gradlew clean test
```
I spent too much thinking, rethinking, thinking about overthinking to not overthink to be sure that I want to use this technology in the future.


Still some questions arise like "Do these verticles try to find a real thread?", "What if that thread dies?", "If I use Vert.x to get that juicy event loop performance and the code is similar to Node.js, then I can get even more performance with Node.js with a small stability risk. Livi/Kry has a lot of call services, then they can use UDP, then why they didn't chose Node.js? hmm hmm hm"

I did a lot of automations in the past year. What I realised is that we might not need a db for persistence.

We need something to keep our records, something reliable, something secure and something that even the BAs knows to use. We need Excel! Not that local excel file, but for example a Sharepoint Excel file. It is secure since we have a lot of security optiosn from Microsoft, it is easy to use ( you can draw on it) and we can just connect to it like we do on a db. If we want to create a report, guess what, we can write some formulas and everything is done for that annoying manager. It sound strange until you have a project like this and try it.

This time, I did not try it since I though that docker file will help me find some edge cases that I should consider.

I added the updated log4j library, but I did not printed that much body content since I want to read the full logs of the updates to be sure. Nowdays a small mistakes breaks 20 good points.

#Future Updates

Besides the Sharepoint implementation I would create an error module to handle a lot of requests. That combined with logs and KPIs can help us understand the network a bit more.

Adding cbeck for DB is not available ( Sharepoint it always available btw ;) )
I used a lastUpdate column instead of a creation time column, which gets updated automatically. The service update function could be created in a few minutes but for that more questions needs to be asked, not to just copy paste the insert code and edit it.


I would remove status in DB, as we check for it and update it in realtime, we can add logs if we are unclear or just watch the logs of that process

Handle special characters in name can also be added to the list


Hope you liked my story. It was way longer than 4 hours since I read/watched a lot of content to understand trends about and surrounding Vert.x and performance.
Exhausted with 3 projects at my current job, I want to skip the Unit/Integration tests accepting any penalty.

