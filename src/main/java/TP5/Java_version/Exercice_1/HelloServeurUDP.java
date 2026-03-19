package TP5.Java_version.Exercice_1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class HelloServeurUDP {
    private static final int PORT = 5000;
    private static final int TAILLE_BUFFER = 250;

    public static void main(String[] args) {
        int numeroClient = 0;

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Lancement du serveur Java sur le port " + PORT);

            while (true) {
                byte[] buffer = new byte[TAILLE_BUFFER];
                DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquetRecu);

                String message = new String(
                        paquetRecu.getData(),
                        paquetRecu.getOffset(),
                        paquetRecu.getLength(),
                        StandardCharsets.UTF_8
                );
                System.out.println("Message recu: " + message);

                numeroClient++;
                System.out.println("Le serveur envoie le numero de client " + numeroClient);

                // Le client C lit l'entier en ordre natif machine, pas en network byte order.
                byte[] numero = ByteBuffer.allocate(4)
                        .order(ByteOrder.nativeOrder())
                        .putInt(numeroClient)
                        .array();

                DatagramPacket paquetEnvoi = new DatagramPacket(
                        numero,
                        numero.length,
                        paquetRecu.getAddress(),
                        paquetRecu.getPort()
                );
                socket.send(paquetEnvoi);
            }
        } catch (Exception e) {
            System.err.println("Erreur serveur UDP: " + e.getMessage());
            System.exit(2);
        }
    }
}
