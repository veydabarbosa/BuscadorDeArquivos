package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Documento implements Serializable {

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

	@Override
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