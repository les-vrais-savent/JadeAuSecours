javac -classpath lib/jade.jar -d classes src/examples/PongAgent.java

java -cp "lib/jade.jar:./classes" jade.Boot -gui -agents pong1:PongAgent
