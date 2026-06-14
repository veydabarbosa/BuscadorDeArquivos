package estruturas;

import java.io.Serializable;

public class MapaDispersao<K, T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private ListaEncadeada<NoMapa<K, T>>[] info;

	public MapaDispersao(int tamanho) {
		info = new ListaEncadeada[tamanho];
	}

	private int calcularHash(K chave) {
		return Math.abs(chave.hashCode()) % info.length;
	}

	public void inserir(K chave, T valor) {
		int indice = calcularHash(chave);

		if (info[indice] == null) {
			info[indice] = new ListaEncadeada<>();
		}

		NoMapa<K, T> no = new NoMapa<>();
		no.setChave(chave);
		no.setValor(valor);

		info[indice].inserir(no);
	}

	public void remover(K chave) {
		int indice = calcularHash(chave);

		if (info[indice] != null) {
			NoMapa<K, T> no = new NoMapa<>();
			no.setChave(chave);

			info[indice].retirar(no);
		}
	}

	public T buscar(K chave) {
		int indice = calcularHash(chave);

		if (info[indice] != null) {
			NoMapa<K, T> no = new NoMapa<>();
			no.setChave(chave);

			NoMapa<K, T> encontrado = info[indice].buscar(no);

			if (encontrado != null) {
				return encontrado.getValor();
			}
		}

		return null;
	}

	public double calcularFatorCarga() {
		int quantidade = 0;

		for (int i = 0; i < info.length; i++) {
			if (info[i] != null) {
				NoLista<NoMapa<K, T>> p = info[i].getPrimeiro();

				while (p != null) {
					quantidade++;
					p = p.getProximo();
				}
			}
		}

		return (double) quantidade / info.length;
	}
}