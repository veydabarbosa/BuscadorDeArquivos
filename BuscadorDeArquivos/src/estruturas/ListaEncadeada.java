package estruturas;

import java.io.Serializable;

public class ListaEncadeada<T> implements Serializable {

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

	public void inserirSeNaoExistir(T valor) {
		T encontrado = buscar(valor);

		if (encontrado == null) {
			inserir(valor);
		}
	}
}