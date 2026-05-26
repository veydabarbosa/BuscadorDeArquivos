package servico;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import indice.IndiceInvertido;

public class PersistenciaIndice {

	public void salvar(IndiceInvertido indice, String caminhoArquivo) throws IOException {
		ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(caminhoArquivo));

		saida.writeObject(indice);

		saida.close();
	}

	public IndiceInvertido carregar(String caminhoArquivo) throws IOException, ClassNotFoundException {
		ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(caminhoArquivo));

		IndiceInvertido indice = (IndiceInvertido) entrada.readObject();

		entrada.close();

		return indice;
	}
}