package estruturas;

import java.util.Objects;

import java.io.Serializable;

public class NoMapa<K, T> implements Serializable {

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

	@Override
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