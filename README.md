# triotwt prom
Plugin developed for triotwt prom.

Code is pretty rushed, especially the queue system as it had to be developed within a day.
___
Bungee queue system was broken a bit into the event.
We had to manually `/send all prom` every few minutes to get players out of the [Limbo](https://github.com/PSNRigner/Limbo) server.
___
Main source of lag at the beginning of the event was the Taco and Waterfall fork we were using.
It was some sort of networking issue.

After a bit we swapped it out for a Taco fork by Sprock and normal Waterfall, and we started to run stable at ~250 players on the prom server, and ~350 on the proxy.

Sprock had to make a plugin mid-event to change player slot amounts after we changed Spigots, as the new one didn't have a command built-in.
___
We were getting hit pretty hard by some bot attacks, server couldn't keep up on the first day, so on the second we put it behind the proxy and bots barely impacted performance. (900k connections throughout the event on 2nd day)
___
Server was hosted standalone with 10G of ram on a Vultr VPS on the first event.  
On the second day, it was hosted with a Waterfall proxy on a dedicated server from OVH with 5G ram dedicated.
___
[Trello Board Used](https://trello.com/b/ixCHLNqO/triotwt-prom)
