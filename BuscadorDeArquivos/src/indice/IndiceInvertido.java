package indice;

import java.io.Serializable;

import estruturas.ListaEncadeada;
import estruturas.MapaDispersao;
import estruturas.NoLista;
import modelo.Documento;

public class IndiceInvertido implements Serializable {

	private static final long serialVersionUID = 1L;

	private MapaDispersao<String, ListaEncadeada<Documento>> indice;

	/*
	 *
	 * quando cria um IndiceInvertido, tambem precisa criar o mapa de dispersao que
	 * vai guardar as palavras
	 *
	 * o número 1000 é o tamanho inicial do mapa significa a quantidade de posicoes
	 * internas do mapa
	 */
	public IndiceInvertido() {
		indice = new MapaDispersao<>(1000);
	}

	/*
	 * esse método adiciona uma palavra ao índice.
	 *
	 * recebe:
	 *
	 * palavra -> a palavra encontrada no arquivo documento -> o arquivo onde essa
	 * palavra apareceu
	 *
	 * ex:
	 *
	 * adicionarPalavra("java", documentoAula1);
	 *
	 * significa:
	 *
	 * A palavra "java" apareceu no arquivo aula1.txt.
	 */
	public void adicionarPalavra(String palavra, Documento documento) {

		ListaEncadeada<Documento> listaDocumentos = indice.buscar(palavra);

		if (listaDocumentos == null) {

			listaDocumentos = new ListaEncadeada<>();

			indice.inserir(palavra, listaDocumentos);
		}

		listaDocumentos.inserirSeNaoExistir(documento);
	}

	public ListaEncadeada<Documento> buscar(String palavra) {
		return indice.buscar(palavra);
	}

	public ListaEncadeada<Documento> buscarTodas(String[] palavras) {
		ListaEncadeada<Documento> resultado = null;

		for (int i = 0; i < palavras.length; i++) {
			ListaEncadeada<Documento> listaAtual = buscar(palavras[i]);

			if (listaAtual == null) {
				return null;
			}

			if (resultado == null) {
				resultado = copiarLista(listaAtual);
			} else {
				resultado = interseccionar(resultado, listaAtual);
			}
		}

		return resultado;
	}

	private ListaEncadeada<Documento> copiarLista(ListaEncadeada<Documento> listaOriginal) {
		ListaEncadeada<Documento> copia = new ListaEncadeada<>();

		NoLista<Documento> p = listaOriginal.getPrimeiro();

		while (p != null) {
			copia.inserirSeNaoExistir(p.getInfo());
			p = p.getProximo();
		}

		return copia;
	}

	private ListaEncadeada<Documento> interseccionar(ListaEncadeada<Documento> lista1,
			ListaEncadeada<Documento> lista2) {

		ListaEncadeada<Documento> resultado = new ListaEncadeada<>();

		NoLista<Documento> p = lista1.getPrimeiro();

		while (p != null) {
			Documento documento = p.getInfo();

			if (lista2.buscar(documento) != null) {
				resultado.inserirSeNaoExistir(documento);
			}

			p = p.getProximo();
		}

		return resultado;
	}
}