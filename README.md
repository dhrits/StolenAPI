## Experiments with Stolen App REST API
Hacking around with Stolen App's REST API. The objective is to build a fake
automated trading engine :D VERY MUCH UNDER CONSTRUCTION. Don't judge :|

## Steps to run
In order to run the script, you will need a unique_id.txt file in the
root of pythonapi package. This is really the only somewhat tricky
part. Stolen uses Oauth. We need the value of the Authentication
field.  Note: PLEASE be careful with the value of this field. It can
be used to impersonate you and we wouldn't want that. You'll lose all
your totally fake wealth :| Below are steps to install mitmproxy on a
Mac.

* Navigate to https://mitmproxy.org/. Download the mitmproxy Mac binary.
* Make sure your iphone and mac are on the same wifi
* In terminal run ifconfig en0 and get your Mac's ip address
* Navigate to wifi settings in your phone, add your Mac's IP as the proxy server
* Use port 8080
* Now that your phone is proxied, use your phone to navigate to mitm.it.
* Install the cert to your phone (this is how you'll bypass SSL)
* Start mitmproxy and you'll now see HTTP requests your phone makes
* Start the stolen app
* Use arrow keys to navigate to any request to api.getstolen.com
* Hit <ENTER> to view the request headers
* You'll see a "Authorization: Bearer <some random string>" field
* Copy the string (not the Bearer part) into a file in the root pythonapi folder under the name "unique_id.txt"
* You're good to go!! Run python strategies.py for a basic strategy
* Play around with strategies. You can define new ones in strategies.py
* Predicates for trading (apply to account being purchased) and halting (apply to you) can be added to predicates.py



