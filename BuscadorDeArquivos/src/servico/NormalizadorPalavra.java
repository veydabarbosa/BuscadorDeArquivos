package servico;

public class NormalizadorPalavra {

	public String normalizar(String palavraBruta) {
		if (palavraBruta == null) {
			return null;
		}

		String palavra = palavraBruta.toLowerCase();

		palavra = palavra.replaceAll("[^\\p{L}\\p{N}]", "");

		if (palavra.length() < 3) {
			return null;
		}

		if (!possuiLetra(palavra)) {
			return null;
		}

		return palavra;
	}

	private boolean possuiLetra(String palavra) {
		for (int i = 0; i < palavra.length(); i++) {
			char caractere = palavra.charAt(i);

			if (Character.isLetter(caractere)) {
				return true;
			}
		}

		return false;
	}
}