/**
* Fichier helloClient.c
*/
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/signal.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>


int main(int argc, char * argv[]){
	char * nom_serveur;
	int port, desc, resultat;
	struct sockaddr_in client, serveur;
	struct hostent * hp;
	int buffer, long_serveur;
	if (argc < 3){
		perror("usage commande nomServeur port\n");
		exit(-1);
	}
	nom_serveur = argv[1];
	port = atoi(argv[2]);
	printf("Port : %d\n", port);
	hp = gethostbyname(argv[1]);
	serveur.sin_family = AF_INET;
	serveur.sin_addr.s_addr = ((struct in_addr *)(hp->h_addr))->s_addr;
	serveur.sin_port = htons(port);
	desc = socket(AF_INET, SOCK_DGRAM, 0);
	if (desc < 0){
		perror("Erreur de creation de socket\n");
		exit(-1);
	}
	client.sin_family = AF_INET;
	client.sin_addr.s_addr = htonl(INADDR_ANY); // host to network long
	client.sin_port = htons(5001);
	resultat = bind(desc, (struct sockaddr *)&client, sizeof(struct sockaddr_in));
	if (resultat < 0){
		perror("Erreur 2 ");
		exit(-2);
	}
	printf("Envoi au serveur Bonjour\n");
	sendto(desc, "Bonjour c est le prof", sizeof(char)*21, 0, (struct sockaddr *)&serveur, sizeof(serveur));
	recvfrom(desc, &buffer, sizeof(int), 0, (struct sockaddr *)&serveur, &long_serveur);
	printf("Le numero attribué par le serveur %d\n",buffer);
	close(desc);
}