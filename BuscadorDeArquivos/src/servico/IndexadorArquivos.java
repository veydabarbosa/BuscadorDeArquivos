package servico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import indice.IndiceInvertido;
import modelo.Documento;

public class IndexadorArquivos {

    private NormalizadorPalavra normalizador;

    public IndexadorArquivos() {
        normalizador = new NormalizadorPalavra();
    }

    public void indexarDiretorio(String caminhoDiretorio, IndiceInvertido indice) throws IOException {
        File diretorio = new File(caminhoDiretorio);

        if (!diretorio.exists()) {
            throw new IOException("Diretorio nao encontrado: " + caminhoDiretorio);
        }

        indexarArquivoOuDiretorio(diretorio, indice);
    }

    private void indexarArquivoOuDiretorio(File arquivoOuDiretorio, IndiceInvertido indice) throws IOException {
        if (arquivoOuDiretorio.isDirectory()) {
            File[] arquivos = arquivoOuDiretorio.listFiles();

            if (arquivos != null) {
                for (int i = 0; i < arquivos.length; i++) {
                    indexarArquivoOuDiretorio(arquivos[i], indice);
                }
            }
        } else {
            if (arquivoOuDiretorio.getName().toLowerCase().endsWith(".txt")) {
                indexarArquivoTexto(arquivoOuDiretorio, indice);
            }
        }
    }

    private void indexarArquivoTexto(File arquivo, IndiceInvertido indice) throws IOException {
        Documento documento = new Documento(arquivo.getAbsolutePath());

        BufferedReader leitor = new BufferedReader(new FileReader(arquivo));

        String linha = leitor.readLine();

        while (linha != null) {
            String[] palavras = linha.split("\\s+");

            for (int i = 0; i < palavras.length; i++) {
                String palavraNormalizada = normalizador.normalizar(palavras[i]);

                if (palavraNormalizada != null) {
                    indice.adicionarPalavra(palavraNormalizada, documento);
                }
            }

            linha = leitor.readLine();
        }

        leitor.close();
    }
}