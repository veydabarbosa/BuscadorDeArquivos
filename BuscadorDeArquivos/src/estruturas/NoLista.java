package estruturas;

import java.io.Serializable;

public class NoLista<T> implements Serializable {

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