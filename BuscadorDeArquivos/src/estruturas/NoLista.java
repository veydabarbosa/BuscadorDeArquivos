package estruturas;

import java.io.Serializable;

/*
 * Esta classe representa um nó da lista encadeada.
 *
 * Um nó guarda duas coisas:
 *
 * 1. A informação armazenada.
 * 2. A referência para o próximo nó da lista.
 *
 * Visualmente:
 *
 * [info | proximo] -> [info | proximo] -> [info | proximo] -> null
 *
 * Esta classe implementa Serializable porque os nós fazem parte da lista
 * encadeada.
 *
 * Se a lista encadeada precisa ser salva em arquivo, todos os seus nós também
 * precisam ser salvos. Caso contrário, o Java não conseguiria reconstruir a
 * lista quando o índice fosse carregado novamente.
 */

public class NoLista<T> implements Serializable {

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

	private T info;
	private NoLista<T> proximo;

	public NoLista() {
	}

	public NoLista(T info) {
		this.info = info;
		this.proximo = null;
	}

	public T getInfo() {
		return info;
	}

	public void setInfo(T info) {
		this.info = info;
	}

	public NoLista<T> getProximo() {
		return proximo;
	}

	public void setProximo(NoLista<T> proximo) {
		this.proximo = proximo;
	}
}
