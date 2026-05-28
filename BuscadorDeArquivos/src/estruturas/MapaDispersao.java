package estruturas;

import java.io.Serializable;

/*
 * Esta classe representa o mapa de dispersão usado como estrutura principal
 * do índice do trabalho.
 *
 * O mapa de dispersão será usado para associar cada palavra encontrada nos
 * arquivos de texto a uma lista de documentos onde essa palavra aparece.
 *
 * Exemplo:
 *
 * "java"  -> lista de documentos onde aparece a palavra java
 * "mapa"  -> lista de documentos onde aparece a palavra mapa
 * "texto" -> lista de documentos onde aparece a palavra texto
 *
 * Esta classe implementa Serializable porque o trabalho pede que, depois de
 * montar o índice em memória, esse índice seja salvo em arquivo no disco.
 *
 * Como o mapa de dispersão faz parte desse índice, ele também precisa poder
 * ser transformado em arquivo pelo Java.
 *
 * Além disso, o mapa contém outras estruturas dentro dele, como ListaEncadeada
 * e NoMapa. Por isso, essas classes internas também precisam implementar
 * Serializable.
 */

public class MapaDispersao<K, T> implements Serializable {

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

	private ListaEncadeada<NoMapa<K, T>>[] info;

	public MapaDispersao(int tamanho) {
		info = new ListaEncadeada[tamanho];
	}

	private int calcularHash(K chave) {
		return Math.abs(chave.hashCode()) % info.length; // converte o valor se for negativo pra positivo e divide pelo
															// total de posicoes
	}

	public void inserir(K chave, T valor) {
		int indice = calcularHash(chave); // descobre em qual posicao do vetor a chave vai ficar

		/*
		 * Como o construtor cria apenas o vetor, as posições começam como null.
		 *
		 * Então, antes de inserir, preciso verificar se já existe uma lista
		 * encadeada nessa posição.
		 *
		 * Se não existir, crio a lista somente agora, quando ela realmente
		 * vai ser usada.
		 */
		if (info[indice] == null) {
			info[indice] = new ListaEncadeada<>();
		}

		NoMapa<K, T> no = new NoMapa<>(); // crio um novo nó
		no.setChave(chave); // coloco chave no nó
		no.setValor(valor); // coloco valor no nó

		info[indice].inserir(no); // pego a lista que ta na posicao do vetor e insiro o no na posicao do vetor da
									// lista
	}

	public void remover(K chave) {
		int indice = calcularHash(chave); // descobre em qual posicao do vetor a chave vai ficar

		/*
		 * Antes de remover, preciso verificar se existe lista naquela posição.
		 *
		 * Se info[indice] for null, significa que nunca foi inserido nada
		 * naquela posição, então não tem o que remover.
		 */
		if (info[indice] != null) {
			NoMapa<K, T> no = new NoMapa<>(); // crio um novo nó
			no.setChave(chave); // coloco chave no nó

			info[indice].retirar(no); // vou na lista da posição calculada e removo o no com aquela chave
		}
	}

	public T buscar(K chave) {
		int indice = calcularHash(chave); // descobre em qual posiçao do vetor essa chave deveria estar

		/*
		 * Antes de buscar, preciso verificar se existe lista naquela posição.
		 *
		 * Se info[indice] for null, significa que não existe nenhum elemento
		 * naquela posição do vetor.
		 *
		 * Então não tem como buscar dentro de uma lista que ainda não foi criada.
		 */
		if (info[indice] != null) {
			NoMapa<K, T> no = new NoMapa<>(); // crio um novo nó
			no.setChave(chave); // coloco chave no nó

			NoMapa<K, T> encontrado = info[indice].buscar(no); // vai na lista daquela posiçao e procura o nó

			if (encontrado != null) { // se achou
				return encontrado.getValor(); // devolve o valor guardado
			}
		}

		return null; // se nao achou, retorna nulo
	}

	public double calcularFatorCarga() { // fator de carga é: quantidade de elementos / tamanho do vetor
		int quantidade = 0; // começo contando do zero;

		for (int i = 0; i < info.length; i++) { // percorro todas as posicoes do vetor

			/*
			 * Como agora nem toda posição do vetor tem lista criada,
			 * preciso verificar se info[i] é diferente de null.
			 *
			 * Se for null, significa que aquela posição nunca recebeu nenhum elemento.
			 *
			 * Se não fizer essa verificação, pode dar NullPointerException
			 * ao tentar fazer info[i].getPrimeiro().
			 */
			if (info[i] != null) {
				NoLista<NoMapa<K, T>> p = info[i].getPrimeiro(); // pego o primeiro nó da lista daquela posição

				while (p != null) { // enquanto tiver nó da lista, ele conta
					quantidade++;
					p = p.getProximo();
				}
			}
		}

		return (double) quantidade / info.length; // divide a quantidade de elementos pelo tamanho do vetor
	}
}