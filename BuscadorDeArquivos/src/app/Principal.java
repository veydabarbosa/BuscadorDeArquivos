package app;

import java.io.IOException;
import java.util.Scanner;

import estruturas.ListaEncadeada;
import estruturas.NoLista;
import indice.IndiceInvertido;
import modelo.Documento;
import servico.IndexadorArquivos;
import servico.NormalizadorPalavra;
import servico.PersistenciaIndice;

public class Principal {

	private static final String ARQUIVO_INDICE = "indice.dat";

	public static void main(String[] args) {
		Scanner teclado = new Scanner(System.in);

		IndiceInvertido indice = new IndiceInvertido();
		IndexadorArquivos indexador = new IndexadorArquivos();
		PersistenciaIndice persistencia = new PersistenciaIndice();
		NormalizadorPalavra normalizador = new NormalizadorPalavra();

		int opcao = 0;

		while (opcao != 4) {
			System.out.println("===== BUSCADOR DE ARQUIVOS =====");
			System.out.println("1 - Indexar diretorio");
			System.out.println("2 - Carregar indice salvo");
			System.out.println("3 - Pesquisar palavra(s)");
			System.out.println("4 - Sair");
			System.out.print("Escolha uma opcao: ");

			opcao = Integer.parseInt(teclado.nextLine());

			if (opcao == 1) {
				System.out.print("Informe o caminho do diretorio: ");
				String caminhoDiretorio = teclado.nextLine();

				try {
					indice = new IndiceInvertido();

					indexador.indexarDiretorio(caminhoDiretorio, indice);

					persistencia.salvar(indice, ARQUIVO_INDICE);

					System.out.println("Indexacao concluida.");
					System.out.println("Indice salvo no arquivo " + ARQUIVO_INDICE);
					System.out.println();

				} catch (IOException e) {
					System.out.println("Erro ao indexar diretorio: " + e.getMessage());
					System.out.println();
				}

			} else if (opcao == 2) {
				try {
					indice = persistencia.carregar(ARQUIVO_INDICE);

					System.out.println("Indice carregado com sucesso.");
					System.out.println();

				} catch (IOException e) {
					System.out.println("Erro ao carregar indice: " + e.getMessage());
					System.out.println();

				} catch (ClassNotFoundException e) {
					System.out.println("Erro ao carregar classe do indice.");
					System.out.println();
				}

			} else if (opcao == 3) {
				System.out.print("Digite uma ou mais palavras para pesquisar: ");
				String pesquisa = teclado.nextLine();

				String[] palavrasBrutas = pesquisa.split("\\s+");
				String[] palavrasNormalizadas = new String[palavrasBrutas.length];

				int quantidadePalavrasValidas = 0;

				for (int i = 0; i < palavrasBrutas.length; i++) {
					String palavra = normalizador.normalizar(palavrasBrutas[i]);

					if (palavra != null) {
						palavrasNormalizadas[quantidadePalavrasValidas] = palavra;
						quantidadePalavrasValidas++;
					}
				}

				if (quantidadePalavrasValidas == 0) {
					System.out.println("Nenhuma palavra valida foi informada.");
					System.out.println();

				} else {
					String[] palavrasBusca = new String[quantidadePalavrasValidas];

					for (int i = 0; i < quantidadePalavrasValidas; i++) {
						palavrasBusca[i] = palavrasNormalizadas[i];
					}

					ListaEncadeada<Documento> resultado;

					if (quantidadePalavrasValidas == 1) {
						resultado = indice.buscar(palavrasBusca[0]);
					} else {
						resultado = indice.buscarTodas(palavrasBusca);
					}

					mostrarResultado(resultado);
				}

			} else if (opcao == 4) {
				System.out.println("Programa encerrado.");

			} else {
				System.out.println("Opcao invalida.");
				System.out.println();
			}
		}

		teclado.close();
	}

	private static void mostrarResultado(ListaEncadeada<Documento> documentos) {
		if (documentos == null || documentos.estaVazia()) {
			System.out.println("Nenhum documento encontrado.");
			System.out.println();
			return;
		}

		System.out.println("Documentos encontrados:");

		NoLista<Documento> p = documentos.getPrimeiro();

		while (p != null) {
			System.out.println(p.getInfo().getCaminho());
			p = p.getProximo();
		}

		System.out.println();
	}
}