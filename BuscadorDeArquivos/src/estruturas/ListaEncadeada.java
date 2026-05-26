package estruturas;

import java.io.Serializable;

/*
 * Esta classe representa uma lista encadeada.
 *
 * No trabalho, a lista encadeada será usada em duas situações importantes:
 *
 * 1. Dentro do mapa de dispersão, para tratar colisões.
 *    Quando duas palavras caem na mesma posição do vetor do mapa,
 *    elas ficam armazenadas em uma lista encadeada.
 *
 * 2. Como lista de documentos de cada palavra.
 *    Cada palavra do índice aponta para uma lista de arquivos onde ela aparece.
 *
 * Exemplo:
 *
 * "java" -> [aula1.txt] -> [resumo.txt] -> [trabalho.txt]
 *
 * Esta classe implementa Serializable porque ela será armazenada dentro do índice.
 * Como o índice precisa ser salvo no disco e carregado depois, a lista também
 * precisa poder ser salva pelo Java.
 */

public class ListaEncadeada<T> implements Serializable {

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

	private NoLista<T> primeiro;

	public ListaEncadeada() {
		primeiro = null;
	}

	public NoLista<T> getPrimeiro() {
		return primeiro;
	}

	public boolean estaVazia() {
		return primeiro == null;
	}

	public void inserir(T valor) {
		NoLista<T> novo = new NoLista<>();
		novo.setInfo(valor);
		novo.setProximo(primeiro);
		primeiro = novo;
	}

	public T buscar(T valor) {
		NoLista<T> p = primeiro;

		while (p != null) {
			if (p.getInfo().equals(valor)) {
				return p.getInfo();
			}

			p = p.getProximo();
		}

		return null;
	}

	public void retirar(T valor) {
		NoLista<T> anterior = null;
		NoLista<T> p = primeiro;

		while (p != null && !p.getInfo().equals(valor)) {
			anterior = p;
			p = p.getProximo();
		}

		if (p != null) {
			if (p == primeiro) {
				primeiro = p.getProximo();
			} else {
				anterior.setProximo(p.getProximo());
			}
		}
	}

	// esse metodo tenta buscar o valor na lista
	// se a busca retornar null,
	// significa que o valor ainda nao existe, entao ele insere
	// se a busca encontrar algo, o metodo nao faz nada, evitando repetição
	public void inserirSeNaoExistir(T valor) {
		T encontrado = buscar(valor);

		if (encontrado == null) {
			inserir(valor);
		}
	}
}
