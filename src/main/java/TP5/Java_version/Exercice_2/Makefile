all: compile

compile:
	javac -d . TicTacToeGame.java TicTacToeServer.java TicTacToeClient.java

run-server-random: compile
	java TP5.Java_version.Exercice_2.TicTacToeServer random 6000

run-server-pvp: compile
	java TP5.Java_version.Exercice_2.TicTacToeServer pvp 6000

run-client: compile
	java TP5.Java_version.Exercice_2.TicTacToeClient localhost 6000

clean:
	rm -rf TP5
	rm -f *.class
