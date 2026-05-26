package estruturas;

import java.util.Objects;

import java.io.Serializable;

/*
 * Esta classe representa um nó do mapa de dispersão.
 *
 * Cada NoMapa guarda uma chave e um valor.
 *
 * No trabalho, a chave será uma palavra encontrada nos arquivos de texto.
 * O valor será uma lista de documentos onde aquela palavra aparece.
 *
 * Exemplo:
 *
 * chave = "java"
 * valor = lista com os documentos que contêm a palavra "java"
 *
 * Esta classe implementa Serializable porque cada NoMapa fica armazenado
 * dentro do MapaDispersao.
 *
 * Como o mapa inteiro será salvo em arquivo, cada par chave-valor também
 * precisa ser salvo.
 */

public class NoMapa<K, T> implements Serializable {

	/*
	 * O serialVersionUID é um identificador de versão da classe.
	 *
	 * Quando o Java salva um objeto em arquivo, ele também guarda uma informação
	 * sobre a versão da classe usada naquele momento.
	 *
	 * Depois, quando o programa tenta carregar esse objeto de volta, o Java confere
	 * se a versão da classe ainda é compatível.
	 *
	 * Neste trabalho, usamos o valor 1L apenas para indicar que esta é a primeira
	 * versão serializável da classe.
	 */
	private static final long serialVersionUID = 1L;

	private K chave; 
	private T valor; 

	public K getChave() {
		return chave;
	}

	public void setChave(K chave) {
		this.chave = chave;
	}

	public T getValor() {
		return valor;
	}

	public void setValor(T valor) {
		this.valor = valor;
	}

	@Override // vou no source (shift + alt _ s) e clico em generate hashcode + equals pra
				// gerar isso e eu nao me preocupar
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NoMapa other = (NoMapa) obj;
		return Objects.equals(chave, other.chave);
	}

	@Override
	public int hashCode() {
		return Objects.hash(chave);
	}
}
