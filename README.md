# Slack Message Remover Tool  

A single file groovy app/script to automatically delete private (DM/IM) messages to other slack team members.  
Runs on command line.  
It uses the Slack API and a unique "API token" to achieve said task.  

## Usage  
### Procuing an API Token
You will need a Slack API token!  
To get one, do this;  
1. Log in to Slack in the browser (i.e. your team domain, username and password in Chrome/Firefox/Safari etc)  
2. Once authenticated, go here: https://api.slack.com/custom-integrations/legacy-tokens, and you'll find your active API token here  
- More info here: https://api.slack.com/custom-integrations/legacy-tokens  
3. Paste your token into the token (String) field at the top of MessageRemover

### Running the app
You can either run via command line, and attempt to resolve all dependencies for the classpath,  
OR you can run via gradle:run  
OR you can run via an IDE if you use one (just because it's easier :))  

Once running, and the list of IMs is printed, follow the on screen prompts.  

#### Running in java as a fat-jar  
If choosing to run via command line, one option is to compile (or get a compiled version) of a jar with the groovy libs included.  
Makes for a big jar file, but you can run it with Java rather than the Groovy interpetor.

I.e. If you procure your token as per "Usage" above, you can run the script via java -jar with your token as an arg,  
like so: java -jar slack-msg-remover.jar xxxx-##########-##########-##########-xxxxxx.

## Notes  
I've not bothered to package this up or make an executable out of it as it was originally intended to be throw away code for a once off.  
However, I realised it could be quite useful for others who are in need of a way to obfuscate or remove their messaging history with certain individuals.  
