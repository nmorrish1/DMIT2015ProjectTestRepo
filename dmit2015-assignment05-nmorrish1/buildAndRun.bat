@echo off
call mvn clean package
call docker build -t dmit2015.assignment04/dmit2015-assignment04-nmorrish1 .
call docker rm -f dmit2015-assignment04-nmorrish1
call docker run -d -p 8080:8080 -p 4848:4848 --name dmit2015-assignment04-nmorrish1 dmit2015.assignment04/dmit2015-assignment04-nmorrish1