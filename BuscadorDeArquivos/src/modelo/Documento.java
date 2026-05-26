package modelo;

import java.io.Serializable;
import java.util.Objects;

/*
 * Esta classe representa um arquivo de texto indexado pelo programa.
 *
 * Cada objeto Documento guarda o caminho de um arquivo .txt encontrado
 * durante a indexação.
 *
 * No índice invertido, cada palavra ficará associada a uma lista de documentos.
 *
 * Exemplo:
 *
 * "java" -> [aula1.txt] -> [resumo.txt]
 *
 * Isso significa que a palavra "java" aparece nos arquivos aula1.txt e resumo.txt.
 *
 * Esta classe implementa Serializable porque objetos Documento serão armazenados
 * dentro do índice. Como o trabalho exige salvar o índice em arquivo no disco
 * e carregar depois sem reindexar tudo, os documentos também precisam ser
 * serializáveis.
 */
public class Documento implements Serializable {

    /*
     * Identificador de versão usado pelo Java no processo de salvar e carregar
     * objetos desta classe.
     */
    private static final long serialVersionUID = 1L;

    private String caminho;

    public Documento(String caminho) {
        this.caminho = caminho;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    @Override
	public int hashCode() {
		return Objects.hash(caminho);
	}

	@Override // vou no source (shift + alt _ s) e clico em generate hashcode + equals pra
	// gerar isso e eu nao me preocupar
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Documento other = (Documento) obj;
		return Objects.equals(caminho, other.caminho);
	}

    @Override
    public String toString() {
        return caminho;
    }
}