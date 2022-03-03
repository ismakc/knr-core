package es.evilmonkey.kinaro.core.util;

/**
 * Clase de utilidades de String.
 *
 * @author Ismael El Kadaoui Calvo
 */
public final class KnrStringUtils {

	private KnrStringUtils() {
	}

	/**
	 * Comprueba si strNum es un número entero.
	 * @param strNum string a comprobar. Puede ser un entero, null o cualquier otra cosa
	 * @return <strong>true</strong> si el string define un número entero.<br>
	 * <strong>false</strong> en cualquier otro caso
	 */
	public static boolean isInt(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Integer.parseInt(strNum);
		}
		catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static String decapitalize(String word) {
		if (word == null || word.length() == 0) {
			return word;
		}

		char[] c = word.toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		return new String(c);
	}

}
