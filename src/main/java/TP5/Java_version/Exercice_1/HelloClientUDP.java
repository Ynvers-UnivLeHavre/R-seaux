package TP5.Java_version.Exercice_1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;


public class HelloClientUDP {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java HelloClientUDP <nomServeur> <port>");
            System.exit(1);
        }

        String nomServeur = args[0];
        int port = Integer.parseInt(args[1]);

        try (DatagramSocket socket = new DatagramSocket(5001)) {
            InetAddress adresseServeur = InetAddress.getByName(nomServeur);

            byte[] message = "Bonjour c est le prof".getBytes(StandardCharsets.UTF_8);
            DatagramPacket envoi = new DatagramPacket(message, message.length, adresseServeur, port);
            socket.send(envoi);
            System.out.println("Envoi au serveur: Bonjour c est Nathan");

            byte[] reponse = new byte[4];
            DatagramPacket recu = new DatagramPacket(reponse, reponse.length);
            socket.receive(recu);

            // Le client C lit l'entier en ordre natif machine, pas en network byte order.
            int numeroClient = ByteBuffer.wrap(recu.getData(), 0, 4)
                    .order(ByteOrder.nativeOrder())
                    .getInt();

            System.out.println("Numero attribue par le serveur: " + numeroClient);
        } catch (Exception e) {
            System.err.println("Erreur client UDP: " + e.getMessage());
            System.exit(2);
        }
    }
}
