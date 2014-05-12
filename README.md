How to run ChatHub

```
1) In src/main, run Server.java. This will start the server on default IP and port: localhost 4444.
2) Also in src/main, run Client.java to create as many GUI clients (users) you would like. 
   Input a username for each client and change the IP and port number as necessary.
```

Directions for running from terminal:
```
$ cd <path/to/this/repo>
$ javac -d bin -cp src:lib/junit-4.11.jar -Xlint src/*/*.java
$ java -cp bin main/Server
$ java -cp bin main/Client
```
