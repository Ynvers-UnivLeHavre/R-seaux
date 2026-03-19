all : hello

clean :
		rm -f helloServeur
		rm -f helloClient

hello : helloClient helloServeur

helloServeur : helloServeur.c
		gcc -o helloServeur helloServeur.c

helloClient : helloClient.c
		gcc -o helloClient helloClient.c