package indice;

import java.io.Serializable;

import estruturas.ListaEncadeada;
import estruturas.MapaDispersao;
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
}