package TP2_NathanADOHO;

public class CRC {

	private static boolean isElementPresent(char[] messages, int key) {
		for (int element : messages) {
			if (element == key) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Méthode pour calculer le CRC et l'adjoindre au message.
	 * @param message Le mot binaire (ex: "101101")
	 * @param generateur Le polynôme générateur (ex: "1011")
	 * @return Le message complet avec le CRC
	 */

	public static String calculerCRC(String message, String generateur) {
		String crc = "";
		String mutableMessage = message;
		for (int i = 1; i < generateur.length(); i++)
			mutableMessage += '0';
		char messages[] = mutableMessage.toCharArray();
		char genChars[] = generateur.toCharArray();
		for (int i = 0; i <= messages.length - generateur.length(); i++) {
			if (messages[i] == '1') {
				for (int j = 0; j < generateur.length(); j++) {
					messages[i+j] = (messages[i+j] == genChars[j]) ? '0' : '1';
				}
			}
		}
		int IndiceDebut = messages.length - generateur.length();
		for (int j = IndiceDebut+1; j < messages.length; j++)
			crc += messages[j];
		return (message + crc);
	}

	public static boolean verifierMEssage(String message, String generateur) {
		Boolean intact;
		char messages[] = message.toCharArray();
		char genChars[] = generateur.toCharArray();
		for (int i = 0; i <= messages.length - generateur.length(); i++) {
			if (messages[i] == '1') {
				for (int j = 0; j < generateur.length(); j++) {
					messages[i+j] = (messages[i+j] == genChars[j]) ? '0' : '1';
				}
			}
		}

		intact = !isElementPresent(messages, '1');
		return intact;
	}

	public static void main(String[] args) {
		System.out.println(calculerCRC("110101", "101"));
	}
}