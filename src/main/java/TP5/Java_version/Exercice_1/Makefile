all: compile

compile:
	javac -d . HelloClientUDP.java HelloServeurUDP.java

run-server: compile
	java TP5.Java_version.Exercice_1.HelloServeurUDP

run-client: compile
	java TP5.Java_version.Exercice_1.HelloClientUDP localhost 5000

clean:
	rm -rf TP5
	rm -f *.class
