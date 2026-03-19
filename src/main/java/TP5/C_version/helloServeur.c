/**
* Fichier helloServeur.c
*/
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/signal.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#define TAILLE 250


int desc;
void finir(int s){
	close (desc);
	exit(1);
}

int main(int argc, char * argv[]){
	int resultat, n;
	struct sockaddr_in serveur;
	struct sockaddr_in client;
	struct hostent * host;
	int long_client, numero_client = 0;
	// creation de la socket en mode UDP
	desc = socket(AF_INET, SOCK_DGRAM, 0);
	if (desc < 0){
		perror("Erreur de création de socket\n");
		exit(-1);
	}
	serveur.sin_family = AF_INET;
	serveur.sin_addr.s_addr = htonl(INADDR_ANY); // host to network long
	serveur.sin_port = htons(5000);
	resultat = bind(desc, (struct sockaddr *)&serveur, sizeof(serveur));
	if (resultat < 0){
		perror("Erreur 2");
		exit(-2);
	}
	// mise en place d’un handler pour fermer proprement la socket
	signal(SIGINT, finir);
	long_client = sizeof(serveur);
	printf ("Lancement du serveur sur le port 5000\n");
	while (1){
		char buffer [TAILLE]="";
		n = recvfrom(desc, buffer, TAILLE, 0, (struct sockaddr *)&client, &long_client);
		printf ("%s\n",buffer);
	if (n<0){
		perror ("Erreur de reception\n");
		exit(-3);
	}
	numero_client++;
	printf("Le serveur envoi le numéro de client %d\n", numero_client);
	sendto(desc, &numero_client, sizeof(int), 0, (struct sockaddr *)&client, long_client);
	}
}
